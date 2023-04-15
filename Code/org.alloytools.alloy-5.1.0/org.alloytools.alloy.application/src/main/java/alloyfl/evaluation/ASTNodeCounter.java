package alloyfl.evaluation;

import static parser.etc.Context.logger;

import alloyfl.metrics.visitor.NodeCountingVisitor;
import edu.mit.csail.sdg.parser.CompModule;
import parser.ast.nodes.ModelUnit;
import parser.util.AlloyUtil;
import parser.util.FileUtil;

public class ASTNodeCounter {

  public static void main(String[] args) {
    if (args.length != 1) {
      logger.error("Wrong number of arguments: " + args.length);
      logger.error("Expect model path as the only argument.");
      return;
    }
    String modelPath = args[0];
    if (!FileUtil.fileExists(modelPath)) {
      logger.error("Cannot find model path at " + modelPath);
    }
    CompModule module = AlloyUtil.compileAlloyModule(modelPath);
    assert module != null;
    ModelUnit mu = new ModelUnit(null, module);
    NodeCountingVisitor nc = new NodeCountingVisitor();
    mu.accept(nc, null);
    System.out.println(nc.getCnt());
  }
}
