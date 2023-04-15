package alloyfl.metrics.reporter;

import static alloyfl.metrics.util.Util.getNumberOfChildren;
import static alloyfl.metrics.util.Util.isSubtree;

import alloyfl.metrics.util.DistanceReportInfo;
import alloyfl.metrics.util.UnifiedNodeInfo;
import alloyfl.metrics.visitor.VisitedNodeMarker;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import parser.ast.nodes.Node;

public class WorstCaseDownwardReporter {

  public DistanceReportInfo report(UnifiedNodeInfo nodeInfo) {
    int res = -1; // Should never be -1.
    Set<Node> visitedNodes = new HashSet<>();
    boolean foundFault = false;
    int numberOfExploredNodes = 0;
    for (Node rankedNode : nodeInfo.getRankedNodes()) {
      int numberOfNodesToExplore = getNumberOfChildren(rankedNode, visitedNodes);
      int numberOfFaultyNodes = 0;
      for (Map.Entry<Node, Integer> faultyNodeAndId : nodeInfo.getFaultyNodes().entrySet()) {
        Node faultyNode = faultyNodeAndId.getKey();
        if (isSubtree(rankedNode, faultyNode)) {
          faultyNode.accept(new VisitedNodeMarker(visitedNodes), null);
          foundFault = true;
          numberOfFaultyNodes++;
        }
      }
      if (foundFault) {
        res = numberOfExploredNodes + getNumberOfChildren(rankedNode, visitedNodes)
            + numberOfFaultyNodes;
        break;
      }
      numberOfExploredNodes += numberOfNodesToExplore;
      rankedNode.accept(new VisitedNodeMarker(visitedNodes), null);
    }
    return new DistanceReportInfo(getClass().getSimpleName(), foundFault, res,
        nodeInfo.getTotalNodeNum());
  }
}
