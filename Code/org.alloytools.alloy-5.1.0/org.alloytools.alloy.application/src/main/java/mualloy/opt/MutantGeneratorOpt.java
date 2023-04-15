package mualloy.opt;

import java.util.LinkedHashSet;
import java.util.Set;

import mualloy.util.AUnitTestCase;
import mualloy.util.SpecialCase;
import parser.opt.Opt;

public class MutantGeneratorOpt extends Opt {

    /**
     * Path to the directory where mutants are generated.
     */
    protected String             mutantDirPath;
    /**
     * Path to the directory where result files are generated.
     */
    protected String             resultDirPath;
    /**
     * Path to the directory where meta files are generated.
     */
    protected String             metaDirPath;
    /**
     * Special treatment for modules
     */
    protected SpecialCase        specialCase;
    /**
     * Save generated tests.
     */
    protected Set<AUnitTestCase> tests;

    public MutantGeneratorOpt(String modelPath, String correctModelPath, String mutantDirPath, String resultDirPath, String metaDirPath, int scope) {
        super(modelPath, correctModelPath, scope);
        this.mutantDirPath = mutantDirPath;
        this.resultDirPath = resultDirPath;
        this.metaDirPath = metaDirPath;
        this.specialCase = new SpecialCase();
        this.tests = new LinkedHashSet<>();
    }

    public String getMutantDirPath() {
        return mutantDirPath;
    }

    public String getResultDirPath() {
        return resultDirPath;
    }

    public String getMetaDirPath() {
        return metaDirPath;
    }

    public SpecialCase getSpecialCase() {
        return specialCase;
    }

    public Set<AUnitTestCase> getTests() {
        return tests;
    }
}
