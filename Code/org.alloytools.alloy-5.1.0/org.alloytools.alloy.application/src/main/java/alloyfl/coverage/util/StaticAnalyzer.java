package alloyfl.coverage.util;

import alloyfl.coverage.visitor.DependencyAnalyzer;
import edu.mit.csail.sdg.parser.CompModule;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import parser.ast.nodes.Fact;
import parser.ast.nodes.ModelUnit;
import parser.ast.nodes.Node;
import parser.ast.nodes.Paragraph;
import parser.ast.visitor.PrettyStringVisitor;
import parser.util.AlloyUtil;

/**
 * Statically and transitively find all used sigs, predicates, functions, facts and assertions that
 * the Alloy command invokes.
 */
public class StaticAnalyzer {

  public static Collection<Node> findAllDependentNodes(ModelUnit modelUnit, Paragraph testPred) {
    DependencyAnalyzer dependencyAnalyzer = new DependencyAnalyzer(modelUnit);
    Set<Node> res = modelUnit.getFactDeclList().stream().map(Paragraph::getBody)
        .collect(Collectors.toSet());
    Queue<Paragraph> invokedParagraphs = new LinkedList<>();
    invokedParagraphs.addAll(modelUnit.getFactDeclList());
    invokedParagraphs.add(testPred);
    while (!invokedParagraphs.isEmpty()) {
      Paragraph toVisit = invokedParagraphs.poll();
      toVisit.accept(dependencyAnalyzer, null);
      dependencyAnalyzer.getUsedParagraphs().forEach(paragraph -> {
        invokedParagraphs.offer(paragraph);
        res.add(paragraph.getBody());
      });
      dependencyAnalyzer.clearUsedParagraphs();
    }
    res.addAll(dependencyAnalyzer.getUsedSigs());
    return res;
  }

  public static void main(String... args) {
    String modelPath = "experiments/realbugs/farmerFaulty.als";
    CompModule module = AlloyUtil.compileAlloyModule(modelPath);
    ModelUnit modelUnit = new ModelUnit(null, module);
    for (Fact fact : modelUnit.getFactDeclList()) {
      if (fact.getName().startsWith("stateTransition")) {
        Collection<Node> dependentNodes = findAllDependentNodes(modelUnit, fact);
        for (Node node : dependentNodes) {
          System.out.println(node.accept(new PrettyStringVisitor(), null));
          System.out.println("==========");
        }
        return;
      }
    }
  }
}
