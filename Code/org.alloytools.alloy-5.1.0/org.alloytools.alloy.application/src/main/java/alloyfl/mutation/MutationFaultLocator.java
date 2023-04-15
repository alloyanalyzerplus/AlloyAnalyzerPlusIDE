package alloyfl.mutation;

import static parser.etc.Context.logger;
import static parser.util.AlloyUtil.countDescendantNum;
import static parser.util.StringUtil.afterSubstring;
import static parser.util.StringUtil.beforeSubstring;
import static parser.util.Util.printMutationFaultLocatorUsage;

import alloyfl.coverage.util.TestResult;
import alloyfl.coverage.util.TestRunner;
import alloyfl.hybrid.visitor.DescendantCollector;
import alloyfl.mutation.opt.FaultLocatorOpt;
import alloyfl.mutation.util.ScoreInfo;
import alloyfl.mutation.visitor.MutationBasedFaultDetector;
import edu.mit.csail.sdg.parser.CompModule;
import java.nio.file.Paths;
import java.util.Collection;
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

public class MutationFaultLocator {

  public static void localize(FaultLocatorOpt opt) {
    CompModule modelModule = AlloyUtil.compileAlloyModule(opt.getModelPath());
    assert modelModule != null;
    ModelUnit modelUnit = new ModelUnit(null, modelModule);
    List<TestResult> testResults = TestRunner
        .runTests(modelUnit, opt.getTestSuitePath(), opt.getPSV(), opt.getOptions());
    Set<Node> nodesCoveredByFailingTests = testResults.stream()
        .filter(TestResult::isFailed)
        .map(TestResult::getRelevantNodes)
        .flatMap(Collection::stream)
        .collect(Collectors.toSet());
    List<Boolean> testBooleanResults = testResults
        .stream()
        // True means the test passes.
        .map(testResult -> !testResult.isFailed())
        .collect(Collectors.toList());
    String modelName = beforeSubstring(afterSubstring(opt.getModelPath(), Names.SLASH, true),
        Names.DOT, false);
    // Count descendants to break tie when ranking nodes.
    Map<Node, Integer> descNum = countDescendantNum(modelUnit);
    Set<Node> visitedNodes = new HashSet<>();
    for (Node coveredNode : nodesCoveredByFailingTests) {
      // Collect all descendants of the ranked node.
      DescendantCollector descendantCollector = new DescendantCollector(coveredNode, visitedNodes);
      modelUnit.accept(descendantCollector, null);
      visitedNodes.addAll(descendantCollector.getDescendants());
    }
    // Mutate descendants.
    MutationBasedFaultDetector mbfd = new MutationBasedFaultDetector(testBooleanResults, opt,
        visitedNodes);
    modelUnit.accept(mbfd, null);
    // Rank nodes.
    List<ScoreInfo> scoreInfos = mbfd.rankNode(descNum);
    // Score > 0 holds because we filter out others in MutationBasedFaultDetector.
    List<Node> reportedNodes = scoreInfos.stream()
        .map(ScoreInfo::getNode)
        .collect(Collectors.toList());
    Codec.serialize(
        Pair.of(modelUnit, reportedNodes),
        Paths.get(opt.getResultDirPath(), modelName + Names.DOT_LST).toString());
  }

  public static void main(String[] args) {
//    args = new String[]{
//        "experiments/gen/2nd/handshake/UOI_UOI_5708.als",
//        "experiments/test-suite/alloyfl.mutation-based/100/handshake.als",
//        "XXX",
//        "5",
//        "ochiai",
//        "experiments/results/2nd/handshake/ochiai/100"
//    };
    if (args.length != 5 && args.length != 6) {
      logger.error("Wrong number of arguments: " + args.length);
      printMutationFaultLocatorUsage();
      return;
    }
    int argCount = 0;
    String modelPath = args[argCount++];
    String testPath = args[argCount++];
    String correctModelPath = (args.length == 6 ? args[argCount++] : "");
    int scope = Integer.valueOf(args[argCount++]);
    String formula = args[argCount++];
    String resultDirPath = args[argCount];
    if (!FileUtil.fileExists(modelPath)) {
      logger.error("Cannot find model at " + modelPath);
      printMutationFaultLocatorUsage();
      return;
    }
    if (!FileUtil.fileExists(testPath)) {
      logger.error("Cannot find test file at " + testPath);
      printMutationFaultLocatorUsage();
      return;
    }
    FileUtil.createDirsIfNotExist(resultDirPath);
    FaultLocatorOpt opt = new FaultLocatorOpt(modelPath, correctModelPath, testPath, scope,
        formula, resultDirPath);
    localize(opt);
  }
}
