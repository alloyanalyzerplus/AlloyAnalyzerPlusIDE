package parser.ast.nodes;

import edu.mit.csail.sdg.ast.ExprVar;
import parser.ast.visitor.GenericVisitor;
import parser.ast.visitor.VoidVisitor;

public class VarExpr extends ExprOrFormula {

  private static final long serialVersionUID = 5221151175082744266L;
  protected String name;

  public VarExpr(Node parent, ExprVar expr) {
    super(parent, expr.type());
    addToNodeMap(expr, this);
    this.name = expr.label;
  }

  public VarExpr(Node parent) {
    super(parent);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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
