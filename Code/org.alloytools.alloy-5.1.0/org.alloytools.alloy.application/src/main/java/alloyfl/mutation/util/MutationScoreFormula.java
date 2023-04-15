package alloyfl.mutation.util;

import java.util.List;
import java.util.function.BiFunction;

public enum MutationScoreFormula {
  TARANTULA("tarantula", (buggyModelRes, mutantRes) -> {
    int i = 0;
    /*
    Passing and failing is in terms of the test results for the original buggy model.
    failed is the number of failing tests that kill the mutant;
    passed is the number of passing tests that kill the mutant;
    totalFailed is the total number of failing tests.
    totalPassed is the total number of passing tests.
    */
    int failed = 0, passed = 0, totalFailed = 0, totalPassed = 0;
    while (i < buggyModelRes.size()) {
      if (buggyModelRes.get(i)) {
        totalPassed++;
        if (!mutantRes.get(i)) {
          passed++;
        }
      } else {
        totalFailed++;
        if (mutantRes.get(i)) {
          failed++;
        }
      }
      i++;
    }
    return failed * totalPassed * 1.0 / (failed * totalPassed + passed * totalFailed);
  }),
  OCHIAI("ochiai", (buggyModelRes, mutantRes) -> {
    int i = 0;
    /*
    Passing and failing is in terms of the test results for the original buggy model.
    failed is the number of failing tests that kill the mutant;
    passed is the number of passing tests that kill the mutant;
    totalFailed is the total number of failing tests.
    */
    int failed = 0, passed = 0, totalFailed = 0;
    while (i < buggyModelRes.size()) {
      if (buggyModelRes.get(i)) {
        if (!mutantRes.get(i)) {
          passed++;
        }
      } else {
        totalFailed++;
        if (mutantRes.get(i)) {
          failed++;
        }
      }
      i++;
    }
    // If all tests pass, then all nodes should be correct.
    // If all failed tests do not kill the mutant, then location
    // of the alloyfl.mutation does not help much.
    if (totalFailed == 0 || failed == 0) {
      return 0.0;
    }
    return failed * 1.0 / Math.sqrt(totalFailed * (failed + passed));
  }),
  OP2("op2", (buggyModelRes, mutantRes) -> {
    int i = 0;
    /*
    Passing and failing is in terms of the test results for the original buggy model.
    failed is the number of failing tests that kill the mutant;
    passed is the number of passing tests that kill the mutant;
    totalPassed is the total number of passing tests.
    */
    int failed = 0, passed = 0, totalPassed = 0;
    while (i < buggyModelRes.size()) {
      if (buggyModelRes.get(i)) {
        totalPassed++;
        if (!mutantRes.get(i)) {
          passed++;
        }
      } else {
        if (mutantRes.get(i)) {
          failed++;
        }
      }
      i++;
    }
    // 1 - (passed + CLOSE_TO_ZERO) * 1.0 / ((totalPassed + 1) * (failed + CLOSE_TO_ZERO)) may be better.
    // Before it was 1 - (passed) * 1.0 / ((totalPassed + 1) * (failed + CLOSE_TO_ZERO))
    return failed - passed * 1.0 / (totalPassed + 1);
  }),
  BARINEL("barinel", (buggyModelRes, mutantRes) -> {
    int i = 0;
    /*
    Passing and failing is in terms of the test results for the original buggy model.
    failed is the number of failing tests that kill the mutant;
    passed is the number of passing tests that kill the mutant;
    totalPassed is the total number of passing tests.
    */
    int failed = 0, passed = 0;
    while (i < buggyModelRes.size()) {
      if (buggyModelRes.get(i)) {
        if (!mutantRes.get(i)) {
          passed++;
        }
      } else {
        if (mutantRes.get(i)) {
          failed++;
        }
      }
      i++;
    }
    return 1 - passed * 1.0 / (passed + failed);
  }),
  DSTAR("dstar", (buggyModelRes, mutantRes) -> {
    int i = 0;
    /*
    Passing and failing is in terms of the test results for the original buggy model.
    failed is the number of failing tests that kill the mutant;
    passed is the number of passing tests that kill the mutant;
    totalPassed is the total number of passing tests.
    */
    int failed = 0, passed = 0, totalFailed = 0;
    while (i < buggyModelRes.size()) {
      if (buggyModelRes.get(i)) {
        if (!mutantRes.get(i)) {
          passed++;
        }
      } else {
        totalFailed++;
        if (mutantRes.get(i)) {
          failed++;
        }
      }
      i++;
    }
    return Math.pow(failed, 2) / (passed + totalFailed - failed);
  });

  private String label;
  private BiFunction<List<Boolean>, List<Boolean>, Double> formula;

  MutationScoreFormula(String label, BiFunction<List<Boolean>, List<Boolean>, Double> formula) {
    this.label = label;
    this.formula = formula;
  }

  public String getLabel() {
    return label;
  }

  public Double compute(List<Boolean> testResult1, List<Boolean> testResult2) {
    return formula.apply(testResult1, testResult2);
  }
}
