package alloyfl.hybrid;

import static parser.etc.Context.logger;
import static parser.util.AlloyUtil.computeNodePathAsString;
import static parser.util.AlloyUtil.countDescendantNum;
import static parser.util.AlloyUtil.findFirstParentIn;
import static parser.util.StringUtil.afterSubstring;
import static parser.util.StringUtil.beforeSubstring;
import static parser.util.Util.printAdvancedHybridAverageFaultLocatorUsage;

import alloyfl.coverage.util.CoverageScoreFormula;
import alloyfl.coverage.util.TestResult;
import alloyfl.coverage.util.TestRunner;
import alloyfl.hybrid.opt.AdvancedHybridFaultLocatorOpt;
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
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import parser.ast.nodes.FieldExpr;
import parser.ast.nodes.ModelUnit;
import parser.ast.nodes.Node;
import parser.ast.nodes.SigExpr;
import parser.ast.nodes.VarExpr;
import parser.ast.visitor.PrettyStringVisitor;
import parser.ast.visitor.VoidVisitorAdapter;
import parser.etc.Names;
import parser.etc.Pair;
import parser.util.AlloyUtil;
import parser.util.Codec;
import parser.util.FileUtil;

public class AdvancedHybridAverageFaultLocatorCopy {

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
  public static void localize(AdvancedHybridFaultLocatorOpt opt) {
    CompModule modelModule = AlloyUtil.compileAlloyModule(opt.getModelPath());
    assert modelModule != null;
    ModelUnit modelUnit = new ModelUnit(null, modelModule);
    // Run coverage based fault localization.
    List<TestResult> testResults = TestRunner
        .runTests(modelUnit, opt.getTestSuitePath(), opt.getPSV(), opt.getOptions());
    // True means the test passes.
    List<Boolean> testBooleanResults = new ArrayList<>();
    Set<Node> nodesCoveredByFailingTests = testResults.stream()
        .filter(testResult -> {
          testBooleanResults.add(!testResult.isFailed());
          return testResult.isFailed();
        })
        .map(TestResult::getRelevantNodes)
        .flatMap(Collection::stream)
        .collect(Collectors.toSet());
    // Count descendants to break tie when ranking nodes.
    Map<Node, Integer> descNum = countDescendantNum(modelUnit);
    // We want to make sure that the SBFL and MBFL formulas are same.
    // We use Ochiai formula for now.
    List<Pair<Node, Double>> nodeAndScoreByCoverageFL = MUTATION_FORMULA_TO_COVERAGE_FORMULA
        .get(opt.getFormula())
        .computeNodeAndScore(testResults, descNum);
    // Run mutation-based fault localization.
    // Mutate descendants.
    MutationBasedFaultDetector mbfd = new MutationBasedFaultDetector(testBooleanResults, opt, null);
    modelUnit.accept(mbfd, null);
    Map<Node, List<ScoreInfo>> rootToDescendantScoreInfo = new HashMap<>();
    // Group score info.
    for (ScoreInfo scoreInfo : mbfd.constructScoreInfos(descNum)) {
      Node root = Objects
          .requireNonNull(findFirstParentIn(scoreInfo.getNode(), nodesCoveredByFailingTests));
      if (!rootToDescendantScoreInfo.containsKey(root)) {
        rootToDescendantScoreInfo.put(root, new ArrayList<>());
      }
      rootToDescendantScoreInfo.get(root).add(scoreInfo);
    }
    // Collect unsat nodes and all descendants.
    Set<Node> unsatNodesToExclude = nodesToExcludeFromUnsatCore(modelUnit);
//    System.out.println("Unsat nodes to exclude:");
//    unsatNodesToExclude.forEach(node -> {
//      System.out.println("Node type: " + node.getClass().getName());
//      System.out.println(node.accept(new PrettyStringVisitor(), null));
//      System.out.println("=====");
//    });
    Set<Node> visitedNodes = new HashSet<>();
    Map<Node, Integer> unsatDescendantToRootSize = new HashMap<>();
    Map<Node, Node> unsatDecendantToRoot = new HashMap<>();
    Set<Node> unsatNodes = new HashSet<>();
    testResults.stream()
        .filter(TestResult::isFailed)
        .filter(testResult -> testResult.getUnsatNodes() != null)
        // Collect all unsat core nodes.
        .flatMap(testResult -> testResult.getUnsatNodes().stream())
        .forEach(node -> {
          if (unsatNodesToExclude.contains(node)) {
            return;
          }
          unsatNodes.add(node);
          DescendantCollector descendantCollector = new DescendantCollector(node, visitedNodes);
          modelUnit.accept(descendantCollector, null);
          int rootSize = descendantCollector.getDescendants().size();
          descendantCollector.getDescendants()
              .forEach(descendant -> {
                unsatDescendantToRootSize.put(descendant, rootSize);
                unsatDecendantToRoot.put(descendant, node);
              });
        });
    List<Node> reportedNodes = rankNodesByAverage(nodeAndScoreByCoverageFL,
        rootToDescendantScoreInfo, unsatDescendantToRootSize, unsatDecendantToRoot, unsatNodes,
        descNum);
    String modelName = beforeSubstring(afterSubstring(opt.getModelPath(), Names.SLASH, true),
        Names.DOT, false);
    Codec.serialize(
        Pair.of(modelUnit, reportedNodes),
        Paths.get(opt.getResultDirPath(), modelName + Names.DOT_LST).toString());
  }

  private static List<Node> rankNodesByAverage(List<Pair<Node, Double>> nodeAndScoreByCoverageFL,
      Map<Node, List<ScoreInfo>> rootToDescendantScoreInfo,
      Map<Node, Integer> unsatDescendantToRootSize,
      Map<Node, Node> unsatDescendantToRoot,
      Set<Node> unsatNodes,
      Map<Node, Integer> descNum) {
    logger.temp("Original unsat nodes");
    unsatNodes.forEach(unsatNode -> {
      logger.temp(
          "unsat node size: " + unsatDescendantToRootSize.get(unsatNode) + ", type: " + unsatNode
              .getClass().getName());
      logger.temp(computeNodePathAsString(unsatNode, new PrettyStringVisitor()));
      logger.temp("=====");
    });
    // Rank nodes by groups.
    List<Pair<Node, Double>> rankedNodeAndScore = new ArrayList<>();
    Set<Node> mutatedNodes = new HashSet<>();
    nodeAndScoreByCoverageFL.forEach(pair -> {
      Node nodeByCoverageFL = pair.a;
      double scoreByCoverageFL = pair.b;
      if (rootToDescendantScoreInfo.containsKey(nodeByCoverageFL)) {
        List<ScoreInfo> scoreInfos = rootToDescendantScoreInfo.get(nodeByCoverageFL);
        scoreInfos.forEach(scoreInfo -> {
          Node node = scoreInfo.getNode();
          // Use the average suspiciousness score from both SBFL and MBFL for the node.
          double averageScore =
              (scoreByCoverageFL + scoreInfo.getScore()) / 2.0;
          rankedNodeAndScore
              .add(Pair.of(node,
                  adjustSuspiciousScore(node, unsatDescendantToRootSize, unsatDescendantToRoot,
                      unsatNodes, averageScore)));
          mutatedNodes.add(node);
        });
      }
      if (mutatedNodes.add(nodeByCoverageFL)) {
        // If the declaring paragraph node is not reported by MBFL, then we use SBFL's score.
        rankedNodeAndScore.add(Pair.of(nodeByCoverageFL,
            adjustSuspiciousScore(nodeByCoverageFL, unsatDescendantToRootSize, unsatDescendantToRoot,
                unsatNodes,
                scoreByCoverageFL)));
      }
    });
    Map<Node, Double> nodeToScore = rankedNodeAndScore.stream()
        .collect(Collectors.toMap(pair -> pair.a, pair -> pair.b));
    logger.temp("Unsat nodes that do not contain mutated nodes");
    unsatNodes.forEach(unsatNodeWithNoMutatedDescendants -> {
      logger.temp(unsatNodeWithNoMutatedDescendants.accept(new PrettyStringVisitor(), null));
      logger.temp("=====");
      // If the unsat node has no mutated descendants in MBFL, we use the score of the first mutated
      // ancestor as the base to compute the score.
      Node cur = unsatNodeWithNoMutatedDescendants;
      while (cur != null && !nodeToScore.containsKey(cur)) {
        cur = cur.getParent();
      }
      double score = nodeToScore.getOrDefault(cur, 0.0) + 1.0 / (unsatDescendantToRootSize
          .get(unsatNodeWithNoMutatedDescendants) + 9);
//      double score = Math.sqrt(nodeToScore.getOrDefault(cur, 0.0));
      rankedNodeAndScore.add(Pair.of(unsatNodeWithNoMutatedDescendants, score));
    });
    rankedNodeAndScore.sort((p1, p2) -> {
      int cmpRes = Double.compare(p2.b, p1.b);
      if (cmpRes != 0) {
        return cmpRes;
      }
      return Integer.compare(descNum.get(p1.a), descNum.get(p2.a));
    });
    logger.temp("Final nodes size: " + rankedNodeAndScore.size());
    rankedNodeAndScore.forEach(pair -> {
      logger.temp("node score: " + pair.b);
      logger.temp(pair.a.accept(new PrettyStringVisitor(), null));
      logger.temp("=====");
    });
    return rankedNodeAndScore.stream().map(pair -> pair.a).collect(Collectors.toList());
  }

  private static double adjustSuspiciousScore(Node nodeToCheck,
      Map<Node, Integer> unsatDescendantToRootSize,
      Map<Node, Node> unsatDescendantToRoot,
      Set<Node> unsatNodes,
      double score) {
    if (unsatDescendantToRootSize.containsKey(nodeToCheck)) {
//      System.out.println("Mutated unsat nodes score enhance 1 / " + unsatDescendantToRootSize.get(nodeToCheck) + " = " + (1.0 / (unsatDescendantToRootSize.get(nodeToCheck) + 9)));
//      System.out.println(nodeToCheck.accept(new PrettyStringVisitor(), null));
      logger.temp(
          "Mutated unsat nodes score enhance from score " + score + " to sqrt(score) " + Math
              .sqrt(score));
      logger.temp(nodeToCheck.accept(new PrettyStringVisitor(), null));
      unsatNodes.remove(unsatDescendantToRoot.get(nodeToCheck));
//      return Math.sqrt(score);
      return score + 1.0 / (unsatDescendantToRootSize.get(nodeToCheck) + 9);
    } else {
//      System.out.println("Mutated node not covered by unsat core");
//      System.out.println(nodeToCheck.accept(new PrettyStringVisitor(), null));
    }
    return score;
  }

  private static Set<Node> nodesToExcludeFromUnsatCore(ModelUnit modelUnit) {
    VoidVisitorAdapter<Set<Node>> nodesCollector = new VoidVisitorAdapter<Set<Node>>() {
      @Override
      public void visit(SigExpr n, Set<Node> collectedNodes) {
        collectedNodes.add(n);
      }

      @Override
      public void visit(FieldExpr n, Set<Node> collectedNodes) {
        collectedNodes.add(n);
      }

      @Override
      public void visit(VarExpr n, Set<Node> collectedNodes) {
        collectedNodes.add(n);
      }
    };
    Set<Node> collectedNodes = new HashSet<>();
    modelUnit.accept(nodesCollector, collectedNodes);
    return collectedNodes;
  }

  public static void main(String... args) {
    if (args.length != 5) {
      logger.error("Wrong number of arguments: " + args.length);
      printAdvancedHybridAverageFaultLocatorUsage();
      return;
    }
    String modelPath = args[0];
    String testPath = args[1];
    int scope = Integer.valueOf(args[2]);
    String formula = args[3];
    String resultDirPath = args[4];
    if (!FileUtil.fileExists(modelPath)) {
      logger.error("Cannot find model at " + modelPath);
      printAdvancedHybridAverageFaultLocatorUsage();
      return;
    }
    if (!FileUtil.fileExists(testPath)) {
      logger.error("Cannot find test file at " + testPath);
      printAdvancedHybridAverageFaultLocatorUsage();
      return;
    }
    FileUtil.createDirsIfNotExist(resultDirPath);
    AdvancedHybridFaultLocatorOpt opt = new AdvancedHybridFaultLocatorOpt(modelPath, testPath,
        scope, formula, resultDirPath);
    localize(opt);
  }
}
