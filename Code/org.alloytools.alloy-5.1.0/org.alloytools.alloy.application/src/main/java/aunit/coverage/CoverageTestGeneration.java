package aunit.coverage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import aunit.gui.AUnitExecution;
import edu.mit.csail.sdg.alloy4.A4Reporter;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4.ErrorWarning;
import edu.mit.csail.sdg.ast.Command;
import edu.mit.csail.sdg.ast.Expr;
import edu.mit.csail.sdg.ast.ExprVar;
import edu.mit.csail.sdg.ast.Func;
import edu.mit.csail.sdg.ast.Sig;
import edu.mit.csail.sdg.parser.CompModule;
import edu.mit.csail.sdg.parser.CompUtil;
import edu.mit.csail.sdg.translator.A4Options;
import edu.mit.csail.sdg.translator.A4Solution;
import edu.mit.csail.sdg.translator.TranslateAlloyToKodkod;

public class CoverageTestGeneration {

    public static CoverageTestSuite makeValuations(File file, CompModule world, ArrayList<String> paras, A4Options opt, int scope) throws Err, FileNotFoundException, IOException {
        /**
         * Build a new world with only the needed paragraphs, find coverage criteria,
         * generate tests to cover all criteria.
         **/
        /** Test suite storage **/
        ArrayList<TestCase> test_suite = new ArrayList<TestCase>();
        /** A4Reporter object **/
        A4Reporter rep = new A4Reporter() {

            @Override
            public void warning(ErrorWarning msg) {
            }
        };
        /** Stores mapping from paragraph name to string of parameters **/
        HashMap<String,String> para_to_params = new HashMap<String,String>();
        for (Sig sig : world.getAllSigs()) {
            String name = sig.label;
            name = name.substring(name.indexOf("/") + 1);
            para_to_params.put(name, "");
        }
        for (Func predicate : world.getAllFunc()) {
            String parameterSome = "";
            String name = predicate.label;
            name = name.substring(name.indexOf("/") + 1);
            for (ExprVar eV : predicate.params()) {
                parameterSome += "some " + eV.label + " : " + eV.type() + " | ";
            }
            para_to_params.put(name, parameterSome);
        }
        para_to_params.put("Facts", "");

        /**
         * Gather the different model representations: original, executable and facts
         * removed
         **/
        String temp = "";
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                temp += line + "\n";
            }
        }
        String[] model_vers = AUnitExecution.buildModelVersions(world, temp, opt);

        PrintWriter writer = new PrintWriter("model.als", "UTF-8");
        writer.println(model_vers[1]);
        writer.close();
        writer = new PrintWriter("modelnofacts.als", "UTF-8");
        writer.println(model_vers[0]);
        writer.close();

        CompModule facts = CompUtil.parseEverything_fromFile(rep, null, "model.als");
        CompModule nofacts = CompUtil.parseEverything_fromFile(rep, null, "modelnofacts.als");

        TargetingCriteriaBuilder tcb = new TargetingCriteriaBuilder(facts, paras);
        tcb.findCriteria(model_vers[1]);
        tcb.world = facts;
        TargetingConstraint target = tcb.getNextTargetingConstraint();

        int numValuations = 0;
        while (target != null) {
            /** Build a command with the targeting constraints, all facts of the model **/
            //Create instance
            Expr expr = CompUtil.parseOneExpression_fromString(facts, para_to_params.get(target.paragraph) + target.constraint);
            Command getinstance = new Command(false, scope, scope, scope, expr.and(facts.getAllReachableFacts()));
            A4Solution valuation = TranslateAlloyToKodkod.execute_command(rep, facts.getAllReachableSigs(), getinstance, opt);
            tcb.world = facts;

            int expect = 1;
            if (!valuation.satisfiable()) {
                expr = CompUtil.parseOneExpression_fromString(nofacts, para_to_params.get(target.paragraph) + target.constraint);
                getinstance = new Command(false, scope, scope, scope, expr);
                valuation = TranslateAlloyToKodkod.execute_command(rep, nofacts.getAllReachableSigs(), getinstance, opt);
                tcb.world = nofacts;
                expect = 0;
            }
            if (!valuation.satisfiable()) { //Mark as infeasible and move on to checking next criteria
                tcb.markInfeasible(target);
            } else {

                numValuations++;
                //have generated a valuation, now build the test case
                ArrayList<TestCase> temp_suite = new ArrayList<TestCase>();
                for (ExprVar eV : valuation.getAllAtoms()) {
                    String var = eV.toString().replaceAll("\\$", "");
                    tcb.world.addGlobal(var, eV);
                }

                for (Func predicate : facts.getAllFunc()) { //loop over all functions in the module
                    if (predicate.isPred && !predicate.isVal && !(predicate.label.contains("run") || predicate.label.contains("check"))) { //make sure Func is not a run or check command - but a Fun, Pred or Assert
                        String label = predicate.label;
                        label = label.substring(label.indexOf("/") + 1);

                        if (paras.contains(label)) {
                            /*
                             * Build up all possible combinations of calls to the predicate - use the
                             * parameter type
                             */
                            ArrayList<String> parameters = new ArrayList<String>();
                            ArrayList<String> nameOfParams = new ArrayList<String>();

                            for (ExprVar eV : predicate.params()) {
                                /*
                                 * Execute the parameter's type over the valuation - the results show all
                                 * possible values that can be passed for this particular parameter - use this
                                 * to build up all possible combinations of parameter calls for a valuation
                                 */
                                String values = valuation.eval(CompUtil.parseOneExpression_fromString(tcb.world, eV.type().toString())).toString();
                                values = values.substring(1, values.length() - 1);
                                if (values.trim().equals("")) {
                                    //if its empty, then then that type is empty for this valuation, just pass the name of the type
                                    values = eV.type().toString();
                                    values = values.substring(values.lastIndexOf("/") + 1, values.length() - 1);
                                }
                                String[] paramValues = values.split(",");
                                if (parameters.size() == 0) {
                                    for (int j = 0; j < paramValues.length; j++) {
                                        if (values.length() == 0) {
                                            String value = eV.type().toString().replaceAll("this/", "");
                                            parameters.add(value.substring(1, value.length() - 1));
                                        } else
                                            parameters.add(paramValues[j]);
                                    }
                                } else {
                                    ArrayList<String> newParameters = new ArrayList<String>();
                                    for (String param : parameters) {
                                        for (int j = 0; j < paramValues.length; j++) {
                                            newParameters.add(param + ", " + paramValues[j]);
                                        }
                                    }
                                    parameters.clear();
                                    parameters.addAll(newParameters);
                                }
                                nameOfParams.add(eV.label); //builds up order of parameters
                            }

                            for (String params : parameters) {
                                /*
                                 * If there are parameters for this function, generate a test case for each
                                 * combination
                                 */
                                TestCase testCase = new TestCase(valuation.toString());
                                String[] values = params.split(",");
                                ArrayList<String> valueOfParameters = new ArrayList<String>();
                                for (int j = 0; j < values.length; j++) {
                                    valueOfParameters.add(values[j].replaceAll("\\$", "").trim());
                                }
                                testCase.addExplicitCall(predicate.label.substring(5), nameOfParams, valueOfParameters);
                                temp_suite.add(testCase);
                            }
                            if (parameters.size() == 0) {
                                /*
                                 * If there are no parameters, make a test case with just the valuation - the
                                 * default command of a TestCase is just the facts of the model.
                                 */
                                TestCase testCase = new TestCase(valuation.toString());
                                testCase.addExplicitCall(new FunctionInvocation(predicate.label.substring(5)));
                                temp_suite.add(testCase);
                            }
                        }
                    }
                }
                TestCase noCommand = new TestCase(valuation.toString());
                temp_suite.add(noCommand);

                for (TestCase testcase : temp_suite) {
                    /*
                     * For each potential test case, check if it adds to the model coverage at all,
                     * if it does, add it to the overall test suite.
                     */
                    if (tcb.markCoverage(valuation, testcase) > 0) {
                        testcase.expect = expect;
                        String xmlDir = opt.tempDirectory + File.separatorChar + "test" + test_suite.size() + ".cnf.xml";
                        valuation.writeXML(xmlDir);
                        testcase.setValuationXML(xmlDir);
                        if ((boolean) valuation.eval(CompUtil.parseOneExpression_fromString(tcb.world, testcase.getExecutableCommand()))) {
                            testcase.call = "";
                        } else {
                            testcase.call = "!";
                        }
                        testcase.formPrintableTC(facts);
                        test_suite.add(testcase);
                    }
                }
            }
            target = tcb.getNextTargetingConstraint();
            /*
             * if(target != null) System.out.println(target.constraint);
             */
        }

        return new CoverageTestSuite(file, model_vers[2], test_suite, scope, 100.0, numValuations, test_suite.size(), facts, nofacts);
    }

    public static void printTestSuite(String orig, File file, ArrayList<TestCase> test_suite, int scope, CompModule facts, CompModule nofacts, CoverageTestSuite ts) throws FileNotFoundException, UnsupportedEncodingException {

        String opening_comment = "/*This file was automatically generated by AUnit v1.0's coverage-based test generation feature.\nTest Suite Details:\n----------\n" + "Tests generated over: " + ts.model.getName() + "\n" + "Number Valuations: " + ts.num_vals + "\n" + "Number Tests: " + ts.num_tests + "\n" + "Scope used: " + ts.scope + "*/\n\n";
        String name = file.getName();
        name = name.substring(0, name.indexOf("."));
        String printTestSuite = opening_comment;
        printTestSuite += orig + "\n\n";
        int numTest = 0;
        for (TestCase testcase : test_suite) {
            printTestSuite += "val Test" + numTest + "{\n" + testcase.print_valuation + "}\n";
            printTestSuite += "@Test Test" + numTest + ": run Test" + numTest + " for " + scope;
            printTestSuite += " expect " + testcase.expect;
            printTestSuite += "\n\n";
            numTest++;
        }
        PrintWriter writer = new PrintWriter(file.getParent() + File.separator + name + "coverageTS.als", "UTF-8");
        writer.println(printTestSuite);
        writer.close();
    }

}
