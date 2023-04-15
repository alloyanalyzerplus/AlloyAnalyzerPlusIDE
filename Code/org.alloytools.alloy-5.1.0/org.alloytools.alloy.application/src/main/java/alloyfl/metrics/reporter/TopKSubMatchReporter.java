package alloyfl.metrics.reporter;

import static alloyfl.metrics.util.Util.isSubtree;

import alloyfl.metrics.util.CountReportInfo;
import alloyfl.metrics.util.UnifiedNodeInfo;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import parser.ast.nodes.Node;

public class TopKSubMatchReporter {

  public CountReportInfo report(UnifiedNodeInfo nodeInfo, int topK) {
    boolean foundFault = false;
    Set<Integer> faultIds = new HashSet<>();
    List<Node> rankedNodes = nodeInfo.getRankedNodes();
    for (int i = 0; i < Math.min(topK, rankedNodes.size()); i++) {
      for (Map.Entry<Node, Integer> faultyNodeAndId : nodeInfo.getFaultyNodes().entrySet()) {
        Node faultyNode = faultyNodeAndId.getKey();
        int faultId = faultyNodeAndId.getValue();
        if (isSubtree(rankedNodes.get(i), faultyNode)) {
          faultIds.add(faultId);
          foundFault = true;
        }
      }
    }
    return new CountReportInfo(getClass().getSimpleName(), foundFault, topK, faultIds.size(),
        nodeInfo.getTotalNodeNum());
  }
}
