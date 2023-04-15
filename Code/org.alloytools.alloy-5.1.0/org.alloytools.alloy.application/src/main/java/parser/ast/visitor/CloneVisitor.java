package parser.ast.visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import parser.ast.nodes.Assertion;
import parser.ast.nodes.BinaryExpr;
import parser.ast.nodes.BinaryFormula;
import parser.ast.nodes.Body;
import parser.ast.nodes.CallExpr;
import parser.ast.nodes.CallFormula;
import parser.ast.nodes.Check;
import parser.ast.nodes.ConstExpr;
import parser.ast.nodes.ExprOrFormula;
import parser.ast.nodes.Fact;
import parser.ast.nodes.FieldDecl;
import parser.ast.nodes.FieldExpr;
import parser.ast.nodes.Function;
import parser.ast.nodes.ITEExpr;
import parser.ast.nodes.ITEExprOrFormula;
import parser.ast.nodes.ITEFormula;
import parser.ast.nodes.LetExpr;
import parser.ast.nodes.ListExpr;
import parser.ast.nodes.ListFormula;
import parser.ast.nodes.ModelUnit;
import parser.ast.nodes.ModuleDecl;
import parser.ast.nodes.Node;
import parser.ast.nodes.OpenDecl;
import parser.ast.nodes.ParamDecl;
import parser.ast.nodes.Predicate;
import parser.ast.nodes.QtExpr;
import parser.ast.nodes.QtFormula;
import parser.ast.nodes.Run;
import parser.ast.nodes.SigDecl;
import parser.ast.nodes.SigExpr;
import parser.ast.nodes.UnaryExpr;
import parser.ast.nodes.UnaryFormula;
import parser.ast.nodes.VarDecl;
import parser.ast.nodes.VarExpr;

/**
 * The CloneVisitor clones a sub-tree.  Note that an enum is defined as a singleton, so it's not
 * deep copied.
 */
public class CloneVisitor implements GenericVisitor<Visitable, Node> {

  @Override
  public Visitable visit(ModelUnit n, Node parent) {
    ModelUnit res = new ModelUnit(parent);
    copyNodeMap(n, res);
    res.setModuleDecl(cloneNode(n.getModuleDecl(), res));
    res.setOpenDeclList(cloneList(n.getOpenDeclList(), res));
    res.setSigDeclList(cloneList(n.getSigDeclList(), res));
    res.setPredDeclList(cloneList(n.getPredDeclList(), res));
    res.setFunDeclList(cloneList(n.getFunDeclList(), res));
    res.setFactDeclList(cloneList(n.getFactDeclList(), res));
    res.setAssertDeclList(cloneList(n.getAssertDeclList(), res));
    res.setRunCmdList(cloneList(n.getRunCmdList(), res));
    res.setCheckCmdList(cloneList(n.getCheckCmdList(), res));
    return res;
  }

  @Override
  public Visitable visit(ModuleDecl n, Node parent) {
    ModuleDecl res = new ModuleDecl(parent);
    copyNodeMap(n, res);
    res.setModelName(n.getModelName());
    return res;
  }

  @Override
  public Visitable visit(OpenDecl n, Node parent) {
    OpenDecl res = new OpenDecl(parent);
    copyNodeMap(n, res);
    res.setFileName(n.getFileName());
    res.setArguments(new ArrayList<>(n.getArguments()));
    res.setAlias(n.getAlias());
    return res;
  }

  @Override
  public Visitable visit(SigDecl n, Node parent) {
    SigDecl res = new SigDecl(parent);
    copyNodeMap(n, res);
    res.setName(n.getName());
    res.setIsAbstract(n.isAbstract());
    res.setMult(n.getMult());
    res.setIsTopLevel(n.isTopLevel());
    res.setIsSubsig(n.isSubsig());
    res.setParentName(n.getParentName());
    res.setFieldList(cloneList(n.getFieldList(), res));
    res.setHasSigFact(n.hasSigFact());
    res.setSigFact(cloneNode(n.getSigFact(), res));
    return res;
  }

  @Override
  public Visitable visit(FieldDecl n, Node parent) {
    FieldDecl res = new FieldDecl(parent);
    copyNodeMap(n, res);
    res.setDisjoint(n.isDisjoint());
    res.setVariables(cloneList(n.getVariables(), res));
    res.setExpr(cloneNode(n.getExpr(), res));
    return res;
  }

  @Override
  public Visitable visit(ParamDecl n, Node parent) {
    ParamDecl res = new ParamDecl(parent);
    copyNodeMap(n, res);
    res.setDisjoint(n.isDisjoint());
    res.setVariables(cloneList(n.getVariables(), res));
    res.setExpr(cloneNode(n.getExpr(), res));
    return res;
  }

  @Override
  public Visitable visit(VarDecl n, Node parent) {
    VarDecl res = new VarDecl(parent);
    copyNodeMap(n, res);
    res.setDisjoint(n.isDisjoint());
    res.setVariables(cloneList(n.getVariables(), res));
    res.setExpr(cloneNode(n.getExpr(), res));
    return res;
  }

  @Override
  public Visitable visit(ExprOrFormula n, Node parent) {
    return n.accept(this, parent);
  }

  @Override
  public Visitable visit(SigExpr n, Node parent) {
    SigExpr res = new SigExpr(parent);
    copyNodeMap(n, res);
    res.setType(n.getType());
    res.setName(n.getName());
    return res;
  }

  @Override
  public Visitable visit(FieldExpr n, Node parent) {
    FieldExpr res = new FieldExpr(parent);
    copyNodeMap(n, res);
    res.setType(n.getType());
    res.setName(n.getName());
    return res;
  }

  @Override
  public Visitable visit(UnaryExpr n, Node parent) {
    UnaryExpr res = new UnaryExpr(parent);
    copyNodeMap(n, res);
    res.setType(n.getType());
    res.setSub(cloneNode(n.getSub(), res));
    res.setOp(n.getOp());
    return res;
  }

  @Override
  public Visitable visit(UnaryFormula n, Node parent) {
    UnaryFormula res = new UnaryFormula(parent);
    copyNodeMap(n, res);
    res.setType(n.getType());
    res.setSub(cloneNode(n.getSub(), res));
    res.setOp(n.getOp());
    return res;
  }

  @Override
  public Visitable visit(BinaryExpr n, Node parent) {
    BinaryExpr res = new BinaryExpr(parent);
    copyNodeMap(n, res);
    res.setType(n.getType());
    res.setLeft(cloneNode(n.getLeft(), res));
    res.setRight(cloneNode(n.getRight(), res));
    res.setOp(n.getOp());
    return res;
  }

  @Override
  public Visitable visit(BinaryFormula n, Node parent) {
    BinaryFormula res = new BinaryFormula(parent);
    copyNodeMap(n, res);
    res.setType(n.getType());
    res.setLeft(cloneNode(n.getLeft(), res));
    res.setRight(cloneNode(n.getRight(), res));
    res.setOp(n.getOp());
    return res;
  }

  @Override
  public Visitable visit(ListExpr n, Node parent) {
    ListExpr res = new ListExpr(parent);
    copyNodeMap(n, res);
    res.setType(n.getType());
    res.setArguments(cloneList(n.getArguments(), res));
    res.setOp(n.getOp());
    return res;
  }

  @Override
  public Visitable visit(ListFormula n, Node parent) {
    ListFormula res = new ListFormula(parent);
    copyNodeMap(n, res);
    res.setType(n.getType());
    res.setArguments(cloneList(n.getArguments(), res));
    res.setOp(n.getOp());
    return res;
  }

  @Override
  public Visitable visit(CallExpr n, Node parent) {
    CallExpr res = new CallExpr(parent);
    copyNodeMap(n, res);
    res.setType(n.getType());
    res.setName(n.getName());
    res.setArguments(cloneList(n.getArguments(), res));
    return res;
  }

  @Override
  public Visitable visit(CallFormula n, Node parent) {
    CallFormula res = new CallFormula(parent);
    copyNodeMap(n, res);
    res.setType(n.getType());
    res.setName(n.getName());
    res.setArguments(cloneList(n.getArguments(), res));
    return res;
  }

  @Override
  public Visitable visit(VarExpr n, Node parent) {
    VarExpr res = new VarExpr(parent);
    copyNodeMap(n, res);
    res.setType(n.getType());
    res.setName(n.getName());
    return res;
  }

  @Override
  public Visitable visit(QtExpr n, Node parent) {
    QtExpr res = new QtExpr(parent);
    copyNodeMap(n, res);
    res.setType(n.getType());
    res.setVarDecls(cloneList(n.getVarDecls(), res));
    res.setBody(cloneNode(n.getBody(), res));
    res.setOp(n.getOp());
    return res;
  }

  @Override
  public Visitable visit(QtFormula n, Node parent) {
    QtFormula res = new QtFormula(parent);
    copyNodeMap(n, res);
    res.setType(n.getType());
    res.setVarDecls(cloneList(n.getVarDecls(), res));
    res.setBody(cloneNode(n.getBody(), res));
    res.setOp(n.getOp());
    return res;
  }

  @Override
  public Visitable visit(ITEExpr n, Node parent) {
    ITEExprOrFormula res = new ITEExpr(parent);
    copyNodeMap(n, res);
    res.setType(n.getType());
    res.setCondition(cloneNode(n.getCondition(), res));
    res.setThenClause(cloneNode(n.getThenClause(), res));
    res.setElseClause(cloneNode(n.getElseClause(), res));
    return res;
  }

  @Override
  public Visitable visit(ITEFormula n, Node parent) {
    ITEExprOrFormula res = new ITEFormula(parent);
    copyNodeMap(n, res);
    res.setType(n.getType());
    res.setCondition(cloneNode(n.getCondition(), res));
    res.setThenClause(cloneNode(n.getThenClause(), res));
    res.setElseClause(cloneNode(n.getElseClause(), res));
    return res;
  }

  @Override
  public Visitable visit(LetExpr n, Node parent) {
    LetExpr res = new LetExpr(parent);
    copyNodeMap(n, res);
    res.setType(n.getType());
    res.setVar(cloneNode(n.getVar(), res));
    res.setBound(cloneNode(n.getBound(), res));
    res.setBody(cloneNode(n.getBody(), res));
    return res;
  }

  @Override
  public Visitable visit(ConstExpr n, Node parent) {
    ConstExpr res = new ConstExpr(parent);
    copyNodeMap(n, res);
    res.setType(n.getType());
    res.setValue(n.getValue());
    return res;
  }

  @Override
  public Visitable visit(Body n, Node parent) {
    Body res = new Body(parent);
    copyNodeMap(n, res);
    res.setBodyExpr(cloneNode(n.getBodyExpr(), res));
    return res;
  }

  @Override
  public Visitable visit(Predicate n, Node parent) {
    Predicate res = new Predicate(parent);
    copyNodeMap(n, res);
    res.setName(n.getName());
    res.setBody(cloneNode(n.getBody(), res));
    res.setParamList(cloneList(n.getParamList(), res));
    return res;
  }

  @Override
  public Visitable visit(Function n, Node parent) {
    Function res = new Function(parent);
    copyNodeMap(n, res);
    res.setName(n.getName());
    res.setBody(cloneNode(n.getBody(), res));
    res.setParamList(cloneList(n.getParamList(), res));
    res.setReturnType(cloneNode(n.getReturnType(), res));
    return res;
  }

  @Override
  public Visitable visit(Fact n, Node parent) {
    Fact res = new Fact(parent);
    copyNodeMap(n, res);
    res.setName(n.getName());
    res.setBody(cloneNode(n.getBody(), res));
    return res;
  }

  @Override
  public Visitable visit(Assertion n, Node parent) {
    Assertion res = new Assertion(parent);
    copyNodeMap(n, res);
    res.setName(n.getName());
    res.setBody(cloneNode(n.getBody(), res));
    return res;
  }

  @Override
  public Visitable visit(Run n, Node parent) {
    Run res = new Run(parent);
    copyNodeMap(n, res);
    res.setName(n.getName());
    res.setScopeAsString(n.getScopeAsString());
    res.setExpect(n.getExpect());
    return res;
  }

  @Override
  public Visitable visit(Check n, Node parent) {
    Check res = new Check(parent);
    copyNodeMap(n, res);
    res.setName(n.getName());
    res.setScopeAsString(n.getScopeAsString());
    res.setExpect(n.getExpect());
    return res;
  }

  @SuppressWarnings("unchecked")
  private <N extends Node> N cloneNode(N node, Node arg) {
    if (node == null) {
      return null;
    }
    Node r = (Node) node.accept(this, arg);
    if (r == null) {
      return null;
    }
    return (N) r;
  }

  @SuppressWarnings("unchecked")
  private <N extends Node> List<N> cloneList(List<N> list, Node arg) {
    if (list == null) {
      return null;
    }
    return list.stream().map(n -> (N) n.accept(this, arg)).collect(Collectors.toList());
  }

  private void copyNodeMap(Node from, Node to) {
    if (from.getNodeMap() == null || to.getNodeMap() == null) {
      return;
    }
    Map<Node, Object> d2s = from.getNodeMap().getD2S();
    if (!d2s.containsKey(from)) {
      return;
    }
    to.getNodeMap().addToMap(d2s.get(from), to);
  }
}
