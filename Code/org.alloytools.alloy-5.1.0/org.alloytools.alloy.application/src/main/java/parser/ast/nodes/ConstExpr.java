package parser.ast.nodes;

import edu.mit.csail.sdg.ast.ExprConstant;
import parser.ast.visitor.GenericVisitor;
import parser.ast.visitor.VoidVisitor;

public class ConstExpr extends ExprOrFormula {

  private static final long serialVersionUID = 5363987844355591738L;
  protected String value;
  protected boolean isBoolean;

  public ConstExpr(Node parent, ExprConstant expr) {
    super(parent, expr.type());
    addToNodeMap(expr, this);
    this.isBoolean = false;
    this.value = findValue(expr);
  }

  public ConstExpr(Node parent) {
    super(parent);
    this.value = "";
  }

  public ConstExpr(String value) {
    super(null);
    this.value = value;
  }

  private String findValue(ExprConstant expr) {
    switch (expr.op) {
      case IDEN:
      case EMPTYNESS:
        return expr.op.toString();
      case STRING:
        return expr.string;
      case NUMBER:
        return String.valueOf(expr.num);
      case TRUE:
      case FALSE:
        isBoolean = true;
      default:
        return "";
    }
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public boolean isBoolean() {
    return isBoolean;
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
