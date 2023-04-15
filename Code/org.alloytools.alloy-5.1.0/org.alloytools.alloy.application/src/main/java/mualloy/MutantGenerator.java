package mualloy;

import static parser.etc.Context.logger;
import static parser.util.Util.printMutantGeneratorUsage;

import java.nio.file.Paths;
import java.util.Arrays;

import edu.mit.csail.sdg.parser.CompModule;
import mualloy.opt.MutantGeneratorOpt;
import mualloy.util.AUnitTestCase;
import mualloy.visitor.ModelMutator;
import parser.ast.nodes.ModelUnit;
import parser.etc.Names;
import parser.util.AlloyUtil;
import parser.util.FileUtil;

public class MutantGenerator {

    public static void mutate(MutantGeneratorOpt opt) {
        CompModule module = AlloyUtil.compileAlloyModule(opt.getModelPath());
        assert module != null;
        ModelUnit mu = new ModelUnit(null, module);
        // Write pretty printed model to file for easy comparison.
        FileUtil.writeText(mu.accept(opt.getPSV(), null), Paths.get(opt.getMutantDirPath(), Names.ORIGINAL_MODEL_NAME + Names.DOT_ALS).toString(), true);
        // Mutate the model.
        ModelMutator mm = new ModelMutator(opt);
        mu.accept(mm, null);
        StringBuilder testSuite = new StringBuilder();
        int count = 1;
        for (AUnitTestCase testCase : opt.getTests()) {
            testSuite.append(testCase.toString(count++)).append("\n");
        }
        FileUtil.writeText(testSuite.toString(), Paths.get(opt.getMutantDirPath(), Names.MUTATION_BASED_TEST_NAME + Names.DOT_ALS).toString(), true);
        FileUtil.writeText(String.join(Names.NEW_LINE, Arrays.asList("EquivMutantNum: " + String.valueOf(mm.getEquivMutantNum()), "NonEquivMutantNum: " + String.valueOf(mm.getNonEquivMutantNum()), "TestNum: " + opt.getTests().size())), Paths.get(opt.getMetaDirPath(), "mutant_gen.num").toString(), false);
    }

    public static void main(String[] args) {
        //        args = new String[] {"mualloy/models/singlyLinkedList.als", "mualloy/mutants/singlyLinkedList", "3"};
        //        args = new String[] {"mualloy/models/binaryTree.als", "mualloy/mutants/binaryTree", "3"};
        //        args = new String[] {"mualloy/models/fullTree.als", "mualloy/mutants/fullTree", "3"};
        //        args = new String[] {"mualloy/models/farmer.als", "mualloy/mutants/farmer", "8"};
        //        args = new String[] {"mualloy/models/handshake.als", "mualloy/mutants/handshake", "4"};
        //        args = new String[] {"mualloy/models/nqueens.als", "mualloy/mutants/nqueens", "4"};
        //        args = new String[] {"mualloy/models/dijkstra.als", "mualloy/mutants/dijkstra", "3"};
        if (args.length != 6) {
            logger.error("Wrong number of arguments: " + args.length);
            printMutantGeneratorUsage();
            return;
        }
        String modelPath = args[0];
        String correctModelPath = args[1];
        String mutantDirPath = args[2];
        String resultDirPath = args[3];
        String metaDirPath = args[4];
        int scope = Integer.valueOf(args[5]);
        /*
         * if (!FileUtil.fileExists(modelPath)) { logger.error("Cannot find model at " +
         * modelPath); printMutantGeneratorUsage(); return; } if
         * (!FileUtil.fileExists(correctModelPath)) {
         * logger.error("Cannot find correct model at " + correctModelPath);
         * printMutantGeneratorUsage(); return; }
         */
        FileUtil.createDirsIfNotExist(mutantDirPath, resultDirPath, metaDirPath);
        MutantGeneratorOpt opt = new MutantGeneratorOpt(modelPath, correctModelPath, mutantDirPath, resultDirPath, metaDirPath, scope);
        mutate(opt);
    }
}
