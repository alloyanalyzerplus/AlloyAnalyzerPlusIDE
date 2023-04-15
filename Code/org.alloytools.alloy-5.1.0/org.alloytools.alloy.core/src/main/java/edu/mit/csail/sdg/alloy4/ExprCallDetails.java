package edu.mit.csail.sdg.alloy4;

import java.util.ArrayList;

import edu.mit.csail.sdg.ast.ExprCall;

public class ExprCallDetails {

    public ExprCall          exprCall;
    public ArrayList<Object> parameters;

    public ExprCallDetails(ExprCall exprCall) {
        this.exprCall = exprCall;
        parameters = new ArrayList<Object>();
    }

    public void addParameter(Object o) {
        parameters.add(o);
    }

}
