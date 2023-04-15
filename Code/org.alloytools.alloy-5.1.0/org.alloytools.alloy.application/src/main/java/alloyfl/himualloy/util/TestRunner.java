package alloyfl.himualloy.util;

import java.util.Arrays;

import edu.mit.csail.sdg.alloy4.A4Reporter;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.ast.Command;
import edu.mit.csail.sdg.parser.CompModule;
import edu.mit.csail.sdg.translator.A4Options;
import edu.mit.csail.sdg.translator.A4Solution;
import edu.mit.csail.sdg.translator.TranslateAlloyToKodkod;
import parser.etc.Names;
import parser.util.AlloyUtil;
import parser.util.FileUtil;

public class TestRunner {

    public static boolean runTestsFailFast(String modelToTestAsString, String testSuitePath, A4Options options) {
        String modelAsString = String.join(Names.NEW_LINE, Arrays.asList(modelToTestAsString, FileUtil.readTextWithFilter(testSuitePath, (line, pattern) -> !line.startsWith(pattern), "open")));
        FileUtil.writeText(modelAsString, Names.TMPT_FILE_PATH, false);
        CompModule model = AlloyUtil.compileAlloyModule(Names.TMPT_FILE_PATH);
        assert model != null;
        for (Command cmd : model.getAllCommands()) {
            if (cmd.check || !cmd.label.startsWith(Names.TEST_PREFIX)) {
                continue;
            }
            try {
                A4Solution ans = TranslateAlloyToKodkod.execute_command(A4Reporter.NOP, model.getAllReachableSigs(), cmd, options);
                int actual = ans.satisfiable() ? 1 : 0;
                if (actual != cmd.expects) {
                    return false;
                }
            } catch (Err err) {
                throw new RuntimeException(err.getMessage(), err.getCause());
            }
        }
        return true;
    }
}
