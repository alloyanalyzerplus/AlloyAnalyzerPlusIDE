package alloyfl.hybrid.opt;

import alloyfl.mutation.opt.FaultLocatorOpt;

public class HybridFaultLocatorOpt extends FaultLocatorOpt {

  /**
   * The threshold of the suspiciousness score.  We rank nodes with score >= threshold before those
   * who don't.
   */
  protected double threshold;
  private final double mutationProportion;

  public HybridFaultLocatorOpt(String modelPath, String testPath, int scope, String formula,
      String resultDirPath) {
    this(modelPath, testPath, scope, formula, 0, resultDirPath, 0.5);
  }

  public HybridFaultLocatorOpt(String modelPath, String testPath, int scope, String formula,
      String resultDirPath, double mutationProportion) {
    this(modelPath, testPath, scope, formula, 0, resultDirPath, mutationProportion);
  }

  public HybridFaultLocatorOpt(String modelPath, String testPath, int scope, String formula,
      double threshold, String resultDirPath) {
    this(modelPath, testPath, scope, formula, threshold, resultDirPath, 0.5);
  }

  public HybridFaultLocatorOpt(String modelPath, String testPath, int scope, String formula,
      double threshold, String resultDirPath, double mutationProportion) {
    super(modelPath, null, testPath, scope, formula, resultDirPath);
    this.threshold = threshold;
    this.mutationProportion = mutationProportion;
    options.coreGranularity = 3;
    options.coreMinimization = 0;
  }

  public double getThreshold() {
    return threshold;
  }

  public double getMutationProportion() {
    return mutationProportion;
  }
}
