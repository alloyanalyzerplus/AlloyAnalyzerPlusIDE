package parser.ast.nodes;

import edu.mit.csail.sdg.ast.Func;
import parser.ast.visitor.GenericVisitor;
import parser.ast.visitor.VoidVisitor;

public class Predicate extends PredOrFun {

  private static final long serialVersionUID = -5884014468089768723L;

  public Predicate(Node parent, Func predOrFun) {
    super(parent, predOrFun);
  }

  public Predicate(Node parent) {
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
