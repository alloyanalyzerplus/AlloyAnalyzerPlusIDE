package parser.ast.nodes;

import edu.mit.csail.sdg.ast.ExprList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ListExprOrFormula extends ExprOrFormula {

  private static final long serialVersionUID = 3254561500476791858L;
  protected List<ExprOrFormula> arguments;

  public ListExprOrFormula(Node parent, ExprList exprList) {
    super(parent, exprList.type());
    addToNodeMap(exprList, this);
    this.arguments = createArgs(exprList);
    // Add children.
    children.addAll(arguments);
  }

  public ListExprOrFormula(Node parent) {
    super(parent);
  }

  private List<ExprOrFormula> createArgs(ExprList exprList) {
    return exprList.args.stream().map(arg -> buildExpression(this, arg))
        .collect(Collectors.toList());
  }

  public List<ExprOrFormula> getArguments() {
    return arguments;
  }

  public void setArguments(List<ExprOrFormula> arguments) {
    this.arguments = arguments;
  }
}
