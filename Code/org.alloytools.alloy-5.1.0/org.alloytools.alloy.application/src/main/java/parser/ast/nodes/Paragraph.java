package parser.ast.nodes;

import edu.mit.csail.sdg.alloy4.Pair;
import edu.mit.csail.sdg.ast.Expr;
import edu.mit.csail.sdg.ast.Func;
import parser.util.StringUtil;

public abstract class Paragraph extends Node {

  private static final long serialVersionUID = -2028199266448157004L;
  protected String name;
  protected Body body;

  public Paragraph(Node parent, Func predOrFun) {
    super(parent);
    addToNodeMap(predOrFun, this);
    this.name = StringUtil.ignoreLeadingThis(predOrFun.label);
    this.body = new Body(this, predOrFun.getBody());
    // Add children.
    children.add(body);
  }

  public Paragraph(Node parent, Pair<String, Expr> pair) {
    super(parent);
    addToNodeMap(pair, this);
    this.name = pair.a;
    this.body = new Body(this, pair.b);
    // Add children.
    children.add(body);
  }

  public Paragraph(Node parent) {
    super(parent);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Body getBody() {
    return body;
  }

  public void setBody(Body body) {
    this.body = body;
  }
}
