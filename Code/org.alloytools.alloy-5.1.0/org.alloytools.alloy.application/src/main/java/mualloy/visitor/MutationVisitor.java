package mualloy.visitor;

import static parser.etc.Names.DOLLAR;
import static parser.etc.Names.IGNORE_NAMES;

import java.util.List;
import java.util.Set;
import mualloy.rule.BOE;
import mualloy.rule.BOR;
import mualloy.rule.IEOE;
import mualloy.rule.LOD;
import mualloy.rule.LOR;
import mualloy.rule.MOR;
import mualloy.rule.PBD;
import mualloy.rule.QOR;
import mualloy.rule.UOD;
import mualloy.rule.UOI;
import mualloy.rule.UOR;
import mualloy.util.MInfo;
import mualloy.util.MInfo.MType;
import parser.ast.nodes.Assertion;
import parser.ast.nodes.BinaryExpr;
import parser.ast.nodes.BinaryFormula;
import parser.ast.nodes.Body;
import parser.ast.nodes.CallExpr;
import parser.ast.nodes.CallFormula;
import parser.ast.nodes.ExprOrFormula;
import parser.ast.nodes.Fact;
import parser.ast.nodes.FieldDecl;
import parser.ast.nodes.FieldExpr;
import parser.ast.nodes.Function;
import parser.ast.nodes.ITEExpr;
import parser.ast.nodes.ITEFormula;
import parser.ast.nodes.LetExpr;
import parser.ast.nodes.ListExpr;
import parser.ast.nodes.ListFormula;
import parser.ast.nodes.ModelUnit;
import parser.ast.nodes.Node;
import parser.ast.nodes.ParamDecl;
import parser.ast.nodes.Predicate;
import parser.ast.nodes.QtExpr;
import parser.ast.nodes.QtFormula;
import parser.ast.nodes.SigDecl;
import parser.ast.nodes.UnaryExpr;
import parser.ast.nodes.UnaryFormula;
import parser.ast.nodes.VarDecl;
import parser.ast.nodes.VarExpr;
import parser.ast.visitor.VoidVisitorAdapter;
import parser.etc.MutationData;
import parser.opt.Opt;

public abstract class MutationVisitor extends VoidVisitorAdapter<Object> {

  protected Opt opt;
  protected ModelUnit mu;
  protected MInfo mi;
  /**
   * If domain is null then we mutate every possible node, otherwise we mutate nodes in the domain.
   */
  protected Set<Node> domain;

  public MutationVisitor(Opt opt) {
    this.opt = opt;
    this.mi = new MInfo();
    this.domain = null;
  }

  @Override
  public void visit(ModelUnit n, Object arg) {
    this.mu = n;
    super.visit(n, arg);
  }

  @Override
  public void visit(SigDecl n, Object arg) {
    MType originalType = mi.getType();
    Node originalNode = mi.getNode();
    mi.setType(MType.SIG);
    mi.setNode(n);
    if (domain == null || domain.contains(n)) {
      List<MutationData> mutationDataList = MOR.mutate(n, mu, opt, mi);
      mutationDataList.forEach(mutationData -> process(n, mutationData, MOR.class));
    }
    super.visit(n, arg);
    mi.setType(originalType);
    mi.setNode(originalNode);
  }

  @Override
  public void visit(FieldDecl n, Object arg) {
    MInfo.MType originalType = mi.getType();
    Node originalExtraNode = mi.getExtraNode();
    mi.setType(MType.FIELD);
    // We don't need to check if n is in domain because we do not mutate anything here.
    mi.setExtraNode(n);
    // We do not visit field expressions in the field declaration.
    n.getExpr().accept(this, arg);
    mi.setType(originalType);
    mi.setExtraNode(originalExtraNode);
  }

  @Override
  public void visit(ParamDecl n, Object arg) {
    MInfo.MType originalType = mi.getType();
    Node originalNode = mi.getNode();
    mi.setType(MType.IGNORE);
    mi.setNode(n);
    // We do not visit variable expressions in the parameter declaration.
    n.getExpr().accept(this, arg);
    mi.setType(originalType);
    mi.setNode(originalNode);
  }

  @Override
  public void visit(VarDecl n, Object arg) {
    // We do not visit variable expressions in the variable declaration.
    n.getExpr().accept(this, arg);
  }

  @Override
  public void visit(ExprOrFormula n, Object arg) {
    n.accept(this, arg);
  }

  @Override
  public void visit(FieldExpr n, Object arg) {
    if (domain == null || domain.contains(n)) {
      List<MutationData> mutationDataList = UOI.mutate(n, mu, opt, mi);
      mutationDataList.forEach(mutationData -> process(n, mutationData, UOI.class));
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(VarExpr n, Object arg) {
    if (domain == null || domain.contains(n)) {
      List<MutationData> mutationDataList = UOI.mutate(n, mu, opt, mi);
      mutationDataList.forEach(mutationData -> process(n, mutationData, UOI.class));
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(UnaryExpr n, Object arg) {
    if (domain == null || domain.contains(n)) {
      List<MutationData> mutationDataList = UOR.mutate(n, mu, opt, mi);
      mutationDataList.forEach(mutationData -> process(n, mutationData, UOR.class));
      mutationDataList = UOI.mutate(n, mu, opt, mi);
      mutationDataList.forEach(mutationData -> process(n, mutationData, UOI.class));
      MutationData mutationData = UOD.mutate(n, mu, opt, mi);
      if (mutationData != null) {
        process(n, mutationData, UOD.class);
      }
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(UnaryFormula n, Object arg) {
    if (domain == null || domain.contains(n)) {
      List<MutationData> mutationDataList = UOR.mutate(n, mu, opt, mi);
      mutationDataList.forEach(mutationData -> process(n, mutationData, UOR.class));
      MutationData mutationData = UOD.mutate(n, mu, opt, mi);
      if (mutationData != null) {
        process(n, mutationData, UOD.class);
      }
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(BinaryExpr n, Object arg) {
    if (domain == null || domain.contains(n)) {
      List<MutationData> mutationDataList = BOR.mutate(n, mu, opt, mi);
      mutationDataList.forEach(mutationData -> process(n, mutationData, BOR.class));
      mutationDataList = UOI.mutate(n, mu, opt, mi);
      mutationDataList.forEach(mutationData -> process(n, mutationData, UOI.class));
      MutationData mutationData = BOE.mutate(n, mu, opt, mi);
      if (mutationData != null) {
        process(n, mutationData, BOE.class);
      }
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(BinaryFormula n, Object arg) {
    if (domain == null || domain.contains(n)) {
      List<MutationData> mutationDataList = BOR.mutate(n, mu, opt, mi);
      mutationDataList.forEach(mutationData -> process(n, mutationData, BOR.class));
      MutationData mutationData = BOE.mutate(n, mu, opt, mi);
      if (mutationData != null) {
        process(n, mutationData, BOE.class);
      }
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(ListExpr n, Object arg) {
    super.visit(n, arg);
  }

  @Override
  public void visit(ListFormula n, Object arg) {
    if (domain == null || domain.contains(n)) {
      List<MutationData> mutationDataList = LOR.mutate(n, mu, opt, mi);
      mutationDataList.forEach(mutationData -> process(n, mutationData, LOR.class));
      mutationDataList = LOD.mutate(n, mu, opt, mi);
      mutationDataList.forEach(mutationData -> process(n, mutationData, LOD.class));
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(CallExpr n, Object arg) {
    super.visit(n, arg);
  }

  @Override
  public void visit(CallFormula n, Object arg) {
    super.visit(n, arg);
  }

  @Override
  public void visit(QtExpr n, Object arg) {
    super.visit(n, arg);
  }

  @Override
  public void visit(QtFormula n, Object arg) {
    if (domain == null || domain.contains(n)) {
      List<MutationData> mutationDataList = QOR.mutate(n, mu, opt, mi);
      mutationDataList.forEach(mutationData -> process(n, mutationData, QOR.class));
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(ITEExpr n, Object arg) {
    super.visit(n, arg);
  }

  @Override
  public void visit(ITEFormula n, Object arg) {
    if (domain == null || domain.contains(n)) {
      MutationData mutationData = IEOE.mutate(n, mu, opt, mi);
      if (mutationData != null) {
        process(n, mutationData, IEOE.class);
      }
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(LetExpr n, Object arg) {
    super.visit(n, arg);
  }

  @Override
  public void visit(Body n, Object arg) {
    if (domain == null || domain.contains(n)) {
      MutationData mutationData = PBD.mutate(n, mu, opt, mi);
      if (mutationData != null) {
        process(n, mutationData, PBD.class);
      }
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(Predicate n, Object arg) {
    MType originalType = mi.getType();
    Node originalNode = mi.getNode();
    if (n.getName().contains(DOLLAR) || IGNORE_NAMES.stream()
        .anyMatch(ignoredName -> n.getName().startsWith(ignoredName))) {
      mi.setType(MType.IGNORE);
    } else {
      mi.setType(MType.PRED);
    }
    mi.setNode(n);
    super.visit(n, arg);
    mi.setType(originalType);
    mi.setNode(originalNode);
  }

  @Override
  public void visit(Function n, Object arg) {
    MType originalType = mi.getType();
    Node originalNode = mi.getNode();
    if (n.getName().contains(DOLLAR) || IGNORE_NAMES.stream()
        .anyMatch(ignoredName -> n.getName().startsWith(ignoredName))) {
      mi.setType(MType.IGNORE);
    } else {
      mi.setType(MType.FUN);
    }
    mi.setNode(n);
    super.visit(n, arg);
    mi.setType(originalType);
    mi.setNode(originalNode);
  }

  @Override
  public void visit(Fact n, Object arg) {
    MType originalType = mi.getType();
    Node originalNode = mi.getNode();
    if (IGNORE_NAMES.stream()
        .anyMatch(ignoredName -> n.getName().startsWith(ignoredName))) {
      mi.setType(MType.IGNORE);
    } else {
      mi.setType(MType.FACT);
    }
    mi.setNode(n);
    super.visit(n, arg);
    mi.setType(originalType);
    mi.setNode(originalNode);
  }

  @Override
  public void visit(Assertion n, Object arg) {
    MType originalType = mi.getType();
    Node originalNode = mi.getNode();
    mi.setType(MType.IGNORE);
    mi.setNode(n);
    super.visit(n, arg);
    mi.setType(originalType);
    mi.setNode(originalNode);
  }

  protected abstract void process(Node node, MutationData mutationData, Class<?> clazz);
}
