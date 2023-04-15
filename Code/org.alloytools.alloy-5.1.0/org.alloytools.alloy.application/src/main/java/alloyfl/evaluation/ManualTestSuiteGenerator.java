package alloyfl.evaluation;

import edu.mit.csail.sdg.alloy4.A4Reporter;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.ast.Command;
import edu.mit.csail.sdg.parser.CompModule;
import edu.mit.csail.sdg.translator.A4Options;
import edu.mit.csail.sdg.translator.A4Solution;
import edu.mit.csail.sdg.translator.TranslateAlloyToKodkod;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import mualloy.util.AUnitTestCase;
import mualloy.util.SpecialCase;
import mualloy.util.TestGenerator;
import parser.ast.nodes.ModelUnit;
import parser.ast.nodes.Predicate;
import parser.ast.visitor.PrettyStringVisitor;
import parser.etc.Names;
import parser.util.AlloyUtil;
import parser.util.FileUtil;

public class ManualTestSuiteGenerator {

  public static int COUNT = 0;

  /**
   * Create a test suite from the instances obtained by running the command.  If you want to invoke
   * a predicate/function in the test, you need to make sure that the skolem names (the one appear
   * in the instances) match the parameter names.
   *
   * @param modelPath the model path.
   * @param scope the scope of the model.
   * @param predicateName the predicate name if we want to invoke it in the generated test suite.
   * @param commandName the command to invoke for generating the instances.
   * @param testSuiteSize the number of test predicates to generate.
   * @return a list of generated test predicates as string.
   */
  public static String createTestSuite(String modelPath, int scope, String predicateName,
      String commandName, int testSuiteSize) throws Err {
    CompModule module = AlloyUtil.compileAlloyModule(modelPath);
    ModelUnit modelUnit = new ModelUnit(null, module);
    SpecialCase specialCase = new SpecialCase();
    modelUnit.getOpenDeclList().stream()
        .filter(openDecl -> openDecl.getFileName().equals("util/ordering"))
        .forEach(specialCase::addOrderingOpenDecl);
    Predicate predicate;
    try {
      predicate = modelUnit.getPredDeclList().stream()
          .filter(p -> p.getName().equals(predicateName))
          .findFirst()
          .get();
    } catch (NoSuchElementException nee) {
      predicate = null;
    }
    String originalModel = FileUtil.readText(modelPath);
    assert module != null;
    Command cmd = module.getAllCommands().stream()
        .filter(command -> command.label.equals(commandName))
        .findFirst()
        .get();
    A4Options options = new A4Options();
    options.noOverflow = true;
    A4Solution ans = TranslateAlloyToKodkod
        .execute_command(A4Reporter.NOP, module.getAllReachableSigs(), cmd, options);
    StringBuilder testSuite = new StringBuilder();
    int cnt = 0;
    while (ans.satisfiable() && cnt < testSuiteSize) {
      AUnitTestCase testCase = TestGenerator
          .translateToTest(
              module, ans, predicate, specialCase, commandName, scope, originalModel, options);
      testSuite.append(testCase.toString(COUNT++)).append("\n");
      ans = ans.next();
      cnt++;
    }
    return testSuite.toString();
  }

  public static List<List<String>> analyzeStudentSolutions(String solutionDir, String predName) {
    List<List<String>> res = new ArrayList<>();
    File dir = new File(solutionDir);
    File[] studentModels = dir
        .listFiles((dir1, name) -> name.startsWith("student") && name.endsWith(Names.DOT_ALS));
    Arrays.sort(studentModels, (o1, o2) -> {
      String name1 = o1.getName();
      int num1 = Integer.valueOf(name1.substring(7, name1.lastIndexOf(".")));
      String name2 = o2.getName();
      int num2 = Integer.valueOf(name2.substring(7, name2.lastIndexOf(".")));
      return num1 - num2;
    });
    Map<String, Integer> model2index = new HashMap<>();
    for (File studentModel : studentModels) {
      CompModule module = AlloyUtil.compileAlloyModule(studentModel.getPath());
      ModelUnit modelUnit = new ModelUnit(null, module);
      Predicate pred = modelUnit.getPredDeclList().stream()
          .filter(predicate -> predicate.getName().equals(predName)).findFirst().get();
      String predAsString = pred.accept(new PrettyStringVisitor(), null);
      if (model2index.containsKey(predAsString)) {
        res.get(model2index.get(predAsString)).add(studentModel.getName());
      } else {
        model2index.put(predAsString, res.size());
        List<String> names = new ArrayList<>();
        names.add(studentModel.getName());
        res.add(names);
      }
    }
    return res;
  }

  public static void main(String[] args) throws Err {
    for (String cmdName : new String[]{"TRY0", "TRY1", "TRY2", "TRY3"}) {
      System.out
          .println(createTestSuite("experiments/manual-play/addrPlay.als", 3, "", cmdName, 25));
    }
//        CompModule module = AlloyUtil.compileAlloyModule("experiments/realbugs/farmerFaulty/farmerFaulty.als");
//        CompModule module = AlloyUtil.compileAlloyModule("experiments/farmer.als");
//        CompModule module = AlloyUtil.compileAlloyModule("experiments/realbugs/dijkstraFaulty/dijkstraFaulty.als");
//        System.out.println(new ModelUnit(null, module).accept(new PrettyStringVisitor(), null));
//        System.out.println(createTestSuite("experiments/dijkstra.als", 3, "GrabMutex", "EQUIV"));
//    System.out.println(createTestSuite("experiments/farmer.als", 4, "", "TRY0", 50));
//    System.out.println(createTestSuite("experiments/farmer.als", 4, "crossRiver", "TRY1", 50));
//    System.out.println(createTestSuite("experiments/farmer.als", 4, "crossRiver", "TRY2", 50));
//    System.out.println(createTestSuite("experiments/farmer.als", 4, "crossRiver", "TRY3", 50));
//    System.out.println(createTestSuite("experiments/farmer.als", 4, "crossRiver", "TRY4", 50));
//    System.out.println(createTestSuite("experiments/farmer.als", 8, "solvePuzzle", "TRY5", 50));
//    System.out.println(createTestSuite("experiments/farmer.als", 8, "solvePuzzle", "TRY6", 50));
//        System.out.println(createTestSuite("experiments/student.als", 3, "", "TRY", 3));
//        analyzeStudentSolutions("experiments/realbugs/studentFaulty", "Contains").stream().forEach(System.out::println);
//        System.out.println(createTestSuite("experiments/student.als", 3, "Contains", "TRY1", 3));
//        System.out.println(createTestSuite("experiments/student.als", 3, "Contains", "TRY2", 3));
//        System.out.println(createTestSuite("experiments/student.als", 3, "Contains", "TRY3", 3));
//        System.out.println(createTestSuite("experiments/student.als", 3, "Contains", "TRY4", 3));
  }
}
