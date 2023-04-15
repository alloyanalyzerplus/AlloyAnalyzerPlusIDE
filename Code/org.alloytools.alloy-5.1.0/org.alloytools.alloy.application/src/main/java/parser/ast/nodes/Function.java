package parser.ast.nodes;

import edu.mit.csail.sdg.ast.Func;
import parser.ast.visitor.GenericVisitor;
import parser.ast.visitor.VoidVisitor;

public class Function extends PredOrFun {

  private static final long serialVersionUID = 2556997005749080887L;
  protected ExprOrFormula returnType;

  public Function(Node parent, Func predOrFun) {
    super(parent, predOrFun);
    this.returnType = ExprOrFormula.buildExpression(this, predOrFun.returnDecl);
    // Add children.
    children.add(returnType);
  }

  public Function(Node parent) {
    super(parent);
  }

  public ExprOrFormula getReturnType() {
    return returnType;
  }

  public void setReturnType(ExprOrFormula returnType) {
    this.returnType = returnType;
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
