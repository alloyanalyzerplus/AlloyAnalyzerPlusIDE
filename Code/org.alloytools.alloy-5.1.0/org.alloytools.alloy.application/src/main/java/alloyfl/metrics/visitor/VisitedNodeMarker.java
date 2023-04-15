package alloyfl.metrics.visitor;

import java.util.Set;
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

public class VisitedNodeMarker extends VoidVisitorAdapter<Object> {

  private Set<Node> visited;

  public VisitedNodeMarker(Set<Node> visited) {
    this.visited = visited;
  }

  public void visit(ModelUnit n, Object arg) {
    visited.add(n);
    super.visit(n, arg);
  }

  public void visit(ModuleDecl n, Object arg) {
    visited.add(n);
    super.visit(n, arg);
  }

  public void visit(OpenDecl n, Object arg) {
    visited.add(n);
    super.visit(n, arg);
  }

  public void visit(SigDecl n, Object arg) {
    visited.add(n);
    super.visit(n, arg);
  }

  public void visit(FieldDecl n, Object arg) {
    visited.add(n);
    super.visit(n, arg);
  }

  public void visit(ParamDecl n, Object arg) {
    visited.add(n);
    super.visit(n, arg);
  }

  public void visit(VarDecl n, Object arg) {
    visited.add(n);
    super.visit(n, arg);
  }

  public void visit(ExprOrFormula n, Object arg) {
    visited.add(n);
    super.visit(n, arg);
  }

  public void visit(SigExpr n, Object arg) {
    visited.add(n);
    super.visit(n, arg);
  }

  public void visit(FieldExpr n, Object arg) {
    visited.add(n);
    super.visit(n, arg);
  }

  public void visit(VarExpr n, Object arg) {
    visited.add(n);
    super.visit(n, arg);
  }

  public void visit(UnaryExpr n, Object arg) {
    // Only count meaningful AST Node
    if (n.getOp() != UnaryExpr.UnaryOp.NOOP) {
      visited.add(n);
    }
    super.visit(n, arg);
  }

  public void visit(UnaryFormula n, Object arg) {
    visited.add(n);
    super.visit(n, arg);
  }

  public void visit(BinaryExpr n, Object arg) {
    visited.add(n);
    super.visit(n, arg);
  }

  public void visit(BinaryFormula n, Object arg) {
    visited.add(n);
    super.visit(n, arg);
  }

  public void visit(ListExpr n, Object arg) {
    visited.add(n);
    super.visit(n, arg);
  }

  public void visit(ListFormula n, Object arg) {
    visited.add(n);
    super.visit(n, arg);
  }

  public void visit(CallExpr n, Object arg) {
    visited.add(n);
    super.visit(n, arg);
  }

  public void visit(CallFormula n, Object arg) {
    visited.add(n);
    super.visit(n, arg);
  }

  public void visit(QtExpr n, Object arg) {
    visited.add(n);
    super.visit(n, arg);
  }

  public void visit(QtFormula n, Object arg) {
    visited.add(n);
    super.visit(n, arg);
  }

  public void visit(ITEExpr n, Object arg) {
    visited.add(n);
    super.visit(n, arg);
  }

  public void visit(ITEFormula n, Object arg) {
    visited.add(n);
    super.visit(n, arg);
  }

  public void visit(LetExpr n, Object arg) {
    visited.add(n);
    super.visit(n, arg);
  }

  public void visit(ConstExpr n, Object arg) {
    visited.add(n);
    super.visit(n, arg);
  }

  public void visit(Body n, Object arg) {
    visited.add(n);
    super.visit(n, arg);
  }

  public void visit(Predicate n, Object arg) {
    visited.add(n);
    super.visit(n, arg);
  }

  public void visit(Function n, Object arg) {
    visited.add(n);
    super.visit(n, arg);
  }

  public void visit(Fact n, Object arg) {
    visited.add(n);
    super.visit(n, arg);
  }

  public void visit(Assertion n, Object arg) {
    visited.add(n);
    super.visit(n, arg);
  }

  public void visit(Run n, Object arg) {
    visited.add(n);
    super.visit(n, arg);
  }

  public void visit(Check n, Object arg) {
    visited.add(n);
    super.visit(n, arg);
  }
}
