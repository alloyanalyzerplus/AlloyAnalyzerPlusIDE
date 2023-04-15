package mualloy;

import static parser.etc.Context.logger;
import static parser.util.Util.printMutationTestingRunnerUsage;

import java.io.File;

import alloyfl.himualloy.util.TestRunner;
import edu.mit.csail.sdg.translator.A4Options;
import parser.etc.Names;
import parser.util.FileUtil;
import parser.util.StringUtil;

/**
 * This class is a validator class to check if all non-equivalent mutants can be
 * killed be the generated tests. It assumes the original model (ORIGIN),
 * mutants and tests (TEST) are in the same directory.
 */
public class MutationTestingRunner {

    public static void run(String dirPath, String testSuitePath) {
        File dir = new File(dirPath);
        File[] mutants = dir.listFiles((d, name) -> name.endsWith(Names.DOT_ALS) && !name.equals(Names.ORIGINAL_MODEL_NAME + Names.DOT_ALS) && !name.equals(Names.MUTATION_BASED_TEST_NAME + Names.DOT_ALS));
        int totalMutantsNum = mutants.length;
        int killedMutantsNum = 0;
        A4Options options = new A4Options();
        options.noOverflow = true;
        //StringUtil.beforeSubstring(mutant.getName(), Names.DOT, true)
        for (File mutant : mutants) {
            String mutantAsString = FileUtil.readText(mutant.getAbsolutePath());
            boolean isKilled = !TestRunner.runTestsFailFast(mutantAsString, testSuitePath, options);
            if (isKilled) {
                killedMutantsNum += 1;
            }
            String mutantName = StringUtil.beforeSubstring(mutant.getName(), Names.DOT, true);
            logger.info("Mutant " + mutantName + " is " + (isKilled ? "" : "not ") + "killed.");
        }
        logger.info("Mutation Score: " + killedMutantsNum + "/" + totalMutantsNum);
    }

    public static void main(String[] args) {
        //    args = new String[]{"experiments/gen/1st/singlyLinkedList", "experiments/test-suite/alloyfl.mutation-based/10/singlyLinkedList.als"};
        //        args = new String[] {"mualloy/mutants/binaryTree"};
        //        args = new String[] {"mualloy/mutants/fullTree"};
        //        args = new String[] {"mualloy/mutants/farmer"};
        //        args = new String[] {"mualloy/mutants/handshake"};
        //        args = new String[] {"mualloy/mutants/nqueens"};
        //        args = new String[] {"mualloy/mutants/dijkstra"};
        if (args.length != 2) {
            logger.error("Wrong number of arguments: " + args.length);
            printMutationTestingRunnerUsage();
            return;
        }
        String dirPath = args[0];
        String testSuitePath = args[1];
        if (!FileUtil.fileExists(dirPath) || FileUtil.isFile(dirPath)) {
            logger.error("Directory " + dirPath + " does not exist.");
            return;
        }
        if (!FileUtil.fileExists(testSuitePath)) {
            logger.error("The test suite at " + testSuitePath + " does not exist.");
            return;
        }
        run(dirPath, testSuitePath);
    }
}
