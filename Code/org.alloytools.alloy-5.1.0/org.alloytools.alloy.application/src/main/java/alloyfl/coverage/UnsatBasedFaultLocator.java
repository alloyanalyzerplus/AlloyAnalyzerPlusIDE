package alloyfl.coverage;

import static parser.etc.Context.logger;
import static parser.util.AlloyUtil.countDescendantNum;
import static parser.util.StringUtil.afterSubstring;
import static parser.util.StringUtil.beforeSubstring;
import static parser.util.Util.printUnsatBasedFaultLocatorUsage;

import alloyfl.coverage.opt.FaultLocatorOpt;
import alloyfl.coverage.util.TestResult;
import alloyfl.coverage.util.TestRunner;
import alloyfl.coverage.visitor.NodeHitter;
import alloyfl.coverage.visitor.UniqueHitNodeCollector;
import edu.mit.csail.sdg.parser.CompModule;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import parser.ast.nodes.ModelUnit;
import parser.ast.nodes.Node;
import parser.etc.Names;
import parser.etc.Pair;
import parser.util.AlloyUtil;
import parser.util.Codec;
import parser.util.FileUtil;

public class UnsatBasedFaultLocator {

  public static void localize(FaultLocatorOpt opt) {
    CompModule modelModule = AlloyUtil.compileAlloyModule(opt.getModelPath());
    assert modelModule != null;
    ModelUnit modelUnit = new ModelUnit(null, modelModule);
    List<TestResult> testResults = TestRunner
        .runTests(modelUnit, opt.getTestSuitePath(), opt.getPSV(), opt.getOptions());
    List<Collection<Node>> nodesReportedByFailingTests = testResults.stream()
        .filter(TestResult::isFailed)
        .filter(testResult -> testResult.getUnsatNodes() != null)
        .map(TestResult::getUnsatNodes)
        .collect(Collectors.toList());
    Map<Node, Integer> hitMap = new HashMap<>();
    for (Collection<Node> nodeToHit : nodesReportedByFailingTests) {
      modelUnit.accept(new NodeHitter(nodeToHit), hitMap);
    }
    UniqueHitNodeCollector collector = new UniqueHitNodeCollector(hitMap);
    modelUnit.accept(collector, null);
    List<Node> reportedNodes = collector.getNodesToReport();
    // Count descendants to break tie when ranking nodes.
    Map<Node, Integer> descNum = countDescendantNum(modelUnit);
    reportedNodes.sort((n1, n2) -> {
      int cmpRes = Integer.compare(hitMap.get(n2), hitMap.get(n1));
      return cmpRes == 0 ? Integer.compare(descNum.get(n1), descNum.get(n2)) : cmpRes;
    });
    String modelName = beforeSubstring(afterSubstring(opt.getModelPath(), Names.SLASH, true),
        Names.DOT, false);
    Codec.serialize(
        Pair.of(modelUnit, reportedNodes),
        Paths.get(opt.getResultDirPath(), modelName + Names.DOT_LST).toString());
  }

  public static void main(String... args) {
//    args = new String[]{"experiments/realbugs/farmerFaulty.als",
//        "experiments/test-suite/alloyfl.mutation-based/100/farmer.als", ""};
    if (args.length != 3) {
      logger.error("Wrong number of arguments: " + args.length);
      printUnsatBasedFaultLocatorUsage();
      return;
    }
    String modelPath = args[0];
    String testPath = args[1];
    String resultDirPath = args[2];
    FileUtil.createDirsIfNotExist(resultDirPath);
    FaultLocatorOpt opt = new FaultLocatorOpt(modelPath, testPath, resultDirPath);
    localize(opt);
  }
}
