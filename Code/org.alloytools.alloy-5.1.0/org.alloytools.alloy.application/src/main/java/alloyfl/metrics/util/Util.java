package alloyfl.metrics.util;

import alloyfl.coverage.util.SubtreeMatcher;
import alloyfl.metrics.visitor.NodeCountingVisitor;
import alloyfl.metrics.visitor.SubtreeDetector;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import parser.ast.nodes.ModelUnit;
import parser.ast.nodes.Node;
import parser.etc.Pair;
import parser.util.Codec;

public class Util {

  @SuppressWarnings("unchecked")
  public static UnifiedNodeInfo unifyRankedNodesAndFaultyNodes(String faultLocFilePath,
      String faultRankingFilePath) {
    // Deserialize the fault location.
    Pair<ModelUnit, Map<Node, Integer>> modelUnitAndFaultyNodesPair = (Pair<ModelUnit, Map<Node, Integer>>) Codec
        .deserialize(faultLocFilePath);
    assert modelUnitAndFaultyNodesPair != null;
    ModelUnit modelUnitA = modelUnitAndFaultyNodesPair.a;
    Map<Node, Integer> faultyNodes = modelUnitAndFaultyNodesPair.b;
    // Deserialize the ranking node list.
    Pair<ModelUnit, List<Node>> modelUnitAndRankedNodesPair = (Pair<ModelUnit, List<Node>>) Codec
        .deserialize(faultRankingFilePath);
    assert modelUnitAndRankedNodesPair != null;
    ModelUnit modelUnitB = modelUnitAndRankedNodesPair.a;
    List<Node> rankedNodes = modelUnitAndRankedNodesPair.b;
    // Add root so the faulty node will eventually be a subtree of the root.
    rankedNodes.add(modelUnitB);
    SubtreeMatcher matcher = new SubtreeMatcher();
    matcher.matches(modelUnitA, modelUnitB);
    Map<Node, Node> a2b = matcher.getS2D();
    Map<Node, Integer> faultyNodesInRankedTree = faultyNodes.entrySet().stream()
        .filter(entry -> a2b.containsKey(entry.getKey()))
        .map(entry -> Pair.of(a2b.get(entry.getKey()), entry.getValue()))
        .collect(Collectors.toMap(pair -> pair.a, pair -> pair.b));
//    rankedNodes.forEach(node -> {
//      System.out.println(AlloyUtil.computeNodePathAsString(node, new PrettyStringVisitor()));
//      System.out.println("=====");
//    });
    return new UnifiedNodeInfo(rankedNodes, faultyNodesInRankedTree,
        getNumberOfChildren(modelUnitB, new HashSet<>()));
  }

  public static int getNumberOfChildren(Node node, Set<Node> visitedNodes) {
    NodeCountingVisitor nodeCountingVisitor = new NodeCountingVisitor(visitedNodes);
    node.accept(nodeCountingVisitor, null);
    return nodeCountingVisitor.getCnt();
  }

  public static boolean isSubtree(Node root, Node target) {
    SubtreeDetector subtreeDetector = new SubtreeDetector(target);
    root.accept(subtreeDetector, null);
    return subtreeDetector.isSubtree();
  }
}
