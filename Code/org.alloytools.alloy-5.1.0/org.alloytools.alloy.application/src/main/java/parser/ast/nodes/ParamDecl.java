package parser.ast.nodes;

import edu.mit.csail.sdg.ast.Decl;
import parser.ast.visitor.GenericVisitor;
import parser.ast.visitor.VoidVisitor;

public class ParamDecl extends RelDecl {

  private static final long serialVersionUID = 8194334765052054628L;

  public ParamDecl(Node parent, Decl decl) {
    super(parent, decl);
  }

  public ParamDecl(Node parent) {
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
