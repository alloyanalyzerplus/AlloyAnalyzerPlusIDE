package alloyfl.mutation.util;

import alloyfl.mutation.opt.FaultLocatorOpt;
import edu.mit.csail.sdg.alloy4.A4Reporter;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.parser.CompModule;
import edu.mit.csail.sdg.translator.A4Solution;
import edu.mit.csail.sdg.translator.TranslateAlloyToKodkod;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import parser.etc.Names;
import parser.util.AlloyUtil;
import parser.util.FileUtil;

public class TestRunner {

  public static List<Boolean> runTests(String modelToTestAsString, FaultLocatorOpt opt) {
    List<Boolean> result = new ArrayList<>();
    String modelAsString = String.join(Names.NEW_LINE,
        Arrays.asList(
            modelToTestAsString,
            FileUtil.readTextWithFilter(
                opt.getTestSuitePath(),
                (line, pattern) -> !line.startsWith(pattern),
                "open")
        )
    );
    FileUtil.writeText(modelAsString, Names.TMPT_FILE_PATH, false);
    CompModule model = AlloyUtil.compileAlloyModule(Names.TMPT_FILE_PATH);
    assert model != null;
    model.getAllCommands().stream()
        .filter(cmd -> !cmd.check && cmd.label.startsWith(Names.TEST_PREFIX)).forEach(cmd -> {
      try {
        A4Solution ans = TranslateAlloyToKodkod
            .execute_command(A4Reporter.NOP, model.getAllReachableSigs(), cmd, opt.getOptions());
        int actual = ans.satisfiable() ? 1 : 0;
        result.add(actual == cmd.expects);
      } catch (Err err) {
        err.printStackTrace();
      }
    });
    return result;
  }
}
