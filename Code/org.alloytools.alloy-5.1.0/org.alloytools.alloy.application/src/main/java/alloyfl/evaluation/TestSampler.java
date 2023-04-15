package alloyfl.evaluation;

import static parser.etc.Context.logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import parser.etc.Names;
import parser.util.FileUtil;

public class TestSampler {

  public static void split(String testSuitePath, double samplingRatio, long seed) {
    String openDecls = FileUtil.readTextWithFilter(testSuitePath, String::startsWith, "open");
    String testSuite = FileUtil
        .readTextWithFilter(testSuitePath, (line, pattern) -> !line.startsWith(pattern), "open");
    assert testSuite != null;
    List<String> testCases = new ArrayList<>();
    String[] lines = testSuite.split(Names.NEW_LINE);
    StringBuilder testCase = new StringBuilder();
    for (String line : lines) {
      if (line.startsWith("run")) {
        testCase.append(line);
        testCases.add(testCase.toString().trim());
        testCase = new StringBuilder();
        continue;
      }
      testCase.append(line).append(Names.NEW_LINE);
    }
    // Sampling without replacement.
    int targetSize = (int) (testCases.size() * samplingRatio);
    Collections.shuffle(testCases, new Random(seed));
    List<String> sampledTestCases = testCases.subList(0, targetSize);
    System.out.println(
        (openDecls + Names.NEW_LINE + String.join(Names.NEW_LINE, sampledTestCases)).trim());
    FileUtil.writeText((openDecls + Names.NEW_LINE + testSuite).trim(), testSuitePath, false);
  }

  public static void main(String[] args) {
//        args = new String[]{"experiments/STUDENT_TEST.als", "44.3"};
    if (args.length != 3) {
      logger.error("Wrong number of arguments: " + args.length);
      logger.error("Requires the test path, the sampling ratio, e.g. 70 for 70%, and the seed.");
      return;
    }
    String testSuitePath = args[0];
    if (!FileUtil.fileExists(testSuitePath) || !FileUtil.isFile(testSuitePath)) {
      logger.error("Test Suite path " + testSuitePath + " does not exist or is not a file.");
      return;
    }
    split(testSuitePath, Double.valueOf(args[1]) / 100.0, Long.valueOf(args[2]));
  }
}
