package mualloy.util;

import static parser.etc.Names.DOLLAR;
import static parser.etc.Names.SLASH;

import java.util.ArrayList;
import java.util.List;

import edu.mit.csail.sdg.alloy4.A4Reporter;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.ast.Command;
import edu.mit.csail.sdg.ast.Expr;
import edu.mit.csail.sdg.ast.ExprVar;
import edu.mit.csail.sdg.ast.Func;
import edu.mit.csail.sdg.ast.Sig;
import edu.mit.csail.sdg.parser.CompModule;
import edu.mit.csail.sdg.translator.A4Options;
import edu.mit.csail.sdg.translator.A4Solution;
import edu.mit.csail.sdg.translator.A4Tuple;
import edu.mit.csail.sdg.translator.A4TupleSet;
import edu.mit.csail.sdg.translator.TranslateAlloyToKodkod;
import mualloy.opt.MutantGeneratorOpt;
import parser.ast.nodes.Function;
import parser.ast.nodes.Node;
import parser.ast.nodes.Paragraph;
import parser.ast.nodes.PredOrFun;
import parser.etc.Context;
import parser.etc.Names;
import parser.util.AlloyUtil;
import parser.util.FileUtil;
import parser.util.StringUtil;

public class TestGenerator {

    public static AUnitTestCase translateToTest(CompModule module, A4Solution sol, Node node, SpecialCase specialCase, String commandName, int scope, String correctModelAsString, A4Options options) {
        try {
            StringBuilder body = new StringBuilder();
            StringBuilder body_val_cmd = new StringBuilder();
            StringBuilder sigDecls = new StringBuilder();
            StringBuilder endParens = new StringBuilder();
            StringBuilder relAssignments = new StringBuilder();
            StringBuilder aunitFormat = new StringBuilder();
            StringBuilder cmd = new StringBuilder();
            StringBuilder valuation = new StringBuilder();

            for (Sig sig : sol.getAllReachableSigs()) {
                String moduleAlias = StringUtil.beforeSubstring(sig.label, Names.SLASH, false);
                if (moduleAlias.equals("this")) {
                    String sigName = StringUtil.afterSubstring(sig.label, Names.SLASH, false);
                    A4TupleSet sigTupleSet = sol.eval(sig);
                    if (sigTupleSet.size() == 0) {
                        relAssignments.append("no ").append(sigName).append(Names.NEW_LINE);
                        aunitFormat.append("\t\t").append("no ").append(sigName).append(Names.NEW_LINE);
                        valuation.append("no ").append(sigName).append(Names.NEW_LINE);
                    } else {
                        sigDecls.append("some disj ");
                        String declPrefix = "";
                        relAssignments.append(sigName).append(" = ");
                        aunitFormat.append("\t\t").append(sigName).append(" = ");
                        valuation.append(sigName).append(" = ");
                        String assignPrefix = "";
                        for (A4Tuple tuple : sigTupleSet) {
                            String sigValue = tuple.toString().replaceAll("\\" + DOLLAR, "");
                            sigDecls.append(declPrefix).append(sigValue);
                            declPrefix = ", ";
                            relAssignments.append(assignPrefix).append(sigValue);
                            aunitFormat.append(assignPrefix).append(sigValue);
                            valuation.append(assignPrefix).append(sigValue);
                            assignPrefix = " + ";
                        }
                        sigDecls.append(": ").append(sigName).append(" {");
                        endParens.append("}");
                        relAssignments.append(Names.NEW_LINE);
                        aunitFormat.append(Names.NEW_LINE);
                        valuation.append(Names.NEW_LINE);
                    }
                    for (Sig.Field field : sig.getFields()) {
                        A4TupleSet fieldTupleSet = sol.eval(field);
                        if (fieldTupleSet.size() == 0) {
                            relAssignments.append("no ").append(field.label).append(Names.NEW_LINE);
                            aunitFormat.append("\t\t").append("no ").append(field.label).append(Names.NEW_LINE);
                            valuation.append("no ").append(field.label).append(Names.NEW_LINE);
                        } else {
                            relAssignments.append(field.label).append(" = ");
                            aunitFormat.append("\t\t").append(field.label).append(" = ");
                            valuation.append(field.label).append(" = ");
                            String assignPrefix = "";
                            for (A4Tuple tuple : fieldTupleSet) {
                                String fieldValue = tuple.toString().replaceAll("\\" + DOLLAR, "");
                                relAssignments.append(assignPrefix).append(fieldValue);
                                aunitFormat.append(assignPrefix).append(fieldValue);
                                valuation.append(assignPrefix).append(fieldValue);
                                assignPrefix = " + ";
                            }
                            relAssignments.append(Names.NEW_LINE);
                            aunitFormat.append(Names.NEW_LINE);
                            valuation.append(Names.NEW_LINE);
                        }
                    }
                } else if (specialCase.getOrderingOpenDecls().stream().anyMatch(openDecl -> openDecl.getAlias().equals(moduleAlias))) {
                    for (Sig.Field field : sig.getFields()) {
                        A4TupleSet fieldTupleSet = sol.eval(field);
                        String fieldName = moduleAlias + Names.SLASH + field.label.toLowerCase();
                        if (fieldTupleSet.size() == 0) {
                            relAssignments.append("no ").append(fieldName).append(Names.NEW_LINE);
                            aunitFormat.append("\t\t").append("no ").append(fieldName).append(Names.NEW_LINE);
                            valuation.append("no ").append(fieldName).append(Names.NEW_LINE);
                        } else {
                            relAssignments.append(fieldName).append(" = ");
                            aunitFormat.append("\t\t").append(fieldName).append(" = ");
                            valuation.append(fieldName).append(" = ");
                            String assignPrefix = "";
                            for (A4Tuple tuple : fieldTupleSet) {
                                String fieldValue = ignoreFirstKArity(tuple, 1).replaceAll("\\" + DOLLAR, "");
                                relAssignments.append(assignPrefix).append(fieldValue);
                                aunitFormat.append(assignPrefix).append(fieldValue);
                                valuation.append(assignPrefix).append(fieldValue);
                                assignPrefix = " + ";
                            }
                            relAssignments.append(Names.NEW_LINE);
                            aunitFormat.append(Names.NEW_LINE);
                            valuation.append(Names.NEW_LINE);
                        }
                    }
                }
            }
            sigDecls.append(Names.NEW_LINE);
            boolean generateCall = node instanceof Paragraph;
            if (generateCall) {
                List<String> args = new ArrayList<>();
                if (node instanceof PredOrFun) {
                    List<String> argNames = new ArrayList<>();
                    ((PredOrFun) node).getParamList().forEach(paramDecl -> argNames.addAll(paramDecl.getNames()));
                    Iterable<ExprVar> skolems = sol.getAllSkolems();
                    for (String argName : argNames) {
                        boolean foundSkolem = false;
                        for (ExprVar skolem : skolems) {
                            String skolemName = skolem.label.substring(commandName.length() + 2);
                            if (argName.equals(skolemName)) {
                                A4TupleSet skolemTupleSet = (A4TupleSet) sol.eval(skolem);
                                StringBuilder argVal = new StringBuilder();
                                String argPrefix = "";
                                for (A4Tuple skolemTuple : skolemTupleSet) {
                                    argVal.append(argPrefix).append(skolemTuple.toString());
                                    argPrefix = " + ";
                                }
                                args.add(skolemTupleSet.size() == 0 ? "none" : argVal.toString());
                                foundSkolem = true;
                                break;
                            }
                        }
                        if (!foundSkolem) {
                            args.add("none");
                        }
                    }
                    Paragraph paragraph = (Paragraph) node;
                    String call = paragraph.getName().replaceAll("\\" + DOLLAR, "_") + "[" + String.join(Names.COMMA, args) + "]";
                    cmd.append(call.replaceAll("\\" + DOLLAR, ""));
                    relAssignments.append(call.replaceAll("\\" + DOLLAR, ""));
                    aunitFormat.append("\t\t@cmd:{ ");
                    aunitFormat.append(call.replaceAll("\\" + DOLLAR, ""));
                    if (node instanceof Function) {
                        Func targetFun = null;
                        for (Func fun : module.getAllFunc()) {
                            String functionName = StringUtil.afterSubstring(fun.label, SLASH, false);
                            if (!fun.isPred && functionName.equals(((Function) node).getName())) {
                                targetFun = fun;
                                break;
                            }
                        }
                        assert targetFun != null;
                        List<ExprVar> skolemsList = new ArrayList<>();
                        skolems.forEach(skolemsList::add);
                        Expr functionCall = targetFun.call(skolemsList.toArray(new ExprVar[skolemsList.size()]));
                        A4TupleSet resultTupleSet = (A4TupleSet) sol.eval(functionCall);
                        StringBuilder value = new StringBuilder();
                        String prefix = "";
                        for (A4Tuple resultTuple : resultTupleSet) {
                            value.append(prefix).append(resultTuple.toString());
                            prefix = " + ";
                        }
                        String resultValuation = resultTupleSet.size() == 0 ? "none" : value.toString();
                        relAssignments.append(" = ").append(resultValuation.replaceAll("\\" + DOLLAR, ""));
                        aunitFormat.append(" = ").append(resultValuation.replaceAll("\\" + DOLLAR, ""));
                        cmd.append(" = ").append(resultValuation.replaceAll("\\" + DOLLAR, ""));
                    }
                    relAssignments.append(Names.NEW_LINE);
                    aunitFormat.append(" }");
                    aunitFormat.append(Names.NEW_LINE);
                }
            }

            body.append(sigDecls).append(relAssignments).append(endParens);
            body_val_cmd.append("\t").append(sigDecls).append(aunitFormat).append("\t").append(endParens);
            valuation.append(endParens);

            String build_val = sigDecls.toString() + valuation.toString();

            String testToEvaluate = "pred " + Names.TEST_PREFIX + "{" + body + "}" + Names.NEW_LINE + " run " + Names.TEST_PREFIX + " for " + scope;
            FileUtil.writeText(correctModelAsString + Names.NEW_LINE + testToEvaluate, Names.TMPT_FILE_PATH, false);
            CompModule originalModule = AlloyUtil.compileAlloyModule(Names.TMPT_FILE_PATH);
            assert originalModule != null;
            Command runCmd = originalModule.getAllCommands().stream().filter(command -> command.label.equals(Names.TEST_PREFIX)).findFirst().get();
            A4Solution ans = TranslateAlloyToKodkod.execute_command(A4Reporter.NOP, originalModule.getAllReachableSigs(), runCmd, options);
            int expect = ans.satisfiable() ? 1 : 0;
            return new AUnitTestCase(Names.TEST_PREFIX, body.toString(), body_val_cmd.toString(), scope, expect, cmd.toString(), build_val);
        } catch (Err er) {
            Context.logger.error("<Evaluator error occurred: " + er + ">");
        }
        return null;
    }

    public static void generateAndSaveAUnitTest(CompModule module, A4Solution sol, Node node, MutantGeneratorOpt opt) {
        AUnitTestCase test = translateToTest(module, sol, node, opt.getSpecialCase(), Names.EQUIV_ASSERTION_NAME, opt.getScope(), opt.getCorrectModelAsString(), opt.getOptions());
        opt.getTests().add(test);
    }

    private static String ignoreFirstKArity(A4Tuple a4Tuple, int k) {
        StringBuilder sb = new StringBuilder();
        for (int i = k; i < a4Tuple.arity(); i++) {
            if (i > k) {
                sb.append("->");
            }
            sb.append(a4Tuple.atom(i));
        }
        return sb.toString();
    }
}
