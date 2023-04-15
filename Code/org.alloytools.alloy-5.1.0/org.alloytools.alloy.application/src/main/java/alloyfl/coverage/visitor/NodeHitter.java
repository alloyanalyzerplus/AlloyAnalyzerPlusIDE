package alloyfl.coverage.visitor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
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

/**
 * Hit the node and all its descendants, and update the hit map accordingly.
 */
public class NodeHitter extends VoidVisitorAdapter<Map<Node, Integer>> {
  private Collection<Node> nodesToHit;
  private Set<Node> hittedNodes;

  public NodeHitter(Collection<Node> nodesToHit) {
    this.nodesToHit = nodesToHit;
    this.hittedNodes = new HashSet<>();
  }

  @Override
  public void visit(ModelUnit n, Map<Node, Integer> hitMap) {
    if (nodesToHit.contains(n) || hittedNodes.contains(n.getParent())) {
      hitMap.put(n, hitMap.getOrDefault(n, 0) + 1);
      hittedNodes.add(n);
    }
    super.visit(n, hitMap);
  }

  @Override
  public void visit(ModuleDecl n, Map<Node, Integer> hitMap) {
    if (nodesToHit.contains(n) || hittedNodes.contains(n.getParent())) {
      hitMap.put(n, hitMap.getOrDefault(n, 0) + 1);
      hittedNodes.add(n);
    }
    super.visit(n, hitMap);
  }

  @Override
  public void visit(OpenDecl n, Map<Node, Integer> hitMap) {
    if (nodesToHit.contains(n) || hittedNodes.contains(n.getParent())) {
      hitMap.put(n, hitMap.getOrDefault(n, 0) + 1);
      hittedNodes.add(n);
    }
    super.visit(n, hitMap);
  }

  @Override
  public void visit(SigDecl n, Map<Node, Integer> hitMap) {
    if (nodesToHit.contains(n) || hittedNodes.contains(n.getParent())) {
      hitMap.put(n, hitMap.getOrDefault(n, 0) + 1);
      hittedNodes.add(n);
    }
    super.visit(n, hitMap);
  }

  @Override
  public void visit(FieldDecl n, Map<Node, Integer> hitMap) {
    if (nodesToHit.contains(n) || hittedNodes.contains(n.getParent())) {
      hitMap.put(n, hitMap.getOrDefault(n, 0) + 1);
      hittedNodes.add(n);
    }
    super.visit(n, hitMap);
  }

  @Override
  public void visit(ParamDecl n, Map<Node, Integer> hitMap) {
    if (nodesToHit.contains(n) || hittedNodes.contains(n.getParent())) {
      hitMap.put(n, hitMap.getOrDefault(n, 0) + 1);
      hittedNodes.add(n);
    }
    super.visit(n, hitMap);
  }

  @Override
  public void visit(VarDecl n, Map<Node, Integer> hitMap) {
    if (nodesToHit.contains(n) || hittedNodes.contains(n.getParent())) {
      hitMap.put(n, hitMap.getOrDefault(n, 0) + 1);
      hittedNodes.add(n);
    }
    super.visit(n, hitMap);
  }

  @Override
  public void visit(SigExpr n, Map<Node, Integer> hitMap) {
    if (nodesToHit.contains(n) || hittedNodes.contains(n.getParent())) {
      hitMap.put(n, hitMap.getOrDefault(n, 0) + 1);
      hittedNodes.add(n);
    }
    super.visit(n, hitMap);
  }

  @Override
  public void visit(FieldExpr n, Map<Node, Integer> hitMap) {
    if (nodesToHit.contains(n) || hittedNodes.contains(n.getParent())) {
      hitMap.put(n, hitMap.getOrDefault(n, 0) + 1);
      hittedNodes.add(n);
    }
    super.visit(n, hitMap);
  }

  @Override
  public void visit(VarExpr n, Map<Node, Integer> hitMap) {
    if (nodesToHit.contains(n) || hittedNodes.contains(n.getParent())) {
      hitMap.put(n, hitMap.getOrDefault(n, 0) + 1);
      hittedNodes.add(n);
    }
    super.visit(n, hitMap);
  }

  @Override
  public void visit(UnaryExpr n, Map<Node, Integer> hitMap) {
    if (nodesToHit.contains(n) || hittedNodes.contains(n.getParent())) {
      hitMap.put(n, hitMap.getOrDefault(n, 0) + 1);
      hittedNodes.add(n);
    }
    super.visit(n, hitMap);
  }

  @Override
  public void visit(UnaryFormula n, Map<Node, Integer> hitMap) {
    if (nodesToHit.contains(n) || hittedNodes.contains(n.getParent())) {
      hitMap.put(n, hitMap.getOrDefault(n, 0) + 1);
      hittedNodes.add(n);
    }
    super.visit(n, hitMap);
  }

  @Override
  public void visit(BinaryExpr n, Map<Node, Integer> hitMap) {
    if (nodesToHit.contains(n) || hittedNodes.contains(n.getParent())) {
      hitMap.put(n, hitMap.getOrDefault(n, 0) + 1);
      hittedNodes.add(n);
    }
    super.visit(n, hitMap);
  }

  @Override
  public void visit(BinaryFormula n, Map<Node, Integer> hitMap) {
    if (nodesToHit.contains(n) || hittedNodes.contains(n.getParent())) {
      hitMap.put(n, hitMap.getOrDefault(n, 0) + 1);
      hittedNodes.add(n);
    }
    super.visit(n, hitMap);
  }

  @Override
  public void visit(ListExpr n, Map<Node, Integer> hitMap) {
    if (nodesToHit.contains(n) || hittedNodes.contains(n.getParent())) {
      hitMap.put(n, hitMap.getOrDefault(n, 0) + 1);
      hittedNodes.add(n);
    }
    super.visit(n, hitMap);
  }

  @Override
  public void visit(ListFormula n, Map<Node, Integer> hitMap) {
    if (nodesToHit.contains(n) || hittedNodes.contains(n.getParent())) {
      hitMap.put(n, hitMap.getOrDefault(n, 0) + 1);
      hittedNodes.add(n);
    }
    super.visit(n, hitMap);
  }

  @Override
  public void visit(CallExpr n, Map<Node, Integer> hitMap) {
    if (nodesToHit.contains(n) || hittedNodes.contains(n.getParent())) {
      hitMap.put(n, hitMap.getOrDefault(n, 0) + 1);
      hittedNodes.add(n);
    }
    super.visit(n, hitMap);
  }

  @Override
  public void visit(CallFormula n, Map<Node, Integer> hitMap) {
    if (nodesToHit.contains(n) || hittedNodes.contains(n.getParent())) {
      hitMap.put(n, hitMap.getOrDefault(n, 0) + 1);
      hittedNodes.add(n);
    }
    super.visit(n, hitMap);
  }

  @Override
  public void visit(QtExpr n, Map<Node, Integer> hitMap) {
    if (nodesToHit.contains(n) || hittedNodes.contains(n.getParent())) {
      hitMap.put(n, hitMap.getOrDefault(n, 0) + 1);
      hittedNodes.add(n);
    }
    super.visit(n, hitMap);
  }

  @Override
  public void visit(QtFormula n, Map<Node, Integer> hitMap) {
    if (nodesToHit.contains(n) || hittedNodes.contains(n.getParent())) {
      hitMap.put(n, hitMap.getOrDefault(n, 0) + 1);
      hittedNodes.add(n);
    }
    super.visit(n, hitMap);
  }

  @Override
  public void visit(ITEExpr n, Map<Node, Integer> hitMap) {
    if (nodesToHit.contains(n) || hittedNodes.contains(n.getParent())) {
      hitMap.put(n, hitMap.getOrDefault(n, 0) + 1);
      hittedNodes.add(n);
    }
    super.visit(n, hitMap);
  }

  @Override
  public void visit(ITEFormula n, Map<Node, Integer> hitMap) {
    if (nodesToHit.contains(n) || hittedNodes.contains(n.getParent())) {
      hitMap.put(n, hitMap.getOrDefault(n, 0) + 1);
      hittedNodes.add(n);
    }
    super.visit(n, hitMap);
  }

  @Override
  public void visit(LetExpr n, Map<Node, Integer> hitMap) {
    if (nodesToHit.contains(n) || hittedNodes.contains(n.getParent())) {
      hitMap.put(n, hitMap.getOrDefault(n, 0) + 1);
      hittedNodes.add(n);
    }
    super.visit(n, hitMap);
  }

  @Override
  public void visit(ConstExpr n, Map<Node, Integer> hitMap) {
    if (nodesToHit.contains(n) || hittedNodes.contains(n.getParent())) {
      hitMap.put(n, hitMap.getOrDefault(n, 0) + 1);
      hittedNodes.add(n);
    }
    super.visit(n, hitMap);
  }

  @Override
  public void visit(Body n, Map<Node, Integer> hitMap) {
    if (nodesToHit.contains(n) || hittedNodes.contains(n.getParent())) {
      hitMap.put(n, hitMap.getOrDefault(n, 0) + 1);
      hittedNodes.add(n);
    }
    super.visit(n, hitMap);
  }

  @Override
  public void visit(Predicate n, Map<Node, Integer> hitMap) {
    if (nodesToHit.contains(n) || hittedNodes.contains(n.getParent())) {
      hitMap.put(n, hitMap.getOrDefault(n, 0) + 1);
      hittedNodes.add(n);
    }
    super.visit(n, hitMap);
  }

  @Override
  public void visit(Function n, Map<Node, Integer> hitMap) {
    if (nodesToHit.contains(n) || hittedNodes.contains(n.getParent())) {
      hitMap.put(n, hitMap.getOrDefault(n, 0) + 1);
      hittedNodes.add(n);
    }
    super.visit(n, hitMap);
  }

  @Override
  public void visit(Fact n, Map<Node, Integer> hitMap) {
    if (nodesToHit.contains(n) || hittedNodes.contains(n.getParent())) {
      hitMap.put(n, hitMap.getOrDefault(n, 0) + 1);
      hittedNodes.add(n);
    }
    super.visit(n, hitMap);
  }

  @Override
  public void visit(Assertion n, Map<Node, Integer> hitMap) {
    if (nodesToHit.contains(n) || hittedNodes.contains(n.getParent())) {
      hitMap.put(n, hitMap.getOrDefault(n, 0) + 1);
      hittedNodes.add(n);
    }
    super.visit(n, hitMap);
  }

  @Override
  public void visit(Run n, Map<Node, Integer> hitMap) {
    if (nodesToHit.contains(n) || hittedNodes.contains(n.getParent())) {
      hitMap.put(n, hitMap.getOrDefault(n, 0) + 1);
      hittedNodes.add(n);
    }
    super.visit(n, hitMap);
  }

  @Override
  public void visit(Check n, Map<Node, Integer> hitMap) {
    if (nodesToHit.contains(n) || hittedNodes.contains(n.getParent())) {
      hitMap.put(n, hitMap.getOrDefault(n, 0) + 1);
      hittedNodes.add(n);
    }
    super.visit(n, hitMap);
  }
}
