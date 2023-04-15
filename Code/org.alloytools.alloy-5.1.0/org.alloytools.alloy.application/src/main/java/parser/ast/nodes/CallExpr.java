package parser.ast.nodes;

import edu.mit.csail.sdg.ast.ExprCall;
import parser.ast.visitor.GenericVisitor;
import parser.ast.visitor.VoidVisitor;

public class CallExpr extends Call {

  private static final long serialVersionUID = -3686193573915011703L;

  public CallExpr(Node parent, ExprCall expr) {
    super(parent, expr);
  }

  public CallExpr(Node parent) {
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
