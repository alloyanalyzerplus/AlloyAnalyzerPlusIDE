package parser.ast.nodes;

import edu.mit.csail.sdg.ast.ExprITE;
import parser.ast.visitor.GenericVisitor;
import parser.ast.visitor.VoidVisitor;

public class ITEExpr extends ITEExprOrFormula {

  private static final long serialVersionUID = -905447276186376514L;

  public ITEExpr(Node parent, ExprITE expr) {
    super(parent, expr);
  }

  public ITEExpr(Node parent) {
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
