package alloyfl.evaluation;

import static parser.etc.Context.logger;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import parser.etc.Names;
import parser.util.FileUtil;

public class MutantSampler {

  public static void sample(String mutantDir, int sampleSize) {
    File dir = new File(mutantDir);
    File[] mutants = dir.listFiles((d, name) -> name.endsWith(Names.DOT_ALS));
    List<String> mutantNames = Arrays.stream(mutants).map(File::getName)
        .collect(Collectors.toList());
    Collections.shuffle(mutantNames, new Random(11042018L));
    sampleSize = Math.min(sampleSize, mutantNames.size());
    List<String> sampledMutantNames = mutantNames.subList(0, sampleSize);
    System.out.println(String.join(Names.NEW_LINE, sampledMutantNames).trim());
  }

  public static void main(String... args) {
//    args = new String[]{"experiments/gen/2nd/singlyLinkedList", "400"};
    if (args.length != 2) {
      logger.error("Wrong number of arguments: " + args.length);
      logger.error("Requires the mutant directory and the number of sampled files.");
      return;
    }
    String mutantDir = args[0];
    int sampleSize = Integer.parseInt(args[1]);
    if (!FileUtil.fileExists(mutantDir) || FileUtil.isFile(mutantDir)) {
      logger.error("Mutant directory path " + mutantDir + " does not exist or is not a directory.");
      return;
    }
    sample(mutantDir, sampleSize);
  }
}
