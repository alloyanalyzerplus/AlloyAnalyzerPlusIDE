package aunit.coverage;

import java.util.ArrayList;
import java.util.HashMap;

import edu.mit.csail.sdg.ast.ExprCall;

public class FunctionInvocation {

    HashMap<String,String>        parameters;
    ArrayList<String>             parameterOrder;
    String                        command;
    public final boolean          isVal;
    public HashMap<String,String> outerdomains;
    public ExprCall               exprCall;

    public FunctionInvocation(String command) {
        this.command = command;
        isVal = false;
        parameters = new HashMap<String,String>();
        parameterOrder = new ArrayList<String>();
        outerdomains = new HashMap<String,String>();
    }

    public FunctionInvocation(String command, boolean isVal, ExprCall exprCall) {
        this.command = command;
        this.isVal = isVal;
        this.exprCall = exprCall;
        parameters = new HashMap<String,String>();
        parameterOrder = new ArrayList<String>();
        outerdomains = new HashMap<String,String>();
    }


    public void addParameter(String parameter, String value) {
        parameters.put(parameter, value);
        parameterOrder.add(parameter);
    }

    public String getCommand() {
        return command;
    }

    public HashMap<String,String> getParameters() {
        return parameters;
    }

    public void updateParameter(String parameter, String value) {
        parameters.put(parameter, value);
    }

    public ArrayList<String> getParameterOrder() {
        return parameterOrder;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void addDomains(HashMap<String,String> outerdomains2) {
        for (String key : outerdomains2.keySet()) {
            outerdomains.put(key, outerdomains2.get(key));
        }
    }
}
