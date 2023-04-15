package alloyfl.metrics.reporter;

import alloyfl.metrics.util.CountReportInfo;
import alloyfl.metrics.util.UnifiedNodeInfo;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import parser.ast.nodes.Node;

public class TopKExactMatchReporter {

  public CountReportInfo report(UnifiedNodeInfo nodeInfo, int topK) {
    Set<Integer> faultIds = new HashSet<>();
    for (Map.Entry<Node, Integer> faultyNodeAndId : nodeInfo.getFaultyNodes().entrySet()) {
      Node faultyNode = faultyNodeAndId.getKey();
      int faultId = faultyNodeAndId.getValue();
      List<Node> rankedNodes = nodeInfo.getRankedNodes();
      for (int i = 0; i < Math.min(topK, rankedNodes.size()); i++) {
        // Since faulty node is unique, so we will not double count the exactly matched node.
        if (rankedNodes.get(i) == faultyNode) {
          faultIds.add(faultId);
          break;
        }
      }
    }
    return new CountReportInfo(getClass().getSimpleName(), !faultIds.isEmpty(), topK,
        faultIds.size(), nodeInfo.getTotalNodeNum());
  }
}
