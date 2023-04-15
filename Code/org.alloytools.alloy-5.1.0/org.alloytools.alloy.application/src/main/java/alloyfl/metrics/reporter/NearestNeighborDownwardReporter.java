package alloyfl.metrics.reporter;

import alloyfl.metrics.util.DistanceReportInfo;
import alloyfl.metrics.util.UnifiedNodeInfo;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import parser.ast.nodes.Node;
import parser.ast.nodes.UnaryExpr;
import parser.ast.nodes.UnaryExpr.UnaryOp;

public class NearestNeighborDownwardReporter {

  public DistanceReportInfo report(UnifiedNodeInfo nodeInfo) {
    Set<Node> visitedNodes = new HashSet<>();
    boolean foundFault = false;
    for (Node rankedNode : nodeInfo.getRankedNodes()) {
      // BFS.
      Queue<Node> toVisit = new LinkedList<>();
      toVisit.offer(rankedNode);
      while (!foundFault && !toVisit.isEmpty()) {
        int size = toVisit.size();
        for (int i = 0; i < size; i++) {
          Node n = toVisit.poll();
          if (nodeInfo.getFaultyNodes().containsKey(n)) {
            foundFault = true;
          }
          // If this node has not been visited before, then BFS its subtree.
          if (visitedNodes.add(n)) {
            assert n.getChildren() != null;
            n.getChildren().forEach(toVisit::offer);
          }
        }
      }
      if (foundFault) {
        break;
      }
    }
    // Remove NOOP unary expressions.
    Set<Node> noopNodes = visitedNodes.stream()
        .filter(node -> node instanceof UnaryExpr && ((UnaryExpr) node).getOp() == UnaryOp.NOOP)
        .collect(Collectors.toSet());
    visitedNodes.removeAll(noopNodes);
    return new DistanceReportInfo(getClass().getSimpleName(), foundFault, visitedNodes.size(),
        nodeInfo.getTotalNodeNum());
  }
}
