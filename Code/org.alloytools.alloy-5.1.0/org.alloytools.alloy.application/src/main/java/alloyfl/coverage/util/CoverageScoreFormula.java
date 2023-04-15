package alloyfl.coverage.util;

import static alloyfl.mutation.util.Util.CLOSE_TO_ZERO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import parser.ast.nodes.Node;
import parser.etc.Pair;

public enum CoverageScoreFormula {
  TARANTULA("tarantula", (testResults, descNum) -> {
    /*
    Passing and failing is in terms of the test results for the original buggy model.
    failed is the number of failing tests that cover the node;
    passed is the number of passing tests that cover the mutant;
    totalFailed is the total number of failing tests.
    totalPassed is the total number of passing tests.
    */
    // Collect data.
    Map<Node, Result> info = collectInfo(testResults);
    // Compute score.
    List<Pair<Node, Double>> res = new ArrayList<>();
    info.forEach((k, v) -> {
      v.score =
          v.failed * v.totalPassed * 1.0 / (v.failed * v.totalPassed + v.passed * v.totalFailed);
      if (v.score > 0) {
        res.add(Pair.of(k, v.score));
      }
    });
    return res;
  }),
  OCHIAI("ochiai", (testResults, descNum) -> {
    // Collect data.
    Map<Node, Result> info = collectInfo(testResults);
    // Compute score.
    List<Pair<Node, Double>> res = new ArrayList<>();
    info.forEach((k, v) -> {
      // If all tests pass, then all nodes should be correct.
      // If all failed tests do not kill the mutant, then location
      // of the alloyfl.mutation does not help much.
      if (v.totalFailed == 0 || v.failed == 0) {
        v.score = 0.0;
        return;
      }
      v.score = v.failed * 1.0 / Math.sqrt(v.totalFailed * (v.failed + v.passed));
      if (v.score > 0) {
        res.add(Pair.of(k, v.score));
      }
    });
    return res;
  }),
  OP2("op2", (testResults, descNum) -> {
    // Collect data.
    Map<Node, Result> info = collectInfo(testResults);
    // Compute score.
    List<Pair<Node, Double>> res = new ArrayList<>();
    info.forEach((k, v) -> {
      v.score = v.failed - v.passed * 1.0 / (v.totalPassed + 1);
      if (v.score > 0) {
        res.add(Pair.of(k, v.score));
      }
    });
    return res;
  }),
  BARINEL("barinel", (testResults, descNum) -> {
    // Collect data.
    Map<Node, Result> info = collectInfo(testResults);
    // Compute score.
    List<Pair<Node, Double>> res = new ArrayList<>();
    info.forEach((k, v) -> {
      v.score = 1 - v.passed * 1.0 / (v.passed + v.failed);
      if (v.score > 0) {
        res.add(Pair.of(k, v.score));
      }
    });
    return res;
  }),
  DSTAR("dstar", ((testResults, descNum) -> {
    // Collect data.
    Map<Node, Result> info = collectInfo(testResults);
    // Compute score.
    List<Pair<Node, Double>> res = new ArrayList<>();
    info.forEach((k, v) -> {
      // Smooth the score in case the mutation fix the model which makes the denominator 0.
      v.score = Math.pow(v.failed, 2) / (v.passed + v.totalFailed - v.failed + CLOSE_TO_ZERO);
      if (v.score > 0) {
        res.add(Pair.of(k, v.score));
      }
    });
    return res;
  }));

  private String label;
  private BiFunction<Collection<TestResult>, Map<Node, Integer>, List<Pair<Node, Double>>> formula;

  CoverageScoreFormula(String label,
      BiFunction<Collection<TestResult>, Map<Node, Integer>, List<Pair<Node, Double>>> formula) {
    this.label = label;
    this.formula = formula;
  }

  public String getLabel() {
    return label;
  }

  public List<Pair<Node, Double>> computeNodeAndScore(Collection<TestResult> testResults,
      Map<Node, Integer> descNum) {
    return formula.apply(testResults, descNum);
  }

  public List<Pair<Node, Double>> rank(Collection<TestResult> testResults,
      Map<Node, Integer> descNum) {
    List<Pair<Node, Double>> res = computeNodeAndScore(testResults, descNum);
    res.sort((n1, n2) -> {
      int cmpRes = Double.compare(n2.b, n1.b);
      return cmpRes == 0 ? Integer.compare(descNum.get(n1.a), descNum.get(n2.a)) : cmpRes;
    });
    return res;
  }

  private static Map<Node, Result> collectInfo(Collection<TestResult> testResults) {
    Map<Node, Result> info = new HashMap<>();
    int totalFailed = 0, totalPassed = 0;
    for (TestResult testResult : testResults) {
      if (testResult.isFailed()) {
        totalFailed++;
      } else {
        totalPassed++;
      }
      for (Node node : testResult.getRelevantNodes()) {
        if (!info.containsKey(node)) {
          info.put(node, new Result());
        }
        if (testResult.isFailed()) {
          info.get(node).failed++;
        } else {
          info.get(node).passed++;
        }
      }
    }
    for (Result result : info.values()) {
      result.totalFailed = totalFailed;
      result.totalPassed = totalPassed;
    }
    return info;
  }

  private static class Result {

    int passed;
    int failed;
    int totalFailed;
    int totalPassed;
    double score;
  }
}
