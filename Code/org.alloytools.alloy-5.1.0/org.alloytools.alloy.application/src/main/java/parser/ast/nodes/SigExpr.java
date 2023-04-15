package parser.ast.nodes;

import static parser.util.StringUtil.ignoreLeadingThis;

import edu.mit.csail.sdg.ast.Sig;
import parser.ast.visitor.GenericVisitor;
import parser.ast.visitor.VoidVisitor;

public class SigExpr extends ExprOrFormula {

  private static final long serialVersionUID = 7082346404696418083L;
  protected String name;

  public SigExpr(Node parent, Sig sig) {
    super(parent, sig.type());
    addToNodeMap(sig, this);
    this.name = ignoreLeadingThis(sig.label);
  }

  public SigExpr(Node parent) {
    super(parent);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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
