package alloyfl.coverage.visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

/**
 * Collect all nodes whose hit count is different than the parent node.  This is based on the
 * assumption that parent node always has a hit count less than or equal to its descendants.
 */
public class UniqueHitNodeCollector extends VoidVisitorAdapter<Object> {

  private Map<Node, Integer> hitMap;
  private List<Node> nodesToReport;

  public UniqueHitNodeCollector(Map<Node, Integer> hitMap) {
    this.hitMap = hitMap;
    this.nodesToReport = new ArrayList<>();
  }

  private void addToReport(Node n) {
    int hitCount = hitMap.getOrDefault(n, 0);
    if (hitCount != 0) {
      if (n.getParent() != null && hitMap.getOrDefault(n.getParent(), 0) < hitCount) {
        nodesToReport.add(n);
      }
    }
  }

  public List<Node> getNodesToReport() {
    return nodesToReport;
  }

  @Override
  public void visit(ModelUnit n, Object arg) {
    addToReport(n);
    super.visit(n, arg);
  }

  @Override
  public void visit(ModuleDecl n, Object arg) {
    addToReport(n);
    super.visit(n, arg);
  }

  @Override
  public void visit(OpenDecl n, Object arg) {
    addToReport(n);
    super.visit(n, arg);
  }

  @Override
  public void visit(SigDecl n, Object arg) {
    addToReport(n);
    super.visit(n, arg);
  }

  @Override
  public void visit(FieldDecl n, Object arg) {
    addToReport(n);
    super.visit(n, arg);
  }

  @Override
  public void visit(ParamDecl n, Object arg) {
    addToReport(n);
    super.visit(n, arg);
  }

  @Override
  public void visit(VarDecl n, Object arg) {
    addToReport(n);
    super.visit(n, arg);
  }

  @Override
  public void visit(SigExpr n, Object arg) {
    addToReport(n);
    super.visit(n, arg);
  }

  @Override
  public void visit(FieldExpr n, Object arg) {
    addToReport(n);
    super.visit(n, arg);
  }

  @Override
  public void visit(VarExpr n, Object arg) {
    addToReport(n);
    super.visit(n, arg);
  }

  @Override
  public void visit(UnaryExpr n, Object arg) {
    addToReport(n);
    super.visit(n, arg);
  }

  @Override
  public void visit(UnaryFormula n, Object arg) {
    addToReport(n);
    super.visit(n, arg);
  }

  @Override
  public void visit(BinaryExpr n, Object arg) {
    addToReport(n);
    super.visit(n, arg);
  }

  @Override
  public void visit(BinaryFormula n, Object arg) {
    addToReport(n);
    super.visit(n, arg);
  }

  @Override
  public void visit(ListExpr n, Object arg) {
    addToReport(n);
    super.visit(n, arg);
  }

  @Override
  public void visit(ListFormula n, Object arg) {
    addToReport(n);
    super.visit(n, arg);
  }

  @Override
  public void visit(CallExpr n, Object arg) {
    addToReport(n);
    super.visit(n, arg);
  }

  @Override
  public void visit(CallFormula n, Object arg) {
    addToReport(n);
    super.visit(n, arg);
  }

  @Override
  public void visit(QtExpr n, Object arg) {
    addToReport(n);
    super.visit(n, arg);
  }

  @Override
  public void visit(QtFormula n, Object arg) {
    addToReport(n);
    super.visit(n, arg);
  }

  @Override
  public void visit(ITEExpr n, Object arg) {
    addToReport(n);
    super.visit(n, arg);
  }

  @Override
  public void visit(ITEFormula n, Object arg) {
    addToReport(n);
    super.visit(n, arg);
  }

  @Override
  public void visit(LetExpr n, Object arg) {
    addToReport(n);
    super.visit(n, arg);
  }

  @Override
  public void visit(ConstExpr n, Object arg) {
    addToReport(n);
    super.visit(n, arg);
  }

  @Override
  public void visit(Body n, Object arg) {
    addToReport(n);
    super.visit(n, arg);
  }

  @Override
  public void visit(Predicate n, Object arg) {
    addToReport(n);
    super.visit(n, arg);
  }

  @Override
  public void visit(Function n, Object arg) {
    addToReport(n);
    super.visit(n, arg);
  }

  @Override
  public void visit(Fact n, Object arg) {
    addToReport(n);
    super.visit(n, arg);
  }

  @Override
  public void visit(Assertion n, Object arg) {
    addToReport(n);
    super.visit(n, arg);
  }

  @Override
  public void visit(Run n, Object arg) {
    addToReport(n);
    super.visit(n, arg);
  }

  @Override
  public void visit(Check n, Object arg) {
    addToReport(n);
    super.visit(n, arg);
  }
}
