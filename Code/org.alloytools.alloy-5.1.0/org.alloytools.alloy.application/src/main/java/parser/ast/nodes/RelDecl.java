package parser.ast.nodes;

import static parser.util.AlloyUtil.getFirstNonNOOPChild;

import edu.mit.csail.sdg.ast.Decl;
import java.util.List;
import java.util.stream.Collectors;

public abstract class RelDecl extends Node {

  private static final long serialVersionUID = 3846064243502566404L;
  protected boolean disjoint;
  protected List<ExprOrFormula> variables;
  protected ExprOrFormula expr;

  public RelDecl(Node parent, Decl decl) {
    super(parent);
    addToNodeMap(decl, this);
    this.disjoint = decl.disjoint != null;
    this.variables = findVariables(decl);
    this.expr = ExprOrFormula.buildExpression(this, decl.expr);
    // Add children.
    children.add(expr);
  }

  public RelDecl(Node parent) {
    super(parent);
  }

  private List<ExprOrFormula> findVariables(Decl decl) {
    return decl.names.stream().map(exprHasName -> ExprOrFormula.buildExpression(this, exprHasName))
        .collect(Collectors.toList());
  }

  public List<ExprOrFormula> getVariables() {
    return variables;
  }

  public List<String> getNames() {
    return variables.stream()
        .map(variable -> {
          Node var = getFirstNonNOOPChild(variable);
          // The variable is either a VarExpr or a FieldExpr according to ExprHasName in Alloy API.
          if (var instanceof VarExpr) {
            return ((VarExpr) var).getName();
          } else {
            return ((FieldExpr) var).getName();
          }
        }).collect(Collectors.toList());
  }

  public void setVariables(List<ExprOrFormula> variables) {
    this.variables = variables;
  }

  public ExprOrFormula getExpr() {
    return expr;
  }

  public void setExpr(ExprOrFormula expr) {
    this.expr = expr;
  }

  public boolean isDisjoint() {
    return disjoint;
  }

  public void setDisjoint(boolean disjoint) {
    this.disjoint = disjoint;
  }
}
