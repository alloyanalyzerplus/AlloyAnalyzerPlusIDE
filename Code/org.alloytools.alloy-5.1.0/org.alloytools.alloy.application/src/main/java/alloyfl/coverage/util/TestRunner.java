package alloyfl.coverage.util;

import static alloyfl.coverage.util.StaticAnalyzer.findAllDependentNodes;
import static alloyfl.coverage.util.UnsatAnalyzer.findHighlightedNodes;
import static parser.util.AlloyUtil.mergeModelAndTests;

import edu.mit.csail.sdg.alloy4.A4Reporter;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.parser.CompModule;
import edu.mit.csail.sdg.translator.A4Options;
import edu.mit.csail.sdg.translator.A4Solution;
import edu.mit.csail.sdg.translator.TranslateAlloyToKodkod;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import parser.ast.nodes.ModelUnit;
import parser.ast.nodes.Node;
import parser.ast.nodes.Predicate;
import parser.ast.visitor.GenericVisitor;
import parser.ast.visitor.PrettyStringVisitor;
import parser.ast.visitor.VoidVisitor;
import parser.etc.Names;
import parser.etc.NodeMap;

public class TestRunner {

  public static List<TestResult> runTests(ModelUnit modelUnit, String testSuitePath,
      PrettyStringVisitor psv, A4Options options) {
    List<TestResult> result = new ArrayList<>();
    CompModule moduleWithTests = mergeModelAndTests(modelUnit, testSuitePath, psv);
    assert moduleWithTests != null;
    Node dummyNode = new Node(null, new NodeMap()) {
      @Override
      public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        return null;
      }

      @Override
      public <A> void accept(VoidVisitor<A> v, A arg) {
      }
    };
    ModelUnit modelUnitWithTests = new ModelUnit(dummyNode, moduleWithTests);
    SubtreeMatcher matcher = new SubtreeMatcher();
    matcher.matches(modelUnitWithTests, modelUnit);
    Map<Node, Node> nodesWithTestsToNodes = matcher.getS2D();
    Map<String, Predicate> testNameToTestPred = modelUnitWithTests.getPredDeclList()
        .stream()
        .filter(predicate -> predicate.getName().startsWith(Names.TEST_PREFIX))
        .collect(Collectors.toMap(Predicate::getName, Function.identity()));

    moduleWithTests.getAllCommands().stream()
        .filter(cmd -> !cmd.check && cmd.label.startsWith(Names.TEST_PREFIX)).forEach(cmd -> {
      try {
        A4Solution sol = TranslateAlloyToKodkod
            .execute_command(A4Reporter.NOP, moduleWithTests.getAllReachableSigs(), cmd, options);
        Predicate testPredicate = testNameToTestPred.get(cmd.label);
        // Get nodes invoked by the test predicate.
        Collection<Node> relevantNodes = findAllDependentNodes(modelUnitWithTests, testPredicate)
            .stream()
            // We only want to get node in the model, but not in the test.
            .filter(nodesWithTestsToNodes::containsKey)
            .map(nodesWithTestsToNodes::get)
            .collect(Collectors.toSet());
        TestResult testResult = new TestResult(cmd.expects == 1, sol.satisfiable(), relevantNodes);
        if (!sol.satisfiable() && cmd.expects == 1) {
          // Get nodes highlighted in unsat core in terms of the original model (without tests).
          Collection<Node> unsatNodes = findHighlightedNodes(dummyNode.getNodeMap(), sol)
              .stream()
              // We only want to get node in the model, but not in the test.
              .filter(nodesWithTestsToNodes::containsKey)
              .map(nodesWithTestsToNodes::get)
              .collect(Collectors.toSet());
          testResult.setUnsatNodes(unsatNodes);
        }
        result.add(testResult);
      } catch (Err err) {
        err.printStackTrace();
      }
    });
    return result;
  }
}
