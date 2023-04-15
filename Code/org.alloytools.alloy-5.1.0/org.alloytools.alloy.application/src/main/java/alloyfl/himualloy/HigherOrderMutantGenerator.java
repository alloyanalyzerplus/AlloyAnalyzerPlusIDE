package alloyfl.himualloy;

import static parser.etc.Context.logger;
import static parser.util.Util.printHigherOrderMutantGeneratorUsage;

import alloyfl.himualloy.opt.HigherOrderMutantGeneratorOpt;
import alloyfl.himualloy.util.HigherOrderBacktracker;
import alloyfl.himualloy.visitor.MutationPointsCollector;
import edu.mit.csail.sdg.parser.CompModule;
import java.util.ArrayList;
import java.util.List;
import parser.ast.nodes.ModelUnit;
import parser.ast.nodes.Node;
import parser.util.AlloyUtil;
import parser.util.FileUtil;

public class HigherOrderMutantGenerator {

  public static void mutate(HigherOrderMutantGeneratorOpt opt) {
    CompModule module = AlloyUtil.compileAlloyModule(opt.getModelPath());
    ModelUnit mu = new ModelUnit(null, module);
    MutationPointsCollector mutationPointsCollector = new MutationPointsCollector();
    mu.accept(mutationPointsCollector, null);
    List<Node> potentialMutationPoints = mutationPointsCollector.getMutationPoints();
//        potentialMutationPoints.stream().forEach(node -> {
//            PrettyStringVisitor psv = new PrettyStringVisitor();
//            System.out.println(node.accept(psv, null));
////            System.out.println(computeNodePathAsString(node, psv));
////            System.out.println(node.getClass().getSimpleName());
//            System.out.println("===========");
//        });
    HigherOrderBacktracker
        .backtrack(mu, potentialMutationPoints, 0, new ArrayList<>(), new ArrayList<>(), opt);
  }

  public static void main(String[] args) {
//        args = new String[] {"experiments/models/singlyLinkedList.als", "experiments/gen-higher", "experiments/results-higher", "2", "experiments/test-suite/alloyfl.mutation-based/100/singlyLinkedList.als"};
//        args = new String[] {"experiments/models/binaryTree.als", "experiments/gen-higher", "experiments/results-higher", "2", "experiments/test-suite/alloyfl.mutation-based/100/binaryTree.als"};
//        args = new String[] {"experiments/models/fullTree.als", "experiments/gen-higher", "experiments/results-higher", "2", "experiments/test-suite/alloyfl.mutation-based/100/fullTree.als"};
//        args = new String[] {"experiments/models/farmer.als", "experiments/gen-higher", "experiments/results-higher", "2", "experiments/test-suite/alloyfl.mutation-based/100/farmer.als"};
//        args = new String[] {"experiments/models/handshake.als", "experiments/gen-higher", "experiments/results-higher", "2", "experiments/test-suite/alloyfl.mutation-based/100/handshake.als"};
//        args = new String[] {"experiments/models/nqueens.als", "experiments/gen-higher", "experiments/results-higher", "2", "experiments/test-suite/alloyfl.mutation-based/100/nqueens.als"};
//        args = new String[] {"experiments/models/dijkstra.als", "experiments/gen-higher", "experiments/results-higher", "2", "experiments/test-suite/alloyfl.mutation-based/100/dijkstra.als"};
//        args = new String[] {"mualloy/models/binaryTree.als", "mualloy/mutants/binaryTree", "3"};
//        args = new String[] {"mualloy/models/fullTree.als", "mualloy/mutants/fullTree", "3"};
//        args = new String[] {"mualloy/models/farmer.als", "mualloy/mutants/farmer", "8"};
//        args = new String[] {"mualloy/models/handshake.als", "mualloy/mutants/handshake", "4"};
//        args = new String[] {"mualloy/models/nqueens.als", "mualloy/mutants/nqueens", "4"};
//        args = new String[] {"mualloy/models/dijkstra.als", "mualloy/mutants/dijkstra", "3"};
    if (args.length != 4 && args.length != 5) {
      logger.error("Wrong number of arguments: " + args.length);
      printHigherOrderMutantGeneratorUsage();
      return;
    }
    String modelPath = args[0];
    String mutantDirPath = args[1];
    String resultDirPath = args[2];
    int orderNumber = Integer.valueOf(args[3]);
    String testSuitePath = args.length == 5 ? args[4] : null;
    if (!FileUtil.fileExists(modelPath)) {
      logger.error("Cannot find model at " + modelPath);
      printHigherOrderMutantGeneratorUsage();
      return;
    }
    if (testSuitePath != null && !FileUtil.fileExists(testSuitePath)) {
      logger.error("Cannot find tests at " + testSuitePath);
      printHigherOrderMutantGeneratorUsage();
      return;
    }
    FileUtil.createDirsIfNotExist(mutantDirPath, resultDirPath);
    HigherOrderMutantGeneratorOpt opt = new HigherOrderMutantGeneratorOpt(modelPath, mutantDirPath,
        resultDirPath, orderNumber, testSuitePath);
    mutate(opt);
  }
}
