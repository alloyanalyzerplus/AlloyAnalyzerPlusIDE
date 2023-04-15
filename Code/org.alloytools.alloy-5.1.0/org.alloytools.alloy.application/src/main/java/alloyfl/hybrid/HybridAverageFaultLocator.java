package alloyfl.hybrid;

import static parser.etc.Context.logger;
import static parser.util.AlloyUtil.countDescendantNum;
import static parser.util.StringUtil.afterSubstring;
import static parser.util.StringUtil.beforeSubstring;
import static parser.util.Util.printHybridAverageFaultLocatorUsage;

import alloyfl.coverage.util.CoverageScoreFormula;
import alloyfl.coverage.util.TestResult;
import alloyfl.coverage.util.TestRunner;
import alloyfl.hybrid.opt.HybridFaultLocatorOpt;
import alloyfl.hybrid.visitor.DescendantCollector;
import alloyfl.mutation.util.MutationScoreFormula;
import alloyfl.mutation.util.ScoreInfo;
import alloyfl.mutation.visitor.MutationBasedFaultDetector;
import edu.mit.csail.sdg.parser.CompModule;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
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

public class HybridAverageFaultLocator {

  private static final Map<MutationScoreFormula, CoverageScoreFormula> MUTATION_FORMULA_TO_COVERAGE_FORMULA;

  static {
    MUTATION_FORMULA_TO_COVERAGE_FORMULA = new LinkedHashMap<>();
    MUTATION_FORMULA_TO_COVERAGE_FORMULA
        .put(MutationScoreFormula.TARANTULA, CoverageScoreFormula.TARANTULA);
    MUTATION_FORMULA_TO_COVERAGE_FORMULA
        .put(MutationScoreFormula.OCHIAI, CoverageScoreFormula.OCHIAI);
    MUTATION_FORMULA_TO_COVERAGE_FORMULA.put(MutationScoreFormula.OP2, CoverageScoreFormula.OP2);
    MUTATION_FORMULA_TO_COVERAGE_FORMULA
        .put(MutationScoreFormula.BARINEL, CoverageScoreFormula.BARINEL);
    MUTATION_FORMULA_TO_COVERAGE_FORMULA
        .put(MutationScoreFormula.DSTAR, CoverageScoreFormula.DSTAR);
  }

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
    // We want to make sure that the SBFL and MBFL formulas are same.

    // We use Ochiai formula for now.
    List<Pair<Node, Double>> rankedResultsByCoverageFL = MUTATION_FORMULA_TO_COVERAGE_FORMULA
        .get(opt.getFormula())
        .rank(testResults, descNum);
    // Keep the order of the node in descending suspiciousness order.
    Map<Node, Double> nodeToScoreByCoverageFL = new LinkedHashMap<>();
    rankedResultsByCoverageFL.stream()
        .filter(pair -> nodesCoveredByFailingTests.contains(pair.a))
        .forEach(pair -> nodeToScoreByCoverageFL.put(pair.a, pair.b));
    // Run mutation-based fault localization.
    List<Boolean> testBooleanResults = testResults
        .stream()
        // True means the test passes.
        .map(testResult -> !testResult.isFailed())
        .collect(Collectors.toList());
    Map<Node, Node> descendant2root = new HashMap<>();
    Set<Node> visitedNodes = new HashSet<>();
    nodeToScoreByCoverageFL.forEach((rankedNode, score) -> {
      // Collect all descendants of the ranked node.
      DescendantCollector descendantCollector = new DescendantCollector(rankedNode, visitedNodes);
      modelUnit.accept(descendantCollector, null);
      // Descendants should never overlap if we use coverage based technique, but we still use
      // visitedNodes in case if we want to switch to other techniques.
      descendantCollector.getDescendants().forEach(node -> {
        descendant2root.put(node, rankedNode);
        visitedNodes.add(node);
      });
    });
    // Mutate descendants.
    MutationBasedFaultDetector mbfd = new MutationBasedFaultDetector(testBooleanResults, opt,
        visitedNodes);
    modelUnit.accept(mbfd, null);
    Map<Node, List<ScoreInfo>> rootToDescendantScoreInfo = new HashMap<>();
    // Group score info.
    for (ScoreInfo scoreInfo : mbfd.constructScoreInfos(descNum)) {
      Node root = descendant2root.get(scoreInfo.getNode());
      assert root != null;
      if (!rootToDescendantScoreInfo.containsKey(root)) {
        rootToDescendantScoreInfo.put(root, new ArrayList<>());
      }
      rootToDescendantScoreInfo.get(root).add(scoreInfo);
    }
    List<Node> reportedNodes = rankNodesByAverage(nodeToScoreByCoverageFL,
        rootToDescendantScoreInfo, descNum, opt.getMutationProportion());
    String modelName = beforeSubstring(afterSubstring(opt.getModelPath(), Names.SLASH, true),
        Names.DOT, false);
    Codec.serialize(
        Pair.of(modelUnit, reportedNodes),
        Paths.get(opt.getResultDirPath(), modelName + Names.DOT_LST).toString());
  }

  public static List<Node> rankNodesByAverage(Map<Node, Double> nodeToScoreByCoverageFL,
      Map<Node, List<ScoreInfo>> rootToDescendantScoreInfo, Map<Node, Integer> descNum,
      double mutationflProportion) {
    // Rank nodes by groups.
    List<Pair<Node, Double>> rankedNodeAndScore = new ArrayList<>();
    Set<Node> visitedNodes = new HashSet<>();
    nodeToScoreByCoverageFL.forEach((rankedNode, scoreByCoverageFL) -> {
      if (rootToDescendantScoreInfo.containsKey(rankedNode)) {
        List<ScoreInfo> scoreInfos = rootToDescendantScoreInfo.get(rankedNode);
        scoreInfos.forEach(scoreInfo -> {
          Node node = scoreInfo.getNode();
          // Use the average suspiciousness score from both SBFL and MBFL for the node.
          if (mutationflProportion > 0) {
            rankedNodeAndScore.add(Pair.of(node,
                scoreByCoverageFL * (1 - mutationflProportion)
                    + scoreInfo.getScore() * mutationflProportion));
            visitedNodes.add(node);
          }
        });
      }
      if (mutationflProportion < 1 && visitedNodes.add(rankedNode)) {
        // If the declaring paragraph node is not reported by MBFL, then we use SBFL's score.
        rankedNodeAndScore.add(Pair.of(rankedNode, scoreByCoverageFL));
      }
    });
    rankedNodeAndScore.sort((p1, p2) -> {
      int cmpRes = Double.compare(p2.b, p1.b);
      if (cmpRes != 0) {
        return cmpRes;
      }
      return Integer.compare(descNum.get(p1.a), descNum.get(p2.a));
    });
    return rankedNodeAndScore.stream().map(pair -> pair.a).collect(Collectors.toList());
  }

  public static void main(String... args) {
    if (args.length != 6) {
      logger.error("Wrong number of arguments: " + args.length);
      printHybridAverageFaultLocatorUsage();
      return;
    }
    String modelPath = args[0];
    String testPath = args[1];
    int scope = Integer.valueOf(args[2]);
    String formula = args[3];
    String resultDirPath = args[4];
    double mutationProportion = Double.valueOf(args[5]);
    if (!FileUtil.fileExists(modelPath)) {
      logger.error("Cannot find model at " + modelPath);
      printHybridAverageFaultLocatorUsage();
      return;
    }
    if (!FileUtil.fileExists(testPath)) {
      logger.error("Cannot find test file at " + testPath);
      printHybridAverageFaultLocatorUsage();
      return;
    }
    if (mutationProportion < 0 || mutationProportion > 1.0) {
      logger.error("MutationProportion out of bound " + mutationProportion);
      printHybridAverageFaultLocatorUsage();
      return;
    }
    FileUtil.createDirsIfNotExist(resultDirPath);
    HybridFaultLocatorOpt opt = new HybridFaultLocatorOpt(modelPath, testPath, scope, formula,
        resultDirPath, mutationProportion);
    localize(opt);
  }
}
