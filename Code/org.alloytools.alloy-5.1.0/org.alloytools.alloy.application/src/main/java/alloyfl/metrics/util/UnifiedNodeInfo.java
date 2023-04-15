package alloyfl.metrics.util;

import java.util.List;
import java.util.Map;
import parser.ast.nodes.Node;

public class UnifiedNodeInfo {
  private final List<Node> rankedNodes;
  private final Map<Node, Integer> faultyNodes;
  private final int totalNodeNum;

  public UnifiedNodeInfo(List<Node> rankedNodes, Map<Node, Integer> faultyNodes, int totalNodeNum) {
    this.rankedNodes = rankedNodes;
    this.faultyNodes = faultyNodes;
    this.totalNodeNum = totalNodeNum;
  }

  public List<Node> getRankedNodes() {
    return rankedNodes;
  }

  public Map<Node, Integer> getFaultyNodes() {
    return faultyNodes;
  }

  public int getTotalNodeNum() {
    return totalNodeNum;
  }
}
