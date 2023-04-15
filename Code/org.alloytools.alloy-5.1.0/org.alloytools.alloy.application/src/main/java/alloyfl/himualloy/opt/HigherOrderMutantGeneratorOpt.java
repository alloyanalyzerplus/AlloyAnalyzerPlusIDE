package alloyfl.himualloy.opt;

import parser.opt.Opt;

public class HigherOrderMutantGeneratorOpt extends Opt {

  /**
   * Path to the directory where mutants are generated.
   */
  private String mutantDirPath;
  /**
   * Path to the directory where result files are generated.
   */
  private String resultDirPath;
  /**
   * The number of mutations in the mutant.
   */
  private int orderNumber;
  /**
   * Path to the tests for filtering purpose. Optional: Filter out higher order mutants that cannot
   * be killed by the tests.
   */
  private String testSuitePath;

  public HigherOrderMutantGeneratorOpt(String modelPath, String mutantDirPath, String resultDirPath,
      int orderNumber, String testSuitePath) {
    super(modelPath, null, 0);
    this.mutantDirPath = mutantDirPath;
    this.resultDirPath = resultDirPath;
    this.orderNumber = orderNumber;
    this.testSuitePath = testSuitePath;
  }

  public String getMutantDirPath() {
    return mutantDirPath;
  }

  public String getResultDirPath() {
    return resultDirPath;
  }

  public int getOrderNumber() {
    return orderNumber;
  }

  public String getTestSuitePath() {
    return testSuitePath;
  }
}
