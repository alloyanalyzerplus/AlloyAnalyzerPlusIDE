package parser.ast.nodes;

import edu.mit.csail.sdg.ast.Sig.Field;
import parser.ast.visitor.GenericVisitor;
import parser.ast.visitor.VoidVisitor;
import parser.etc.Names;
import parser.util.StringUtil;

public class FieldExpr extends ExprOrFormula {

  private static final long serialVersionUID = -5929115584453622708L;
  protected String name;

  public FieldExpr(Node parent, Field field) {
    super(parent, field.type());
    addToNodeMap(field, this);
    this.name = StringUtil.afterSubstring(field.label, Names.SLASH, true);
  }

  public FieldExpr(Node parent) {
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
