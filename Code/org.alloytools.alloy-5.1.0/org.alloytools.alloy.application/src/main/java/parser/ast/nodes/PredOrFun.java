package parser.ast.nodes;

import edu.mit.csail.sdg.ast.Decl;
import edu.mit.csail.sdg.ast.Func;
import java.util.List;
import java.util.stream.Collectors;

public abstract class PredOrFun extends Paragraph {

  private static final long serialVersionUID = -2124934577669611750L;
  protected List<ParamDecl> paramList;

  public PredOrFun(Node parent, Func predOrFun) {
    super(parent, predOrFun);
    this.paramList = createParameterList(predOrFun.decls);
    // Add children.
    children.addAll(paramList);
  }

  public PredOrFun(Node parent) {
    super(parent);
  }

  public List<ParamDecl> getParamList() {
    return paramList;
  }

  public void setParamList(List<ParamDecl> paramList) {
    this.paramList = paramList;
  }

  private List<ParamDecl> createParameterList(List<Decl> decls) {
    return decls.stream().map(decl -> new ParamDecl(this, decl)).collect(Collectors.toList());
  }
}
