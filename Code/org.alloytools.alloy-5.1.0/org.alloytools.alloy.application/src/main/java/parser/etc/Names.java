package parser.etc;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Names {

    // Suffix
    public final static String       DOT_ALS                  = ".als";
    public final static String       DOT_FLT                  = ".flt";
    public final static String       DOT_LST                  = ".lst";

    // Directory paths
    public final static String       PROJECT_DIR_PATH         = System.getProperty("user.dir");
    public final static String       HIDDEN_DIR_PATH          = Paths.get(PROJECT_DIR_PATH, ".hidden").toString();

    // File paths
    public final static String       CHECK_FILE_PATH          = Paths.get(HIDDEN_DIR_PATH, "attempt" + DOT_ALS).toString();
    public final static String       EQUIV_FILE_PATH          = Paths.get(HIDDEN_DIR_PATH, "equiv" + DOT_ALS).toString();
    public final static String       TMPT_FILE_PATH           = Paths.get(HIDDEN_DIR_PATH, "tmp" + DOT_ALS).toString();

    // Special characters
    public final static String       SLASH                    = "/";
    public final static String       NEW_LINE                 = "\n";
    public final static String       COMMA                    = ",";
    public final static String       DOT                      = ".";
    public final static String       SPACE                    = "\\s";
    public final static String       UNDERSCORE               = "_";
    public final static String       VERTICAL_BAR             = "|";
    public final static String       DOLLAR                   = "$";
    public final static String       SINGLE_QUOTE             = "'";

    // Special names
    public final static String       MUTATED                  = "MUTATED";
    public final static String       EQUIV_ASSERTION_NAME     = "EQUIV";
    public final static String       ORIGINAL_MODEL_NAME      = "ORIGIN";
    public final static String       MUTATION_BASED_TEST_NAME = "TEST";
    public final static List<String> IGNORE_NAMES             = Arrays.asList("IGNORE", "Reachable");

    public final static String       TEST_PREFIX              = "test";
}
