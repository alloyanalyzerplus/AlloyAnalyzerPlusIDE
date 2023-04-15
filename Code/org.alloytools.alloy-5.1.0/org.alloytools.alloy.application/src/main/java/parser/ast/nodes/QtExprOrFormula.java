package parser.ast.nodes;

import edu.mit.csail.sdg.ast.Decl;
import edu.mit.csail.sdg.ast.ExprQt;
import java.util.List;
import java.util.stream.Collectors;

public abstract class QtExprOrFormula extends ExprOrFormula {

  private static final long serialVersionUID = -2400625701424347107L;
  protected List<VarDecl> varDecls;
  protected Body body;

  public QtExprOrFormula(Node parent, ExprQt expr) {
    super(parent, expr.type());
    addToNodeMap(expr, this);
    this.varDecls = createVarDecls(expr.decls);
    this.body = new Body(this, expr.sub);
    // Add children.
    children.addAll(varDecls);
    children.add(body);
  }

  public QtExprOrFormula(Node parent) {
    super(parent);
  }

  private List<VarDecl> createVarDecls(List<Decl> decls) {
    return decls.stream().map(varDecl -> new VarDecl(this, varDecl)).collect(Collectors.toList());
  }

  public List<VarDecl> getVarDecls() {
    return varDecls;
  }

  public void setVarDecls(List<VarDecl> varDecls) {
    this.varDecls = varDecls;
  }

  public Body getBody() {
    return body;
  }

  public void setBody(Body body) {
    this.body = body;
  }
}
