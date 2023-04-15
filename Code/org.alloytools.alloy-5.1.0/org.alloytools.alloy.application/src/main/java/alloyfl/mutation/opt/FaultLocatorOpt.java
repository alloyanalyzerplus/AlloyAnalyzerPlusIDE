package alloyfl.mutation.opt;

import alloyfl.mutation.util.MutationScoreFormula;
import parser.exception.UnsupportedOptionException;
import parser.opt.Opt;

public class FaultLocatorOpt extends Opt {

  /**
   * Path to the test file which contains the test suite for the model.
   */
  protected String testSuitePath;
  /**
   * Formula used to compute suspiciousness score.
   */
  protected MutationScoreFormula formula;
  /**
   * Path to the directory where result files are generated.
   */
  protected String resultDirPath;

  public FaultLocatorOpt(String modelPath, String correctModelPath, String testPath, int scope,
      String formula, String resultDirPath) {
    super(modelPath, correctModelPath, scope);
    this.testSuitePath = testPath;
    this.formula = findScoreFormula(formula);
    this.resultDirPath = resultDirPath;
  }

  public FaultLocatorOpt(String modelPath, String correctModelPath, String testPath, int scope,
      String formula, String resultDirPath, String solverName) {
    super(modelPath, correctModelPath, scope, solverName);
    this.testSuitePath = testPath;
    this.formula = findScoreFormula(formula);
    this.resultDirPath = resultDirPath;
  }

  private MutationScoreFormula findScoreFormula(String formula) {
    switch (formula.toLowerCase()) {
      case "tarantula":
        return MutationScoreFormula.TARANTULA;
      case "ochiai":
        return MutationScoreFormula.OCHIAI;
      case "op2":
        return MutationScoreFormula.OP2;
      case "barinel":
        return MutationScoreFormula.BARINEL;
      case "dstar":
        return MutationScoreFormula.DSTAR;
      default:
        throw new UnsupportedOptionException("Unsupported formula " + formula);
    }
  }

  public String getTestSuitePath() {
    return testSuitePath;
  }

  public boolean isModelToAvoid(String modelAsString) {
    return correctModelAsString.equals(modelAsString);
  }

  public MutationScoreFormula getFormula() {
    return formula;
  }

  public String getResultDirPath() {
    return resultDirPath;
  }
}
