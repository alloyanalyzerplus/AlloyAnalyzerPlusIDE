package parser.ast.nodes;

import edu.mit.csail.sdg.ast.Decl;
import parser.ast.visitor.GenericVisitor;
import parser.ast.visitor.VoidVisitor;

public class FieldDecl extends RelDecl {

  private static final long serialVersionUID = 2955021824493799249L;

  public FieldDecl(Node parent, Decl decl) {
    super(parent, decl);
  }

  public FieldDecl(Node parent) {
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
