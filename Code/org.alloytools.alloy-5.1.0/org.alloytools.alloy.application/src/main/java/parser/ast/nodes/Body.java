package parser.ast.nodes;


import edu.mit.csail.sdg.ast.Expr;
import parser.ast.visitor.GenericVisitor;
import parser.ast.visitor.VoidVisitor;

public class Body extends Node {

  private static final long serialVersionUID = -8808772204350739177L;
  protected ExprOrFormula bodyExpr;

  public Body(Node parent, Expr expr) {
    super(parent);
    addToNodeMap(expr, this);
    this.bodyExpr = ExprOrFormula.buildExpression(this, expr);
    // Add children.
    children.add(bodyExpr);
  }

  public Body(Node parent) {
    super(parent);
  }

  public ExprOrFormula getBodyExpr() {
    return bodyExpr;
  }

  public void setBodyExpr(ExprOrFormula bodyExpr) {
    this.bodyExpr = bodyExpr;
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
