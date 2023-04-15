package alloyfl.coverage.visitor;

import java.util.HashSet;
import java.util.Set;
import parser.ast.nodes.Assertion;
import parser.ast.nodes.BinaryExpr;
import parser.ast.nodes.BinaryFormula;
import parser.ast.nodes.Body;
import parser.ast.nodes.CallExpr;
import parser.ast.nodes.CallFormula;
import parser.ast.nodes.Check;
import parser.ast.nodes.ConstExpr;
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
import parser.ast.visitor.VoidVisitorAdapter;

public class CollectNodeOfSameTypeVisitor extends VoidVisitorAdapter<Object> {

  private Class<? extends Node> clz;
  private Set<Node> nodes;

  public CollectNodeOfSameTypeVisitor(Class<? extends Node> clz) {
    this.clz = clz;
    this.nodes = new HashSet<>();
  }

  public Set<Node> getNodes() {
    return nodes;
  }

  @Override
  public void visit(ModelUnit n, Object arg) {
    if (clz.isAssignableFrom(n.getClass())) {
      nodes.add(n);
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(ModuleDecl n, Object arg) {
    if (clz.isAssignableFrom(n.getClass())) {
      nodes.add(n);
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(OpenDecl n, Object arg) {
    if (clz.isAssignableFrom(n.getClass())) {
      nodes.add(n);
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(SigDecl n, Object arg) {
    if (clz.isAssignableFrom(n.getClass())) {
      nodes.add(n);
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(FieldDecl n, Object arg) {
    if (clz.isAssignableFrom(n.getClass())) {
      nodes.add(n);
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(ParamDecl n, Object arg) {
    if (clz.isAssignableFrom(n.getClass())) {
      nodes.add(n);
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(VarDecl n, Object arg) {
    if (clz.isAssignableFrom(n.getClass())) {
      nodes.add(n);
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(SigExpr n, Object arg) {
    if (clz.isAssignableFrom(n.getClass())) {
      nodes.add(n);
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(FieldExpr n, Object arg) {
    if (clz.isAssignableFrom(n.getClass())) {
      nodes.add(n);
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(VarExpr n, Object arg) {
    if (clz.isAssignableFrom(n.getClass())) {
      nodes.add(n);
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(UnaryExpr n, Object arg) {
    if (clz.isAssignableFrom(n.getClass())) {
      nodes.add(n);
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(UnaryFormula n, Object arg) {
    if (clz.isAssignableFrom(n.getClass())) {
      nodes.add(n);
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(BinaryExpr n, Object arg) {
    if (clz.isAssignableFrom(n.getClass())) {
      nodes.add(n);
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(BinaryFormula n, Object arg) {
    if (clz.isAssignableFrom(n.getClass())) {
      nodes.add(n);
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(ListExpr n, Object arg) {
    if (clz.isAssignableFrom(n.getClass())) {
      nodes.add(n);
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(ListFormula n, Object arg) {
    if (clz.isAssignableFrom(n.getClass())) {
      nodes.add(n);
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(CallExpr n, Object arg) {
    if (clz.isAssignableFrom(n.getClass())) {
      nodes.add(n);
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(CallFormula n, Object arg) {
    if (clz.isAssignableFrom(n.getClass())) {
      nodes.add(n);
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(QtExpr n, Object arg) {
    if (clz.isAssignableFrom(n.getClass())) {
      nodes.add(n);
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(QtFormula n, Object arg) {
    if (clz.isAssignableFrom(n.getClass())) {
      nodes.add(n);
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(ITEExpr n, Object arg) {
    if (clz.isAssignableFrom(n.getClass())) {
      nodes.add(n);
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(ITEFormula n, Object arg) {
    if (clz.isAssignableFrom(n.getClass())) {
      nodes.add(n);
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(LetExpr n, Object arg) {
    if (clz.isAssignableFrom(n.getClass())) {
      nodes.add(n);
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(ConstExpr n, Object arg) {
    if (clz.isAssignableFrom(n.getClass())) {
      nodes.add(n);
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(Body n, Object arg) {
    if (clz.isAssignableFrom(n.getClass())) {
      nodes.add(n);
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(Predicate n, Object arg) {
    if (clz.isAssignableFrom(n.getClass())) {
      nodes.add(n);
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(Function n, Object arg) {
    if (clz.isAssignableFrom(n.getClass())) {
      nodes.add(n);
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(Fact n, Object arg) {
    if (clz.isAssignableFrom(n.getClass())) {
      nodes.add(n);
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(Assertion n, Object arg) {
    if (clz.isAssignableFrom(n.getClass())) {
      nodes.add(n);
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(Run n, Object arg) {
    if (clz.isAssignableFrom(n.getClass())) {
      nodes.add(n);
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(Check n, Object arg) {
    if (clz.isAssignableFrom(n.getClass())) {
      nodes.add(n);
    }
    super.visit(n, arg);
  }
}
