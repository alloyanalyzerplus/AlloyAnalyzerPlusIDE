package alloyfl.hybrid;

import static alloyfl.mutation.util.ScoreInfo.sortScoreInfos;
import static parser.etc.Context.logger;
import static parser.util.AlloyUtil.countDescendantNum;
import static parser.util.StringUtil.afterSubstring;
import static parser.util.StringUtil.beforeSubstring;
import static parser.util.Util.printHybridThresholdFaultLocatorUsage;

import alloyfl.coverage.util.CoverageScoreFormula;
import alloyfl.coverage.util.TestResult;
import alloyfl.coverage.util.TestRunner;
import alloyfl.hybrid.opt.HybridFaultLocatorOpt;
import alloyfl.hybrid.visitor.DescendantCollector;
import alloyfl.mutation.util.ScoreInfo;
import alloyfl.mutation.visitor.MutationBasedFaultDetector;
import edu.mit.csail.sdg.parser.CompModule;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import parser.ast.nodes.ModelUnit;
import parser.ast.nodes.Node;
import parser.etc.Names;
import parser.etc.Pair;
import parser.util.AlloyUtil;
import parser.util.Codec;
import parser.util.FileUtil;

public class HybridThresholdFaultLocator {

  /**
   * Coverage + Mutation + Prioritize nodes with score of 1 suspiciousness.
   */
  public static void localize(HybridFaultLocatorOpt opt) {
    CompModule modelModule = AlloyUtil.compileAlloyModule(opt.getModelPath());
    assert modelModule != null;
    ModelUnit modelUnit = new ModelUnit(null, modelModule);
    // Run coverage based fault localization.
    List<TestResult> testResults = TestRunner
        .runTests(modelUnit, opt.getTestSuitePath(), opt.getPSV(), opt.getOptions());
    Set<Node> nodesCoveredByFailingTests = testResults.stream()
        .filter(TestResult::isFailed)
        .map(TestResult::getRelevantNodes)
        .flatMap(Collection::stream)
        .collect(Collectors.toSet());
    // Count descendants to break tie when ranking nodes.
    Map<Node, Integer> descNum = countDescendantNum(modelUnit);
    // We use Ochiai formula for now.
    List<Pair<Node, Double>> rankedResultsByCoverageFL = CoverageScoreFormula.OCHIAI
        .rank(testResults, descNum);
    List<Node> rankedNodesByCoverageFL = rankedResultsByCoverageFL.stream()
        .map(pair -> pair.a)
        .filter(nodesCoveredByFailingTests::contains)
        .collect(Collectors.toList());
    // Run mutation-based fault localization.
    List<Boolean> testBooleanResults = testResults
        .stream()
        // True means the test passes.
        .map(testResult -> !testResult.isFailed())
        .collect(Collectors.toList());
    Map<Node, Node> node2node = new HashMap<>();
    Set<Node> visitedNodes = new HashSet<>();
    for (Node rankedNode : rankedNodesByCoverageFL) {
      // Collect all descendants of the ranked node.
      DescendantCollector descendantCollector = new DescendantCollector(rankedNode, visitedNodes);
      modelUnit.accept(descendantCollector, null);
      // Descendants should never overlap if we use coverage based technique, but we still use
      // visitedNodes in case if we want to switch to other techniques.
      descendantCollector.getDescendants().forEach(node -> {
        node2node.put(node, rankedNode);
        visitedNodes.add(node);
      });
    }
    // Mutate descendants.
    MutationBasedFaultDetector mbfd = new MutationBasedFaultDetector(testBooleanResults, opt,
        visitedNodes);
    modelUnit.accept(mbfd, null);
    Map<Node, List<ScoreInfo>> node2group = new HashMap<>();
    // Group score info.
    for (ScoreInfo scoreInfo : mbfd.constructScoreInfos(descNum)) {
      Node rootKey = node2node.get(scoreInfo.getNode());
      assert rootKey != null;
      if (!node2group.containsKey(rootKey)) {
        node2group.put(rootKey, new ArrayList<>());
      }
      node2group.get(rootKey).add(scoreInfo);
    }
    List<Node> reportedNodes = rankNodesByThreshold(rankedNodesByCoverageFL, node2group,
        opt.getThreshold());
    String modelName = beforeSubstring(afterSubstring(opt.getModelPath(), Names.SLASH, true),
        Names.DOT, false);
    Codec.serialize(
        Pair.of(modelUnit, reportedNodes),
        Paths.get(opt.getResultDirPath(), modelName + Names.DOT_LST).toString());
  }

  public static List<Node> rankNodesByThreshold(List<Node> rankedNodesByCoverageFL,
      Map<Node, List<ScoreInfo>> node2group, double threshold) {
    // Rank nodes by groups.
    List<ScoreInfo> mostSuspiciousScoreInfos = new ArrayList<>();
    List<Node> lessSuspiciousNodes = new ArrayList<>();
    Set<Node> visitedNodes = new HashSet<>();
    for (Node rankedNode : rankedNodesByCoverageFL) {
      if (node2group.containsKey(rankedNode)) {
        List<ScoreInfo> scoreInfos = node2group.get(rankedNode);
        sortScoreInfos(scoreInfos);
        for (ScoreInfo scoreInfo : scoreInfos) {
          // Score > 0 holds because we filter out others in MutationBasedFaultDetector.
          if (scoreInfo.getScore() >= threshold) {
            mostSuspiciousScoreInfos.add(scoreInfo);
          } else {
            lessSuspiciousNodes.add(scoreInfo.getNode());
          }
          visitedNodes.add(scoreInfo.getNode());
        }
      }
      if (visitedNodes.add(rankedNode)) {
        lessSuspiciousNodes.add(rankedNode);
      }
    }
    sortScoreInfos(mostSuspiciousScoreInfos);
    List<Node> reportedNodes = mostSuspiciousScoreInfos.stream()
        .map(ScoreInfo::getNode)
        .collect(Collectors.toList());
    reportedNodes.addAll(lessSuspiciousNodes);
    return reportedNodes;
  }

  public static void main(String... args) {
    if (args.length != 6) {
      logger.error("Wrong number of arguments: " + args.length);
      printHybridThresholdFaultLocatorUsage();
      return;
    }
    String modelPath = args[0];
    String testPath = args[1];
    int scope = Integer.valueOf(args[2]);
    String formula = args[3];
    double threshold = Double.valueOf(args[4]);
    String resultDirPath = args[5];
    if (!FileUtil.fileExists(modelPath)) {
      logger.error("Cannot find model at " + modelPath);
      printHybridThresholdFaultLocatorUsage();
      return;
    }
    if (!FileUtil.fileExists(testPath)) {
      logger.error("Cannot find test file at " + testPath);
      printHybridThresholdFaultLocatorUsage();
      return;
    }
    FileUtil.createDirsIfNotExist(resultDirPath);
    HybridFaultLocatorOpt opt = new HybridFaultLocatorOpt(modelPath, testPath, scope, formula,
        threshold, resultDirPath);
    localize(opt);
  }
}
