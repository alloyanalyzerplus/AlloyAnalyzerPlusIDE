package aunit.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import aunit.coverage.FunctionInvocation;
import edu.mit.csail.sdg.alloy4.ErrorWarning;
import edu.mit.csail.sdg.alloy4.Pair;
import edu.mit.csail.sdg.alloy4.Pos;
import edu.mit.csail.sdg.ast.Decl;
import edu.mit.csail.sdg.ast.Expr;
import edu.mit.csail.sdg.ast.ExprBinary;
import edu.mit.csail.sdg.ast.ExprCall;
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
import edu.mit.csail.sdg.translator.A4Options;

/** Helper class for executing AUnit tests. **/
public class AUnitExecution {

    /** List to find the explicit calls made for a given test. **/
    static ArrayList<FunctionInvocation>                 explicit_calls;
    /** List to find all the "Expr" objects that form the command for a test. **/
    static ArrayList<Expr>                               exprs_in_command;
    /**
     * Mapping of the function name to all functions invoked from that function.
     **/
    static HashMap<String,ArrayList<FunctionInvocation>> calledMap = new HashMap<String,ArrayList<FunctionInvocation>>();

    static HashMap<ExprCall,HashMap<String,String>>      domains   = new HashMap<ExprCall,HashMap<String,String>>();


    /**
     * Build up the different model versions - removedFacts: the version will all
     * facts removed from the model, so that every test can produce a satisfiable
     * instance to be displayed and/or analyzed for coverage. - executableOriginal:
     * removed () which are not handled by Pos. - original: unmodified model, so
     * that textual displays can be accurate to user written content.
     **/
    public static String[] buildModelVersions(CompModule world, String original, A4Options options) {
        String removedFacts = "";
        String executableOriginal = "";
        String opens = "";
        String[] modelVersions = new String[3];



        /**
         * Build 'executable' representation - this enables easy grabbing of the formula
         * and expressions in the format the used typed. Pos is of depending on the use
         * of parenthesis
         **/
        String[] iterator = original.split("\n");

        for (Pair<String,Expr> e : world.getAllFacts()) {
            int col_start = e.b.span().x;
            int col_end = e.b.span().x2;
            int line_start = e.b.span().y;
            int line_end = e.b.span().y2;

            //Replace '(' and ')' with '{' and '}' within function bodies to handle
            String first_line = iterator[line_start - 1].substring(col_start);
            first_line = first_line.replaceAll("\\(", "\\{");
            first_line = first_line.replaceAll("\\)", "\\}");
            iterator[line_start - 1] = iterator[line_start - 1].substring(0, col_start) + first_line;
            for (int i = line_start; i < line_end - 1; i++) {
                iterator[i] = iterator[i].replaceAll("\\(", "\\{");
                iterator[i] = iterator[i].replaceAll("\\)", "\\}");
            }
            if (line_start != line_end) {
                first_line = iterator[line_end - 1].substring(0, col_end);
                first_line = first_line.replaceAll("\\(", "\\{");
                first_line = first_line.replaceAll("\\)", "\\}");
                if (col_end < iterator[line_end - 1].length()) {
                    iterator[line_end - 1] = first_line + iterator[line_end - 1].substring(col_end);
                } else {
                    iterator[line_end - 1] = first_line;
                }
            }
        }

        for (Func func : world.getAllFunc()) {
            int col_start = func.getBody().pos.x;
            int col_end = func.pos().x2;
            int line_start = func.getBody().pos.y;
            int line_end = func.pos().y2;

            /**
             * Retain as is for the removed facts model. Facts do not get captured as
             * Function objects.
             **/
            if (line_start == line_end) {
                removedFacts += iterator[line_start - 1].substring(func.pos().x - 1, col_end) + "\n";
            } else {
                removedFacts += iterator[line_start - 1].substring(func.pos().x - 1) + "\n";
                for (int i = line_start; i < line_end - 1; i++) {
                    removedFacts += iterator[i] + "\n";
                }
                removedFacts += iterator[line_end - 1].substring(0, col_end) + "\n";
            }

            /** Handle predicate and assertions **/
            if (!(func.label.contains("run$") || func.label.contains("check$"))) {
                //Replace '(' and ')' with '{' and '}' within function bodies to handle
                String first_line = iterator[line_start - 1].substring(col_start);
                first_line = first_line.replaceAll("\\(", "\\{");
                first_line = first_line.replaceAll("\\)", "\\}");
                iterator[line_start - 1] = iterator[line_start - 1].substring(0, col_start) + first_line;
                for (int i = line_start; i < line_end - 1; i++) {
                    iterator[i] = iterator[i].replaceAll("\\(", "\\{");
                    iterator[i] = iterator[i].replaceAll("\\)", "\\}");
                }
                if (line_start != line_end) {
                    first_line = iterator[line_end - 1].substring(0, col_end);
                    first_line = first_line.replaceAll("\\(", "\\{");
                    first_line = first_line.replaceAll("\\)", "\\}");
                    if (col_end < iterator[line_end - 1].length()) {
                        iterator[line_end - 1] = first_line + iterator[line_end - 1].substring(col_end);
                    } else {
                        iterator[line_end - 1] = first_line;
                    }
                }
            }
        }

        /**
         * Make executable version 1. enables accurate Pos objects 2. makes all commands
         * similar with {} around the body in order to avoid in-lining
         **/
        for (String add : iterator) {
            executableOriginal += add + "\n";
            if (add.startsWith("open ")) {
                opens += add + "\n";
            }
        }

        /**
         * Handle signature declarations for the removedFacts model 1. remove any
         * multiplicity constraints 2. Remove abstract constraints
         **/
        String new_sig_decls = "";
        for (Sig sig : world.getAllReachableSigs()) {
            if (sig.pos.filename.equals(options.originalFilename)) { //if signature is actually defined here
                new_sig_decls += "sig " + sig.label.substring(sig.label.indexOf("/") + 1); //remove any facts imposed by the signature declaration

                //Does the signature extend another signature?
                if (sig.isTopLevel() != true) {
                    PrimSig primSig = (PrimSig) sig;
                    new_sig_decls += " extends " + primSig.parent.label.substring(sig.label.indexOf("/") + 1);
                }
                new_sig_decls += "{\n";

                String comma = "";
                for (Field rel : sig.getFields()) { //remove any facts imposed by the relation declaration - namely multiplicity constraints (one, lone, some)
                    new_sig_decls += comma + rel.label + ": ";
                    Decl relation = rel.decl();
                    Expr relationExpr = relation.expr;
                    if (relationExpr instanceof ExprUnary) {
                        ExprUnary unaryExp = (ExprUnary) relationExpr;
                        switch (unaryExp.op) {
                            case SOMEOF :
                            case LONEOF :
                            case ONEOF :
                            case SETOF :
                                Pos pos = unaryExp.sub.span();
                                new_sig_decls += "set " + iterator[pos.y - 1].substring(pos.x - 1, pos.x2);
                                comma = ",\n";
                                break;
                            default :
                                break;
                        }
                    } else if (relationExpr instanceof ExprBinary) {
                        ExprBinary binaryExp = (ExprBinary) relationExpr;
                        switch (binaryExp.op) {
                            case ARROW :
                            case ANY_ARROW_SOME :
                            case ANY_ARROW_ONE :
                            case ANY_ARROW_LONE :
                                Pos posLeft = binaryExp.left.span();
                                new_sig_decls += "set {" + iterator[posLeft.y - 1].substring(posLeft.x - 1, posLeft.x2) + "} -> {" + stripRels(binaryExp.right, iterator) + "}";
                                comma = ",\n";
                                break;
                            default :
                                break;
                        }
                    }
                }
                new_sig_decls += "\n}\n";
            }
        }

        /** Assign different model versions for return. **/
        modelVersions[0] = opens + new_sig_decls + "\n\n" + removedFacts;
        modelVersions[1] = executableOriginal;
        modelVersions[2] = original;
        return modelVersions;
    }

    public static String buildMuAlloyModelVersion(CompModule world, String original, A4Options options) {
        String removedTests = "";

        /**
         * Build 'executable' representation - this enables easy grabbing of the formula
         * and expressions in the format the used typed. Pos is of depending on the use
         * of parenthesis
         **/
        String[] iterator = original.split("\n");
        ArrayList<Integer> skip = new ArrayList<Integer>();

        for (Pair<String,Expr> e : world.getAllFacts()) {
            int col_start = e.b.span().x;
            int col_end = e.b.span().x2;
            int line_start = e.b.span().y;
            int line_end = e.b.span().y2;

            //Replace '(' and ')' with '{' and '}' within function bodies to handle
            String first_line = iterator[line_start - 1].substring(col_start);
            first_line = first_line.replaceAll("\\(", "\\{");
            first_line = first_line.replaceAll("\\)", "\\}");
            iterator[line_start - 1] = iterator[line_start - 1].substring(0, col_start) + first_line;
            for (int i = line_start; i < line_end - 1; i++) {
                iterator[i] = iterator[i].replaceAll("\\(", "\\{");
                iterator[i] = iterator[i].replaceAll("\\)", "\\}");
            }
            if (line_start != line_end) {
                first_line = iterator[line_end - 1].substring(0, col_end);
                first_line = first_line.replaceAll("\\(", "\\{");
                first_line = first_line.replaceAll("\\)", "\\}");
                if (col_end < iterator[line_end - 1].length()) {
                    iterator[line_end - 1] = first_line + iterator[line_end - 1].substring(col_end);
                } else {
                    iterator[line_end - 1] = first_line;
                }
            }
        }

        for (Func func : world.getAllFunc()) {
            int col_start = func.getBody().pos.x;
            int col_end = func.pos().x2;
            int line_start = func.getBody().pos.y;
            int line_end = func.pos().y2;

            /** Handle predicate and assertions **/
            if (func.isVal) {
                line_start = func.span().y;
                line_end = func.span().y2;

                skip.add(line_start - 1);
                for (int i = line_start; i < line_end + 1; i++) {
                    skip.add(i - 1);
                }

            } else if (!(func.label.contains("run$") || func.label.contains("check$"))) {
                //Replace '(' and ')' with '{' and '}' within function bodies to handle
                String first_line = iterator[line_start - 1].substring(col_start);
                first_line = first_line.replaceAll("\\(", "\\{");
                first_line = first_line.replaceAll("\\)", "\\}");
                iterator[line_start - 1] = iterator[line_start - 1].substring(0, col_start) + first_line;
                for (int i = line_start; i < line_end - 1; i++) {
                    iterator[i] = iterator[i].replaceAll("\\(", "\\{");
                    iterator[i] = iterator[i].replaceAll("\\)", "\\}");
                }
                if (line_start != line_end) {
                    first_line = iterator[line_end - 1].substring(0, col_end);
                    first_line = first_line.replaceAll("\\(", "\\{");
                    first_line = first_line.replaceAll("\\)", "\\}");
                    if (col_end < iterator[line_end - 1].length()) {
                        iterator[line_end - 1] = first_line + iterator[line_end - 1].substring(col_end);
                    } else {
                        iterator[line_end - 1] = first_line;
                    }
                }
            }
        }

        for (Test test : world.getAllTests()) {
            int line_start = test.getOrigCmd().span().y;
            int line_end = test.getOrigCmd().span().y2;
            skip.add(line_start - 1);
            for (int i = line_start; i < line_end + 1; i++) {
                skip.add(i);
            }
        }

        /**
         * Make executable version 1. enables accurate Pos objects 2. makes all commands
         * similar with {} around the body in order to avoid in-lining
         **/
        for (int i = 0; i < iterator.length; i++) {
            if (!skip.contains(i))
                removedTests += iterator[i] + "\n";
        }

        return removedTests;
    }


    public static String stripRels(Expr exp, String[] iterator) {
        if (exp instanceof ExprUnary) {
            ExprUnary unaryExp = (ExprUnary) exp;
            switch (unaryExp.op) {
                case SOMEOF :
                case LONEOF :
                case ONEOF :
                case SETOF :
                    Pos pos = unaryExp.sub.pos;
                    return "set " + iterator[pos.y - 1].substring(pos.x - 1, pos.x2);
                default :
                    pos = unaryExp.pos;
                    return iterator[pos.y - 1].substring(pos.x - 1, pos.x2);
            }
        } else if (exp instanceof ExprBinary) {
            ExprBinary binaryExp = (ExprBinary) exp;
            switch (binaryExp.op) {
                case ARROW :
                case ANY_ARROW_SOME :
                case ANY_ARROW_ONE :
                case ANY_ARROW_LONE :
                    Pos posLeft = binaryExp.left.pos;
                    return "set " + iterator[posLeft.y - 1].substring(posLeft.x - 1, posLeft.x2) + "-> " + stripRels(binaryExp.right, iterator);
                default :
                    Pos pos = binaryExp.span();
                    return iterator[pos.y - 1].substring(pos.x - 1, pos.x2);
            }
        }
        return null;
    }

    public static HashMap<String,ArrayList<FunctionInvocation>> buildCalledMap(Module world, HashMap<String,ArrayList<String>> func_to_parameters, HashMap<String,Func> name_to_func) {
        calledMap = new HashMap<String,ArrayList<FunctionInvocation>>();
        for (Func func : world.getAllFunc()) {
            String funcName = func.label;
            String[] removeOrigin = funcName.split("/");
            if (removeOrigin.length > 1) {
                funcName = removeOrigin[1];
            }
            if (!funcName.contains("run$") && !funcName.contains("check$")) {
                calledMap.put(funcName, new ArrayList<FunctionInvocation>());
                for (ExprCall exp : func.getBody().findAllFunctionsWithCalls()) {
                    FunctionInvocation func_invocation = null;
                    String called_pred = exp.fun.toString(); //isolate the name of the function invoked
                    called_pred = called_pred.substring(called_pred.indexOf("/") + 1);
                    if (!exp.fun.isVal) {
                        func_invocation = new FunctionInvocation(called_pred, false, exp);
                    } else {
                        func_invocation = new FunctionInvocation(called_pred, true, exp);
                    }
                    int counter = 0;
                    for (Expr value : exp.args) {
                        if (value instanceof ExprUnary) {
                            ExprUnary expU = (ExprUnary) value;
                            if (expU.sub instanceof ExprVar) {
                                ExprVar expV = (ExprVar) expU.sub;
                                Collection<ErrorWarning> warnings = null;
                                String param_value = value.toString();
                                param_value = "" + expV.resolve_as_set(warnings);
                                func_invocation.addParameter(func_to_parameters.get(called_pred).get(counter), param_value);
                            } else if (expU.sub instanceof Sig.PrimSig) {
                                String param_value = value.toString();
                                func_invocation.addParameter(func_to_parameters.get(called_pred).get(counter), param_value);
                            }
                        } else if (value instanceof ExprBinary) {
                            String param_value = value.toString();
                            func_invocation.addParameter(func_to_parameters.get(called_pred).get(counter), param_value);
                        }
                        counter++;
                    }
                    func_invocation.addDomains(domains.get(exp));

                    calledMap.get(funcName).add(func_invocation);
                }
            }
        }
        return calledMap;
    }

    public static void addDomains(Expr e, HashMap<String,String> outerdomains) {
        /* Used to flag which formulas are primitive booleans and thus add coverage */

        if (e instanceof ExprBinary) {
            ExprBinary binExp = (ExprBinary) e;
            if (!(binExp.op == ExprBinary.Op.JOIN)) {
                addDomains(binExp.left, outerdomains);
                addDomains(binExp.right, outerdomains);
            }
        } else if (e instanceof ExprQt) {
            ExprQt quantFormula = (ExprQt) e;

            for (int decl = 0; decl < quantFormula.decls.size(); decl++) {
                for (int v = 0; v < quantFormula.decls.get(decl).names.size(); v++) {
                    String var = quantFormula.decls.get(decl).names.get(v).label;
                    ExprUnary exprUnary = (ExprUnary) quantFormula.decls.get(decl).expr;
                    Expr domainExpr = exprUnary.sub;
                    String domain = domainExpr.toString();//getText(domainExpr);
                    outerdomains.put(var, domain);
                }
            }

            addDomains(quantFormula.sub, outerdomains);
        } else if (e instanceof ExprUnary) {
            ExprUnary unExp = (ExprUnary) e;
            addDomains(unExp.sub, outerdomains);
        } else if (e instanceof ExprList) {
            ExprList listExp = (ExprList) e;
            for (Expr e1 : listExp.args) {
                addDomains(e1, outerdomains);
            }
        } else if (e instanceof ExprCall) {
            ExprCall call = (ExprCall) e;
            domains.put(call, outerdomains);
        } else if (e instanceof ExprITE) {
            ExprITE exITE = (ExprITE) e;
            addDomains(exITE.cond, outerdomains);
            addDomains(exITE.left, outerdomains);
            addDomains(exITE.right, outerdomains);
        } else if (e instanceof ExprLet) {
            ExprLet exLet = (ExprLet) e;
            outerdomains.put(exLet.var.label, exLet.expr.toString());
            addDomains(exLet.sub, outerdomains);
        }
    }

    public static void findExplicitCalls(Expr e, HashMap<String,ArrayList<String>> func_to_parameters, HashMap<String,String> outerdomains) {
        /* Used to flag which formulas are primitive booleans and thus add coverage */

        if (e instanceof ExprBinary) {
            ExprBinary binExp = (ExprBinary) e;
            if (!(binExp.op == ExprBinary.Op.JOIN)) {
                findExplicitCalls(binExp.left, func_to_parameters, outerdomains);
                findExplicitCalls(binExp.right, func_to_parameters, outerdomains);
            }
        } else if (e instanceof ExprQt) {
            ExprQt quantFormula = (ExprQt) e;

            for (int decl = 0; decl < quantFormula.decls.size(); decl++) {
                for (int v = 0; v < quantFormula.decls.get(decl).names.size(); v++) {
                    String var = quantFormula.decls.get(decl).names.get(v).label;
                    ExprUnary exprUnary = (ExprUnary) quantFormula.decls.get(decl).expr;
                    Expr domainExpr = exprUnary.sub;
                    String domain = domainExpr.toString();//getText(domainExpr);
                    outerdomains.put(var, domain);
                }
            }

            findExplicitCalls(quantFormula.sub, func_to_parameters, outerdomains);
        } else if (e instanceof ExprUnary) {
            ExprUnary unExp = (ExprUnary) e;
            findExplicitCalls(unExp.sub, func_to_parameters, outerdomains);
        } else if (e instanceof ExprList) {
            ExprList listExp = (ExprList) e;
            for (Expr e1 : listExp.args) {
                findExplicitCalls(e1, func_to_parameters, outerdomains);
            }
        } else if (e instanceof ExprCall) {
            ExprCall callExp = (ExprCall) e;

            FunctionInvocation func_invocation = null;
            String func_name = callExp.fun.toString(); //isolate the name of the function invoked
            func_name = func_name.substring(func_name.indexOf("/") + 1);
            if (!callExp.fun.isVal) {
                func_invocation = new FunctionInvocation(func_name, false, callExp);
            } else {
                func_invocation = new FunctionInvocation(func_name, true, callExp);
            }

            //Handle parameters as needed for coverage - the name of the parameter and the direct substitution
            int counter = 0;
            for (Expr value : callExp.args) {
                String param_value = value.toString();
                param_value = param_value.substring(param_value.lastIndexOf("/") + 1);
                func_invocation.addParameter(func_to_parameters.get(func_name).get(counter), param_value);
                counter++;
            }
            func_invocation.addDomains(outerdomains);
            explicit_calls.add(func_invocation);
        } else if (e instanceof ExprITE) {
            ExprITE exITE = (ExprITE) e;
            findExplicitCalls(exITE.cond, func_to_parameters, outerdomains);
            findExplicitCalls(exITE.left, func_to_parameters, outerdomains);
            findExplicitCalls(exITE.right, func_to_parameters, outerdomains);
        } else if (e instanceof ExprLet) {
            ExprLet exLet = (ExprLet) e;
            outerdomains.put(exLet.var.label, exLet.expr.toString());
            findExplicitCalls(exLet.sub, func_to_parameters, outerdomains);
        }

    }

    public static void resetExprsInCommand() {
        exprs_in_command = new ArrayList<Expr>();
    }

    public static ArrayList<Expr> getExprsInCommand() {
        return exprs_in_command;
    }

    public static void resetExplicitCalls() {
        explicit_calls = new ArrayList<FunctionInvocation>();
    }

    public static ArrayList<FunctionInvocation> getExplicitCalls() {
        return explicit_calls;
    }
}
