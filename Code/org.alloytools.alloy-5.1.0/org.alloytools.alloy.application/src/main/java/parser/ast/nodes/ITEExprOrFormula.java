package parser.ast.nodes;


import edu.mit.csail.sdg.ast.ExprITE;

public abstract class ITEExprOrFormula extends ExprOrFormula {

  private static final long serialVersionUID = 4580695871434919456L;
  protected ExprOrFormula condition;
  protected ExprOrFormula thenClause;
  protected ExprOrFormula elseClause;

  public ITEExprOrFormula(Node parent, ExprITE expr) {
    super(parent, expr.type());
    addToNodeMap(expr, this);
    this.condition = buildExpression(this, expr.cond);
    this.thenClause = buildExpression(this, expr.left);
    this.elseClause = buildExpression(this, expr.right);
    // Add children.
    children.add(condition);
    children.add(thenClause);
    children.add(elseClause);
  }

  public ITEExprOrFormula(Node parent) {
    super(parent);
  }

  public ExprOrFormula getCondition() {
    return condition;
  }

  public void setCondition(ExprOrFormula condition) {
    this.condition = condition;
  }

  public ExprOrFormula getThenClause() {
    return thenClause;
  }

  public void setThenClause(ExprOrFormula thenClause) {
    this.thenClause = thenClause;
  }

  public ExprOrFormula getElseClause() {
    return elseClause;
  }

  public void setElseClause(ExprOrFormula elseClause) {
    this.elseClause = elseClause;
  }
}
