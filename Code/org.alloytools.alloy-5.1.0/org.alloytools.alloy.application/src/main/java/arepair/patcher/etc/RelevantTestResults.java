package arepair.patcher.etc;

import java.util.ArrayList;
import java.util.List;

import alloyfl.coverage.util.TestResult;

public class RelevantTestResults {

    private final List<TestResult> passingTestResults;
    private final List<TestResult> failingTestResults;

    public RelevantTestResults() {
        this.passingTestResults = new ArrayList<>();
        this.failingTestResults = new ArrayList<>();
    }

    public void addTestResult(TestResult testResult) {
        if (testResult.isFailed()) {
            failingTestResults.add(testResult);
        } else {
            passingTestResults.add(testResult);
        }
    }

    public List<TestResult> getPassingTestResults() {
        return passingTestResults;
    }

    public List<TestResult> getFailingTestResults() {
        return failingTestResults;
    }
}
