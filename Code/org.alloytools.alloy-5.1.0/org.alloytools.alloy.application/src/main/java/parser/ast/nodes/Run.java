package parser.ast.nodes;

import edu.mit.csail.sdg.ast.Command;
import parser.ast.visitor.GenericVisitor;
import parser.ast.visitor.VoidVisitor;

public class Run extends Cmd {

  private static final long serialVersionUID = -2332733386566557622L;

  public Run(Node parent, Command cmd) {
    super(parent, cmd);
  }

  public Run(Node parent) {
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
