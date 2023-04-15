package alloyfl.hybrid.visitor;

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

/**
 * We collect all descendants of a given node which are not previously processed.
 */
public class DescendantCollector extends VoidVisitorAdapter<Object> {

  private Node target;
  private boolean collect;
  private Set<Node> visited;
  private Set<Node> descendants;

  public DescendantCollector(Node target) {
    this(target, new HashSet<>());
  }

  public DescendantCollector(Node target, Set<Node> visited) {
    this.target = target;
    this.collect = false;
    this.visited = visited;
    this.descendants = new HashSet<>();
  }

  public Set<Node> getDescendants() {
    return descendants;
  }

  public void visit(ModelUnit n, Object arg) {
    // If the node was visited before, we do not need to proceed.
    if (visited.contains(n)) {
      return;
    }
    if (n == target) {
      collect = true;
    }
    if (collect) {
      descendants.add(n);
    }
    super.visit(n, arg);
    if (n == target) {
      collect = false;
    }
  }

  public void visit(ModuleDecl n, Object arg) {
    if (visited.contains(n)) {
      return;
    }
    if (n == target) {
      collect = true;
    }
    if (collect) {
      descendants.add(n);
    }
    super.visit(n, arg);
    if (n == target) {
      collect = false;
    }
  }

  public void visit(OpenDecl n, Object arg) {
    if (visited.contains(n)) {
      return;
    }
    if (n == target) {
      collect = true;
    }
    if (collect) {
      descendants.add(n);
    }
    super.visit(n, arg);
    if (n == target) {
      collect = false;
    }
  }

  public void visit(SigDecl n, Object arg) {
    if (visited.contains(n)) {
      return;
    }
    if (n == target) {
      collect = true;
    }
    if (collect) {
      descendants.add(n);
    }
    super.visit(n, arg);
    if (n == target) {
      collect = false;
    }
  }

  public void visit(FieldDecl n, Object arg) {
    if (visited.contains(n)) {
      return;
    }
    if (n == target) {
      collect = true;
    }
    if (collect) {
      descendants.add(n);
    }
    super.visit(n, arg);
    if (n == target) {
      collect = false;
    }
  }

  public void visit(ParamDecl n, Object arg) {
    if (visited.contains(n)) {
      return;
    }
    if (n == target) {
      collect = true;
    }
    if (collect) {
      descendants.add(n);
    }
    super.visit(n, arg);
    if (n == target) {
      collect = false;
    }
  }

  public void visit(VarDecl n, Object arg) {
    if (visited.contains(n)) {
      return;
    }
    if (n == target) {
      collect = true;
    }
    if (collect) {
      descendants.add(n);
    }
    super.visit(n, arg);
    if (n == target) {
      collect = false;
    }
  }

  public void visit(ExprOrFormula n, Object arg) {
    if (visited.contains(n)) {
      return;
    }
    if (n == target) {
      collect = true;
    }
    if (collect) {
      descendants.add(n);
    }
    super.visit(n, arg);
    if (n == target) {
      collect = false;
    }
  }

  public void visit(SigExpr n, Object arg) {
    if (visited.contains(n)) {
      return;
    }
    if (n == target) {
      collect = true;
    }
    if (collect) {
      descendants.add(n);
    }
    super.visit(n, arg);
    if (n == target) {
      collect = false;
    }
  }

  public void visit(FieldExpr n, Object arg) {
    if (visited.contains(n)) {
      return;
    }
    if (n == target) {
      collect = true;
    }
    if (collect) {
      descendants.add(n);
    }
    super.visit(n, arg);
    if (n == target) {
      collect = false;
    }
  }

  public void visit(VarExpr n, Object arg) {
    if (visited.contains(n)) {
      return;
    }
    if (n == target) {
      collect = true;
    }
    if (collect) {
      descendants.add(n);
    }
    super.visit(n, arg);
    if (n == target) {
      collect = false;
    }
  }

  public void visit(UnaryExpr n, Object arg) {
    if (visited.contains(n)) {
      return;
    }
    if (n == target) {
      collect = true;
    }
    if (collect) {
      // Only count meaningful AST Node
      if (n.getOp() != UnaryExpr.UnaryOp.NOOP) {
        descendants.add(n);
      }
    }
    super.visit(n, arg);
    if (n == target) {
      collect = false;
    }
  }

  public void visit(UnaryFormula n, Object arg) {
    if (visited.contains(n)) {
      return;
    }
    if (n == target) {
      collect = true;
    }
    if (collect) {
      descendants.add(n);
    }
    super.visit(n, arg);
    if (n == target) {
      collect = false;
    }
  }

  public void visit(BinaryExpr n, Object arg) {
    if (visited.contains(n)) {
      return;
    }
    if (n == target) {
      collect = true;
    }
    if (collect) {
      descendants.add(n);
    }
    super.visit(n, arg);
    if (n == target) {
      collect = false;
    }
  }

  public void visit(BinaryFormula n, Object arg) {
    if (visited.contains(n)) {
      return;
    }
    if (n == target) {
      collect = true;
    }
    if (collect) {
      descendants.add(n);
    }
    super.visit(n, arg);
    if (n == target) {
      collect = false;
    }
  }

  public void visit(ListExpr n, Object arg) {
    if (visited.contains(n)) {
      return;
    }
    if (n == target) {
      collect = true;
    }
    if (collect) {
      descendants.add(n);
    }
    super.visit(n, arg);
    if (n == target) {
      collect = false;
    }
  }

  public void visit(ListFormula n, Object arg) {
    if (visited.contains(n)) {
      return;
    }
    if (n == target) {
      collect = true;
    }
    if (collect) {
      descendants.add(n);
    }
    super.visit(n, arg);
    if (n == target) {
      collect = false;
    }
  }

  public void visit(CallExpr n, Object arg) {
    if (visited.contains(n)) {
      return;
    }
    if (n == target) {
      collect = true;
    }
    if (collect) {
      descendants.add(n);
    }
    super.visit(n, arg);
    if (n == target) {
      collect = false;
    }
  }

  public void visit(CallFormula n, Object arg) {
    if (visited.contains(n)) {
      return;
    }
    if (n == target) {
      collect = true;
    }
    if (collect) {
      descendants.add(n);
    }
    super.visit(n, arg);
    if (n == target) {
      collect = false;
    }
  }

  public void visit(QtExpr n, Object arg) {
    if (visited.contains(n)) {
      return;
    }
    if (n == target) {
      collect = true;
    }
    if (collect) {
      descendants.add(n);
    }
    super.visit(n, arg);
    if (n == target) {
      collect = false;
    }
  }

  public void visit(QtFormula n, Object arg) {
    if (visited.contains(n)) {
      return;
    }
    if (n == target) {
      collect = true;
    }
    if (collect) {
      descendants.add(n);
    }
    super.visit(n, arg);
    if (n == target) {
      collect = false;
    }
  }

  public void visit(ITEExpr n, Object arg) {
    if (visited.contains(n)) {
      return;
    }
    if (n == target) {
      collect = true;
    }
    if (collect) {
      descendants.add(n);
    }
    super.visit(n, arg);
    if (n == target) {
      collect = false;
    }
  }

  public void visit(ITEFormula n, Object arg) {
    if (visited.contains(n)) {
      return;
    }
    if (n == target) {
      collect = true;
    }
    if (collect) {
      descendants.add(n);
    }
    super.visit(n, arg);
    if (n == target) {
      collect = false;
    }
  }

  public void visit(LetExpr n, Object arg) {
    if (visited.contains(n)) {
      return;
    }
    if (n == target) {
      collect = true;
    }
    if (collect) {
      descendants.add(n);
    }
    super.visit(n, arg);
    if (n == target) {
      collect = false;
    }
  }

  public void visit(ConstExpr n, Object arg) {
    if (visited.contains(n)) {
      return;
    }
    if (n == target) {
      collect = true;
    }
    if (collect) {
      descendants.add(n);
    }
    super.visit(n, arg);
    if (n == target) {
      collect = false;
    }
  }

  public void visit(Body n, Object arg) {
    if (visited.contains(n)) {
      return;
    }
    if (n == target) {
      collect = true;
    }
    if (collect) {
      descendants.add(n);
    }
    super.visit(n, arg);
    if (n == target) {
      collect = false;
    }
  }

  public void visit(Predicate n, Object arg) {
    if (visited.contains(n)) {
      return;
    }
    if (n == target) {
      collect = true;
    }
    if (collect) {
      descendants.add(n);
    }
    super.visit(n, arg);
    if (n == target) {
      collect = false;
    }
  }

  public void visit(Function n, Object arg) {
    if (visited.contains(n)) {
      return;
    }
    if (n == target) {
      collect = true;
    }
    if (collect) {
      descendants.add(n);
    }
    super.visit(n, arg);
    if (n == target) {
      collect = false;
    }
  }

  public void visit(Fact n, Object arg) {
    if (visited.contains(n)) {
      return;
    }
    if (n == target) {
      collect = true;
    }
    if (collect) {
      descendants.add(n);
    }
    super.visit(n, arg);
    if (n == target) {
      collect = false;
    }
  }

  public void visit(Assertion n, Object arg) {
    if (visited.contains(n)) {
      return;
    }
    if (n == target) {
      collect = true;
    }
    if (collect) {
      descendants.add(n);
    }
    super.visit(n, arg);
    if (n == target) {
      collect = false;
    }
  }

  public void visit(Run n, Object arg) {
    if (visited.contains(n)) {
      return;
    }
    if (n == target) {
      collect = true;
    }
    if (collect) {
      descendants.add(n);
    }
    super.visit(n, arg);
    if (n == target) {
      collect = false;
    }
  }

  public void visit(Check n, Object arg) {
    if (visited.contains(n)) {
      return;
    }
    if (n == target) {
      collect = true;
    }
    if (collect) {
      descendants.add(n);
    }
    super.visit(n, arg);
    if (n == target) {
      collect = false;
    }
  }
}
