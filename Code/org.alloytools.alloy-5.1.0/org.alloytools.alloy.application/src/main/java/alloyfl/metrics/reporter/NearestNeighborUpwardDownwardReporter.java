package alloyfl.metrics.reporter;

import alloyfl.metrics.util.DistanceReportInfo;
import alloyfl.metrics.util.UnifiedNodeInfo;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import parser.ast.nodes.Node;
import parser.ast.nodes.UnaryExpr;
import parser.ast.nodes.UnaryExpr.UnaryOp;

public class NearestNeighborUpwardDownwardReporter {

  public DistanceReportInfo report(UnifiedNodeInfo nodeInfo, int topK) {
    Set<Node> visitedNodes = new HashSet<>();
    boolean foundFault = false;
    List<Node> rankedNodes = nodeInfo.getRankedNodes();
    Queue<Node> toVisit = new LinkedList<>(
        rankedNodes.subList(0, Math.min(topK, rankedNodes.size())));
    while (!foundFault && !toVisit.isEmpty()) {
      int size = toVisit.size();
      for (int i = 0; i < size; i++) {
        Node n = toVisit.poll();
        if (nodeInfo.getFaultyNodes().containsKey(n)) {
          foundFault = true;
        }
        // If this node has not been visited before, then bfs its parent and subtree.
        if (visitedNodes.add(n)) {
          if (n.getParent() != null) {
            toVisit.offer(n.getParent());
          }
          assert n.getChildren() != null;
          n.getChildren().forEach(toVisit::offer);
        }
      }
    }
    // Remove NOOP unary expressions.
    Set<Node> noopNodes = visitedNodes.stream()
        .filter(node -> node instanceof UnaryExpr && ((UnaryExpr) node).getOp() == UnaryOp.NOOP)
        .collect(Collectors.toSet());
    visitedNodes.removeAll(noopNodes);
    return new DistanceReportInfo(getClass().getSimpleName(), foundFault, topK, visitedNodes.size(),
        nodeInfo.getTotalNodeNum());
  }
}
