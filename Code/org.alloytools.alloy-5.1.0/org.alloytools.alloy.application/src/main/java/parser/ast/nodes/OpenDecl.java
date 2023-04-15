package parser.ast.nodes;

import edu.mit.csail.sdg.parser.CompModule;
import java.util.List;
import parser.ast.visitor.GenericVisitor;
import parser.ast.visitor.VoidVisitor;

public class OpenDecl extends Node {

  private static final long serialVersionUID = -2956139666166286771L;
  protected String fileName;
  protected List<String> arguments;
  protected String alias;

  public OpenDecl(Node parent, CompModule.Open open) {
    super(parent);
    addToNodeMap(open, this);
    this.fileName = open.filename;
    this.arguments = open.args;
    this.alias = open.alias;
  }

  public OpenDecl(Node parent) {
    super(parent);
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public List<String> getArguments() {
    return arguments;
  }

  public void setArguments(List<String> arguments) {
    this.arguments = arguments;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
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
