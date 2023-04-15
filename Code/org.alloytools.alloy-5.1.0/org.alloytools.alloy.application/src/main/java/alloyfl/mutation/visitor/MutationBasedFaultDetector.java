package alloyfl.mutation.visitor;

import static alloyfl.mutation.util.ScoreInfo.sortScoreInfos;

import alloyfl.mutation.opt.FaultLocatorOpt;
import alloyfl.mutation.util.ScoreInfo;
import alloyfl.mutation.util.TestRunner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mualloy.visitor.MutationVisitor;
import parser.ast.nodes.Node;
import parser.etc.MutationData;
import parser.etc.Pair;

public class MutationBasedFaultDetector extends MutationVisitor {

  protected List<Boolean> testResultsForBuggyModel;
  protected Map<Node, Double> node2score;

  public MutationBasedFaultDetector(List<Boolean> testResultsForBuggyModel, FaultLocatorOpt opt) {
    this(testResultsForBuggyModel, opt, null);
  }

  public MutationBasedFaultDetector(List<Boolean> testResultsForBuggyModel,
      FaultLocatorOpt opt, Set<Node> domain) {
    super(opt);
    this.testResultsForBuggyModel = testResultsForBuggyModel;
    this.node2score = new HashMap<>();
    this.domain = domain;
  }

  @Override
  protected void process(Node node, MutationData mutationData, Class<?> clazz) {
    if (mutationData.isEquivalent()) {
//      System.out.println("Equivalent");
//            node2score.put(node, -1.0);
      return;
    }
    FaultLocatorOpt flo = (FaultLocatorOpt) opt;
    if (flo.isModelToAvoid(mutationData.getMutantAsString())) {
      return;
    }
    List<Boolean> testResultsForMutant = TestRunner.runTests(mutationData.getMutantAsString(), flo);
    double oldScore = node2score.getOrDefault(node, 0.0);
    node2score.put(node, Math.max(oldScore,
        flo.getFormula().compute(testResultsForBuggyModel, testResultsForMutant)));
  }

  /**
   * Checks if any passing test fails or failing test passes.
   */
  private Pair<Boolean, Boolean> checkTestResultChange(List<Boolean> testResultsForMutant) {
    assert testResultsForBuggyModel.size() == testResultsForMutant.size();
    boolean pass2fail = false;
    boolean fail2pass = false;
    for (int i = 0; i < testResultsForBuggyModel.size(); i++) {
      // If test passes for the original buggy model and fails for the mutant, return true.
      if (testResultsForBuggyModel.get(i) && !testResultsForMutant.get(i)) {
        pass2fail = true;
      }
      if (!testResultsForBuggyModel.get(i) && testResultsForMutant.get(i)) {
        fail2pass = true;
      }
    }
    return Pair.of(pass2fail, fail2pass);
  }

  public List<ScoreInfo> constructScoreInfos(Map<Node, Integer> descCount) {
    List<ScoreInfo> scoreInfos = new ArrayList<>();
    node2score.forEach(
        (node, score) -> {
          // Filter out nodes with suspiciousness score <= 0.
          if (score > 0) {
            scoreInfos.add(
                new ScoreInfo(node, score, descCount.get(node), opt.getPSV()));
          }
        });
    return scoreInfos;
  }

  /**
   * Rank first based on score, then based on the number of descendants.
   */
  public List<ScoreInfo> rankNode(Map<Node, Integer> descCount) {
    List<ScoreInfo> scoreInfos = constructScoreInfos(descCount);
    sortScoreInfos(scoreInfos);
    return scoreInfos;
  }
}
