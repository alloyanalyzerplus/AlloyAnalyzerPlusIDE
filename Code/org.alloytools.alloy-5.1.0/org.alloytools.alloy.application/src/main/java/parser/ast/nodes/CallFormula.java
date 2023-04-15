package parser.ast.nodes;

import edu.mit.csail.sdg.ast.ExprCall;
import parser.ast.visitor.GenericVisitor;
import parser.ast.visitor.VoidVisitor;

public class CallFormula extends Call {

  private static final long serialVersionUID = 1219753376147755808L;

  public CallFormula(Node parent, ExprCall expr) {
    super(parent, expr);
  }

  public CallFormula(Node parent) {
    super(parent);
  }

  @Override
  public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
    return v.visit(this, arg);
  }

  @Override
  public <A> void accept(VoidVisitor<A> v, A arg) {
    v.visit(this, arg);
  }
}
