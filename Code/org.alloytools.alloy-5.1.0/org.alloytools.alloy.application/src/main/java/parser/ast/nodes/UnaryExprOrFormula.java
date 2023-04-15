package parser.ast.nodes;


import edu.mit.csail.sdg.ast.ExprUnary;
import edu.mit.csail.sdg.ast.Type;

public abstract class UnaryExprOrFormula extends ExprOrFormula {

  private static final long serialVersionUID = -6971128717998342380L;
  protected ExprOrFormula sub;

  public UnaryExprOrFormula(Node parent, ExprUnary expr) {
    super(parent, expr.type());
    addToNodeMap(expr, this);
    this.sub = buildExpression(this, expr.sub);
    // Add children.
    children.add(sub);
  }

  public UnaryExprOrFormula(Node parent, Type type, ExprOrFormula sub) {
    super(parent, type);
    this.sub = sub;
    // Add children.
    children.add(sub);
  }

  public UnaryExprOrFormula(Node parent) {
    super(parent);
  }

  public ExprOrFormula getSub() {
    return sub;
  }

  /**
   * This setter is used quite often to create a NOOP unary expression, which serves as the wrapper
   * of another node.  So we update the children field here to avoid changing too many other
   * places.
   */
  public void setSub(ExprOrFormula sub) {
    if (children != null) {
      children.remove(this.sub);
    }
    this.sub = sub;
    if (children != null) {
      children.add(sub);
    }
  }
}
