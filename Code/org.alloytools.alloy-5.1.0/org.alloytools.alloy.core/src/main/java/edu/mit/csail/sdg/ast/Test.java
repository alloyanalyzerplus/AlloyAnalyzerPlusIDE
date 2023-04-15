package edu.mit.csail.sdg.ast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

    /**
     * The command written by the Alloy end-user that is prefaced with @test to
     * indicate an AUnit test case.
     **/
    Command             exe_test;
    Command             unresolved;

    /**
     * The (unique) id from this test case - the label of the alloy command prefaced
     * with @test.
     **/
    public final String id;

    /**
     * Strings to make pretty printing for (1) the AUnit command and (2) the AUnit
     * valuation (with any command removed).
     **/
    String              prty_print_cmd;
    String              prty_print_val;
    String              prty_print_val_with_cmd;

    /** The "val" alloy paragraph tied to this test case. **/
    Func                valuation;
    Expr                cmd;
    /**
     * List of all the formulas that make up the test case's "command" -- not the
     * same as the Alloy command used to execute the test case.
     **/
    ArrayList<Expr>     cmd_forms;

    public Test(Command exe_test, String id) {
        this.exe_test = exe_test;
        unresolved = exe_test;
        this.id = id;
        cmd_forms = new ArrayList<Expr>();
        valuation = null;
        prty_print_cmd = "";
        prty_print_val = "";
        prty_print_val_with_cmd = "";
    }

    /**
     * The valuation will be a func invoked by the command formula - directly or
     * indirectly - that is defined using the "val" keyword.
     **/
    public boolean establishValuation(Module world) {
        for (Func potential_val : exe_test.formula.findAllFunctions()) {
            if (potential_val.isVal) {
                if (valuation == null) {
                    valuation = potential_val;
                } else {
                    return false;
                }
            }
        }

        if (valuation == null) {
            return false;
        }
        return true;
    }

    public void setCmd(Expr e) {
        cmd = e;
    }

    public void establishCommand(Module world, String[] text, HashSet<String> preds) {
        /**
         * Command can be: cmd: {...} within valuation and/or any part of exe_test's
         * formula that is not a valuation
         **/
        getExprCmds(valuation.getBody());
        createValString(text);
        getExprCmdsInExeTest(cmd.deNOP(), preds);
        createCmdString(text);
    }

    public void createCmdString(String[] text) {
        //prty_print_cmd = "";
        String and = "";
        for (Expr cmd : cmd_forms) {
            if (cmd instanceof ExprUnary) {
                ExprUnary unExp = (ExprUnary) cmd;
                if (unExp.op == ExprUnary.Op.CMD) {
                    String cmd_text = getText(cmd, text);
                    prty_print_cmd += and + cmd_text.substring(1, cmd_text.length() - 1).trim();
                    and = " and ";
                } else {
                    prty_print_cmd += and + getText(cmd, text);
                    and = " and ";
                }
            } else {
                prty_print_cmd += and + getText(cmd, text);
                and = " and ";
            }
        }
    }

    public void createValString(String[] text) {
        prty_print_val = getText(valuation.getBody(), text);
        prty_print_val_with_cmd = getText(valuation.getBody(), text);
        prty_print_val_with_cmd = prty_print_val_with_cmd.replaceAll("@cmd:", "");
        //replace formula with blank spaces
        for (Expr form : cmd_forms) {
            String temp = removeSpecialChars(getText(form, text));
            temp = "(@cmd:)(\\s*)(" + temp + ")";
            Pattern p = Pattern.compile(temp);
            Matcher m = p.matcher(prty_print_val);
            prty_print_val = m.replaceAll("");
        }
    }

    /** To properly handle the regex expression. **/
    public String removeSpecialChars(String input) {
        String temp = "";
        for (char c : input.toCharArray()) {
            switch (c) {
                case '{' :
                    temp += "\\{";
                    break;
                case '}' :
                    temp += "\\}";
                    break;
                case '[' :
                    temp += "\\[";
                    break;
                case ']' :
                    temp += "\\]";
                    break;
                case '(' :
                    temp += "\\(";
                    break;
                case ')' :
                    temp += "\\)";
                    break;
                case '+' :
                    temp += "\\+";
                    break;
                case '*' :
                    temp += "\\*";
                    break;
                default :
                    temp += c;
                    break;
            }
        }
        return temp;
    }

    public void getExprCmdsInExeTest(Expr e, HashSet<String> preds) {
        if (e instanceof ExprList) {
            ExprList listExp = (ExprList) e;
            for (Expr e1 : listExp.args) {
                if (e1 instanceof ExprCall) {
                    ExprCall exprCall = (ExprCall) e1;
                    if (!exprCall.fun.isVal) {
                        cmd_forms.add(e1);
                    }
                } else if (e1 instanceof ExprUnary) {
                    ExprUnary exprUnary = (ExprUnary) e1;
                    if (exprUnary.op != ExprUnary.Op.NOOP) {
                        cmd_forms.add(e1);
                    }
                } else if (e1 instanceof ExprVar) {
                    ExprVar exprVar = (ExprVar) e1;
                    if (preds.contains(exprVar.label)) {
                        cmd_forms.add(e1);
                    }
                } else {
                    cmd_forms.add(e1);
                }
            }
        }
    }

    public void getExprCmds(Expr e) {
        /**
         * Expression classes that do not lead to futher recursive calls: -ExprBad,
         * -ExprBadCall, -ExprBadJoin, -ExprCall, -ExprChoice -ExprConstant,
         * -ExprCustom, -ExprHasName, -ExprVar
         **/
        if (e instanceof ExprBinary) {
            ExprBinary binExp = (ExprBinary) e;
            getExprCmds(binExp.left);
            getExprCmds(binExp.right);
        } else if (e instanceof ExprITE) {
            ExprITE exITE = (ExprITE) e;
            getExprCmds(exITE.cond);
            getExprCmds(exITE.left);
            getExprCmds(exITE.right);
        } else if (e instanceof ExprLet) {
            ExprLet exLet = (ExprLet) e;
            getExprCmds(exLet.sub);
        } else if (e instanceof ExprList) {
            ExprList listExp = (ExprList) e;
            for (Expr e1 : listExp.args) {
                getExprCmds(e1);
            }
        } else if (e instanceof ExprQt) {
            ExprQt quantFormula = (ExprQt) e;
            getExprCmds(quantFormula.sub);
        } else if (e instanceof ExprUnary) {
            ExprUnary unExp = (ExprUnary) e;
            if (unExp.op == ExprUnary.Op.CMD) {
                cmd_forms.add(e);
            }
            getExprCmds(unExp.sub);
        }
    }

    public String getText(Expr exp, String[] text) {
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

    public Command getTestCmd() {
        return exe_test;
    }

    public Command getOrigCmd() {
        return unresolved;
    }

    public Command getUnresolvedTestCmd() {
        return unresolved;
    }

    public String prettyPrintCommand() {
        return prty_print_cmd;
    }

    public String prettyPrintValuation() {
        return prty_print_val;
    }

    public String prettyPrintValuationWithCmd() {
        return prty_print_val_with_cmd;
    }

    public Func getValuationFunc() {
        return valuation;
    }

    public ArrayList<Expr> getCommand() {
        return cmd_forms;
    }

    public void setExeTest(Command cmd) {
        exe_test = cmd;
    }

    public void setValuation(Func valuation) {
        this.valuation = valuation;
    }
}
