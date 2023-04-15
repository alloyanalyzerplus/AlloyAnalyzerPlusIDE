package parser.ast.nodes;

import edu.mit.csail.sdg.ast.Decl;
import parser.ast.visitor.GenericVisitor;
import parser.ast.visitor.VoidVisitor;

public class VarDecl extends RelDecl {

  private static final long serialVersionUID = -5184781991426611338L;

  public VarDecl(Node parent, Decl decl) {
    super(parent, decl);
  }

  public VarDecl(Node parent) {
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
