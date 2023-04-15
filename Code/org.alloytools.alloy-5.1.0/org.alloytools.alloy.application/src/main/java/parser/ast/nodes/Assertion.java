package parser.ast.nodes;

import edu.mit.csail.sdg.alloy4.Pair;
import edu.mit.csail.sdg.ast.Expr;
import parser.ast.visitor.GenericVisitor;
import parser.ast.visitor.VoidVisitor;

public class Assertion extends Paragraph {

  private static final long serialVersionUID = -544718572755658237L;

  public Assertion(Node parent, Pair<String, Expr> pair) {
    super(parent, pair);
  }

  public Assertion(Node parent) {
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
