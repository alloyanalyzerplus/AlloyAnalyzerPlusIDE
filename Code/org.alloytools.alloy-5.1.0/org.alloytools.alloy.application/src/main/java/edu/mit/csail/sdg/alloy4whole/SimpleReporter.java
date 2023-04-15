/* Alloy Analyzer 4 -- Copyright (c) 2006-2009, Felix Chang
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files
 * (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF
 * OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package edu.mit.csail.sdg.alloy4whole;

import static arepair.clean.ModelSimplifier.simplify;
import static arepair.patcher.etc.Constants.BOUND_TYPE;
import static arepair.patcher.etc.Constants.MAX_ARITY;
import static arepair.patcher.etc.Constants.MAX_DEPTH_OR_COST;
import static arepair.patcher.etc.Constants.MAX_OP_NUM;
import static arepair.patcher.etc.Constants.SUSPICIOUSNESS_THRESHOLD;
import static arepair.patcher.etc.SearchStrategy.ALL_COMBINATIONS;
import static parser.etc.Context.timer;
import static parser.util.AlloyUtil.countDescendantNum;
import static parser.util.AlloyUtil.mergeModelAndTests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import org.alloytools.alloy.core.AlloyCore;
//import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;

import alloyfl.coverage.util.CoverageScoreFormula;
import alloyfl.coverage.util.TestResult;
import alloyfl.himualloy.util.TestRunner;
import alloyfl.hybrid.opt.HybridFaultLocatorOpt;
import alloyfl.hybrid.visitor.DescendantCollector;
import alloyfl.mutation.util.MutationScoreFormula;
import alloyfl.mutation.util.ScoreInfo;
import alloyfl.mutation.visitor.MutationBasedFaultDetector;
import arepair.fl.FaultLocator;
import arepair.fl.MutationImpact;
import arepair.generator.Generator;
import arepair.generator.opt.GeneratorOpt;
import arepair.generator.util.TypeAnalyzer;
import arepair.patcher.etc.RelevantTestResults;
import arepair.patcher.etc.SearchStrategy;
import arepair.patcher.opt.PatcherOpt;
import arepair.synthesizer.Synthesizer;
import arepair.synthesizer.util.DepthInfo;
import aunit.coverage.AlloyParagraph;
import aunit.coverage.Construct;
import aunit.coverage.CoverageCriteriaBuilder;
import aunit.coverage.Formula;
import aunit.coverage.FunctionInvocation;
import aunit.coverage.RelMultiplicity;
import aunit.coverage.SigAbstract;
import aunit.coverage.SigParagraph;
import aunit.coverage.Signature;
import aunit.coverage.TestCase;
import aunit.gui.AUnitExecution;
import aunit.gui.AUnitTreeNode;
import aunit.gui.AlloyFLFormulaTreeNode;
import aunit.gui.AlloyFLHeaderNode;
import aunit.gui.AlloyFLHighlightNode;
import aunit.gui.AlloyFLScoreTreeNode;
import aunit.gui.CommandFactFormulaNode;
import aunit.gui.CommandFormulaNode;
import aunit.gui.CommandHeaderNode;
import aunit.gui.CoverageInformation;
import aunit.gui.CoverageLeafNode;
import aunit.gui.CoverageTreeNode;
import aunit.gui.FuncCallHolder;
import aunit.gui.MuAlloyHeaderNode;
import aunit.gui.MuAlloyTreeNode;
import aunit.gui.PassFailTreeNode;
import edu.mit.csail.sdg.alloy4.A4Preferences.SusFormula;
import edu.mit.csail.sdg.alloy4.A4Reporter;
import edu.mit.csail.sdg.alloy4.ConstList;
import edu.mit.csail.sdg.alloy4.ConstMap;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4.ErrorFatal;
import edu.mit.csail.sdg.alloy4.ErrorSyntax;
import edu.mit.csail.sdg.alloy4.ErrorType;
import edu.mit.csail.sdg.alloy4.ErrorWarning;
import edu.mit.csail.sdg.alloy4.MailBug;
import edu.mit.csail.sdg.alloy4.OurDialog;
import edu.mit.csail.sdg.alloy4.Pair;
import edu.mit.csail.sdg.alloy4.Pos;
import edu.mit.csail.sdg.alloy4.Util;
import edu.mit.csail.sdg.alloy4.Version;
import edu.mit.csail.sdg.alloy4.WorkerEngine.WorkerCallback;
import edu.mit.csail.sdg.alloy4.WorkerEngine.WorkerTask;
import edu.mit.csail.sdg.alloy4.XMLNode;
import edu.mit.csail.sdg.alloy4viz.StaticInstanceReader;
import edu.mit.csail.sdg.alloy4viz.VizGUI;
import edu.mit.csail.sdg.ast.Command;
import edu.mit.csail.sdg.ast.Decl;
import edu.mit.csail.sdg.ast.Expr;
import edu.mit.csail.sdg.ast.ExprBinary;
import edu.mit.csail.sdg.ast.ExprCall;
import edu.mit.csail.sdg.ast.ExprConstant;
import edu.mit.csail.sdg.ast.ExprHasName;
import edu.mit.csail.sdg.ast.ExprITE;
import edu.mit.csail.sdg.ast.ExprLet;
import edu.mit.csail.sdg.ast.ExprList;
import edu.mit.csail.sdg.ast.ExprQt;
import edu.mit.csail.sdg.ast.ExprUnary;
import edu.mit.csail.sdg.ast.ExprVar;
import edu.mit.csail.sdg.ast.Func;
import edu.mit.csail.sdg.ast.Module;
import edu.mit.csail.sdg.ast.Sig;
import edu.mit.csail.sdg.ast.Sig.Field;
import edu.mit.csail.sdg.ast.Sig.PrimSig;
import edu.mit.csail.sdg.ast.Test;
import edu.mit.csail.sdg.parser.CompModule;
import edu.mit.csail.sdg.parser.CompUtil;
import edu.mit.csail.sdg.translator.A4Options;
import edu.mit.csail.sdg.translator.A4Solution;
import edu.mit.csail.sdg.translator.A4SolutionReader;
import edu.mit.csail.sdg.translator.A4SolutionWriter;
import edu.mit.csail.sdg.translator.Simplifier;
import edu.mit.csail.sdg.translator.TranslateAlloyToKodkod;
import kodkod.ast.BinaryExpression;
import kodkod.engine.CapacityExceededException;
import kodkod.engine.fol2sat.HigherOrderDeclException;
import mualloy.opt.MutantGeneratorOpt;
import mualloy.util.AUnitTestCase;
import mualloy.visitor.ModelMutator;
import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;
import parser.ast.nodes.Assertion;
import parser.ast.nodes.Fact;
import parser.ast.nodes.Function;
import parser.ast.nodes.ModelUnit;
import parser.ast.nodes.Node;
import parser.ast.nodes.Predicate;
import parser.ast.nodes.SigDecl;
import parser.ast.visitor.PrettyStringVisitor;
import parser.ast.visitor.PrettyStringVisitorWithoutEmptyRun;
import parser.etc.Names;
import parser.util.AlloyUtil;
import parser.util.FileUtil;
import parser.util.StringUtil;

/** This helper method is used by SimpleGUI. */

@SuppressWarnings("restriction" )
final class SimpleReporter extends A4Reporter {

    public static final class SimpleCallback1 implements WorkerCallback {

        private final SimpleGUI                   gui;
        private final VizGUI                      viz;
        private final SwingLogPanel               span;
        private final Set<ErrorWarning>           warnings = new HashSet<ErrorWarning>();
        private final List<String>                results  = new ArrayList<String>();
        private int                               len2     = 0, len3 = 0, verbosity = 0;
        private final String                      latestName;
        private final int                         latestVersion;

        //Variables for printing and storing AUnit results
        private ArrayList<JTree>                  aunitTrees;
        private HashMap<String,ArrayList<JTree>>  coverageTrees;
        private ArrayList<String>                 coverageOrder;
        private ArrayList<CoverageInformation>    covInfos;
        private ArrayList<DefaultMutableTreeNode> cmdNodes;

        //Variables for printing and storing MuAlloy results
        private ArrayList<JTree>                  mualloyTrees;

        //Variables for printing and storing AlloyFL results
        private ArrayList<JTree>                  alloyFLTrees;
        private ArrayList<String>                 highlight_colors;
        private ArrayList<Pos>                    highlight_locs;

        //Variables for printing and storing ARepair results
        private ArrayList<JTree>                  arepairTrees;
        private ArrayList<DefaultMutableTreeNode> cmdNodesARepair;

        public SimpleCallback1(SimpleGUI gui, VizGUI viz, SwingLogPanel span, int verbosity, String latestName, int latestVersion) {
            this.gui = gui;
            this.viz = viz;
            this.span = span;
            this.verbosity = verbosity;
            this.latestName = latestName;
            this.latestVersion = latestVersion;
            len2 = len3 = span.getLength();

            aunitTrees = new ArrayList<JTree>();
            coverageTrees = new HashMap<String,ArrayList<JTree>>();
            coverageOrder = new ArrayList<String>();
            covInfos = new ArrayList<CoverageInformation>();
            cmdNodes = new ArrayList<DefaultMutableTreeNode>();

            mualloyTrees = new ArrayList<JTree>();

            alloyFLTrees = new ArrayList<JTree>();
            highlight_colors = new ArrayList<String>();
            highlight_locs = new ArrayList<Pos>();

            arepairTrees = new ArrayList<JTree>();
            cmdNodesARepair = new ArrayList<DefaultMutableTreeNode>();
        }

        @Override
        public void done() {
            if (viz != null)
                span.setLength(len2);
            else
                span.logDivider();
            span.flush();
            gui.doStop(0);
        }

        @Override
        public void fail() {
            span.logBold("\nAn error has occurred!\n");
            span.logDivider();
            span.flush();
            gui.doStop(1);
        }

        @Override
        public void callback(Object msg) {
            if (msg == null) {
                span.logBold("Done\n");
                span.flush();
                return;
            }
            if (msg instanceof String) {
                span.logBold(((String) msg).trim() + "\n");
                span.flush();
                return;
            }
            if (msg instanceof Throwable) {
                for (Throwable ex = (Throwable) msg; ex != null; ex = ex.getCause()) {
                    if (ex instanceof OutOfMemoryError) {
                        span.logBold("\nFatal Error: the solver ran out of memory!\n" + "Try simplifying your model or reducing the scope,\n" + "or increase memory under the Options menu.\n");
                        return;
                    }
                    if (ex instanceof StackOverflowError) {
                        span.logBold("\nFatal Error: the solver ran out of stack space!\n" + "Try simplifying your model or reducing the scope,\n" + "or increase stack under the Options menu.\n");
                        return;
                    }
                }
            }
            if (msg instanceof Err) {
                Err ex = (Err) msg;
                String text = "fatal";
                boolean fatal = false;
                if (ex instanceof ErrorSyntax)
                    text = "syntax";
                else if (ex instanceof ErrorType)
                    text = "type";
                else
                    fatal = true;
                if (ex.pos == Pos.UNKNOWN)
                    span.logBold("A " + text + " error has occurred:  ");
                else
                    span.logLink("A " + text + " error has occurred:  ", "POS: " + ex.pos.x + " " + ex.pos.y + " " + ex.pos.x2 + " " + ex.pos.y2 + " " + ex.pos.filename);
                if (verbosity > 2) {
                    span.log("(see the ");
                    span.logLink("stacktrace", "MSG: " + ex.dump());
                    span.log(")\n");
                } else {
                    span.log("\n");
                }
                span.logIndented(ex.msg.trim());
                span.log("\n");
                if (fatal && latestVersion > Version.buildNumber())
                    span.logBold("\nNote: You are running Alloy build#" + Version.buildNumber() + ",\nbut the most recent is Alloy build#" + latestVersion + ":\n( version " + latestName + " )\nPlease try to upgrade to the newest version," + "\nas the problem may have been fixed already.\n");
                span.flush();
                if (!fatal)
                    gui.doVisualize("POS: " + ex.pos.x + " " + ex.pos.y + " " + ex.pos.x2 + " " + ex.pos.y2 + " " + ex.pos.filename);
                return;
            }
            if (msg instanceof Throwable) {
                Throwable ex = (Throwable) msg;
                span.logBold(ex.toString().trim() + "\n");
                span.flush();
                return;
            }
            if (!(msg instanceof Object[]))
                return;
            Object[] array = (Object[]) msg;
            if (array[0].equals("pop")) {
                span.setLength(len2);
                String x = (String) (array[1]);
                if (viz != null && x.length() > 0)
                    OurDialog.alert(x);
            }
            if (array[0].equals("declare")) {
                gui.doSetLatest((String) (array[1]));
            }
            if (array[0].equals("S2")) {
                len3 = len2 = span.getLength();
                span.logBold("" + array[1]);
            }
            if (array[0].equals("R3")) {
                span.setLength(len3);
                span.log("" + array[1]);
            }
            if (array[0].equals("link")) {
                span.logLink((String) (array[1]), (String) (array[2]));
            }
            if (array[0].equals("bold")) {
                span.logBold("" + array[1]);
            }
            if (array[0].equals("")) {
                span.log("" + array[1]);
            }
            if (array[0].equals("scope") && verbosity > 0) {
                span.log("   " + array[1]);
            }
            if (array[0].equals("bound") && verbosity > 1) {
                span.log("   " + array[1]);
            }
            if (array[0].equals("resultCNF")) {
                results.add(null);
                span.setLength(len3);
                span.log("   File written to " + array[1] + "\n\n");
            }
            if (array[0].equals("highlight")) {
                gui.doVisualize("COV");
            }
            if (array[0].equals("coverage")) {
                span.logBold("" + array[1]);
            }
            if (array[0].equals("startResults")) {
                //Initialize for AUnit displays
                gui.doDisplayAUnitExetensionStart();
            }
            if (array[0].equals("summaryResults")) {
                //Send information for the summary results bar for AUnit
                gui.doDisplaySummaryBar((Integer) array[1], (Integer) array[2], (Integer) array[3], (String) array[4], (Integer) array[5]);
            }
            if (array[0].equals("addResult")) {
                //Add an AUnit test result
                DefaultMutableTreeNode cmnHeader = new DefaultMutableTreeNode(new CommandHeaderNode("Command:"));
                cmdNodes.add(cmnHeader);
                boolean no_cmd = true;
                boolean no_facts = false;

                //Command node - display any invoked formulas for AUnit and the facts of the model
                if (((String) array[3]).length() > 0) {
                    cmnHeader.add(new DefaultMutableTreeNode(new CommandFormulaNode((String) array[3])));
                    no_cmd = false;
                }
                String[] facts = ((String) array[6]).split("\n");
                if (facts[0].equals("")) {
                    no_facts = true;
                } else {
                    DefaultMutableTreeNode factNodes = new DefaultMutableTreeNode(new CommandFormulaNode("Facts of the model:"));
                    for (int i = 0; i < facts.length; i++) {
                        factNodes.add(new DefaultMutableTreeNode(new CommandFactFormulaNode(facts[i])));
                    }
                    cmnHeader.add(factNodes);
                }
                //Catch if there is no contraints to add
                if (no_cmd && no_facts) {
                    cmnHeader.add(new DefaultMutableTreeNode(new CommandFormulaNode("true")));
                }

                //Create the necessary result JTree node to display based on the AUnit test result
                if (((String) array[2]).contains("passes")) {
                    DefaultMutableTreeNode newResult = new DefaultMutableTreeNode(new PassFailTreeNode("pass", array[1] + " " + array[2]));
                    newResult.add(new DefaultMutableTreeNode(new AUnitTreeNode("Valuation", "" + array[4], array[1] + "")));
                    newResult.add(cmnHeader);
                    aunitTrees.add(new JTree(newResult));
                } else if (((String) array[2]).contains("fails")) {
                    DefaultMutableTreeNode newResult = new DefaultMutableTreeNode(new PassFailTreeNode("fail", array[1] + " " + array[2]));
                    newResult.add(new DefaultMutableTreeNode(new AUnitTreeNode("Valuation", "" + array[4], array[1] + "")));
                    newResult.add(cmnHeader);
                    aunitTrees.add(new JTree(newResult));
                } else {
                    DefaultMutableTreeNode newResult = new DefaultMutableTreeNode(new PassFailTreeNode("error", array[1] + " " + array[2]));
                    newResult.add(new DefaultMutableTreeNode(new AUnitTreeNode("Valuation", "" + array[4], array[1] + "")));
                    newResult.add(cmnHeader);
                    aunitTrees.add(new JTree(newResult));
                }
            }
            if (array[0].equals("finishTree")) {
                //Wrap up and display all the AUnit results in the AUnit tab
                gui.doDisplayAUnitTree(aunitTrees, cmdNodes);
            }
            if (array[0].equals("finishCoverageTree")) {
                //If displaying coverage, send invocation to update the coverage tab
                gui.doDisplayCoverageTable(covInfos, cov_per_test);
            }
            if (array[0].equals("addCoverageNode")) {
                //Generate information for coverage of each coverage criteria
                String[] split = array[1].toString().split("::-::");
                String paragraph_name = split[0];

                //Store coverage information based on the Alloy paragrapg it belongs to
                CoverageInformation covInfo = new CoverageInformation(paragraph_name);
                coverageOrder.add(paragraph_name);
                coverageTrees.put(paragraph_name, new ArrayList<JTree>());

                //Build up and nest trees for coverae as needed
                DefaultMutableTreeNode current = null;
                DefaultMutableTreeNode tree = null;
                int totalCriteria = 0;
                int numCovered = 0;
                for (int i = 1; i < split.length; i++) {
                    String color = "";
                    String[] get_content = split[i].split(":-:");

                    if (get_content[0].equals("criteria")) {
                        String coverage_of_criteria = get_content[2];
                        if (coverage_of_criteria.equals("covered")) {
                            color = "green";
                        } else if (coverage_of_criteria.equals("not")) {
                            color = "red";
                        } else {
                            color = "yellow";
                        }

                        if (current != null) {
                            coverageTrees.get(paragraph_name).add(new JTree(current));
                        }
                        current = new DefaultMutableTreeNode(new CoverageTreeNode(color, get_content[1], get_content[3], Integer.valueOf(get_content[4]), Integer.valueOf(get_content[5])));
                        tree = new DefaultMutableTreeNode(new CoverageTreeNode(color, get_content[1], get_content[3], Integer.valueOf(get_content[4]), Integer.valueOf(get_content[5])));
                        covInfo.add(get_content[1], new JTree(tree));
                    } else if (get_content[0].equals("node")) {
                        String coverage_of_criteria = get_content[2];
                        if (coverage_of_criteria.equals("true")) {
                            totalCriteria++;
                            numCovered++;
                            color = "green";
                        } else {
                            totalCriteria++;
                            color = "red";
                        }

                        tree.add(new DefaultMutableTreeNode(new CoverageLeafNode(color, get_content[1])));
                        current.add(new DefaultMutableTreeNode(new CoverageLeafNode(color, get_content[1])));
                    }
                }
                covInfo.totalCriteria = totalCriteria;
                covInfo.covered = numCovered;
                covInfos.add(covInfo);
                coverageTrees.get(paragraph_name).add(new JTree(current));
            }
            if (array[0].equals("addMuAlloyTestSummary")) {
                //Send information to the main display for mualloy summary
                gui.doDisplaySummaryMuAlloyTest((Integer) array[1], (Integer) array[2], (long) array[3], (long) array[4], (long) array[5], (String) array[6]);
            }
            if (array[0].equals("addNonKilledMutant")) {
                //Add a mutation result for non-killed mutants
                //Contains information outlining the non-killed mutant and a mutation killing test case
                DefaultMutableTreeNode newResult = new DefaultMutableTreeNode(new MuAlloyHeaderNode((String) array[1]));
                newResult.add(new DefaultMutableTreeNode(new MuAlloyTreeNode((String) array[1], (String) array[2], (String) array[3], (String) array[4], (String) array[5])));
                mualloyTrees.add(new JTree(newResult));
            }
            if (array[0].equals("finishMutation")) {
                //Send information to the main display to generate the mualloy result tab
                gui.doDisplayMuAlloy(mualloyTrees);
            }
            if (array[0].equals("addAlloyFLResult")) {
                //Add details about a node AlloyFL found suspicious
                //Details include the score, the color and position to highlight, and the pretty print text for the node

                //Process inputs for highlighting locations/color
                Pos pos = new Pos((String) array[5], (Integer) array[6], (Integer) array[7], (Integer) array[8], (Integer) array[9]);
                String color = (String) array[10];
                highlight_colors.add(color);
                highlight_locs.add(pos);

                //Generate new JTree to display information about the node AlloyFL found suspicious
                DefaultMutableTreeNode newResult = new DefaultMutableTreeNode(new AlloyFLHeaderNode((String) array[1], (String) array[2]));
                newResult.add(new DefaultMutableTreeNode(new AlloyFLScoreTreeNode((Double) array[4])));
                newResult.add(new DefaultMutableTreeNode(new AlloyFLFormulaTreeNode((String) array[3], (String) array[11])));
                newResult.add(new DefaultMutableTreeNode(new AlloyFLHighlightNode(pos, color)));
                alloyFLTrees.add(new JTree(newResult));
            }
            if (array[0].equals("finishAlloyFL")) {
                //Send information to the main display to generate the alloyfl tab
                gui.doDisplayAlloyFL(alloyFLTrees);
            }
            if (array[0].equals("highlightFL")) {
                gui.doVisualizeHighlights(highlight_locs, highlight_colors, true);
            }
            if (array[0].equals("finishARepair")) {
                gui.doDisplayARepair((String) array[1], arepairTrees, cmdNodesARepair);
            }
            if (array[0].equals("addResultARepair")) {
                //Add an AUnit test result
                DefaultMutableTreeNode cmnHeader = new DefaultMutableTreeNode(new CommandHeaderNode("Command:"));
                cmdNodesARepair.add(cmnHeader);
                boolean no_cmd = true;
                boolean no_facts = false;

                //Command node - display any invoked formulas for AUnit and the facts of the model
                if (((String) array[3]).length() > 0) {
                    cmnHeader.add(new DefaultMutableTreeNode(new CommandFormulaNode((String) array[3])));
                    no_cmd = false;
                }
                String[] facts = ((String) array[6]).split("\n");
                if (facts[0].equals("")) {
                    no_facts = true;
                } else {
                    DefaultMutableTreeNode factNodes = new DefaultMutableTreeNode(new CommandFormulaNode("Facts of the model:"));
                    for (int i = 0; i < facts.length; i++) {
                        factNodes.add(new DefaultMutableTreeNode(new CommandFactFormulaNode(facts[i])));
                    }
                    cmnHeader.add(factNodes);
                }
                //Catch if there is no contraints to add
                if (no_cmd && no_facts) {
                    cmnHeader.add(new DefaultMutableTreeNode(new CommandFormulaNode("true")));
                }

                //Create the necessary result JTree node to display based on the AUnit test result
                if (((String) array[2]).contains("passes")) {
                    DefaultMutableTreeNode newResult = new DefaultMutableTreeNode(new PassFailTreeNode("pass", array[1] + " " + array[2]));
                    newResult.add(new DefaultMutableTreeNode(new AUnitTreeNode("Valuation", "" + array[4], array[1] + "")));
                    newResult.add(cmnHeader);
                    arepairTrees.add(new JTree(newResult));
                } else if (((String) array[2]).contains("fails")) {
                    DefaultMutableTreeNode newResult = new DefaultMutableTreeNode(new PassFailTreeNode("fail", array[1] + " " + array[2]));
                    newResult.add(new DefaultMutableTreeNode(new AUnitTreeNode("Valuation", "" + array[4], array[1] + "")));
                    newResult.add(cmnHeader);
                    arepairTrees.add(new JTree(newResult));
                } else {
                    DefaultMutableTreeNode newResult = new DefaultMutableTreeNode(new PassFailTreeNode("error", array[1] + " " + array[2]));
                    newResult.add(new DefaultMutableTreeNode(new AUnitTreeNode("Valuation", "" + array[4], array[1] + "")));
                    newResult.add(cmnHeader);
                    arepairTrees.add(new JTree(newResult));
                }
            }
            // if (array[0].equals("finishARepairFail")) {
            //   gui.doDisplayARepairFail();
            //}
            if (array[0].equals("debug") && verbosity > 2) {
                span.log("   " + array[1] + "\n");
                len2 = len3 = span.getLength();
            }
            if (array[0].equals("translate")) {
                span.log("   " + array[1]);
                len3 = span.getLength();
                span.logBold("   Generating CNF...\n");
            }
            if (array[0].equals("solve")) {
                span.setLength(len3);
                span.log("   " + array[1]);
                len3 = span.getLength();
                span.logBold("   Solving...\n");
            }
            if (array[0].equals("warnings")) {
                if (warnings.size() == 0)
                    span.setLength(len2);
                else if (warnings.size() > 1)
                    span.logBold("Note: There were " + warnings.size() + " compilation warnings. Please scroll up to see them.\n\n");
                else
                    span.logBold("Note: There was 1 compilation warning. Please scroll up to see them.\n\n");
                if (warnings.size() > 0 && Boolean.FALSE.equals(array[1])) {
                    Pos e = warnings.iterator().next().pos;
                    gui.doVisualize("POS: " + e.x + " " + e.y + " " + e.x2 + " " + e.y2 + " " + e.filename);
                    span.logBold("Warnings often indicate errors in the model.\n" + "Some warnings can affect the soundness of the analysis.\n" + "To proceed despite the warnings, go to the Options menu.\n");
                }
            }
            if (array[0].equals("warning")) {
                ErrorWarning e = (ErrorWarning) (array[1]);
                if (!warnings.add(e))
                    return;
                Pos p = e.pos;
                span.logLink("Warning #" + warnings.size(), "POS: " + p.x + " " + p.y + " " + p.x2 + " " + p.y2 + " " + p.filename);
                span.log("\n");
                span.logIndented(e.msg.trim());
                span.log("\n\n");
            }
            if (array[0].equals("sat")) {
                boolean chk = Boolean.TRUE.equals(array[1]);
                int expects = (Integer) (array[2]);
                String filename = (String) (array[3]), formula = (String) (array[4]);
                results.add(filename);
                (new File(filename)).deleteOnExit();
                gui.doSetLatest(filename);
                span.setLength(len3);
                span.log("   ");
                span.logLink(chk ? "Counterexample" : "Instance", "XML: " + filename);
                span.log(" found. ");
                span.logLink(chk ? "Assertion" : "Predicate", formula);
                span.log(chk ? " is invalid" : " is consistent");
                if (expects == 0)
                    span.log(", contrary to expectation");
                else if (expects == 1)
                    span.log(", as expected");
                span.log(". " + array[5] + "ms.\n\n");
                gui.doDisplayRun();
            }
            if (array[0].equals("metamodel")) {
                String outf = (String) (array[1]);
                span.setLength(len2);
                (new File(outf)).deleteOnExit();
                gui.doSetLatest(outf);
                span.logLink("Metamodel", "XML: " + outf);
                span.log(" successfully generated.\n\n");
            }
            if (array[0].equals("minimizing")) {
                boolean chk = Boolean.TRUE.equals(array[1]);
                int expects = (Integer) (array[2]);
                span.setLength(len3);
                span.log(chk ? "   No counterexample found." : "   No instance found.");
                if (chk)
                    span.log(" Assertion may be valid");
                else
                    span.log(" Predicate may be inconsistent");
                if (expects == 1)
                    span.log(", contrary to expectation");
                else if (expects == 0)
                    span.log(", as expected");
                span.log(". " + array[4] + "ms.\n");
                span.logBold("   Minimizing the unsat core of " + array[3] + " entries...\n");
            }
            if (array[0].equals("unsat")) {
                boolean chk = Boolean.TRUE.equals(array[1]);
                int expects = (Integer) (array[2]);
                String formula = (String) (array[4]);
                span.setLength(len3);
                span.log(chk ? "   No counterexample found. " : "   No instance found. ");
                span.logLink(chk ? "Assertion" : "Predicate", formula);
                span.log(chk ? " may be valid" : " may be inconsistent");
                gui.doDisplayRun();
                if (expects == 1)
                    span.log(", contrary to expectation");
                else if (expects == 0)
                    span.log(", as expected");
                if (array.length == 5) {
                    span.log(". " + array[3] + "ms.\n\n");
                    span.flush();
                    return;
                }
                String core = (String) (array[5]);
                int mbefore = (Integer) (array[6]), mafter = (Integer) (array[7]);
                span.log(". " + array[3] + "ms.\n");
                if (core.length() == 0) {
                    results.add("");
                    span.log("   No unsat core is available in this case. " + array[8] + "ms.\n\n");
                    span.flush();
                    return;
                }
                results.add(core);
                (new File(core)).deleteOnExit();
                span.log("   ");
                span.logLink("Core", core);
                if (mbefore <= mafter)
                    span.log(" contains " + mafter + " top-level formulas. " + array[8] + "ms.\n\n");
                else
                    span.log(" reduced from " + mbefore + " to " + mafter + " top-level formulas. " + array[8] + "ms.\n\n");
            }
            span.flush();
        }
    }

    private void cb(Serializable... objs) {
        cb.callback(objs);
    }

    /** {@inheritDoc} */
    @Override
    public void resultCNF(final String filename) {
        cb("resultCNF", filename);
    }

    /** {@inheritDoc} */
    @Override
    public void warning(final ErrorWarning ex) {
        warn++;
        cb("warning", ex);
    }

    /** {@inheritDoc} */
    @Override
    public void scope(final String msg) {
        cb("scope", msg);
    }

    /** {@inheritDoc} */
    @Override
    public void bound(final String msg) {
        cb("bound", msg);
    }

    /** {@inheritDoc} */
    @Override
    public void debug(final String msg) {
        cb("debug", msg.trim());
    }

    /** {@inheritDoc} */
    @Override
    public void translate(String solver, int bitwidth, int maxseq, int skolemDepth, int symmetry) {
        if (aunit_extension_executions) {
            //Currently no details to track
        } else {
            cb("translate", "Solver=" + solver + " Bitwidth=" + bitwidth + " MaxSeq=" + maxseq + (skolemDepth == 0 ? "" : " SkolemDepth=" + skolemDepth) + " Symmetry=" + (symmetry > 0 ? ("" + symmetry) : "OFF") + '\n');
        }
    }

    /** {@inheritDoc} */
    @Override
    public void solve(final int primaryVars, final int totalVars, final int clauses) {
        if (aunit_extension_executions) {
            //No details saved currently
        } else {
            cb("solve", "" + totalVars + " vars. " + primaryVars + " primary vars. " + clauses + " clauses. " + (System.currentTimeMillis() - lastTime) + "ms.\n");
        }
        lastTime = System.currentTimeMillis();
    }

    /** {@inheritDoc} */
    @Override
    public void resultSAT(Object command, long solvingTime, Object solution) {
        if (!(solution instanceof A4Solution) || !(command instanceof Command))
            return;
        A4Solution sol = (A4Solution) solution;
        Command cmd = (Command) command;
        String formula = recordKodkod ? sol.debugExtractKInput() : "";
        String filename = tempfile + ".xml";
        synchronized (SimpleReporter.class) {
            try {
                if (!aunit_extension_executions)
                    cb("R3", "   Writing the XML file...");
                if (latestModule != null)
                    writeXML(this, latestModule, filename, sol, latestKodkodSRC);
            } catch (Throwable ex) {
                cb("bold", "\n" + (ex.toString().trim()) + "\nStackTrace:\n" + (MailBug.dump(ex).trim()) + "\n");
                return;
            }
            latestKodkods.clear();
            latestKodkods.add(sol.toString());
            latestKodkod = sol;
            latestKodkodXML = filename;
        }
        String formulafilename = "";
        if (formula.length() > 0 && tempfile != null) {
            formulafilename = tempfile + ".java";
            try {
                Util.writeAll(formulafilename, formula);
                formulafilename = "CNF: " + formulafilename;
            } catch (Throwable ex) {
                formulafilename = "";
            }
        }
        if (aunit_extension_executions) {
            String details = System.currentTimeMillis() - lastTime + "ms";
            aunitTestDetails.add(details);
        } else {
            cb("sat", cmd.check, cmd.expects, filename, formulafilename, System.currentTimeMillis() - lastTime);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void minimizing(Object command, int before) {
        if (!(command instanceof Command))
            return;
        Command cmd = (Command) command;
        minimized = System.currentTimeMillis();
        cb("minimizing", cmd.check, cmd.expects, before, minimized - lastTime);
    }

    /** {@inheritDoc} */
    @Override
    public void minimized(Object command, int before, int after) {
        minimizedBefore = before;
        minimizedAfter = after;
    }

    /** {@inheritDoc} */
    @Override
    public void resultUNSAT(Object command, long solvingTime, Object solution) {
        if (!(solution instanceof A4Solution) || !(command instanceof Command))
            return;
        A4Solution sol = (A4Solution) solution;
        Command cmd = (Command) command;
        String originalFormula = recordKodkod ? sol.debugExtractKInput() : "";
        String corefilename = "", formulafilename = "";
        if (originalFormula.length() > 0 && tempfile != null) {
            formulafilename = tempfile + ".java";
            try {
                Util.writeAll(formulafilename, originalFormula);
                formulafilename = "CNF: " + formulafilename;
            } catch (Throwable ex) {
                formulafilename = "";
            }
        }
        Pair<Set<Pos>,Set<Pos>> core = sol.highLevelCore();
        if ((core.a.size() > 0 || core.b.size() > 0) && tempfile != null) {
            corefilename = tempfile + ".core";
            OutputStream fs = null;
            ObjectOutputStream os = null;
            try {
                fs = new FileOutputStream(corefilename);
                os = new ObjectOutputStream(fs);
                os.writeObject(core);
                os.writeObject(sol.lowLevelCore());
                corefilename = "CORE: " + corefilename;
            } catch (Throwable ex) {
                corefilename = "";
            } finally {
                Util.close(os);
                Util.close(fs);
            }
        }
        if (aunit_extension_executions) {
            String details = System.currentTimeMillis() - lastTime + "ms";
            aunitTestDetails.add(details);
        } else {
            if (minimized == 0)
                cb("unsat", cmd.check, cmd.expects, (System.currentTimeMillis() - lastTime), formulafilename);
            else
                cb("unsat", cmd.check, cmd.expects, minimized - lastTime, formulafilename, corefilename, minimizedBefore, minimizedAfter, (System.currentTimeMillis() - minimized));
        }
    }

    private final WorkerCallback cb;

    // ========== These fields should be set each time we execute a set of
    // commands

    /** Whether we should record Kodkod input/output. */
    private final boolean recordKodkod;

    /**
     * The time that the last action began; we subtract it from
     * System.currentTimeMillis() to determine the elapsed time.
     */
    private long          lastTime  = 0;

    /**
     * If we performed unsat core minimization, then this is the start of the
     * minimization, else this is 0.
     */
    private long          minimized = 0;

    /** The unsat core size before minimization. */
    private int           minimizedBefore;

    /** The unsat core size after minimization. */
    private int           minimizedAfter;

    /**
     * The filename where we can write a temporary Java file or Core file.
     */
    private String        tempfile  = null;

    // ========== These fields may be altered as each successful command
    // generates a Kodkod or Metamodel instance

    /**
     * The set of Strings already enumerated for this current solution.
     */
    private static final Set<String>        latestKodkods              = new LinkedHashSet<String>();

    /**
     * The A4Solution corresponding to the latest solution generated by Kodkod; this
     * field must be synchronized.
     */
    private static A4Solution               latestKodkod               = null;

    /**
     * The root Module corresponding to this.latestKodkod; this field must be
     * synchronized.
     */
    private static Module                   latestModule               = null;

    /**
     * The source code corresponding to the latest solution generated by Kodkod;
     * this field must be synchronized.
     */
    private static ConstMap<String,String>  latestKodkodSRC            = null;

    /**
     * The XML filename corresponding to the latest solution generated by Kodkod;
     * this field must be synchronized.
     */
    private static String                   latestKodkodXML            = null;

    /**
     * The XML filename corresponding to the latest metamodel generated by
     * TranslateAlloyToMetamodel; this field must be synchronized.
     */
    private static String                   latestMetamodelXML         = null;

    //Track details for aunit tests
    static ArrayList<String>                aunitTestDetails           = new ArrayList<String>();
    //boolean flag to indicate aunit or one of its extensions is executing which changes logging information
    static boolean                          aunit_extension_executions = false;
    public static ArrayList<FuncCallHolder> func_calls_per_test;
    static HashMap<String,JTree>            cov_per_test               = new HashMap<String,JTree>();

    /** Constructor is private. */
    private SimpleReporter(WorkerCallback cb, boolean recordKodkod) {
        this.cb = cb;
        this.recordKodkod = recordKodkod;
    }

    /** Helper method to write out a full XML file. */
    private static void writeXML(A4Reporter rep, Module mod, String filename, A4Solution sol, Map<String,String> sources) throws Exception {
        sol.writeXML(rep, filename, mod.getAllFunc(), sources);
        if (AlloyCore.isDebug())
            validate(filename);
    }

    private int warn = 0;

    /** Task that performs solution enumeration. */
    public static final class SimpleTask2 implements WorkerTask {

        private static final long       serialVersionUID = 0;
        public String                   filename         = "";
        public transient WorkerCallback out              = null;

        private void cb(Object... objs) throws Exception {
            out.callback(objs);
        }

        @Override
        public void run(WorkerCallback out) throws Exception {
            this.out = out;
            cb("S2", "Enumerating...\n");
            A4Solution sol;
            Module mod;
            synchronized (SimpleReporter.class) {
                if (latestMetamodelXML != null && latestMetamodelXML.equals(filename)) {
                    cb("pop", "You cannot enumerate a metamodel.\n");
                    return;
                }
                if (latestKodkodXML == null || !latestKodkodXML.equals(filename)) {
                    cb("pop", "You can only enumerate the solutions of the most-recently-solved command.");
                    return;
                }
                if (latestKodkod == null || latestModule == null || latestKodkodSRC == null) {
                    cb("pop", "Error: the SAT solver that generated the instance has exited,\nso we cannot enumerate unless you re-solve that command.\n");
                    return;
                }
                sol = latestKodkod;
                mod = latestModule;
            }
            if (!sol.satisfiable()) {
                cb("pop", "Error: This command is unsatisfiable,\nso there are no solutions to enumerate.");
                return;
            }
            if (!sol.isIncremental()) {
                cb("pop", "Error: This solution was not generated by an incremental SAT solver.\n" + "Currently only MiniSat and SAT4J are supported.");
                return;
            }
            int tries = 0;
            while (true) {
                sol = sol.next();
                if (!sol.satisfiable()) {
                    cb("pop", "There are no more satisfying instances.\n\n" + "Note: due to symmetry breaking and other optimizations,\n" + "some equivalent solutions may have been omitted.");
                    return;
                }
                String toString = sol.toString();
                synchronized (SimpleReporter.class) {
                    if (!latestKodkods.add(toString))
                        if (tries < 100) {
                            tries++;
                            continue;
                        }
                    // The counter is needed to avoid a Kodkod bug where
                    // sometimes we might repeat the same solution infinitely
                    // number of times; this at least allows the user to keep
                    // going
                    writeXML(null, mod, filename, sol, latestKodkodSRC);
                    latestKodkod = sol;
                }
                cb("declare", filename);
                return;
            }
        }
    }

    /**
     * Validate the given filename to see if it is a valid Alloy XML instance file.
     */
    private static void validate(String filename) throws Exception {
        A4SolutionReader.read(new ArrayList<Sig>(), new XMLNode(new File(filename))).toString();
        StaticInstanceReader.parseInstance(new File(filename));
    }

    /** Task that perform one command. */
    public static final class SimpleTask1 implements WorkerTask {

        private static final long serialVersionUID = 0;
        public A4Options          options;
        public String             tempdir;
        public boolean            bundleWarningNonFatal;
        public int                bundleIndex;
        public int                resolutionMode;
        public Map<String,String> map;

        public SimpleTask1() {
        }

        public void cb(WorkerCallback out, Object... objs) throws IOException {
            out.callback(objs);
        }

        @Override
        public void run(WorkerCallback out) throws Exception {
            cb(out, "S2", "Starting the solver...\n\n");
            final SimpleReporter rep = new SimpleReporter(out, options.recordKodkod);
            final Module world = CompUtil.parseEverything_fromFile(rep, map, options.originalFilename, resolutionMode);
            final List<Sig> sigs = world.getAllReachableSigs();
            final ConstList<Command> cmds = world.getAllCommands();
            cb(out, "warnings", bundleWarningNonFatal);
            if (rep.warn > 0 && !bundleWarningNonFatal)
                return;
            List<String> result = new ArrayList<String>(cmds.size());
            if (bundleIndex == -2) {
                final String outf = tempdir + File.separatorChar + "m.xml";
                cb(out, "S2", "Generating the metamodel...\n");
                PrintWriter of = new PrintWriter(outf, "UTF-8");
                Util.encodeXMLs(of, "\n<alloy builddate=\"", Version.buildDate(), "\">\n\n");
                A4SolutionWriter.writeMetamodel(ConstList.make(sigs), options.originalFilename, of);
                Util.encodeXMLs(of, "\n</alloy>");
                Util.close(of);
                if (AlloyCore.isDebug())
                    validate(outf);
                cb(out, "metamodel", outf);
                synchronized (SimpleReporter.class) {
                    latestMetamodelXML = outf;
                }
            } else
                for (int i = 0; i < cmds.size(); i++)
                    if (bundleIndex < 0 || i == bundleIndex) {
                        synchronized (SimpleReporter.class) {
                            latestModule = world;
                            latestKodkodSRC = ConstMap.make(map);
                        }
                        final String tempXML = tempdir + File.separatorChar + i + ".cnf.xml";
                        final String tempCNF = tempdir + File.separatorChar + i + ".cnf";
                        final Command cmd = cmds.get(i);
                        rep.tempfile = tempCNF;
                        cb(out, "bold", "Executing \"" + cmd + "\"\n");
                        A4Solution ai = TranslateAlloyToKodkod.execute_commandFromBook(rep, world.getAllReachableSigs(), cmd, options);
                        if (ai == null)
                            result.add(null);
                        else if (ai.satisfiable())
                            result.add(tempXML);
                        else if (ai.highLevelCore().a.size() > 0)
                            result.add(tempCNF + ".core");
                        else
                            result.add("");
                    }
            (new File(tempdir)).delete(); // In case it was UNSAT, or
                                         // canceled...
            if (result.size() > 1) {
                rep.cb("bold", "" + result.size() + " commands were executed. The results are:\n");
                for (int i = 0; i < result.size(); i++) {
                    Command r = world.getAllCommands().get(i);
                    if (result.get(i) == null) {
                        rep.cb("", "   #" + (i + 1) + ": Unknown.\n");
                        continue;
                    }
                    if (result.get(i).endsWith(".xml")) {
                        rep.cb("", "   #" + (i + 1) + ": ");
                        rep.cb("link", r.check ? "Counterexample found. " : "Instance found. ", "XML: " + result.get(i));
                        rep.cb("", r.label + (r.check ? " is invalid" : " is consistent"));
                        if (r.expects == 0)
                            rep.cb("", ", contrary to expectation");
                        else if (r.expects == 1)
                            rep.cb("", ", as expected");
                    } else if (result.get(i).endsWith(".core")) {
                        rep.cb("", "   #" + (i + 1) + ": ");
                        rep.cb("link", r.check ? "No counterexample found. " : "No instance found. ", "CORE: " + result.get(i));
                        rep.cb("", r.label + (r.check ? " may be valid" : " may be inconsistent"));
                        if (r.expects == 1)
                            rep.cb("", ", contrary to expectation");
                        else if (r.expects == 0)
                            rep.cb("", ", as expected");
                    } else {
                        if (r.check)
                            rep.cb("", "   #" + (i + 1) + ": No counterexample found. " + r.label + " may be valid");
                        else
                            rep.cb("", "   #" + (i + 1) + ": No instance found. " + r.label + " may be inconsistent");
                        if (r.expects == 1)
                            rep.cb("", ", contrary to expectation");
                        else if (r.expects == 0)
                            rep.cb("", ", as expected");
                    }
                    rep.cb("", ".\n");
                }
                rep.cb("", "\n");
            }
            if (rep.warn > 1)
                rep.cb("bold", "Note: There were " + rep.warn + " compilation warnings. Please scroll up to see them.\n");
            if (rep.warn == 1)
                rep.cb("bold", "Note: There was 1 compilation warning. Please scroll up to see it.\n");
        }
    }

    /** Task that perform aunit test execution and coverage calculations. */
    static final class AunitExecutionTask implements WorkerTask {

        private static final long serialVersionUID = 0;
        public A4Options          options;
        public String             tempdir;
        public boolean            bundleWarningNonFatal;
        public int                bundleIndex;
        public int                resolutionMode;
        public Map<String,String> map;
        public boolean            coverage;
        public boolean            highlight;

        enum Outcome {
                      PASSING,
                      FAILING,
                      ERROR
        }

        ArrayList<Expr> exprs_in_command = new ArrayList<Expr>();

        public AunitExecutionTask() {
        }

        public void cb(WorkerCallback out, Object... objs) throws IOException {
            out.callback(objs);
        }

        @SuppressWarnings("resource" )

        public void run(WorkerCallback out) throws Exception {
            cb(out, "S2", "Starting the solver...\n\n");
            final SimpleReporter rep = new SimpleReporter(out, options.recordKodkod);
            CompModule world = CompUtil.parseEverything_fromFile(rep, map, options.originalFilename, resolutionMode);
            cb(out, "warnings", bundleWarningNonFatal);
            if (rep.warn > 0 && !bundleWarningNonFatal)
                return;

            rep.cb("startResults");
            aunitTestDetails.clear();
            aunit_extension_executions = true;
            func_calls_per_test = new ArrayList<FuncCallHolder>();
            int totalTime = 0;

            /**
             * Create the different model versions to enable easy (1) testing, (2) coverage
             * calculations
             **/
            String temporig = "";
            for (String s : map.keySet()) {
                if (s.equals(options.originalFilename)) {
                    temporig = map.get(s);
                    break;
                }
            }
            String[] model_vers = AUnitExecution.buildModelVersions(world, temporig, options);

            /**
             * Create the easy-printing Alloy model and parse the file into a Module object
             * (world)
             **/
            PrintWriter writer = new PrintWriter("executableOriginal.als", "UTF-8");
            writer.println(model_vers[1]);
            writer.close();
            world = CompUtil.parseEverything_fromFile(rep, map, "executableOriginal.als", resolutionMode);

            /**
             * Create the module without any facts of the model for coverage calculations
             * and displaying of any valuation
             **/
            writer = new PrintWriter("removedFacts.als", "UTF-8");
            writer.println(model_vers[0]);
            writer.close();
            CompModule worldRemovedFacts = CompUtil.parseEverything_fromFile(rep, map, "removedFacts.als", resolutionMode);

            /* Helper storage for executing tasks, display results */
            /** Store the result from executing any tests **/
            ArrayList<Outcome> results = new ArrayList<Outcome>();
            /** Map the unique test label to the associated test case **/
            HashMap<String,TestCase> aunit_tests = new HashMap<String,TestCase>();
            /**
             * A mapping of the name of the parameters for each function in the alloy model
             * under test
             **/
            HashMap<String,ArrayList<String>> func_to_parameters = new HashMap<String,ArrayList<String>>();
            /** Array of all tests in the Model Under Test **/
            Test[] parsed_tests = new Test[world.getAllTests().size()];
            parsed_tests = world.getAllTests().toArray(parsed_tests);
            HashMap<String,Func> name_to_func = new HashMap<String,Func>();
            HashSet<String> preds = new HashSet<String>();
            HashSet<String> vals = new HashSet<String>();

            /** Populate func_to_parameters mapping **/
            for (Func func : world.getAllFunc()) {
                String func_name = func.label.toString(); //isolate the name of the function invoked
                func_name = func_name.substring(func_name.indexOf("/") + 1);
                if (!func_name.contains("run$") && !func_name.contains("check$")) { //Check that the function is not a command 
                    func_to_parameters.put(func_name, new ArrayList<String>());
                    AUnitExecution.addDomains(func.getBody(), new HashMap<String,String>());
                    if (func.decls.size() > 0) { //Add the name of the parameters to the mapping i.e. Acyclic[l: List] maps as Acyclic->l
                        for (Decl parameter : func.decls) {
                            for (ExprHasName var : parameter.names) {
                                func_to_parameters.get(func_name).add(var.toString());
                            }
                        }
                    }
                    if (!func.isVal) {
                        preds.add(func_name);
                    } else {
                        vals.add(func_name);
                    }
                }
                name_to_func.put(func_name, func);
            }

            AUnitExecution.addDomains(world.getAllReachableFacts(), new HashMap<String,String>());
            /** Map the name of the function to the other functions it calls **/
            HashMap<String,ArrayList<FunctionInvocation>> func_to_calls = AUnitExecution.buildCalledMap(world, func_to_parameters, name_to_func);

            /** Start to piece together (1) valuation and (2) commands for AUnit tests **/
            cb(out, "bold", "Parsing " + parsed_tests.length + " AUnit tests\n");
            HashSet<Expr> sig_fact_exprs = new HashSet<Expr>();
            /**
             * Get the invoked commands starting with the valuations which call commands
             * with parameters within them.
             **/
            for (int i = 0; i < parsed_tests.length; i++) {
                TestCase testcase = new TestCase();
                /**
                 * Set up the predicate(s) invoked by the command - this is needed for coverage
                 * to know which predicates to look over for coverage.
                 **/
                AUnitExecution.resetExplicitCalls();

                if (parsed_tests[i].getUnresolvedTestCmd().formula instanceof ExprVar && (!((ExprVar) parsed_tests[i].getUnresolvedTestCmd().formula).label.contains("run$")) && (!((ExprVar) parsed_tests[i].getUnresolvedTestCmd().formula).label.contains("check$"))) { //directly invoked a function that has no parameters
                    String func_name = ((ExprVar) parsed_tests[i].getUnresolvedTestCmd().formula).label;

                    Func pontenial_val = name_to_func.get(func_name);
                    if (!pontenial_val.isVal) {
                        aunit_extension_executions = false;
                        cb(out, "error", "AUnit Execution Error: Test \"" + parsed_tests[i].id + "\" does not reference a valuation.\n\n", parsed_tests[i].getTestCmd().pos);
                        /** Clean up files the get created **/
                        File delete_creations = new File("executableOriginal.als");
                        delete_creations.delete();
                        delete_creations = new File("removedFacts.als");
                        delete_creations.delete();
                        return;
                    }
                    for (FunctionInvocation func_invocation : func_to_calls.get(func_name)) {
                        testcase.addExplicitCall(func_invocation);
                    }
                    parsed_tests[i].setValuation(pontenial_val);
                } else {
                    AUnitExecution.findExplicitCalls(parsed_tests[i].getTestCmd().formula, func_to_parameters, new HashMap<String,String>());
                    for (FunctionInvocation func_call : AUnitExecution.getExplicitCalls()) {
                        if (!func_call.isVal) {
                            testcase.addExplicitCall(func_call);
                        }
                        for (FunctionInvocation func_invocation : func_to_calls.get(func_call.getCommand())) {
                            testcase.addExplicitCall(func_invocation);
                        }
                    }

                    /**
                     * Report an error if (1) no valuation in the test or (2) more than one
                     * valuation in the test.
                     **/

                    if (!parsed_tests[i].establishValuation(world)) {
                        if (parsed_tests[i].getValuationFunc() != null)
                            cb(out, "error", "AUnit Execution Error: Test \"" + parsed_tests[i].id + "\" references more than one valuation.\n\n", parsed_tests[i].getTestCmd().pos);
                        else
                            cb(out, "error", "AUnit Execution Error: Test \"" + parsed_tests[i].id + "\" does not reference a valuation.\n\n", parsed_tests[i].getTestCmd().pos);
                        aunit_extension_executions = false;

                        /** Clean up files the get created **/
                        File delete_creations = new File("executableOriginal.als");
                        delete_creations.delete();
                        delete_creations = new File("removedFacts.als");
                        delete_creations.delete();
                        return;
                    }
                }
                parsed_tests[i].establishCommand(world, model_vers[2].split("\n"), preds);

                AUnitExecution.resetExplicitCalls();
                AUnitExecution.findExplicitCalls(world.getAllReachableFacts(), func_to_parameters, new HashMap<String,String>());
                for (FunctionInvocation func_call : AUnitExecution.getExplicitCalls()) {
                    if (!func_call.isVal) {
                        testcase.addExplicitCall(func_call);
                    }
                    for (FunctionInvocation func_invocation : func_to_calls.get(func_call.getCommand())) {
                        testcase.addExplicitCall(func_invocation);
                    }
                }

                for (Sig sig : world.getAllReachableSigs()) {
                    for (Expr sig_fact : sig.getFacts()) {
                        AUnitExecution.resetExplicitCalls();
                        AUnitExecution.findExplicitCalls(sig_fact, func_to_parameters, new HashMap<String,String>());
                        for (FunctionInvocation func_call : AUnitExecution.getExplicitCalls()) {
                            if (!func_call.isVal) {
                                sig_fact_exprs.add(func_call.exprCall);
                            }
                            for (FunctionInvocation func_invocation : func_to_calls.get(func_call.getCommand())) {
                                sig_fact_exprs.add(func_invocation.exprCall);
                            }
                        }
                    }
                }
                testcase.removeRecursiveCalls(sig_fact_exprs);
                aunit_tests.put(parsed_tests[i].id, testcase);
            }

            /** Execute all AUnit Tests **/
            if (coverage) {
                cb(out, "bold", "Executing AUnit tests and calculating coverage\n");
            } else {
                cb(out, "bold", "Executing AUnit tests\n");
            }

            /** If calculating coverage, find all criteria in the model under test. **/

            CoverageCriteriaBuilder ccb = null;
            ccb = new CoverageCriteriaBuilder(world);
            ccb.findCriteria(model_vers[2]);
            if (coverage) {
                //  cb(out, "", ccb.printCoverageString());
            }
            String facts = "";
            for (SigParagraph sigP : ccb.getSigParagraphs()) {
                for (Construct fact : sigP.getImplicitFacts()) {
                    if (fact instanceof SigAbstract) {
                        SigAbstract formula = (SigAbstract) fact;
                        facts += formula.prettyPrintName + "\n";
                    } else if (fact instanceof RelMultiplicity) {
                        RelMultiplicity formula = (RelMultiplicity) fact;
                        facts += formula.prettyPrintName + "\n";
                    } else if (fact instanceof Formula) {
                        Formula formula = (Formula) fact;
                        facts += formula.prettyPrintName + "\n";
                    }
                }
            }

            for (String origin : ccb.getParagraphs().keySet()) {
                if (origin.contains("fact ")) {
                    facts += origin + "\n";
                } else if (origin.contains("facts")) {
                    facts += "Unamed fact paragraph(s)\n";
                }
            }


            /**
             * Loop over all previously located tests. For each test: a. Build up storage
             * areas for the results b. Build a command that adds in the facts of the model
             * along with the valuation and test case command c. Execute this command and
             * store the results d. Calculate coverage along the way, if necessary
             **/
            int numPassing = 0;
            int numFailing = 0;
            int numErrors = 0;

            for (int i = 0; i < parsed_tests.length; i++) {
                synchronized (SimpleReporter.class) {
                    latestModule = world;
                    latestKodkodSRC = ConstMap.make(map);
                }
                final String tempXML = tempdir + File.separatorChar + i + ".cnf.xml";
                final String tempCNF = tempdir + File.separatorChar + i + ".cnf";
                final Command cmd = parsed_tests[i].getTestCmd();

                rep.tempfile = tempCNF;
                cb(out, "", "     Executing \"" + cmd + "\"\n");

                /* Execute test */
                Command test_with_facts = new Command(cmd.check, cmd.overall, cmd.bitwidth, cmd.maxseq, cmd.formula.and(world.getAllReachableFacts()));
                TranslateAlloyToKodkod k = new TranslateAlloyToKodkod(rep, options, world.getAllReachableSigs(), test_with_facts);
                k.makeFacts(test_with_facts.formula);
                A4Solution ai = null;
                try {
                    ai = k.frame.solve(rep, cmd, new Simplifier(), true);
                } catch (UnsatisfiedLinkError ex) {
                    throw new ErrorFatal("The required JNI library cannot be found: " + ex.toString().trim(), ex);
                } catch (CapacityExceededException ex) {
                    throw ex;
                } catch (HigherOrderDeclException ex) {
                    Pos p = k.frame.kv2typepos(ex.decl().variable()).b;
                    throw new ErrorType(p, "Analysis cannot be performed since it requires higher-order quantification that could not be skolemized.");
                } catch (Throwable ex) {
                    if (ex instanceof Err)
                        throw (Err) ex;
                    else
                        throw new ErrorFatal("Unknown exception occurred: " + ex, ex);
                }
                /*
                 * Store result, and calculate number of passing, failing or erroring producing
                 * tests
                 */
                if (ai == null) {
                    results.add(Outcome.ERROR);
                    numErrors++;
                    aunitTestDetails.add("");
                } else if (ai.satisfiable()) {
                    if (cmd.expects == 0) {
                        results.add(Outcome.FAILING);
                        numFailing++;
                    } else if (cmd.expects == 1) {
                        results.add(Outcome.PASSING);
                        numPassing++;
                    } else {
                        if (cmd.check) {
                            results.add(Outcome.FAILING);
                            numFailing++;
                        } else {
                            results.add(Outcome.PASSING);
                            numPassing++;
                        }
                    }
                } else {
                    if (cmd.expects == 1) {
                        results.add(Outcome.FAILING);
                        numFailing++;
                    } else if (cmd.expects == 0) {
                        results.add(Outcome.PASSING);
                        numPassing++;
                    } else {
                        if (!cmd.check) {
                            results.add(Outcome.FAILING);
                            numFailing++;
                        } else {
                            results.add(Outcome.PASSING);
                            numPassing++;
                        }
                    }
                }

                /* Set valuation for storage, and calculate coverage if needed */
                Expr valuation = CompUtil.parseOneExpression_fromString(world, parsed_tests[i].prettyPrintValuation());
                Command getValuation = new Command(cmd.check, cmd.overall, cmd.bitwidth, cmd.maxseq, valuation.and(world.getAllReachableFacts()));
                ai = TranslateAlloyToKodkod.execute_commandFromBook(rep, world.getAllReachableSigs(), getValuation, options);
                FuncCallHolder func_calls = new FuncCallHolder(i);

                boolean add = true;
                for (int c = 0; c < k.calls.size(); c++) {
                    String f_call_params = "";
                    String comma = "";
                    if (!sig_fact_exprs.contains(k.calls.get(c).exprCall)) {
                        for (int j = 0; j < k.calls.get(c).parameters.size(); j++) {
                            if (k.calls.get(c).parameters.get(j) instanceof kodkod.ast.Relation) {
                                f_call_params += comma + k.calls.get(c).parameters.get(j).toString();
                                aunit_tests.get(parsed_tests[i].id).eCommands.get(k.calls.get(c).exprCall).updateParameter(aunit_tests.get(parsed_tests[i].id).eCommands.get(k.calls.get(c).exprCall).getParameterOrder().get(j), k.calls.get(c).parameters.get(j).toString());
                            } else if (k.calls.get(c).parameters.get(j) instanceof kodkod.ast.Variable) {
                                String param = k.calls.get(c).parameters.get(j).toString();
                                if (param.contains("_")) {
                                    String is_val = param.substring(0, param.indexOf("_"));
                                    if (vals.contains(is_val)) {
                                        String var = param.substring(param.indexOf("_") + 1);
                                        f_call_params += comma + var;
                                        aunit_tests.get(parsed_tests[i].id).eCommands.get(k.calls.get(c).exprCall).updateParameter(aunit_tests.get(parsed_tests[i].id).eCommands.get(k.calls.get(c).exprCall).getParameterOrder().get(j), "$" + var);
                                    } else if (preds.contains(is_val)) {
                                        f_call_params += comma + param;
                                        String domain = aunit_tests.get(parsed_tests[i].id).eCommands.get(k.calls.get(c).exprCall).outerdomains.get(param.substring(param.indexOf("_") + 1));
                                        aunit_tests.get(parsed_tests[i].id).eCommands.get(k.calls.get(c).exprCall).updateParameter(aunit_tests.get(parsed_tests[i].id).eCommands.get(k.calls.get(c).exprCall).getParameterOrder().get(j), " : " + domain + " | ");
                                    } else {
                                        f_call_params += comma + param;
                                        aunit_tests.get(parsed_tests[i].id).eCommands.get(k.calls.get(c).exprCall).updateParameter(aunit_tests.get(parsed_tests[i].id).eCommands.get(k.calls.get(c).exprCall).getParameterOrder().get(j), "$" + param);
                                    }
                                } else if (aunit_tests.get(parsed_tests[i].id).eCommands.get(k.calls.get(c).exprCall).outerdomains.containsKey(param)) {
                                    f_call_params += comma + param;
                                    String domain = aunit_tests.get(parsed_tests[i].id).eCommands.get(k.calls.get(c).exprCall).outerdomains.get(param);
                                    aunit_tests.get(parsed_tests[i].id).eCommands.get(k.calls.get(c).exprCall).updateParameter(aunit_tests.get(parsed_tests[i].id).eCommands.get(k.calls.get(c).exprCall).getParameterOrder().get(j), " : " + domain + " | ");

                                } else {
                                    f_call_params += comma + param;
                                    aunit_tests.get(parsed_tests[i].id).eCommands.get(k.calls.get(c).exprCall).updateParameter(aunit_tests.get(parsed_tests[i].id).eCommands.get(k.calls.get(c).exprCall).getParameterOrder().get(j), "$" + param);
                                }
                            } else if (k.calls.get(c).parameters.get(j) instanceof kodkod.ast.BinaryExpression) {
                                String param = buildExp((BinaryExpression) k.calls.get(c).parameters.get(j));
                                if (param.contains(" remainder")) { //extended sig directly called
                                    param = param.substring(param.indexOf("+") + 1, param.indexOf(" remainder"));
                                    param = param.substring(param.indexOf("/") + 1);
                                    aunit_tests.get(parsed_tests[i].id).eCommands.get(k.calls.get(c).exprCall).updateParameter(aunit_tests.get(parsed_tests[i].id).eCommands.get(k.calls.get(c).exprCall).getParameterOrder().get(j), param);

                                } else {
                                    f_call_params += comma + param;
                                    aunit_tests.get(parsed_tests[i].id).eCommands.get(k.calls.get(c).exprCall).updateParameter(aunit_tests.get(parsed_tests[i].id).eCommands.get(k.calls.get(c).exprCall).getParameterOrder().get(j), param);
                                }
                            }
                            comma = ", ";
                        }
                        if (add)
                            func_calls.addExplicitCall(aunit_tests.get(parsed_tests[i].id).eCommands.get(k.calls.get(c).exprCall).getCommand() + "[" + f_call_params.replaceAll("\\$", "") + "]");
                        add = true;
                    }
                }

                func_calls_per_test.add(func_calls);
                if (!ai.satisfiable()) {
                    valuation = CompUtil.parseOneExpression_fromString(worldRemovedFacts, parsed_tests[i].prettyPrintValuation());
                    getValuation = new Command(cmd.check, cmd.overall, cmd.bitwidth, cmd.maxseq, valuation);
                    ai = TranslateAlloyToKodkod.execute_commandFromBook(rep, worldRemovedFacts.getAllReachableSigs(), getValuation, options);
                    if (coverage) {
                        ccb.world = worldRemovedFacts;
                        ccb.markCoverage(ai, aunit_tests.get(parsed_tests[i].id));
                    }
                } else {
                    if (coverage) {
                        ccb.world = world;
                        ccb.markCoverage(ai, aunit_tests.get(parsed_tests[i].id));
                    }
                }
                /* Store valuation */
                aunit_tests.get(parsed_tests[i].id).setValuationXML(tempXML);
            }

            /** Start displaying coverage results **/
            if (coverage) {
                rep.cb("startCoverageTree", world.getModelName());
                String coverage_string = "";
                String coverage_color = "";
                String covfilename = "model.cov";
                OutputStream fs = null;
                ObjectOutputStream os = null;
                fs = new FileOutputStream(covfilename);
                os = new ObjectOutputStream(fs);

                for (SigParagraph sigP : ccb.getSigParagraphs()) {
                    Signature sig = sigP.getSig();
                    Pos temp = sig.getAlloySig().pos;
                    Pos sigPos = new Pos(options.originalFilename, temp.x - 4, temp.y, temp.x + sig.label.length() - 1, 0);
                    os.writeObject(sigPos);

                    coverage_string = "Signature " + sig.label + "::-::";
                    if (sig.isCovered()) {
                        coverage_string += "criteria:-:sig " + sig.label + ":-:covered:-:" + sigPos.y + ":-:" + sigPos.y2 + ":-:" + sig.getNumCovered() + ":-:" + sig.getNumCriteria() + "::-::";
                        coverage_color = "green";
                    } else if (sig.noCoverage()) {
                        coverage_string += "criteria:-:sig " + sig.label + ":-:not:-:" + sigPos.y + ":-:" + sigPos.y2 + ":-:" + sig.getNumCovered() + ":-:" + sig.getNumCriteria() + "::-::";
                        coverage_color = "red";
                    } else {
                        coverage_string += "criteria:-:sig " + sig.label + ":-:partial:-:" + sigPos.y + ":-:" + sigPos.y2 + ":-:" + sig.getNumCovered() + ":-:" + sig.getNumCriteria() + "::-::";
                        coverage_color = "yellow";
                    }
                    os.writeObject(coverage_color);

                    for (String criteria : sig.getPrettyPrintOrder()) {
                        if (sig.getCurrentCoverage().get(criteria) == Construct.Coverage.COVERED) {
                            coverage_string += "node:-:" + criteria + ":-:" + "true::-::";
                        } else {
                            coverage_string += "node:-:" + criteria + ":-:" + "false::-::";
                        }
                    }

                    for (aunit.coverage.Relation rel : sigP.getRelations()) {
                        Pos oldPos = rel.getAlloyRelation().pos;
                        Pos newPos = new Pos(options.originalFilename, oldPos.x, oldPos.y, oldPos.x2, oldPos.y2);
                        os.writeObject(newPos);
                        if (rel.isCovered()) {
                            coverage_string += "criteria:-:" + rel.label + ":-:covered:-:" + newPos.y + ":-:" + newPos.y2 + ":-:" + rel.getNumCovered() + ":-:" + rel.getNumCriteria() + "::-::";
                            coverage_color = "green";
                        } else if (rel.noCoverage()) {
                            coverage_string += "criteria:-:" + rel.label + ":-:not:-:" + newPos.y + ":-:" + newPos.y2 + ":-:" + rel.getNumCovered() + ":-:" + rel.getNumCriteria() + "::-::";
                            coverage_color = "red";
                        } else {
                            coverage_string += "criteria:-:" + rel.label + ":-:partial:-:" + newPos.y + ":-:" + newPos.y2 + ":-:" + rel.getNumCovered() + ":-:" + rel.getNumCriteria() + "::-::";
                            coverage_color = "yellow";
                        }
                        os.writeObject(coverage_color);

                        for (String criteria : rel.getPrettyPrintOrder()) {
                            if (rel.getCurrentCoverage().get(criteria) == Construct.Coverage.COVERED) {
                                coverage_string += "node:-:" + criteria + ":-:" + "true::-::";
                            } else {
                                coverage_string += "node:-:" + criteria + ":-:" + "false::-::";
                            }
                        }
                    }

                    for (Construct fact : sigP.getImplicitFacts()) {
                        Pos newPos = null;
                        if (fact instanceof SigAbstract) {
                            SigAbstract formula = (SigAbstract) fact;
                            Pos oldPos = formula.getAlloySig().isAbstract;
                            newPos = new Pos(options.originalFilename, oldPos.x, oldPos.y, oldPos.x2, oldPos.y2);
                            os.writeObject(newPos);
                        } else if (fact instanceof RelMultiplicity) {
                            RelMultiplicity formula = (RelMultiplicity) fact;
                            Pos oldPos = formula.getAlloyExpr().span();
                            newPos = new Pos(options.originalFilename, oldPos.x, oldPos.y, oldPos.x2, oldPos.y2);
                            os.writeObject(newPos);
                        } else if (fact instanceof Formula) {
                            Formula formula = (Formula) fact;
                            if (formula.pos == null) {
                                Pos oldPos = formula.getAlloyFormula().span();
                                newPos = new Pos(options.originalFilename, oldPos.x, oldPos.y, oldPos.x2, oldPos.y2);
                                os.writeObject(newPos);
                            } else {
                                Pos oldPos = formula.pos;
                                newPos = new Pos(options.originalFilename, oldPos.x, oldPos.y, oldPos.x2, oldPos.y2);
                                os.writeObject(newPos);
                            }
                        }

                        if (fact.isCovered()) {
                            coverage_string += "criteria:-:" + fact.prettyPrintName + ":-:covered:-:" + newPos.y + ":-:" + newPos.y2 + ":-:" + fact.getNumCovered() + ":-:" + fact.getNumCriteria() + "::-::";
                            coverage_color = "green";
                        } else if (fact.noCoverage()) {
                            coverage_string += "criteria:-:" + fact.prettyPrintName + ":-:not:-:" + newPos.y + ":-:" + newPos.y2 + ":-:" + fact.getNumCovered() + ":-:" + fact.getNumCriteria() + "::-::";
                            coverage_color = "red";
                        } else {
                            coverage_string += "criteria:-:" + fact.prettyPrintName + ":-:partial:-:" + newPos.y + ":-:" + newPos.y2 + ":-:" + fact.getNumCovered() + ":-:" + fact.getNumCriteria() + "::-::";
                            coverage_color = "yellow";
                        }

                        for (String criteria : fact.getPrettyPrintOrder()) {
                            if (fact.getCurrentCoverage().get(criteria) == Construct.Coverage.COVERED) {
                                coverage_string += "node:-:" + criteria + ":-:" + "true::-::";
                            } else {
                                coverage_string += "node:-:" + criteria + ":-:" + "false::-::";
                            }
                        }

                        os.writeObject(coverage_color);
                    }
                    rep.cb("addCoverageNode", coverage_string);
                }

                HashMap<String,AlloyParagraph> paragraphs = ccb.getParagraphs();
                for (String origin : paragraphs.keySet()) {
                    coverage_string = origin + "::-::";
                    Pos newPos = null;
                    for (Construct expOrForm : paragraphs.get(origin).getExpressionsAndFormulas()) {
                        if (expOrForm.isCovered()) {
                            coverage_color = "green";
                        } else if (expOrForm.noCoverage()) {
                            coverage_color = "red";
                        } else {
                            coverage_color = "yellow";
                        }

                        if (highlight) {
                            for (Pos highlight : expOrForm.getHighlightPos()) {
                                if (!highlight.filename.equals("")) {
                                    newPos = new Pos(options.originalFilename, highlight.x, highlight.y, highlight.x2, highlight.y2);
                                    os.writeObject(newPos);
                                    os.writeObject(coverage_color);
                                }
                            }
                        }

                        if (expOrForm.isCovered()) {
                            coverage_string += "criteria:-:" + expOrForm.label + ":-:covered:-:" + expOrForm.line_start + ":-:" + expOrForm.getNumCovered() + ":-:" + expOrForm.getNumCriteria() + "::-::";
                            coverage_color = "green";
                        } else if (expOrForm.noCoverage()) {
                            coverage_string += "criteria:-:" + expOrForm.label + ":-:not:-:" + expOrForm.line_start + ":-:" + expOrForm.getNumCovered() + ":-:" + expOrForm.getNumCriteria() + "::-::";
                            coverage_color = "red";
                        } else {
                            coverage_string += "criteria:-:" + expOrForm.label + ":-:partial:-:" + expOrForm.line_start + ":-:" + expOrForm.getNumCovered() + ":-:" + expOrForm.getNumCriteria() + "::-::";
                            coverage_color = "yellow";
                        }

                        for (String criteria : expOrForm.getPrettyPrintOrder()) {
                            if (expOrForm.getCurrentCoverage().get(criteria) == Construct.Coverage.COVERED) {
                                coverage_string += "node:-:" + criteria + ":-:" + "true::-::";
                            } else {
                                coverage_string += "node:-:" + criteria + ":-:" + "false::-::";
                            }
                        }
                    }

                    rep.cb("addCoverageNode", coverage_string);
                }

                rep.cb("finishCoverageTree");
                rep.cb("highlight");
                os.close();
                fs.close();

            }

            if (results.size() > 0) { //at least one test was run
                rep.cb("startResults");

                for (int i = 0; i < results.size(); i++) {
                    String name = " Test: \"" + parsed_tests[i].id + "\" ";
                    String outcome = "";
                    if (results.get(i) == Outcome.ERROR) {
                        outcome = " resulted in an error.";
                    } else if (results.get(i) == Outcome.PASSING) {
                        outcome = " passes. (" + aunitTestDetails.get(i) + ")";
                    } else {
                        outcome = " fails. (" + aunitTestDetails.get(i) + ")";
                    }

                    String timewithMS = aunitTestDetails.get(i);
                    totalTime += Integer.valueOf(timewithMS.substring(0, timewithMS.length() - 2));
                    rep.cb("addResult", name, outcome, parsed_tests[i].prettyPrintCommand(), aunit_tests.get(parsed_tests[i].id).getValuationXML(), func_calls_per_test.get(i).getCallString(), facts);
                }

                String modelName = options.originalFilename.substring(options.originalFilename.lastIndexOf(File.separator) + 1);
                modelName = modelName.substring(0, modelName.indexOf("."));
                rep.cb("summaryResults", numPassing, numFailing, numErrors, modelName, totalTime);
            }

            rep.cb("finishTree");
            if (rep.warn > 1)
                rep.cb("bold", "Note: There were " + rep.warn + " compilation warnings. Please scroll up to see them.\n");
            if (rep.warn == 1)
                rep.cb("bold", "Note: There was 1 compilation warning. Please scroll up to see it.\n");

            /** Clean up and delete files that got created **/
            aunit_extension_executions = false;
            File delete_creations = new File("executableOriginal.als");
            delete_creations.delete();
            delete_creations = new File("removedFacts.als");
            delete_creations.delete();
        }

        private String buildExp(kodkod.ast.Expression exp) {
            String name = "";
            if (exp instanceof kodkod.ast.BinaryExpression) {
                kodkod.ast.BinaryExpression bExp = (kodkod.ast.BinaryExpression) exp;
                return "(" + buildExp(bExp.left()) + " " + bExp.op() + " " + buildExp(bExp.right()) + ")";
            } else if (exp instanceof kodkod.ast.UnaryExpression) {
                kodkod.ast.UnaryExpression uExp = (kodkod.ast.UnaryExpression) exp;
                return uExp.op().toString() + buildExp(uExp.expression());
            } else if (exp instanceof kodkod.ast.Variable) {
                kodkod.ast.Variable var = (kodkod.ast.Variable) exp;
                if (var.toString().contains("_")) {
                    return "$" + var.toString().substring(var.toString().indexOf("_") + 1);
                } else {
                    return "$" + var.toString();
                }
            } else if (exp instanceof kodkod.ast.Relation) {
                kodkod.ast.Relation rel = (kodkod.ast.Relation) exp;
                return rel.toString();
            } else if (exp instanceof kodkod.ast.ConstantExpression) {
                kodkod.ast.ConstantExpression cExp = (kodkod.ast.ConstantExpression) exp;
                return cExp.toString();
            }
            return name;
        }
    }

    /** Task that perform aunit test execution and coverage calculations. */
    static final class MuAlloyExecutionTask implements WorkerTask {

        private static final long serialVersionUID = 0;
        public A4Options          options;
        public String             tempdir;
        public boolean            bundleWarningNonFatal;
        public int                bundleIndex;
        public int                resolutionMode;
        public Map<String,String> map;

        ArrayList<Expr>           exprs_in_command = new ArrayList<Expr>();

        public MuAlloyExecutionTask() {
        }

        public void cb(WorkerCallback out, Object... objs) throws IOException {
            out.callback(objs);
        }

        @SuppressWarnings("resource" )

        public void run(WorkerCallback out) throws Exception {
            cb(out, "S2", "Starting the solver...\n\n");
            final SimpleReporter rep = new SimpleReporter(out, options.recordKodkod);
            CompModule world = CompUtil.parseEverything_fromFile(rep, map, options.originalFilename, resolutionMode);
            cb(out, "warnings", bundleWarningNonFatal);
            if (rep.warn > 0 && !bundleWarningNonFatal)
                return;

            rep.cb("startResults");
            aunitTestDetails.clear();
            aunit_extension_executions = true;
            func_calls_per_test = new ArrayList<FuncCallHolder>();
            int totalTime = 0;

            /**
             * Create the different model versions to enable easy (1) testing, (2) coverage
             * calculations
             **/
            String temporig = "";
            for (String s : map.keySet()) {
                if (s.equals(options.originalFilename)) {
                    temporig = map.get(s);
                    break;
                }
            }

            String modelName = options.originalFilename.substring(options.originalFilename.lastIndexOf(File.separator) + 1);
            modelName = modelName.substring(0, modelName.indexOf("."));

            String[] model_vers = AUnitExecution.buildModelVersions(world, temporig, options);

            PrintWriter writer = new PrintWriter("removedFacts.als", "UTF-8");
            writer.println(model_vers[0]);
            writer.close();
            CompModule worldRemovedFacts = CompUtil.parseEverything_fromFile(rep, map, "removedFacts.als", resolutionMode);


            /** Map the unique test label to the associated test case **/
            HashMap<String,TestCase> aunit_tests = new HashMap<String,TestCase>();
            /**
             * A mapping of the name of the parameters for each function in the alloy model
             * under test
             **/
            HashMap<String,ArrayList<String>> func_to_parameters = new HashMap<String,ArrayList<String>>();
            /** Array of all tests in the Model Under Test **/
            Test[] parsed_tests = new Test[world.getAllTests().size()];
            parsed_tests = world.getAllTests().toArray(parsed_tests);

            HashMap<String,Func> name_to_func = new HashMap<String,Func>();
            HashSet<String> preds = new HashSet<String>();
            HashSet<String> vals = new HashSet<String>();

            /** Populate func_to_parameters mapping **/
            for (Func func : world.getAllFunc()) {
                String func_name = func.label.toString(); //isolate the name of the function invoked
                func_name = func_name.substring(func_name.indexOf("/") + 1);
                if (!func_name.contains("run$") && !func_name.contains("check$")) { //Check that the function is not a command
                    func_to_parameters.put(func_name, new ArrayList<String>());
                    AUnitExecution.addDomains(func.getBody(), new HashMap<String,String>());
                    if (func.decls.size() > 0) { //Add the name of the parameters to the mapping i.e. Acyclic[l: List] maps as Acyclic->l
                        for (Decl parameter : func.decls) {
                            for (ExprHasName var : parameter.names) {
                                func_to_parameters.get(func_name).add(var.toString());
                            }
                        }
                    }
                    if (!func.isVal) {
                        preds.add(func_name);
                    } else {
                        vals.add(func_name);
                    }
                }
                name_to_func.put(func_name, func);
            }

            AUnitExecution.addDomains(world.getAllReachableFacts(), new HashMap<String,String>());
            /** Map the name of the function to the other functions it calls **/
            HashMap<String,ArrayList<FunctionInvocation>> func_to_calls = AUnitExecution.buildCalledMap(world, func_to_parameters, name_to_func);

            /** Start to piece together (1) valuation and (2) commands for AUnit tests **/
            cb(out, "bold", "Parsing " + parsed_tests.length + " AUnit tests\n");
            HashSet<Expr> sig_fact_exprs = new HashSet<Expr>();
            /**
             * Get the invoked commands starting with the valuations which call commands
             * with parameters within them.
             **/
            for (int i = 0; i < parsed_tests.length; i++) {
                TestCase testcase = new TestCase();
                /**
                 * Set up the predicate(s) invoked by the command - this is needed for coverage
                 * to know which predicates to look over for coverage.
                 **/
                AUnitExecution.resetExplicitCalls();

                if (parsed_tests[i].getUnresolvedTestCmd().formula instanceof ExprVar && (!((ExprVar) parsed_tests[i].getUnresolvedTestCmd().formula).label.contains("run$")) && (!((ExprVar) parsed_tests[i].getUnresolvedTestCmd().formula).label.contains("check$"))) { //directly invoked a function that has no parameters
                    String func_name = ((ExprVar) parsed_tests[i].getUnresolvedTestCmd().formula).label;

                    Func pontenial_val = name_to_func.get(func_name);
                    if (!pontenial_val.isVal) {
                        aunit_extension_executions = false;
                        cb(out, "error", "AUnit Execution Error: Test \"" + parsed_tests[i].id + "\" does not reference a valuation.\n\n", parsed_tests[i].getTestCmd().pos);
                        /** Clean up files the get created **/
                        File delete_creations = new File("executableOriginal.als");
                        delete_creations.delete();
                        delete_creations = new File("removedFacts.als");
                        delete_creations.delete();
                        return;
                    }

                    for (FunctionInvocation func_invocation : func_to_calls.get(func_name)) {
                        testcase.addExplicitCall(func_invocation);
                    }
                    parsed_tests[i].setValuation(pontenial_val);
                } else {
                    AUnitExecution.findExplicitCalls(parsed_tests[i].getTestCmd().formula, func_to_parameters, new HashMap<String,String>());
                    for (FunctionInvocation func_call : AUnitExecution.getExplicitCalls()) {
                        if (!func_call.isVal) {
                            testcase.addExplicitCall(func_call);
                        }
                        for (FunctionInvocation func_invocation : func_to_calls.get(func_call.getCommand())) {
                            testcase.addExplicitCall(func_invocation);
                        }
                    }

                    /**
                     * Report an error if (1) no valuation in the test or (2) more than one
                     * valuation in the test.
                     **/

                    if (!parsed_tests[i].establishValuation(world)) {
                        if (parsed_tests[i].getValuationFunc() != null)
                            cb(out, "error", "Test case parsing error: Test \"" + parsed_tests[i].id + "\" references more than one valuation.\n\n", parsed_tests[i].getTestCmd().pos);
                        else
                            cb(out, "error", "Test case parsing error: Test \"" + parsed_tests[i].id + "\" does not reference a valuation.\n\n", parsed_tests[i].getTestCmd().pos);
                        aunit_extension_executions = false;

                        /** Clean up files the get created **/
                        File delete_creations = new File("executableOriginal.als");
                        delete_creations.delete();
                        delete_creations = new File("removedFacts.als");
                        delete_creations.delete();
                        return;
                    }
                }


                parsed_tests[i].establishCommand(world, model_vers[2].split("\n"), preds);

                AUnitExecution.resetExplicitCalls();
                AUnitExecution.findExplicitCalls(world.getAllReachableFacts(), func_to_parameters, new HashMap<String,String>());
                for (FunctionInvocation func_call : AUnitExecution.getExplicitCalls()) {
                    if (!func_call.isVal) {
                        testcase.addExplicitCall(func_call);
                    }
                    for (FunctionInvocation func_invocation : func_to_calls.get(func_call.getCommand())) {
                        testcase.addExplicitCall(func_invocation);
                    }
                }

                for (Sig sig : world.getAllReachableSigs()) {
                    for (Expr sig_fact : sig.getFacts()) {
                        AUnitExecution.resetExplicitCalls();
                        AUnitExecution.findExplicitCalls(sig_fact, func_to_parameters, new HashMap<String,String>());
                        for (FunctionInvocation func_call : AUnitExecution.getExplicitCalls()) {
                            if (!func_call.isVal) {
                                sig_fact_exprs.add(func_call.exprCall);
                            }
                            for (FunctionInvocation func_invocation : func_to_calls.get(func_call.getCommand())) {
                                sig_fact_exprs.add(func_invocation.exprCall);
                            }
                        }
                    }
                }
                testcase.removeRecursiveCalls(sig_fact_exprs);
                aunit_tests.put(parsed_tests[i].id, testcase);
            }

            /** Perform Mutation Testing **/
            cb(out, "bold", "Generating mutants.\n");

            int max_scope = -1;

            //Build up string of all the test in the format mualloy expects
            String test_only_model = "";
            for (Test testcase : parsed_tests) {
                test_only_model += "pred test" + testcase.id + "{\n";
                test_only_model += testcase.prettyPrintValuationWithCmd() + "\n}\n";

                int scope = 3;
                if (testcase.getOrigCmd().overall != -1) {
                    scope = testcase.getOrigCmd().overall;
                    if (testcase.getOrigCmd().overall > max_scope)
                        max_scope = testcase.getOrigCmd().overall;
                }

                if (testcase.getOrigCmd().expects == -1) {
                    test_only_model += "run test" + testcase.id + " for " + scope + " expect 1 \n\n";

                } else {
                    test_only_model += "run test" + testcase.id + " for " + scope + " expect " + testcase.getOrigCmd().expects + "\n\n";
                }
            }

            //No scope specified, used default scope
            if (max_scope == -1) {
                max_scope = 3;
            }

            /**
             * Print Alloy model without tests, and a file with just tests.
             **/

            String model_mutant = AUnitExecution.buildMuAlloyModelVersion(world, temporig, options);
            model_mutant += "\nrun {}";

            writer = new PrintWriter("mutation_model.als", "UTF-8");
            writer.println(model_mutant);
            writer.close();

            writer = new PrintWriter("test_only_model.als", "UTF-8");
            writer.println(test_only_model);
            writer.close();

            String[] mutantGenArgs = new String[6];
            mutantGenArgs[0] = "mutation_model.als";
            mutantGenArgs[1] = "mutation_model.als";
            mutantGenArgs[2] = "mutants";
            mutantGenArgs[3] = "results";
            mutantGenArgs[4] = "meta";

            FileUtil.createDirsIfNotExist(mutantGenArgs[2], mutantGenArgs[3], mutantGenArgs[4]);

            long start_gen = System.nanoTime();
            MutantGeneratorOpt opt = new MutantGeneratorOpt(mutantGenArgs[0], mutantGenArgs[1], mutantGenArgs[2], mutantGenArgs[3], mutantGenArgs[4], max_scope);

            CompModule module = AlloyUtil.compileAlloyModule(opt.getModelPath());
            assert module != null;
            ModelUnit mu = new ModelUnit(null, module);
            // Write pretty printed model to file for easy comparison.
            FileUtil.writeText(mu.accept(opt.getPSV(), null), Paths.get(opt.getMutantDirPath(), Names.ORIGINAL_MODEL_NAME + Names.DOT_ALS).toString(), true);
            // Mutate the model.
            ModelMutator mm = new ModelMutator(opt);
            mu.accept(mm, null);
            StringBuilder testSuite = new StringBuilder();
            int count = 0;

            String test_suite = "";
            String print_tests = "";
            ArrayList<String> valuations = new ArrayList<String>();
            ArrayList<String> aunit_valuations = new ArrayList<String>();
            ArrayList<String> aunit_cmds = new ArrayList<String>();
            for (AUnitTestCase testCase : opt.getTests()) {
                test_suite += testCase.toString(count) + "\n\n";
                aunit_valuations.add(testCase.getAUnitFormat(count));
                print_tests += testCase.getAUnitFormat(count) + "\n\n";
                aunit_cmds.add(testCase.getCommand());
                valuations.add(testCase.getValuation());
                testSuite.append(testCase.toString(count++)).append("\n");

            }

            long end_gen = System.nanoTime();
            cb(out, "bold", "EquivMutantNum: ");
            cb(out, "", String.valueOf(mm.getEquivMutantNum()) + "\n");
            cb(out, "bold", "NonEquivMutantNum: ");
            cb(out, "", String.valueOf(mm.getNonEquivMutantNum()) + "\n");
            cb(out, "bold", "Performing mutation testing.\n");

            FileUtil.writeText(print_tests, "tests.als", false);

            long start_mutation = System.nanoTime();
            File dir = new File("mutants");
            File[] mutants = dir.listFiles((d, name) -> name.endsWith(Names.DOT_ALS) && !name.equals(Names.ORIGINAL_MODEL_NAME + Names.DOT_ALS) && !name.equals(Names.MUTATION_BASED_TEST_NAME + Names.DOT_ALS));

            int totalMutantsNum = mutants.length;
            int killedMutantsNum = 0;
            A4Options options = new A4Options();
            options.noOverflow = true;

            HashMap<String,String> mutant_killing_tests = new HashMap<String,String>();
            ArrayList<String> mutant_order = new ArrayList<String>();
            HashMap<String,String> mutant_strings = new HashMap<String,String>();

            for (File mutant : mutants) {
                String mutantAsString = FileUtil.readText(mutant.getAbsolutePath());
                boolean isKilled = !TestRunner.runTestsFailFast(mutantAsString, Paths.get("test_only_model.als").toString(), options);

                if (isKilled) {
                    killedMutantsNum += 1;
                }
                String mutantName = StringUtil.beforeSubstring(mutant.getName(), Names.DOT, true);
                mutant_strings.put(mutantName, mutantAsString);

                //cb(out, "", "Mutant " + mutantName + " is " + (isKilled ? "" : "not ") + "killed.\n");
                if (!isKilled) {
                    String modelAsString = String.join(Names.NEW_LINE, Arrays.asList(mutantAsString, test_suite));
                    FileUtil.writeText(modelAsString, Names.TMPT_FILE_PATH, false);
                    CompModule model = AlloyUtil.compileAlloyModule(Names.TMPT_FILE_PATH);
                    assert model != null;
                    for (Command cmd : model.getAllCommands()) {
                        try {
                            A4Solution ans = TranslateAlloyToKodkod.execute_command(A4Reporter.NOP, model.getAllReachableSigs(), cmd, options);
                            int actual = ans.satisfiable() ? 1 : 0;
                            if (actual != cmd.expects) {
                                mutant_killing_tests.put(mutantName, cmd.label);
                            }
                        } catch (Err err) {
                            throw new RuntimeException(err.getMessage(), err.getCause());
                        }
                    }
                    mutant_order.add(mutantName);
                }

            }

            long end_mutation = System.nanoTime();
            cb(out, "", "Mutation Score: " + killedMutantsNum + "/" + totalMutantsNum + "\n");

            cb(out, "bold", "Creating mutant killing test(s) if needed.\n");
            long start_testgen = System.nanoTime();

            int i = 0;
            for (String mutant : mutant_order) {
                synchronized (SimpleReporter.class) {
                    latestModule = world;
                    latestKodkodSRC = ConstMap.make(map);
                }
                final String tempXML = tempdir + File.separatorChar + "mut" + i + ".cnf.xml";
                final String tempCNF = tempdir + File.separatorChar + "mut" + i + ".cnf";
                i++;

                rep.tempfile = tempCNF;
                int test_id = Integer.valueOf(mutant_killing_tests.get(mutant).substring(4));
                Expr valuation = CompUtil.parseOneExpression_fromString(worldRemovedFacts, valuations.get(test_id));
                Command getValuation = new Command(true, max_scope, max_scope, max_scope, valuation);
                A4Solution ai = TranslateAlloyToKodkod.execute_commandFromBook(rep, worldRemovedFacts.getAllReachableSigs(), getValuation, options);
                String mutant_string = FileUtil.readText("results" + File.separatorChar + mutant + Names.DOT_FLT);
                rep.cb("addNonKilledMutant", mutant, tempXML, mutant_string, aunit_valuations.get(test_id), aunit_cmds.get(test_id));
            }
            long end_testgen = System.nanoTime();

            if (rep.warn > 1)
                rep.cb("bold", "Note: There were " + rep.warn + " compilation warnings. Please scroll up to see them.\n");
            if (rep.warn == 1)
                rep.cb("bold", "Note: There was 1 compilation warning. Please scroll up to see it.\n");

            long time_gen = end_gen - start_gen;
            long time_mutation = end_mutation - start_mutation;
            long time_testgen = end_testgen - start_testgen;

            rep.cb("addMuAlloyTestSummary", killedMutantsNum, (totalMutantsNum - killedMutantsNum), time_gen, time_mutation, time_testgen, modelName);
            rep.cb("finishMutation");

            /** Clean up and delete files that got created **/
            aunit_extension_executions = false;
            File delete_creations = new File("test_only_model.als");
            delete_creations.delete();
            delete_creations = new File("mutation_model.als");
            delete_creations.delete();
            delete_creations = new File("removedFacts.als");
            delete_creations.delete();

            dir = new File("mutants");
            File[] allContents = dir.listFiles();
            if (allContents != null) {
                for (File file : allContents) {
                    file.delete();
                }
            }
            delete_creations = new File("mutants");
            delete_creations.delete();

            dir = new File("results");
            allContents = dir.listFiles();
            if (allContents != null) {
                for (File file : allContents) {
                    file.delete();
                }
            }
            delete_creations = new File("results");
            delete_creations.delete();

            dir = new File("meta");
            allContents = dir.listFiles();
            if (allContents != null) {
                for (File file : allContents) {
                    file.delete();
                }
            }
            delete_creations = new File("meta");
            delete_creations.delete();
        }
    }


    /** Task that perform alloy fault localization. */
    static final class AlloyFLExecutionTask implements WorkerTask {

        private static final long serialVersionUID = 0;
        public A4Options          options;
        public String             tempdir;
        public boolean            bundleWarningNonFatal;
        public int                bundleIndex;
        public double             flweight;
        public SusFormula         susformula;
        public int                resolutionMode;
        public Map<String,String> map;

        ArrayList<Expr>           exprs_in_command = new ArrayList<Expr>();

        public AlloyFLExecutionTask() {
        }

        public void cb(WorkerCallback out, Object... objs) throws IOException {
            out.callback(objs);
        }

        @SuppressWarnings("resource" )

        public void run(WorkerCallback out) throws Exception {
            cb(out, "S2", "Starting the solver...\n\n");
            final SimpleReporter rep = new SimpleReporter(out, options.recordKodkod);
            CompModule world = CompUtil.parseEverything_fromFile(rep, map, options.originalFilename, resolutionMode);
            cb(out, "warnings", bundleWarningNonFatal);
            if (rep.warn > 0 && !bundleWarningNonFatal)
                return;

            rep.cb("startResults");
            aunitTestDetails.clear();
            aunit_extension_executions = true;
            func_calls_per_test = new ArrayList<FuncCallHolder>();
            int totalTime = 0;

            /**
             * Create the different model versions to enable easy (1) testing, (2) coverage
             * calculations
             **/
            String temporig = "";
            for (String s : map.keySet()) {
                if (s.equals(options.originalFilename)) {
                    temporig = map.get(s);
                    break;
                }
            }

            String modelName = options.originalFilename.substring(options.originalFilename.lastIndexOf(File.separator) + 1);
            modelName = modelName.substring(0, modelName.indexOf("."));

            String[] model_vers = AUnitExecution.buildModelVersions(world, temporig, options);

            PrintWriter writer = new PrintWriter("removedFacts.als", "UTF-8");
            writer.println(model_vers[0]);
            writer.close();
            CompModule worldRemovedFacts = CompUtil.parseEverything_fromFile(rep, map, "removedFacts.als", resolutionMode);

            String[] model_to_text = model_vers[1].split("\n");

            /** Map the unique test label to the associated test case **/
            HashMap<String,TestCase> aunit_tests = new HashMap<String,TestCase>();
            /**
             * A mapping of the name of the parameters for each function in the alloy model
             * under test
             **/
            HashMap<String,ArrayList<String>> func_to_parameters = new HashMap<String,ArrayList<String>>();
            /** Array of all tests in the Model Under Test **/
            Test[] parsed_tests = new Test[world.getAllTests().size()];
            parsed_tests = world.getAllTests().toArray(parsed_tests);

            HashMap<String,Func> name_to_func = new HashMap<String,Func>();
            HashSet<String> preds = new HashSet<String>();
            HashSet<String> vals = new HashSet<String>();

            /** Populate func_to_parameters mapping **/
            for (Func func : world.getAllFunc()) {
                String func_name = func.label.toString(); //isolate the name of the function invoked
                func_name = func_name.substring(func_name.indexOf("/") + 1);
                if (!func_name.contains("run$") && !func_name.contains("check$")) { //Check that the function is not a command
                    func_to_parameters.put(func_name, new ArrayList<String>());
                    AUnitExecution.addDomains(func.getBody(), new HashMap<String,String>());
                    if (func.decls.size() > 0) { //Add the name of the parameters to the mapping i.e. Acyclic[l: List] maps as Acyclic->l
                        for (Decl parameter : func.decls) {
                            for (ExprHasName var : parameter.names) {
                                func_to_parameters.get(func_name).add(var.toString());
                            }
                        }
                    }
                    if (!func.isVal) {
                        preds.add(func_name);
                    } else {
                        vals.add(func_name);
                    }
                }
                name_to_func.put(func_name, func);
            }

            AUnitExecution.addDomains(world.getAllReachableFacts(), new HashMap<String,String>());
            /** Map the name of the function to the other functions it calls **/
            HashMap<String,ArrayList<FunctionInvocation>> func_to_calls = AUnitExecution.buildCalledMap(world, func_to_parameters, name_to_func);

            /** Start to piece together (1) valuation and (2) commands for AUnit tests **/
            cb(out, "bold", "Parsing " + parsed_tests.length + " AUnit tests\n");
            HashSet<Expr> sig_fact_exprs = new HashSet<Expr>();
            /**
             * Get the invoked commands starting with the valuations which call commands
             * with parameters within them.
             **/
            for (int i = 0; i < parsed_tests.length; i++) {
                TestCase testcase = new TestCase();
                /**
                 * Set up the predicate(s) invoked by the command - this is needed for coverage
                 * to know which predicates to look over for coverage.
                 **/
                AUnitExecution.resetExplicitCalls();

                if (parsed_tests[i].getUnresolvedTestCmd().formula instanceof ExprVar && (!((ExprVar) parsed_tests[i].getUnresolvedTestCmd().formula).label.contains("run$")) && (!((ExprVar) parsed_tests[i].getUnresolvedTestCmd().formula).label.contains("check$"))) { //directly invoked a function that has no parameters
                    String func_name = ((ExprVar) parsed_tests[i].getUnresolvedTestCmd().formula).label;

                    Func pontenial_val = name_to_func.get(func_name);
                    if (!pontenial_val.isVal) {
                        aunit_extension_executions = false;
                        cb(out, "error", "AUnit Execution Error: Test \"" + parsed_tests[i].id + "\" does not reference a valuation.\n\n", parsed_tests[i].getTestCmd().pos);
                        /** Clean up files the get created **/
                        File delete_creations = new File("executableOriginal.als");
                        delete_creations.delete();
                        delete_creations = new File("removedFacts.als");
                        delete_creations.delete();
                        return;
                    }

                    for (FunctionInvocation func_invocation : func_to_calls.get(func_name)) {
                        testcase.addExplicitCall(func_invocation);
                    }
                    parsed_tests[i].setValuation(pontenial_val);
                } else {
                    AUnitExecution.findExplicitCalls(parsed_tests[i].getTestCmd().formula, func_to_parameters, new HashMap<String,String>());
                    for (FunctionInvocation func_call : AUnitExecution.getExplicitCalls()) {
                        if (!func_call.isVal) {
                            testcase.addExplicitCall(func_call);
                        }
                        for (FunctionInvocation func_invocation : func_to_calls.get(func_call.getCommand())) {
                            testcase.addExplicitCall(func_invocation);
                        }
                    }

                    /**
                     * Report an error if (1) no valuation in the test or (2) more than one
                     * valuation in the test.
                     **/

                    if (!parsed_tests[i].establishValuation(world)) {
                        if (parsed_tests[i].getValuationFunc() != null)
                            cb(out, "error", "Test case parsing error: Test \"" + parsed_tests[i].id + "\" references more than one valuation.\n\n", parsed_tests[i].getTestCmd().pos);
                        else
                            cb(out, "error", "Test case parsing error: Test \"" + parsed_tests[i].id + "\" does not reference a valuation.\n\n", parsed_tests[i].getTestCmd().pos);
                        aunit_extension_executions = false;

                        /** Clean up files the get created **/
                        File delete_creations = new File("executableOriginal.als");
                        delete_creations.delete();
                        delete_creations = new File("removedFacts.als");
                        delete_creations.delete();
                        return;
                    }
                }


                parsed_tests[i].establishCommand(world, model_vers[2].split("\n"), preds);

                AUnitExecution.resetExplicitCalls();
                AUnitExecution.findExplicitCalls(world.getAllReachableFacts(), func_to_parameters, new HashMap<String,String>());
                for (FunctionInvocation func_call : AUnitExecution.getExplicitCalls()) {
                    if (!func_call.isVal) {
                        testcase.addExplicitCall(func_call);
                    }
                    for (FunctionInvocation func_invocation : func_to_calls.get(func_call.getCommand())) {
                        testcase.addExplicitCall(func_invocation);
                    }
                }

                for (Sig sig : world.getAllReachableSigs()) {
                    for (Expr sig_fact : sig.getFacts()) {
                        AUnitExecution.resetExplicitCalls();
                        AUnitExecution.findExplicitCalls(sig_fact, func_to_parameters, new HashMap<String,String>());
                        for (FunctionInvocation func_call : AUnitExecution.getExplicitCalls()) {
                            if (!func_call.isVal) {
                                sig_fact_exprs.add(func_call.exprCall);
                            }
                            for (FunctionInvocation func_invocation : func_to_calls.get(func_call.getCommand())) {
                                sig_fact_exprs.add(func_invocation.exprCall);
                            }
                        }
                    }
                }
                testcase.removeRecursiveCalls(sig_fact_exprs);
                aunit_tests.put(parsed_tests[i].id, testcase);
            }


            int max_scope = -1;

            String test_only_model = "";
            for (Test testcase : parsed_tests) {
                test_only_model += "pred test" + testcase.id + "{\n";
                test_only_model += testcase.prettyPrintValuationWithCmd() + "\n}\n";

                int scope = 3;
                if (testcase.getOrigCmd().overall != -1) {
                    scope = testcase.getOrigCmd().overall;
                    if (testcase.getOrigCmd().overall > max_scope)
                        max_scope = testcase.getOrigCmd().overall;
                }

                if (testcase.getOrigCmd().expects == -1) {
                    test_only_model += "run test" + testcase.id + " for " + scope + " expect 1 \n\n";

                } else {
                    test_only_model += "run test" + testcase.id + " for " + scope + " expect " + testcase.getOrigCmd().expects + "\n\n";
                }
            }

            if (max_scope == -1) {
                max_scope = 3;
            }

            /**
             * Print Alloy model without tests, and a file with just tests.
             **/

            String model_mutant = AUnitExecution.buildMuAlloyModelVersion(world, temporig, options);
            model_mutant += "\nrun {}";

            writer = new PrintWriter("test_stripped_model.als", "UTF-8");
            //writer.println(model_mutant);
            writer.println(model_vers[1]);
            writer.close();

            writer = new PrintWriter("test_only_model.als", "UTF-8");
            writer.println(test_only_model);
            writer.close();

            String[] alloyFLArgs = new String[6];
            alloyFLArgs[0] = "test_stripped_model.als";
            alloyFLArgs[1] = "test_only_model.als";
            alloyFLArgs[2] = "";
            alloyFLArgs[3] = susformula.name();
            alloyFLArgs[4] = "resultsfl";

            int scope = max_scope;
            double mutationProportion = flweight;

            FileUtil.createDirsIfNotExist(alloyFLArgs[4]);
            HybridFaultLocatorOpt opt = new HybridFaultLocatorOpt(alloyFLArgs[0], alloyFLArgs[1], scope, alloyFLArgs[3], alloyFLArgs[4], mutationProportion);

            Map<MutationScoreFormula,CoverageScoreFormula> MUTATION_FORMULA_TO_COVERAGE_FORMULA;
            MUTATION_FORMULA_TO_COVERAGE_FORMULA = new LinkedHashMap<>();
            MUTATION_FORMULA_TO_COVERAGE_FORMULA.put(MutationScoreFormula.TARANTULA, CoverageScoreFormula.TARANTULA);
            MUTATION_FORMULA_TO_COVERAGE_FORMULA.put(MutationScoreFormula.OCHIAI, CoverageScoreFormula.OCHIAI);
            MUTATION_FORMULA_TO_COVERAGE_FORMULA.put(MutationScoreFormula.OP2, CoverageScoreFormula.OP2);
            MUTATION_FORMULA_TO_COVERAGE_FORMULA.put(MutationScoreFormula.BARINEL, CoverageScoreFormula.BARINEL);
            MUTATION_FORMULA_TO_COVERAGE_FORMULA.put(MutationScoreFormula.DSTAR, CoverageScoreFormula.DSTAR);


            cb(out, "bold", "Running coverage-based fault localization using " + alloyFLArgs[3] + " and a weight split between mutation and coverage of " + flweight + ".\n");

            /**
             * Coverage + Mutation + Prioritize nodes with score of 1 suspiciousness.
             */
            CompModule modelModule = AlloyUtil.compileAlloyModule(opt.getModelPath());
            assert modelModule != null;
            ModelUnit modelUnit = new ModelUnit(null, modelModule);

            // Run coverage based fault localization.
            List<TestResult> testResults = alloyfl.coverage.util.TestRunner.runTests(modelUnit, opt.getTestSuitePath(), opt.getPSV(), opt.getOptions());
            Set<Node> nodesCoveredByFailingTests = testResults.stream().filter(TestResult::isFailed).map(TestResult::getRelevantNodes).flatMap(Collection::stream).collect(Collectors.toSet());

            // Count descendants to break tie when ranking nodes.
            Map<Node,Integer> descNum = countDescendantNum(modelUnit);
            // We want to make sure that the SBFL and MBFL formulas are same.

            // We use Ochiai formula for now.
            List<parser.etc.Pair<parser.ast.nodes.Node,Double>> rankedResultsByCoverageFL = MUTATION_FORMULA_TO_COVERAGE_FORMULA.get(opt.getFormula()).rank(testResults, descNum);
            // Keep the order of the node in descending suspiciousness order.
            Map<Node,Double> nodeToScoreByCoverageFL = new LinkedHashMap<>();
            rankedResultsByCoverageFL.stream().filter(pair -> nodesCoveredByFailingTests.contains(pair.a)).forEach(pair -> nodeToScoreByCoverageFL.put(pair.a, pair.b));
            // Run mutation-based fault localization.

            cb(out, "bold", "Running mutation-based fault localization.\n");

            List<Boolean> testBooleanResults = testResults.stream()
                                                          // True means the test passes.
                                                          .map(testResult -> !testResult.isFailed()).collect(Collectors.toList());
            Map<Node,Node> descendant2root = new HashMap<>();
            Set<Node> visitedNodes = new HashSet<>();
            nodeToScoreByCoverageFL.forEach((rankedNode, score) -> {
                // Collect all descendants of the ranked node.
                DescendantCollector descendantCollector = new DescendantCollector(rankedNode, visitedNodes);
                modelUnit.accept(descendantCollector, null);
                // Descendants should never overlap if we use coverage based technique, but we still use
                // visitedNodes in case if we want to switch to other techniques.
                descendantCollector.getDescendants().forEach(node -> {
                    descendant2root.put(node, rankedNode);
                    visitedNodes.add(node);
                });
            });
            // Mutate descendants.
            MutationBasedFaultDetector mbfd = new MutationBasedFaultDetector(testBooleanResults, opt, visitedNodes);
            modelUnit.accept(mbfd, null);
            Map<Node,List<ScoreInfo>> rootToDescendantScoreInfo = new HashMap<>();

            cb(out, "bold", "Calculating suspiciousness scores of each AST node.\n");

            int total_pass = 0;
            int total_fail = 0;
            for (boolean result : testBooleanResults) {
                if (result) {
                    total_pass++;
                } else {
                    total_fail++;
                }
            }

            // Group score info.
            for (ScoreInfo scoreInfo : mbfd.constructScoreInfos(descNum)) {
                Node root = descendant2root.get(scoreInfo.getNode());
                assert root != null;
                if (!rootToDescendantScoreInfo.containsKey(root)) {
                    rootToDescendantScoreInfo.put(root, new ArrayList<>());
                }
                rootToDescendantScoreInfo.get(root).add(scoreInfo);
            }

            cb(out, "bold", "Ranking AST nodes by suspiciousness score.\n");
            List<parser.etc.Pair<Node,Double>> reportedNodesAndScore = rankNodesByAverage(nodeToScoreByCoverageFL, rootToDescendantScoreInfo, descNum, opt.getMutationProportion());

            //Adjust highlighting colors for the two sus formulas that do not range from 0 to 1.
            double max_score = 0.0;
            if (susformula.name().equals("OP2")) {
                max_score = total_fail - 0 * 1.0 / (total_pass + 1);
            }

            if (susformula.name().equals("DSTAR")) {
                for (parser.etc.Pair<Node,Double> node_to_score : reportedNodesAndScore) {
                    max_score = Double.max(max_score, node_to_score.b);
                }
            }

            //Collect information from the ranked nodes to display
            for (parser.etc.Pair<Node,Double> node_to_score : reportedNodesAndScore) {
                //Get the node, its Alloy Expr and its parent Alloy Expr
                Node node = node_to_score.a;
                Object node_to_alloy = node.getNodeMap().findSrc(node);
                Object node_to_parent = node.getNodeMap().findSrc(node.getParent());
                //Get the score of the node
                Double score = node_to_score.b;
                //Get text details: the span, the operator, the class name, and the type
                String formula = getTextSpan(model_to_text, (Expr) node_to_alloy);
                String op = getTextPos(model_to_text, (Expr) node_to_alloy);
                String clazz = getExprClassName((Expr) node_to_alloy);
                String type = "Formula";
                if (clazz.contains("expression"))
                    type = "Expression";
                else if (clazz.contains("relation"))
                    type = "Relation";
                else if (clazz.contains("sig"))
                    type = "Signature";

                double color_score = score;

                if (susformula.name().equals("OP2") || susformula.name().equals("DSTAR")) {
                    color_score = score / max_score;
                }

                //Flag which color to highlight locations
                String coverage_color = "";
                if (color_score >= 0.9) {
                    coverage_color = "0.9";
                } else if (color_score >= 0.8) {
                    coverage_color = "0.8";
                } else if (color_score >= 0.7) {
                    coverage_color = "0.7";
                } else if (color_score >= 0.6) {
                    coverage_color = "0.6";
                } else if (color_score >= 0.5) {
                    coverage_color = "0.5";
                } else if (color_score >= 0.4) {
                    coverage_color = "0.4";
                } else if (color_score >= 0.3) {
                    coverage_color = "0.3";
                } else if (color_score >= 0.2) {
                    coverage_color = "0.2";
                } else if (color_score >= 0.1) {
                    coverage_color = "0.1";
                } else {
                    coverage_color = "0.0";
                }

                //Get locations to highlight
                Pos pos = ((Expr) node_to_alloy).pos();
                Pos span = ((Expr) node_to_alloy).span();

                //Positions for Sig and Field may be different than their given node location
                //The pos and span values are assigned based on their declaraed locations not their use in the model
                //First: handle signatures
                if (node_to_alloy instanceof PrimSig) {
                    PrimSig sig = (PrimSig) node_to_alloy;
                    //Generate pretty print name
                    String name = sig.label;
                    if (name.contains("/")) {
                        name = name.substring(name.indexOf("/") + 1);
                    }

                    if (node_to_parent instanceof ExprBinary) {
                        //If used in a binary expression, assign its position based on if its on the left or right hand side of the expression
                        ExprBinary parent = (ExprBinary) node_to_parent;
                        if (parent.left.equals(node_to_alloy)) {
                            pos = new Pos(parent.pos.filename, parent.span().x, parent.span().y, parent.span().x + name.length() - 1, parent.span().y);
                        } else {
                            pos = new Pos(parent.pos.filename, parent.span().x2 - name.length() + 1, parent.span().y2, parent.span().x2, parent.span().y2);
                        }
                    } else if (node_to_parent instanceof ExprUnary) {
                        //If in a unary expression, take the parent location
                        ExprUnary parent = (ExprUnary) node_to_parent;
                        pos = new Pos(parent.pos.filename, parent.span().x2 - name.length() + 1, parent.span().y2, parent.span().x2, parent.span().y2);
                    }
                } else if (node_to_alloy instanceof Field) { //Second: handle relations
                    Field relation = (Field) node_to_alloy;
                    String name = relation.label;

                    if (node_to_parent instanceof ExprBinary) {
                        //If used in a binary expression, assign its position based on if its on the left or right hand side of the expression
                        ExprBinary parent = (ExprBinary) node_to_parent;
                        ExprBinary exprBinary = parent;
                        if (exprBinary.left.equals(node_to_alloy)) {
                            pos = new Pos(((Expr) node_to_alloy).pos.filename, parent.span().x, parent.span().y, parent.span().x + name.length() - 1, parent.span().y);
                        } else {
                            pos = new Pos(((Expr) node_to_alloy).pos.filename, parent.span().x2 - name.length() + 1, parent.span().y2, parent.span().x2, parent.span().y2);
                        }
                    } else if (node_to_parent instanceof ExprUnary) {
                        //If in a unary expression, take the parent location
                        ExprUnary parent = (ExprUnary) node_to_parent;
                        pos = new Pos(((Expr) node_to_alloy).pos.filename, parent.span().x2 - name.length() + 1, parent.span().y2, parent.span().x2, parent.span().y2);
                    }
                }
                //Send results to the main display
                //1. class, 2. text of operator, 3. formula (span) text, 4. sus score
                //5 - 9 parameters to declare a new position
                //10. highlight color, 11. type of node for display
                rep.cb("addAlloyFLResult", clazz, op, formula, score, options.originalFilename, span.x, span.y, span.x2, span.y2, coverage_color, type);
            }
            rep.cb("finishAlloyFL");
            rep.cb("highlightFL");

            /** Clean up and delete files that got created **/
            aunit_extension_executions = false;

            File delete_creations = new File("test_only_model.als");
            delete_creations.delete();
            delete_creations = new File("test_stripped_model.als");
            delete_creations.delete();

            File dir = new File("resultsfl");
            File[] allContents = dir.listFiles();
            if (allContents != null) {
                for (File file : allContents) {
                    file.delete();
                }
            }
            delete_creations = new File("resultsfl");
            delete_creations.delete();
            delete_creations = new File("removedFacts.als");
            delete_creations.delete();

        }

        public static List<parser.etc.Pair<Node,Double>> rankNodesByAverage(Map<Node,Double> nodeToScoreByCoverageFL, Map<Node,List<ScoreInfo>> rootToDescendantScoreInfo, Map<Node,Integer> descNum, double mutationflProportion) {
            // Rank nodes by groups.
            List<parser.etc.Pair<Node,Double>> rankedNodeAndScore = new ArrayList<>();
            Set<Node> visitedNodes = new HashSet<>();
            nodeToScoreByCoverageFL.forEach((rankedNode, scoreByCoverageFL) -> {
                if (rootToDescendantScoreInfo.containsKey(rankedNode)) {
                    List<ScoreInfo> scoreInfos = rootToDescendantScoreInfo.get(rankedNode);
                    scoreInfos.forEach(scoreInfo -> {
                        Node node = scoreInfo.getNode();
                        // Use the average suspiciousness score from both SBFL and MBFL for the node.
                        if (mutationflProportion > 0) {
                            rankedNodeAndScore.add(parser.etc.Pair.of(node, scoreByCoverageFL * (1 - mutationflProportion) + scoreInfo.getScore() * mutationflProportion));
                            visitedNodes.add(node);
                        }
                    });
                }
                if (mutationflProportion < 1 && visitedNodes.add(rankedNode)) {
                    // If the declaring paragraph node is not reported by MBFL, then we use SBFL's score.
                    rankedNodeAndScore.add(parser.etc.Pair.of(rankedNode, scoreByCoverageFL));
                }
            });
            rankedNodeAndScore.sort((p1, p2) -> {
                int cmpRes = Double.compare(p2.b, p1.b);
                if (cmpRes != 0) {
                    return cmpRes;
                }
                return Integer.compare(descNum.get(p1.a), descNum.get(p2.a));
            });
            return rankedNodeAndScore;
            //return rankedNodeAndScore.stream().map(pair -> pair.a).collect(Collectors.toList());
        }

        /** Determines the type of expression or formula found by fault localization. */
        public String getExprClassName(Expr e) {
            String clazz = "";
            if (e instanceof ExprBinary) {
                ExprBinary binExp = (ExprBinary) e;
                /* ExprBinary can be an expression or a formula, depending on the operator */
                switch (binExp.op) {
                    case JOIN :
                    case INTERSECT :
                    case PLUS :
                    case ARROW :
                    case MINUS :
                        clazz = "binary expression";
                        break;
                    default :
                        clazz = "binary formula";
                }
            } else if (e instanceof ExprQt) {

                ExprQt quantFormula = (ExprQt) e;
                try {
                    quantFormula = (ExprQt) quantFormula.desugar();
                } catch (ErrorSyntax e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

                String quant = quantFormula.op.toString();

                if (quant.equals("comprehension")) {
                    clazz = "set comprehension";
                } else {
                    clazz = "quantified formula";
                }
            } else if (e instanceof ExprUnary) {
                ExprUnary unExp = (ExprUnary) e;
                if (unExp.op.toString().equals("NOOP")) {
                    clazz = "unary expression";
                } else {
                    if (unExp.sub == null) {
                        clazz = "unary expression";
                    } else {
                        /* Check is UnaryExpr is a expression or formula */
                        switch (unExp.op) {
                            case TRANSPOSE :
                            case RCLOSURE :
                            case CLOSURE :
                            case CARDINALITY :
                                clazz = "unary expression";
                                break;
                            default :
                                clazz = "unary formula";
                        }
                    }
                }
            } else if (e instanceof ExprList) {
                clazz = "conjunction of formulas";
            } else if (e instanceof ExprCall) {
                clazz = "call to another predicate or function";
            } else if (e instanceof ExprITE) {
                clazz = "if-then-else formula";
            } else if (e instanceof ExprLet) {
                clazz = "let formula";
            } else if (e instanceof ExprConstant) {
                clazz = "constant expression";
            } else if (e instanceof PrimSig) {
                clazz = "signature";
            } else if (e instanceof Field) {
                clazz = "relation";
            }
            return clazz;
        }

        //Returns the text contained by the spanned position
        //This position includes the entire formula, not just the operator
        public String getTextSpan(String[] text, Expr exp) {
            int start = exp.span().x;
            int end = exp.span().x2;
            int lineStart = exp.span().y;
            int lineEnd = exp.span().y2;
            String name = "";

            if (lineStart == lineEnd) {

                String temp = text[lineStart - 1] + " ";
                name = temp.substring(start - 1, end);
                if (temp.charAt(end) == ']') {
                    name += "]";
                }
                if (name.trim().endsWith("{")) {
                    name = name.substring(0, name.length() - 1);
                }
            } else {
                name = text[lineStart - 1].substring(start - 1) + "\n";
                for (int i = lineStart; i < lineEnd - 1; i++) {
                    name += text[i] + "\n";
                }
                name += text[lineEnd - 1].substring(0, end);
            }
            name = name.replaceAll("\t", "");
            if (name.startsWith("pred")) {
                name = name.substring(name.indexOf("{"));
            }

            return name;
        }

        //Returns the text contained by the pos position
        //This text is usually the operator of the formula or expression
        public String getTextPos(String[] text, Expr exp) {
            int start = exp.pos().x;
            int end = exp.pos().x2;
            int lineStart = exp.pos().y;
            int lineEnd = exp.pos().y2;
            String name = "";

            if (lineStart == lineEnd) {

                String temp = text[lineStart - 1] + " ";
                name = temp.substring(start - 1, end);
                if (temp.charAt(end) == ']') {
                    name += "]";
                }
                if (name.trim().endsWith("{")) {
                    name = name.substring(0, name.length() - 1);
                }
            } else {
                name = text[lineStart - 1].substring(start - 1) + "\n";
                for (int i = lineStart; i < lineEnd - 1; i++) {
                    name += text[i] + "\n";
                }
                name += text[lineEnd - 1].substring(0, end);
            }
            name = name.replaceAll("\t", "");
            if (name.startsWith("pred")) {
                name = name.substring(name.indexOf("{"));
            }

            return name;
        }
    }


    /** Task that perform alloy fault localization. */
    static final class ARepairExecutionTask implements WorkerTask {

        private static final long serialVersionUID = 0;
        public A4Options          options;
        public String             tempdir;
        public boolean            bundleWarningNonFatal;
        public int                bundleIndex;
        public boolean            searchstrat;
        public int                maxtriesperhole;
        public int                maxtriesperdepth;
        public int                numpartitions;
        public int                resolutionMode;
        public Map<String,String> map;

        ArrayList<Expr>           exprs_in_command = new ArrayList<Expr>();

        enum Outcome {
                      PASSING,
                      FAILING,
                      ERROR
        }


        public ARepairExecutionTask() {
        }

        public void cb(WorkerCallback out, Object... objs) throws IOException {
            out.callback(objs);
        }

        private String buildExp(kodkod.ast.Expression exp) {
            String name = "";
            if (exp instanceof kodkod.ast.BinaryExpression) {
                kodkod.ast.BinaryExpression bExp = (kodkod.ast.BinaryExpression) exp;
                return "(" + buildExp(bExp.left()) + " " + bExp.op() + " " + buildExp(bExp.right()) + ")";
            } else if (exp instanceof kodkod.ast.UnaryExpression) {
                kodkod.ast.UnaryExpression uExp = (kodkod.ast.UnaryExpression) exp;
                return uExp.op().toString() + buildExp(uExp.expression());
            } else if (exp instanceof kodkod.ast.Variable) {
                kodkod.ast.Variable var = (kodkod.ast.Variable) exp;
                if (var.toString().contains("_")) {
                    return "$" + var.toString().substring(var.toString().indexOf("_") + 1);
                } else {
                    return "$" + var.toString();
                }
            } else if (exp instanceof kodkod.ast.Relation) {
                kodkod.ast.Relation rel = (kodkod.ast.Relation) exp;
                return rel.toString();
            } else if (exp instanceof kodkod.ast.ConstantExpression) {
                kodkod.ast.ConstantExpression cExp = (kodkod.ast.ConstantExpression) exp;
                return cExp.toString();
            }
            return name;
        }

        @SuppressWarnings("resource" )

        public void run(WorkerCallback out) throws Exception {
            cb(out, "S2", "Starting the solver...\n\n");
            final SimpleReporter rep = new SimpleReporter(out, options.recordKodkod);
            CompModule world = CompUtil.parseEverything_fromFile(rep, map, options.originalFilename, resolutionMode);
            cb(out, "warnings", bundleWarningNonFatal);
            if (rep.warn > 0 && !bundleWarningNonFatal)
                return;

            rep.cb("startResults");
            aunitTestDetails.clear();
            aunit_extension_executions = true;
            func_calls_per_test = new ArrayList<FuncCallHolder>();
            int totalTime = 0;

            /**
             * Create the different model versions to enable easy (1) testing, (2) coverage
             * calculations
             **/
            String temporig = "";
            for (String s : map.keySet()) {
                if (s.equals(options.originalFilename)) {
                    temporig = map.get(s);
                    break;
                }
            }

            String modelName = options.originalFilename.substring(options.originalFilename.lastIndexOf(File.separator) + 1);
            modelName = modelName.substring(0, modelName.indexOf("."));

            String[] model_vers = AUnitExecution.buildModelVersions(world, temporig, options);

            PrintWriter writer = new PrintWriter("removedFacts.als", "UTF-8");
            writer.println(model_vers[0]);
            writer.close();
            CompModule worldRemovedFacts = CompUtil.parseEverything_fromFile(rep, map, "removedFacts.als", resolutionMode);

            String[] model_to_text = model_vers[1].split("\n");

            /** Map the unique test label to the associated test case **/
            HashMap<String,TestCase> aunit_tests = new HashMap<String,TestCase>();
            /**
             * A mapping of the name of the parameters for each function in the alloy model
             * under test
             **/
            HashMap<String,ArrayList<String>> func_to_parameters = new HashMap<String,ArrayList<String>>();
            /** Array of all tests in the Model Under Test **/
            Test[] parsed_tests = new Test[world.getAllTests().size()];
            parsed_tests = world.getAllTests().toArray(parsed_tests);

            HashMap<String,Func> name_to_func = new HashMap<String,Func>();
            HashSet<String> preds = new HashSet<String>();
            HashSet<String> vals = new HashSet<String>();

            /** Populate func_to_parameters mapping **/
            for (Func func : world.getAllFunc()) {
                String func_name = func.label.toString(); //isolate the name of the function invoked
                func_name = func_name.substring(func_name.indexOf("/") + 1);
                if (!func_name.contains("run$") && !func_name.contains("check$")) { //Check that the function is not a command
                    func_to_parameters.put(func_name, new ArrayList<String>());
                    AUnitExecution.addDomains(func.getBody(), new HashMap<String,String>());
                    if (func.decls.size() > 0) { //Add the name of the parameters to the mapping i.e. Acyclic[l: List] maps as Acyclic->l
                        for (Decl parameter : func.decls) {
                            for (ExprHasName var : parameter.names) {
                                func_to_parameters.get(func_name).add(var.toString());
                            }
                        }
                    }
                    if (!func.isVal) {
                        preds.add(func_name);
                    } else {
                        vals.add(func_name);
                    }
                }
                name_to_func.put(func_name, func);
            }

            AUnitExecution.addDomains(world.getAllReachableFacts(), new HashMap<String,String>());
            /** Map the name of the function to the other functions it calls **/
            HashMap<String,ArrayList<FunctionInvocation>> func_to_calls = AUnitExecution.buildCalledMap(world, func_to_parameters, name_to_func);

            /** Start to piece together (1) valuation and (2) commands for AUnit tests **/
            cb(out, "bold", "Parsing " + parsed_tests.length + " AUnit tests\n");
            HashSet<Expr> sig_fact_exprs = new HashSet<Expr>();
            /**
             * Get the invoked commands starting with the valuations which call commands
             * with parameters within them.
             **/
            for (int i = 0; i < parsed_tests.length; i++) {
                TestCase testcase = new TestCase();
                /**
                 * Set up the predicate(s) invoked by the command - this is needed for coverage
                 * to know which predicates to look over for coverage.
                 **/
                AUnitExecution.resetExplicitCalls();

                if (parsed_tests[i].getUnresolvedTestCmd().formula instanceof ExprVar && (!((ExprVar) parsed_tests[i].getUnresolvedTestCmd().formula).label.contains("run$")) && (!((ExprVar) parsed_tests[i].getUnresolvedTestCmd().formula).label.contains("check$"))) { //directly invoked a function that has no parameters
                    String func_name = ((ExprVar) parsed_tests[i].getUnresolvedTestCmd().formula).label;

                    Func pontenial_val = name_to_func.get(func_name);
                    if (!pontenial_val.isVal) {
                        aunit_extension_executions = false;
                        cb(out, "error", "AUnit Execution Error: Test \"" + parsed_tests[i].id + "\" does not reference a valuation.\n\n", parsed_tests[i].getTestCmd().pos);
                        /** Clean up files the get created **/
                        File delete_creations = new File("executableOriginal.als");
                        delete_creations.delete();
                        delete_creations = new File("removedFacts.als");
                        delete_creations.delete();
                        return;
                    }

                    for (FunctionInvocation func_invocation : func_to_calls.get(func_name)) {
                        testcase.addExplicitCall(func_invocation);
                    }
                    parsed_tests[i].setValuation(pontenial_val);
                } else {
                    AUnitExecution.findExplicitCalls(parsed_tests[i].getTestCmd().formula, func_to_parameters, new HashMap<String,String>());
                    for (FunctionInvocation func_call : AUnitExecution.getExplicitCalls()) {
                        if (!func_call.isVal) {
                            testcase.addExplicitCall(func_call);
                        }
                        for (FunctionInvocation func_invocation : func_to_calls.get(func_call.getCommand())) {
                            testcase.addExplicitCall(func_invocation);
                        }
                    }

                    /**
                     * Report an error if (1) no valuation in the test or (2) more than one
                     * valuation in the test.
                     **/

                    if (!parsed_tests[i].establishValuation(world)) {
                        if (parsed_tests[i].getValuationFunc() != null)
                            cb(out, "error", "Test case parsing error: Test \"" + parsed_tests[i].id + "\" references more than one valuation.\n\n", parsed_tests[i].getTestCmd().pos);
                        else
                            cb(out, "error", "Test case parsing error: Test \"" + parsed_tests[i].id + "\" does not reference a valuation.\n\n", parsed_tests[i].getTestCmd().pos);
                        aunit_extension_executions = false;

                        /** Clean up files the get created **/
                        File delete_creations = new File("executableOriginal.als");
                        delete_creations.delete();
                        delete_creations = new File("removedFacts.als");
                        delete_creations.delete();
                        return;
                    }
                }


                parsed_tests[i].establishCommand(world, model_vers[2].split("\n"), preds);

                AUnitExecution.resetExplicitCalls();
                AUnitExecution.findExplicitCalls(world.getAllReachableFacts(), func_to_parameters, new HashMap<String,String>());
                for (FunctionInvocation func_call : AUnitExecution.getExplicitCalls()) {
                    if (!func_call.isVal) {
                        testcase.addExplicitCall(func_call);
                    }
                    for (FunctionInvocation func_invocation : func_to_calls.get(func_call.getCommand())) {
                        testcase.addExplicitCall(func_invocation);
                    }
                }

                for (Sig sig : world.getAllReachableSigs()) {
                    for (Expr sig_fact : sig.getFacts()) {
                        AUnitExecution.resetExplicitCalls();
                        AUnitExecution.findExplicitCalls(sig_fact, func_to_parameters, new HashMap<String,String>());
                        for (FunctionInvocation func_call : AUnitExecution.getExplicitCalls()) {
                            if (!func_call.isVal) {
                                sig_fact_exprs.add(func_call.exprCall);
                            }
                            for (FunctionInvocation func_invocation : func_to_calls.get(func_call.getCommand())) {
                                sig_fact_exprs.add(func_invocation.exprCall);
                            }
                        }
                    }
                }
                testcase.removeRecursiveCalls(sig_fact_exprs);
                aunit_tests.put(parsed_tests[i].id, testcase);
            }


            int max_scope = -1;

            String test_only_model = "";
            for (Test testcase : parsed_tests) {
                test_only_model += "pred test" + testcase.id + "{\n";
                test_only_model += testcase.prettyPrintValuationWithCmd() + "\n}\n";

                int scope = 3;
                if (testcase.getOrigCmd().overall != -1) {
                    scope = testcase.getOrigCmd().overall;
                    if (testcase.getOrigCmd().overall > max_scope)
                        max_scope = testcase.getOrigCmd().overall;
                }

                if (testcase.getOrigCmd().expects == -1) {
                    test_only_model += "run test" + testcase.id + " for " + scope + " expect 1 \n\n";

                } else {
                    test_only_model += "run test" + testcase.id + " for " + scope + " expect " + testcase.getOrigCmd().expects + "\n\n";
                }
            }

            if (max_scope == -1) {
                max_scope = 3;
            }

            /**
             * Print Alloy model without tests, and a file with just tests.
             **/

            model_vers[1] += "\n run {}";
            writer = new PrintWriter("test_stripped_model.als", "UTF-8");
            //writer.println(model_mutant);
            writer.println(model_vers[1]);
            writer.close();

            writer = new PrintWriter("test_only_model.als", "UTF-8");
            writer.println(test_only_model);
            writer.close();


            /** For presenting the result, gather failing tests **/
            /* Helper storage for executing tasks, display results */
            /** Store the result from executing any tests **/
            ArrayList<Outcome> results = new ArrayList<Outcome>();

            CoverageCriteriaBuilder ccb = null;
            ccb = new CoverageCriteriaBuilder(world);
            ccb.findCriteria(model_vers[2]);

            String facts = "";
            for (SigParagraph sigP : ccb.getSigParagraphs()) {
                for (Construct fact : sigP.getImplicitFacts()) {
                    if (fact instanceof SigAbstract) {
                        SigAbstract formula = (SigAbstract) fact;
                        facts += formula.prettyPrintName + "\n";
                    } else if (fact instanceof RelMultiplicity) {
                        RelMultiplicity formula = (RelMultiplicity) fact;
                        facts += formula.prettyPrintName + "\n";
                    } else if (fact instanceof Formula) {
                        Formula formula = (Formula) fact;
                        facts += formula.prettyPrintName + "\n";
                    }
                }
            }

            for (String origin : ccb.getParagraphs().keySet()) {
                if (origin.contains("fact ")) {
                    facts += origin + "\n";
                } else if (origin.contains("facts")) {
                    facts += "Unamed fact paragraph(s)\n";
                }
            }

            for (int i = 0; i < parsed_tests.length; i++) {
                synchronized (SimpleReporter.class) {
                    latestModule = world;
                    latestKodkodSRC = ConstMap.make(map);
                }
                final String tempXML = tempdir + File.separatorChar + i + ".cnf.xml";
                final String tempCNF = tempdir + File.separatorChar + i + ".cnf";
                final Command cmd = parsed_tests[i].getTestCmd();

                rep.tempfile = tempCNF;
                cb(out, "", "     Executing \"" + cmd + "\"\n");

                /* Execute test */
                Command test_with_facts = new Command(cmd.check, cmd.overall, cmd.bitwidth, cmd.maxseq, cmd.formula.and(world.getAllReachableFacts()));
                TranslateAlloyToKodkod k = new TranslateAlloyToKodkod(rep, options, world.getAllReachableSigs(), test_with_facts);
                k.makeFacts(test_with_facts.formula);
                A4Solution ai = null;
                try {
                    ai = k.frame.solve(rep, cmd, new Simplifier(), true);
                } catch (UnsatisfiedLinkError ex) {
                    throw new ErrorFatal("The required JNI library cannot be found: " + ex.toString().trim(), ex);
                } catch (CapacityExceededException ex) {
                    throw ex;
                } catch (HigherOrderDeclException ex) {
                    Pos p = k.frame.kv2typepos(ex.decl().variable()).b;
                    throw new ErrorType(p, "Analysis cannot be performed since it requires higher-order quantification that could not be skolemized.");
                } catch (Throwable ex) {
                    if (ex instanceof Err)
                        throw (Err) ex;
                    else
                        throw new ErrorFatal("Unknown exception occurred: " + ex, ex);
                }
                /*
                 * Store result, and calculate number of passing, failing or erroring producing
                 * tests
                 */
                if (ai == null) {
                    results.add(Outcome.ERROR);
                    aunitTestDetails.add("");
                } else if (ai.satisfiable()) {
                    if (cmd.expects == 0) {
                        results.add(Outcome.FAILING);
                    } else if (cmd.expects == 1) {
                        results.add(Outcome.PASSING);
                    } else {
                        if (cmd.check) {
                            results.add(Outcome.FAILING);
                        } else {
                            results.add(Outcome.PASSING);
                        }
                    }
                } else {
                    if (cmd.expects == 1) {
                        results.add(Outcome.FAILING);
                    } else if (cmd.expects == 0) {
                        results.add(Outcome.PASSING);
                    } else {
                        if (!cmd.check) {
                            results.add(Outcome.FAILING);
                        } else {
                            results.add(Outcome.PASSING);
                        }
                    }
                }

                /* Set valuation for storage, and calculate coverage if needed */
                Expr valuation = CompUtil.parseOneExpression_fromString(world, parsed_tests[i].prettyPrintValuation());
                Command getValuation = new Command(cmd.check, cmd.overall, cmd.bitwidth, cmd.maxseq, valuation.and(world.getAllReachableFacts()));
                ai = TranslateAlloyToKodkod.execute_commandFromBook(rep, world.getAllReachableSigs(), getValuation, options);
                FuncCallHolder func_calls = new FuncCallHolder(i);

                boolean add = true;
                for (int c = 0; c < k.calls.size(); c++) {
                    String f_call_params = "";
                    String comma = "";
                    if (!sig_fact_exprs.contains(k.calls.get(c).exprCall)) {
                        for (int j = 0; j < k.calls.get(c).parameters.size(); j++) {
                            if (k.calls.get(c).parameters.get(j) instanceof kodkod.ast.Relation) {
                                f_call_params += comma + k.calls.get(c).parameters.get(j).toString();
                                aunit_tests.get(parsed_tests[i].id).eCommands.get(k.calls.get(c).exprCall).updateParameter(aunit_tests.get(parsed_tests[i].id).eCommands.get(k.calls.get(c).exprCall).getParameterOrder().get(j), k.calls.get(c).parameters.get(j).toString());
                            } else if (k.calls.get(c).parameters.get(j) instanceof kodkod.ast.Variable) {
                                String param = k.calls.get(c).parameters.get(j).toString();
                                if (param.contains("_")) {
                                    String is_val = param.substring(0, param.indexOf("_"));
                                    if (vals.contains(is_val)) {
                                        String var = param.substring(param.indexOf("_") + 1);
                                        f_call_params += comma + var;
                                        aunit_tests.get(parsed_tests[i].id).eCommands.get(k.calls.get(c).exprCall).updateParameter(aunit_tests.get(parsed_tests[i].id).eCommands.get(k.calls.get(c).exprCall).getParameterOrder().get(j), "$" + var);
                                    } else if (preds.contains(is_val)) {
                                        f_call_params += comma + param;
                                        String domain = aunit_tests.get(parsed_tests[i].id).eCommands.get(k.calls.get(c).exprCall).outerdomains.get(param.substring(param.indexOf("_") + 1));
                                        aunit_tests.get(parsed_tests[i].id).eCommands.get(k.calls.get(c).exprCall).updateParameter(aunit_tests.get(parsed_tests[i].id).eCommands.get(k.calls.get(c).exprCall).getParameterOrder().get(j), " : " + domain + " | ");
                                    } else {
                                        f_call_params += comma + param;
                                        aunit_tests.get(parsed_tests[i].id).eCommands.get(k.calls.get(c).exprCall).updateParameter(aunit_tests.get(parsed_tests[i].id).eCommands.get(k.calls.get(c).exprCall).getParameterOrder().get(j), "$" + param);
                                    }
                                } else if (aunit_tests.get(parsed_tests[i].id).eCommands.get(k.calls.get(c).exprCall).outerdomains.containsKey(param)) {
                                    f_call_params += comma + param;
                                    String domain = aunit_tests.get(parsed_tests[i].id).eCommands.get(k.calls.get(c).exprCall).outerdomains.get(param);
                                    aunit_tests.get(parsed_tests[i].id).eCommands.get(k.calls.get(c).exprCall).updateParameter(aunit_tests.get(parsed_tests[i].id).eCommands.get(k.calls.get(c).exprCall).getParameterOrder().get(j), " : " + domain + " | ");

                                } else {
                                    f_call_params += comma + param;
                                    aunit_tests.get(parsed_tests[i].id).eCommands.get(k.calls.get(c).exprCall).updateParameter(aunit_tests.get(parsed_tests[i].id).eCommands.get(k.calls.get(c).exprCall).getParameterOrder().get(j), "$" + param);
                                }
                            } else if (k.calls.get(c).parameters.get(j) instanceof kodkod.ast.BinaryExpression) {
                                String param = buildExp((BinaryExpression) k.calls.get(c).parameters.get(j));
                                if (param.contains(" remainder")) { //extended sig directly called
                                    param = param.substring(param.indexOf("+") + 1, param.indexOf(" remainder"));
                                    param = param.substring(param.indexOf("/") + 1);
                                    aunit_tests.get(parsed_tests[i].id).eCommands.get(k.calls.get(c).exprCall).updateParameter(aunit_tests.get(parsed_tests[i].id).eCommands.get(k.calls.get(c).exprCall).getParameterOrder().get(j), param);

                                } else {
                                    f_call_params += comma + param;
                                    aunit_tests.get(parsed_tests[i].id).eCommands.get(k.calls.get(c).exprCall).updateParameter(aunit_tests.get(parsed_tests[i].id).eCommands.get(k.calls.get(c).exprCall).getParameterOrder().get(j), param);
                                }
                            }
                            comma = ", ";
                        }
                        if (add)
                            func_calls.addExplicitCall(aunit_tests.get(parsed_tests[i].id).eCommands.get(k.calls.get(c).exprCall).getCommand() + "[" + f_call_params.replaceAll("\\$", "") + "]");
                        add = true;
                    }
                }

                func_calls_per_test.add(func_calls);
                if (!ai.satisfiable()) {
                    valuation = CompUtil.parseOneExpression_fromString(worldRemovedFacts, parsed_tests[i].prettyPrintValuation());
                    getValuation = new Command(cmd.check, cmd.overall, cmd.bitwidth, cmd.maxseq, valuation);
                    ai = TranslateAlloyToKodkod.execute_commandFromBook(rep, worldRemovedFacts.getAllReachableSigs(), getValuation, options);

                }
                /* Store valuation */
                aunit_tests.get(parsed_tests[i].id).setValuationXML(tempXML);
            }

            if (results.size() > 0) { //at least one test was run

                for (int i = 0; i < results.size(); i++) {
                    String name = " Test: \"" + parsed_tests[i].id + "\" ";
                    String outcome = "";
                    if (results.get(i) == Outcome.ERROR) {
                        outcome = " resulted in an error.";
                    } else if (results.get(i) == Outcome.PASSING) {
                        outcome = " passes. (" + aunitTestDetails.get(i) + ")";
                    } else {
                        outcome = " fails. (" + aunitTestDetails.get(i) + ")";
                    }

                    String timewithMS = aunitTestDetails.get(i);
                    totalTime += Integer.valueOf(timewithMS.substring(0, timewithMS.length() - 2));
                    if (results.get(i) == Outcome.FAILING)
                        rep.cb("addResultARepair", name, outcome, parsed_tests[i].prettyPrintCommand(), aunit_tests.get(parsed_tests[i].id).getValuationXML(), func_calls_per_test.get(i).getCallString(), facts);
                }
            }

            String[] arepairArgs = new String[2];
            arepairArgs[0] = "test_stripped_model.als";
            arepairArgs[1] = "test_only_model.als";

            int scope = max_scope;
            int maxTryPerHole = maxtriesperhole;
            int partitionNum = numpartitions;
            int maxTryPerDepth = maxtriesperdepth;
            int minimumCost = 1;

            // String strategy = "all-combinations";
            SearchStrategy searchStrategy = null;

            if (searchstrat) {
                searchStrategy = SearchStrategy.BASE_CHOICE;
            } else {
                searchStrategy = ALL_COMBINATIONS;
            }

            boolean enableChache = true;
            PatcherOpt opt = new PatcherOpt(arepairArgs[0], arepairArgs[1], scope, searchStrategy, enableChache, minimumCost, maxTryPerHole, partitionNum, maxTryPerDepth);

            cb(out, "bold", "Running ARepair.\n");
            String FIX_FILE_PATH = "fix.als";

            CompModule modelModule = AlloyUtil.compileAlloyModule(opt.getModelPath());
            assert modelModule != null;
            ModelUnit modelUnit = new ModelUnit(null, modelModule);
            FileUtil.writeText(modelUnit.accept(opt.getPSV(), null), FIX_FILE_PATH, false);

            cb(out, "", "Original model:\n");
            cb(out, "", modelUnit.accept(opt.getPSV(), null));
            // Keep track of the depths visited by the synthesizer before.  We do not want to
            // synthesize expressions from the lowest depth every time we fixed some tests.
            DepthInfo depthInfo = new DepthInfo();
            int iteration = 1; // Debugging
            // Assume the model can be fixed.
            boolean canFix = true;
            HashSet<String> affected = new HashSet<String>();
            while (canFix) {
                cb(out, "bold", "Iteration " + iteration++ + "\n");
                CompModule moduleToFix = AlloyUtil.compileAlloyModule(FIX_FILE_PATH);
                ModelUnit modelToFix = new ModelUnit(null, moduleToFix);

                // The test runner saves the model with tests into TMPT_FILE_PATH.
                List<TestResult> testResults = alloyfl.coverage.util.TestRunner.runTests(modelToFix, opt.getTestSuitePath(), opt.getPSV(), opt.getOptions());
                List<Boolean> testBooleanResults = testResults.stream()
                                                              // True means the test passes.
                                                              .map(testResult -> !testResult.isFailed()).collect(Collectors.toList());
                if (testBooleanResults.stream().allMatch(result -> result)) {
                    break;
                }


                canFix = false;
                Set<Node> nodesCoveredByFailingTests = getNodesCoveredByFailingTests(modelToFix, testResults);

                FaultLocator faultLocator = new FaultLocator(opt, testBooleanResults, nodesCoveredByFailingTests);
                timer.record();

                modelToFix.accept(faultLocator, null);
                Map<Node,Integer> descNum = countDescendantNum(modelToFix);

                List<Node> rankedNodes = faultLocator.rankNode(descNum).stream().map(ScoreInfo::getNode).collect(Collectors.toList());
                timer.show("Fault localization time");
                if (rankedNodes.isEmpty()) {
                    // Cannot find any location to fix.
                    break;
                }

                Node mostSuspiciousNode = rankedNodes.get(0);
                MutationImpact mostSuspiciousImpact = faultLocator.getMutationImpact(mostSuspiciousNode);



                // We prioritize mutations that make some failing test pass and no passing test fails.
                // If such mutation does not exist, then we use the most suspicious mutation.
                if (mostSuspiciousImpact.getFailToPass() > 0 && mostSuspiciousImpact.getPassToFail() == 0 || mostSuspiciousImpact.getScore() > SUSPICIOUSNESS_THRESHOLD) {
                    cb(out, "bold", "Most suspicious node: ");
                    cb(out, "", mostSuspiciousNode.accept(opt.getPSV(), null) + ", ");
                    cb(out, "bold", "Score: ");
                    cb(out, "", mostSuspiciousImpact.getScore() + "\n");
                    cb(out, "bold", "FailToPass: ");
                    cb(out, "", mostSuspiciousImpact.getFailToPass() + " ");
                    cb(out, "bold", "PassToFail: ");
                    cb(out, "", mostSuspiciousImpact.getPassToFail() + "\n");

                    for (String nodes : Synthesizer.findAffectedParagraphNames(mostSuspiciousNode)) {
                        affected.add(nodes);
                    }

                    // Mutation can partially fix the bug.
                    FileUtil.writeText(mostSuspiciousImpact.getMutant(), FIX_FILE_PATH, false);
                    cb(out, "bold", "Fixed by mutation, intermediate model:\n");
                    cb(out, "", FileUtil.readText(FIX_FILE_PATH).trim());
                    cb(out, "", "==========\n");
                    canFix = true;
                } else { // Mutation cannot fix the bug and we need to synthesize the components.
                    GeneratorOpt generatorOpt = new GeneratorOpt(BOUND_TYPE, MAX_DEPTH_OR_COST, MAX_ARITY, MAX_OP_NUM, opt.getScope(), opt.enableCaching());
                    TypeAnalyzer analyzer = new TypeAnalyzer(modelToFix);

                    // ModuloInputChecker needs model with tests.
                    CompModule moduleWithTests = mergeModelAndTests(modelUnit, opt.getTestSuitePath(), opt.getPSV());
                    ModelUnit modelUnitWithTests = new ModelUnit(null, moduleWithTests);
                    Generator generator = new Generator(analyzer, generatorOpt, modelUnitWithTests);

                    cb(out, "bold", "Test num: ");
                    cb(out, "", generator.getTests().size());
                    Synthesizer synthesizer = new Synthesizer(modelToFix, modelUnitWithTests, generatorOpt, generator, analyzer);
                    for (int i = 0; i < rankedNodes.size(); i++) {
                        Node rankedNode = rankedNodes.get(i);
                        cb(out, "bold", "Rank Node " + i + ":");
                        cb(out, "", rankedNode.accept(opt.getPSV(), null) + "\n");
                        if (synthesizer.synthesize(rankedNode, depthInfo, opt)) {

                            for (String nodes : Synthesizer.findAffectedParagraphNames(mostSuspiciousNode)) {
                                affected.add(nodes);
                            }

                            cb(out, "bold", "Fixed by synthesizer, intermediate model:\n");
                            cb(out, "", FileUtil.readText(FIX_FILE_PATH).trim());
                            cb(out, "", "==========\n");
                            canFix = true;
                            break;
                        }
                    }
                }
            }
            if (canFix) {
                cb(out, "bold", "All tests pass.\n");
            } else {
                cb(out, "bold", "Cannot fix the model.\n");
            }
            cb(out, "bold", "Raw patched model:\n");
            cb(out, "", FileUtil.readText(FIX_FILE_PATH));
            cb(out, "bold", "\nAfter simplification:\n");
            String simplifiedModel = simplify(new ModelUnit(null, AlloyUtil.compileAlloyModule(FIX_FILE_PATH)));
            FileUtil.writeText(simplifiedModel, FIX_FILE_PATH, false);
            PrettyStringVisitorWithoutEmptyRun psver = new PrettyStringVisitorWithoutEmptyRun();

            simplifiedModel = psver.visit(new ModelUnit(null, AlloyUtil.compileAlloyModule(FIX_FILE_PATH)), null);
            FileUtil.writeText(simplifiedModel, FIX_FILE_PATH, false);
            cb(out, "", simplifiedModel.trim());

            String original = simplify(new ModelUnit(null, AlloyUtil.compileAlloyModule(opt.getModelPath())));

            ModelUnit mu = new ModelUnit(null, AlloyUtil.compileAlloyModule(FIX_FILE_PATH));
            ModelUnit orig_mu = new ModelUnit(null, AlloyUtil.compileAlloyModule(opt.getModelPath()));
            String patch = "";
            String orig = "";

            String front = "";

            PrettyStringVisitor psv = new PrettyStringVisitor();
            for (String patched : affected) {
                for (SigDecl sig : mu.getSigDeclList()) {
                    if (patched.contains("_")) {
                        String[] temp = patched.split("_");
                        if (sig.getName().equals(temp[0]))
                            patch += front + psv.visit(sig, null);
                    } else if (sig.getName().equals(patched)) {
                        patch += front + psv.visit(sig, null);
                    }
                }
                for (Predicate pred : mu.getPredDeclList()) {
                    if (pred.getName().equals(patched)) {
                        patch += front + psv.visit(pred, null);
                    }
                }
                for (Function func : mu.getFunDeclList()) {
                    if (func.getName().equals(patched)) {
                        patch += front + psv.visit(func, null);
                    }
                }
                for (Assertion assertion : mu.getAssertDeclList()) {
                    if (assertion.getName().equals(patched)) {
                        patch += front + psv.visit(assertion, null);
                    }
                }
                for (Fact fact : mu.getFactDeclList()) {
                    if (fact.getName().equals(patched)) {
                        patch += front + psv.visit(fact, null);
                    }
                }
                for (SigDecl sig : orig_mu.getSigDeclList()) {
                    if (patched.contains("_")) {
                        String[] temp = patched.split("_");
                        if (sig.getName().equals(temp[0]))
                            orig += front + psv.visit(sig, null);
                    } else if (sig.getName().equals(patched)) {
                        orig += front + psv.visit(sig, null);
                    }
                }
                for (Predicate pred : orig_mu.getPredDeclList()) {
                    if (pred.getName().equals(patched)) {
                        orig += front + psv.visit(pred, null);
                    }
                }
                for (Function func : orig_mu.getFunDeclList()) {
                    if (func.getName().equals(patched)) {
                        orig += front + psv.visit(func, null);
                    }
                }
                for (Assertion assertion : orig_mu.getAssertDeclList()) {
                    if (assertion.getName().equals(patched)) {
                        orig += front + psv.visit(assertion, null);
                    }
                }
                for (Fact fact : orig_mu.getFactDeclList()) {
                    if (fact.getName().equals(patched)) {
                        orig += front + psv.visit(fact, null);
                    }
                }
                front = "\n";
            }

            diff_match_patch dmp = new diff_match_patch();
            String different = "";
            for (Diff diff : dmp.diff_main(orig, patch)) {
                different += diff.toString();
            }

            if (canFix) {
                rep.cb("finishARepair", different, iteration);
            } else {
                rep.cb("ARepairResultFail", "Cannot fix the model.");
            }



            /** Clean up and delete files that got created **/
            aunit_extension_executions = false;

            File delete_creations = new File("test_only_model.als");
            delete_creations.delete();
            delete_creations = new File("test_stripped_model.als");
            delete_creations.delete();
            delete_creations = new File("removedFacts.als");
            delete_creations.delete();

            File dir = new File("resultsrepair");
            File[] allContents = dir.listFiles();
            if (allContents != null) {
                for (File file : allContents) {
                    file.delete();
                }
            }
            delete_creations = new File("resultsrepair");
            delete_creations.delete();

        }

        private static Set<Node> getNodesCoveredByFailingTests(ModelUnit modelUnit, List<TestResult> testResults) {
            Set<Node> paragraphsCoveredByFailingTests = testResults.stream().filter(TestResult::isFailed).map(TestResult::getRelevantNodes).flatMap(Collection::stream).collect(Collectors.toSet());
            Set<Node> visitedNodes = new HashSet<>();
            Set<Node> descendants = new HashSet<>();
            for (Node coveredParagraph : paragraphsCoveredByFailingTests) {
                DescendantCollector descendantCollector = new DescendantCollector(coveredParagraph, visitedNodes);
                modelUnit.accept(descendantCollector, null);
                descendants.addAll(descendantCollector.getDescendants());
                visitedNodes.addAll(descendantCollector.getDescendants());
            }
            return descendants;
        }

        private static Map<Node,RelevantTestResults> constructNodeToTestMapping(ModelUnit modelUnit, List<TestResult> testResults) {
            // Find all nodes that are covered by failing tests and establish the mapping
            // between those nodes and the test results.
            Map<Node,RelevantTestResults> node2test = new HashMap<>();
            // First collect nodes covered by failing tests.
            Set<Node> nodesCoveredByFailingTests = testResults.stream().filter(TestResult::isFailed).map(TestResult::getRelevantNodes).flatMap(Collection::stream).collect(Collectors.toSet());
            // Then collect tests that execute nodes covered by failing tests.
            testResults.forEach(testResult -> {
                testResult.getRelevantNodes().stream().filter(nodesCoveredByFailingTests::contains).forEach(nodeCoveredByFailingTests -> {
                    if (!node2test.containsKey(nodeCoveredByFailingTests)) {
                        node2test.put(nodeCoveredByFailingTests, new RelevantTestResults());
                    }
                    node2test.get(nodeCoveredByFailingTests).addTestResult(testResult);
                });
            });
            // Finally map all descendants to the same test results as the paragraph node.
            // Note that all descendants keep the same test results copy as the paragraph node.
            node2test.forEach((paraNode, results) -> {
                DescendantCollector descendantCollector = new DescendantCollector(paraNode, new HashSet<>());
                modelUnit.accept(descendantCollector, null);
                descendantCollector.getDescendants().forEach(descendant -> {
                    node2test.put(descendant, results);
                });
            });
            return node2test;
        }
    }
}
