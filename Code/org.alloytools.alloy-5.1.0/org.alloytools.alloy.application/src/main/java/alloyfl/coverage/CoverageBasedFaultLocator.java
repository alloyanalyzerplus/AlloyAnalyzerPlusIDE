package alloyfl.coverage;

import static parser.etc.Context.logger;
import static parser.util.AlloyUtil.countDescendantNum;
import static parser.util.StringUtil.afterSubstring;
import static parser.util.StringUtil.beforeSubstring;
import static parser.util.Util.printCoverageBasedFaultLocatorUsage;

import alloyfl.coverage.opt.FaultLocatorOpt;
import alloyfl.coverage.util.TestResult;
import alloyfl.coverage.util.TestRunner;
import edu.mit.csail.sdg.parser.CompModule;
import java.nio.file.Paths;
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

public class CoverageBasedFaultLocator {

  public static void localize(FaultLocatorOpt opt) {
    CompModule modelModule = AlloyUtil.compileAlloyModule(opt.getModelPath());
    assert modelModule != null;
    ModelUnit modelUnit = new ModelUnit(null, modelModule);
    List<TestResult> testResults = TestRunner
        .runTests(modelUnit, opt.getTestSuitePath(), opt.getPSV(), opt.getOptions());
    // Count descendants to break tie when ranking nodes.
    Map<Node, Integer> descNum = countDescendantNum(modelUnit);
    // We use Ochiai formula for now.
    // Score > 0 holds because we filter out others in CoverageScoreFormula.
    List<Node> reportedNodes = opt.getFormula().rank(testResults, descNum).stream()
        .map(pair -> pair.a)
        .collect(Collectors.toList());
    String modelName = beforeSubstring(afterSubstring(opt.getModelPath(), Names.SLASH, true),
        Names.DOT, false);
    Codec.serialize(
        Pair.of(modelUnit, reportedNodes),
        Paths.get(opt.getResultDirPath(), modelName + Names.DOT_LST).toString());
  }

  public static void main(String... args) {
    if (args.length != 4) {
      logger.error("Wrong number of arguments: " + args.length);
      printCoverageBasedFaultLocatorUsage();
      return;
    }
    String modelPath = args[0];
    String testPath = args[1];
    String formula = args[2];
    String resultDirPath = args[3];
    FileUtil.createDirsIfNotExist(resultDirPath);
    FaultLocatorOpt opt =
        new FaultLocatorOpt(modelPath, testPath, formula, resultDirPath);
    localize(opt);
  }
}
