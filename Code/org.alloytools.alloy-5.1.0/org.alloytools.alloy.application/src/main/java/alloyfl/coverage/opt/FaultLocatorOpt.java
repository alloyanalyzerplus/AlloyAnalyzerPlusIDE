package alloyfl.coverage.opt;

import alloyfl.coverage.util.CoverageScoreFormula;
import parser.exception.UnsupportedOptionException;
import parser.opt.Opt;

public class FaultLocatorOpt extends Opt {

  /**
   * Path to the test file which contains the test suite for the model.
   */
  protected String testSuitePath;
  /**
   * Score formula.
   */
  protected CoverageScoreFormula formula;
  /**
   * Path to the directory where result files are generated.
   */
  protected String resultDirPath;

  public FaultLocatorOpt(String modelPath, String testPath, String resultDirPath) {
    this(modelPath, testPath, null, resultDirPath);
  }

  public FaultLocatorOpt(String modelPath, String testPath, String formula,
      String resultDirPath) {
    super(modelPath, null, 0, "mini-sat-unsat-core");
    this.testSuitePath = testPath;
    this.formula = findScoreFormula(formula);
    this.resultDirPath = resultDirPath;
    options.coreGranularity = 3;
    options.coreMinimization = 0;
  }

  private CoverageScoreFormula findScoreFormula(String formula) {
    if (formula == null) {
      return null;
    }
    switch (formula.toLowerCase()) {
      case "tarantula":
        return CoverageScoreFormula.TARANTULA;
      case "ochiai":
        return CoverageScoreFormula.OCHIAI;
      case "op2":
        return CoverageScoreFormula.OP2;
      case "barinel":
        return CoverageScoreFormula.BARINEL;
      case "dstar":
        return CoverageScoreFormula.DSTAR;
      default:
        throw new UnsupportedOptionException("Unsupported formula " + formula);
    }
  }

  public String getTestSuitePath() {
    return testSuitePath;
  }

  public String getResultDirPath() {
    return resultDirPath;
  }

  public CoverageScoreFormula getFormula() {
    return formula;
  }
}
