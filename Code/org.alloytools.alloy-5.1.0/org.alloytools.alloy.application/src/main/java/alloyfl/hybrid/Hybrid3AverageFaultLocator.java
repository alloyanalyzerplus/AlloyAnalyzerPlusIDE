package alloyfl.hybrid;

import static parser.etc.Context.logger;
import static parser.util.AlloyUtil.countDescendantNum;
import static parser.util.StringUtil.afterSubstring;
import static parser.util.StringUtil.beforeSubstring;
import static parser.util.Util.printHybrid3AverageFaultLocatorUsage;

import alloyfl.coverage.util.CoverageScoreFormula;
import alloyfl.coverage.util.TestResult;
import alloyfl.coverage.util.TestRunner;
import alloyfl.coverage.visitor.NodeHitter;
import alloyfl.hybrid.opt.HybridFaultLocatorOpt;
import alloyfl.hybrid.visitor.AverageScoreCollector;
import alloyfl.hybrid.visitor.DescendantCollector;
import alloyfl.hybrid.visitor.DescendantScorePropagator;
import alloyfl.mutation.util.MutationScoreFormula;
import alloyfl.mutation.visitor.MutationBasedFaultDetector;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import edu.mit.csail.sdg.parser.CompModule;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Comparator;
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

public class Hybrid3AverageFaultLocator {
  private static ImmutableMap<MutationScoreFormula, CoverageScoreFormula> MUTATION_FORMULA_TO_COVERAGE_FORMULA = ImmutableMap.of(
      MutationScoreFormula.TARANTULA, CoverageScoreFormula.TARANTULA,
      MutationScoreFormula.OCHIAI, CoverageScoreFormula.OCHIAI,
      MutationScoreFormula.OP2, CoverageScoreFormula.OP2,
      MutationScoreFormula.BARINEL, CoverageScoreFormula.BARINEL,
      MutationScoreFormula.DSTAR, CoverageScoreFormula.DSTAR
  );

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
    propagateSuspiciousScoresToDescendants(nodeToScoreByCoverageFL);

    // Run mutation-based fault localization.
    List<Boolean> testBooleanResults = testResults
        .stream()
        // True means the test passes.
        .map(testResult -> !testResult.isFailed())
        .collect(Collectors.toList());
    Set<Node> visitedNodes = new HashSet<>();
    nodeToScoreByCoverageFL.forEach((rankedNode, score) -> {
      // Collect all descendants of the ranked node.
      DescendantCollector descendantCollector = new DescendantCollector(rankedNode, visitedNodes);
      modelUnit.accept(descendantCollector, null);
      // Descendants should never overlap if we use coverage based technique, but we still use
      // visitedNodes in case if we want to switch to other techniques.
      visitedNodes.addAll(descendantCollector.getDescendants());
    });
    // Mutate descendants.
    MutationBasedFaultDetector mbfd = new MutationBasedFaultDetector(testBooleanResults, opt,
        visitedNodes);
    modelUnit.accept(mbfd, null);

    Map<Node, Double> nodeToScoreByMutationFL = new HashMap<>();
    mbfd.constructScoreInfos(descNum).forEach(scoreInfo -> nodeToScoreByMutationFL.put(scoreInfo.getNode(), scoreInfo.getScore()));
    propagateSuspiciousScoresToDescendants(nodeToScoreByMutationFL);

    // Run SatUnsatFL
    List<Collection<Node>> nodesReportedByFailingTests = testResults.stream()
        .filter(TestResult::isFailed)
        .map(testResult -> {
          if (testResult.getUnsatNodes() != null) {
            return testResult.getUnsatNodes();
          }
          return testResult.getRelevantNodes();
        }).collect(Collectors.toList());
    Map<Node, Integer> hitMap = new HashMap<>();
    for (Collection<Node> nodeToHit : nodesReportedByFailingTests) {
      modelUnit.accept(new NodeHitter(nodeToHit), hitMap);
    }

//    long failingTestNum = testResults.stream().filter(TestResult::isFailed).count();
    Map<Node, Double> nodeToScoreBySatUnsatFL = new HashMap<>();
    hitMap
        .forEach((node, score) -> nodeToScoreBySatUnsatFL.put(node, score * 1.0 / testResults.size()));
    propagateSuspiciousScoresToDescendants(nodeToScoreBySatUnsatFL);

    // Collect average score
    AverageScoreCollector averageScoreCollector = new AverageScoreCollector(nodeToScoreByCoverageFL, nodeToScoreBySatUnsatFL, nodeToScoreByMutationFL);
    modelUnit.accept(averageScoreCollector, null);
    List<Node> reportedNodes = averageScoreCollector.reportedNodes();
    Map<Node, Double> nodeToScoreOnAverage = averageScoreCollector.getNodeToScoreOnAverage();
    reportedNodes.sort(Comparator.comparing(nodeToScoreOnAverage::get).reversed().thenComparing(descNum::get));

    String modelName = beforeSubstring(afterSubstring(opt.getModelPath(), Names.SLASH, true),
        Names.DOT, false);
    Codec.serialize(
        Pair.of(modelUnit, reportedNodes),
        Paths.get(opt.getResultDirPath(), modelName + Names.DOT_LST).toString());
  }

  private static void propagateSuspiciousScoresToDescendants(Map<Node, Double> nodeToScore) {
    ImmutableList<Node> interestingNodes = ImmutableList.copyOf(nodeToScore.keySet());
    interestingNodes.forEach(node -> {
      DescendantScorePropagator descendantScorePropagator = new DescendantScorePropagator(node, nodeToScore);
      node.accept(descendantScorePropagator, null);
    });
  }

  public static void main(String... args) {
    if (args.length != 5) {
      logger.error("Wrong number of arguments: " + args.length);
      printHybrid3AverageFaultLocatorUsage();
      return;
    }
    String modelPath = args[0];
    String testPath = args[1];
    int scope = Integer.valueOf(args[2]);
    String formula = args[3];
    String resultDirPath = args[4];
    if (!FileUtil.fileExists(modelPath)) {
      logger.error("Cannot find model at " + modelPath);
      printHybrid3AverageFaultLocatorUsage();
      return;
    }
    if (!FileUtil.fileExists(testPath)) {
      logger.error("Cannot find test file at " + testPath);
      printHybrid3AverageFaultLocatorUsage();
      return;
    }
    FileUtil.createDirsIfNotExist(resultDirPath);
    HybridFaultLocatorOpt opt = new HybridFaultLocatorOpt(modelPath, testPath, scope, formula,
        resultDirPath);
    localize(opt);
  }
}
