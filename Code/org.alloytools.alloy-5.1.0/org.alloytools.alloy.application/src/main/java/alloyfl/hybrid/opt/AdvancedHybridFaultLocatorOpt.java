package alloyfl.hybrid.opt;

import alloyfl.mutation.opt.FaultLocatorOpt;
import java.util.function.BiFunction;

public class AdvancedHybridFaultLocatorOpt extends FaultLocatorOpt {

  private enum AdjustUnsatScore {
    SQUARE_ROOT("sqrt", (baseScore, rootUnsatNodeSize) -> Math.sqrt(baseScore)),
    INVERSE_UNSAT_NODE_SIZE("inverse",
        (baseScore, rootUnsatNodeSize) -> baseScore + 1.0 / (rootUnsatNodeSize + 3));

    private String label;
    private BiFunction<Double /* base score */, Integer /* root unsat node size */, Double /* adjusted score */> formula;

    AdjustUnsatScore(String label, BiFunction<Double, Integer, Double> formula) {
      this.label = label;
      this.formula = formula;
    }

    public String getLabel() {
      return label;
    }

    private double compute(double baseScore, int rootUnsatNodeSize) {
      return formula.apply(baseScore, rootUnsatNodeSize);
    }
  }

  private AdjustUnsatScore adjustUnsatScore;

  public AdvancedHybridFaultLocatorOpt(String modelPath, String testPath, int scope, String formula,
      String resultDirPath) {
    super(modelPath, null, testPath, scope, formula, resultDirPath, "mini-sat-unsat-core");
    options.coreGranularity = 3;
    options.coreMinimization = 0;
    adjustUnsatScore = AdjustUnsatScore.INVERSE_UNSAT_NODE_SIZE;
  }

  public double getThreshold() {
    throw new UnsupportedOperationException();
  }

  public double compute(double baseScore, int rootUnsatNodeSize) {
    return adjustUnsatScore.compute(baseScore, rootUnsatNodeSize);
  }
}
