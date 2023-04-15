package aunit.coverage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import edu.mit.csail.sdg.ast.Expr;
import edu.mit.csail.sdg.ast.ExprCall;
import edu.mit.csail.sdg.ast.Module;
import edu.mit.csail.sdg.ast.Sig;

public class TestCase {

    String                                      valuation;
    String                                      val_func_name;
    String                                      valuation_xml;
    String                                      print_valuation;
    int                                         numCommands;
    public ArrayList<FunctionInvocation>        explicitCommands;
    public HashMap<ExprCall,FunctionInvocation> eCommands;
    ArrayList<FunctionInvocation>               implicitCalls;
    String                                      command;
    public int                                  expect;
    public String                               call;

    public TestCase() {
        explicitCommands = new ArrayList<FunctionInvocation>();
        implicitCalls = new ArrayList<FunctionInvocation>();
        eCommands = new HashMap<ExprCall,FunctionInvocation>();
        numCommands = 0;
        valuation = "";
        val_func_name = "";
        valuation_xml = "";
        command = "Facts";
    }

    public TestCase(String val) {
        explicitCommands = new ArrayList<FunctionInvocation>();
        implicitCalls = new ArrayList<FunctionInvocation>();
        eCommands = new HashMap<ExprCall,FunctionInvocation>();
        valuation = val;
        numCommands = 0;
        val_func_name = "";
        valuation_xml = "";
        command = "Facts";
    }


    public TestCase(String val, String com) {
        explicitCommands = new ArrayList<FunctionInvocation>();
        implicitCalls = new ArrayList<FunctionInvocation>();
        eCommands = new HashMap<ExprCall,FunctionInvocation>();
        valuation = val;
        explicitCommands.add(new FunctionInvocation(com));
        numCommands = 0;
        val_func_name = "";
        valuation_xml = "";
        command = "Facts " + com;
    }

    public void setValuation(String valuation) {
        this.valuation = valuation;
    }

    public String getValuation() {
        return valuation;
    }

    public void setValuationXML(String valuation_xml) {
        this.valuation_xml = valuation_xml;
    }

    public String getValuationXML() {
        return valuation_xml;
    }

    public void setValuationFunc(String val_func_name) {
        this.val_func_name = val_func_name;
    }

    public String getValuationFunc() {
        return val_func_name;
    }

    public void addImplicitCall(String function, ArrayList<String> parameterNames, ArrayList<String> parameterValues) {
        FunctionInvocation functionCall = new FunctionInvocation(function);
        for (int i = 0; i < parameterNames.size(); i++) {
            functionCall.addParameter(parameterNames.get(i), parameterValues.get(i));
        }
        implicitCalls.add(functionCall);
    }

    public void addImplicitCall(FunctionInvocation functionCall) {
        implicitCalls.add(functionCall);
    }

    public void addExplicitCall(String function, ArrayList<String> parameterNames, ArrayList<String> parameterValues) {
        FunctionInvocation functionCall = new FunctionInvocation(function);
        for (int i = 0; i < parameterNames.size(); i++) {
            functionCall.addParameter(parameterNames.get(i), parameterValues.get(i));
        }
        explicitCommands.add(functionCall);
        command += " and " + function;
    }

    public void addExplicitCall(FunctionInvocation functionCall) {
        explicitCommands.add(functionCall);
        command += " and " + functionCall.getCommand();
        eCommands.put(functionCall.exprCall, functionCall);
    }

    public String getCommand() {
        return command;
    }

    public int numCommands() {
        return numCommands;
    }

    public String formPrintableTC(Module world) {
        HashSet<String> notExtended = new HashSet<String>();
        HashMap<String,String> extended = new HashMap<String,String>();
        for (Sig sig : world.getAllReachableSigs()) {
            if (sig.isTopLevel())
                notExtended.add(sig.label);
            else {
                for (Sig sig_parent : world.getAllReachableSigs()) {
                    if (sig.isSameOrDescendentOf(sig_parent)) {
                        if (!sig.label.equals(sig_parent.label))
                            extended.put(sig.label, sig_parent.label);
                    }
                }

            }
        }

        String command = "";
        ArrayList<String> parameterOrder = new ArrayList<String>();
        HashMap<String,String> parameters = new HashMap<String,String>();

        if (explicitCommands.size() != 0) {
            command = explicitCommands.get(0).getCommand();
            parameterOrder = explicitCommands.get(0).getParameterOrder();
            parameters = explicitCommands.get(0).getParameters();
        }

        String test = "";
        String[] lines = valuation.split("\n");
        String sigValues = "";
        String noSig = "";
        String disjSig = "";
        String relValues = "";
        String noRel = "";
        String and = "\n\t\t";

        for (String line : lines) {
            if (line.contains("this/")) {
                if (line.contains("<:")) { //relation
                    String[] temp = line.split("=");
                    String relation = temp[0].substring(temp[0].indexOf(":") + 1);
                    if (temp[1].equals("{}")) {
                        noRel += and + "no " + relation;
                    } else {
                        String vals = temp[1];
                        vals = vals.substring(1, vals.length() - 1);
                        vals = vals.replaceAll("\\$", "");
                        vals = vals.replaceAll(",", " +");
                        relValues += and + relation + " = " + vals;
                    }
                } else { //sig
                    String[] temp = line.split("=");
                    String sig = temp[0].substring(5);
                    if (temp[1].equals("{}")) {
                        noSig += "\n\t\tno " + sig;
                    } else {
                        String vals = temp[1];
                        vals = vals.substring(1, vals.length() - 1);
                        vals = vals.replaceAll("\\$", "");
                        vals = vals.replaceAll(",", " +");
                        sigValues += and + sig + " = " + vals;

                        //if(notExtended.contains("this/" + sig)){
                        String disjVals = temp[1];
                        disjVals = disjVals.substring(1, disjVals.length() - 1);
                        disjVals = disjVals.replaceAll("\\$", "");
                        disjSig += "some disj " + disjVals + " : " + sig + " | ";
                        //	}	
                    }
                }
                and = "\n\t\t";
            } else {
                if (line.contains("<:")) { //relation
                    String relation = line.substring(0, line.indexOf("/") + 1);
                    String[] temp = line.split("<:");
                    relation += temp[1].substring(0, temp[1].indexOf("=")).toLowerCase();
                    String vals = temp[1].substring(temp[1].indexOf("{") + 1, temp[1].indexOf("}"));
                    String[] getValues = vals.split(", ");
                    vals = "";
                    for (int i = 0; i < getValues.length; i++) {
                        String[] narrow = getValues[i].split("->");
                        for (int j = 1; j < narrow.length; j++) {
                            vals += narrow[j];
                            if (j < narrow.length - 1) {
                                vals += "->";
                            }
                        }
                        if (i < getValues.length - 1) {
                            vals += " + ";
                        }
                    }
                    vals = vals.replaceAll("\\$", "");
                    ;
                    relValues += and + relation + " = " + vals;
                }
            }
        }

        String params = "";
        String comma = "";
        for (String param : parameterOrder) {
            String temp = parameters.get(param);
            if (temp.contains("/")) {
                temp = temp.substring(temp.indexOf("/") + 1);
                params += comma + temp;
            } else
                params += comma + parameters.get(param);
            comma = ",";
        }
        if (!command.equals("")) {
            String com = and + "@cmd:{ " + call + command + "[" + params + "] }";
            test = "\t" + disjSig + " { " + sigValues + noSig + relValues + noRel + com + " } ";
            test = test.replaceFirst(" and ", " ");
            print_valuation = test + "\n";
            return test + "\n";
        }

        test = "\t" + disjSig + " { " + sigValues + noSig + relValues + noRel + " } ";
        test = test.replaceFirst(" and ", " ");
        print_valuation = test + "\n";
        return test + "\n";
    }

    public String getExecutableCommand() {
        String command = "";
        ArrayList<String> parameterOrder = new ArrayList<String>();
        HashMap<String,String> parameters = new HashMap<String,String>();
        if (explicitCommands.size() != 0) {
            command = explicitCommands.get(0).getCommand();
            parameterOrder = explicitCommands.get(0).getParameterOrder();
            parameters = explicitCommands.get(0).getParameters();
        } else {
            return "";
        }

        String params = "";
        String comma = "";
        for (String param : parameterOrder) {
            String temp = parameters.get(param);
            if (temp.contains("/")) {
                temp = temp.substring(temp.indexOf("/") + 1);
                params += comma + temp;
            } else
                params += comma + parameters.get(param);
            comma = ",";
        }

        return command + "[" + params + "]";
    }

    public void removeRecursiveCalls(HashSet<Expr> sig_fact_exprs) {
        // TODO Auto-generated method stub
        ArrayList<FunctionInvocation> new_explicitCommands = new ArrayList<FunctionInvocation>();
        for (FunctionInvocation call : explicitCommands) {
            if (!sig_fact_exprs.contains(call.exprCall)) {
                new_explicitCommands.add(call);
            }
        }
        explicitCommands.clear();
        explicitCommands.addAll(new_explicitCommands);

    }
}
