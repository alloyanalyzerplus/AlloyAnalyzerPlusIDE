package parser.ast.nodes;

import static parser.util.AlloyUtil.findNodeWithType;

import alloyfl.coverage.visitor.CollectNodeOfSameTypeVisitor;
import edu.mit.csail.sdg.ast.ExprLet;
import parser.ast.visitor.GenericVisitor;
import parser.ast.visitor.VoidVisitor;

public class LetExpr extends ExprOrFormula {

  private static final long serialVersionUID = 7819446706313572084L;
  protected ExprOrFormula var;
  protected ExprOrFormula bound;
  protected Body body;

  public LetExpr(Node parent, ExprLet expr) {
    super(parent, expr.type());
    addToNodeMap(expr, this);
    this.var = buildExpression(this, expr.var);
    this.bound = buildExpression(this, expr.expr);
    this.body = new Body(this, expr.sub);
    // Add children.
    children.add(var);
    children.add(bound);
    children.add(body);
  }

  public LetExpr(Node parent) {
    super(parent);
  }

  public ExprOrFormula getVar() {
    return var;
  }

  /**
   * This method returns the let variable of the type VarExpr.  Note that if we do not invoke this
   * method, then it's possible that the variable is wrapped with NOOP unary expression.
   */
  public VarExpr getRealVar() {
    // Let expression only contains a single variable name.
    return (VarExpr) findNodeWithType(var, VarExpr.class).iterator().next();
  }

  public void setVar(ExprOrFormula var) {
    this.var = var;
  }

  public ExprOrFormula getBound() {
    return bound;
  }

  public void setBound(ExprOrFormula bound) {
    this.bound = bound;
  }

  public Body getBody() {
    return body;
  }

  public void setBody(Body body) {
    this.body = body;
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
