package aunit.coverage;

import java.util.HashMap;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.ast.Module;
import edu.mit.csail.sdg.ast.Sig;
import edu.mit.csail.sdg.parser.CompUtil;
import edu.mit.csail.sdg.translator.A4Solution;

public class Signature extends Construct {

    Sig           signature;
    public String label;

    public Signature(String name, Sig signature) {
        super(name);
        this.signature = signature;
        label = name;
        coverage.put("|s| = 0", Construct.Coverage.NOTCOVERED);
        coverage.put("|s| = 1", Construct.Coverage.NOTCOVERED);
        coverage.put("|s| > 1", Construct.Coverage.NOTCOVERED);

        prettyPrintOrder = new String[] {
                                         "|s| = 0", "|s| = 1", "|s| > 1"
        };
    }

    @Override
    public int extractCoverage(A4Solution instance, Module world) throws Err {
        if (coverage.get("|s| = 0") == Construct.Coverage.NOTCOVERED) {
            if ((boolean) instance.eval(CompUtil.parseOneExpression_fromString(world, "no " + name))) {
                markCovered("|s| = 0");
                return 1;
            }
        }
        if (coverage.get("|s| = 1") == Construct.Coverage.NOTCOVERED) {
            if ((boolean) instance.eval(CompUtil.parseOneExpression_fromString(world, "one " + name))) {
                markCovered("|s| = 1");
                return 1;
            }
        }
        if (coverage.get("|s| > 1") == Construct.Coverage.NOTCOVERED) {
            if ((boolean) instance.eval(CompUtil.parseOneExpression_fromString(world, "!{ lone " + name + "}"))) {
                markCovered("|s| > 1");
                return 1;
            }
        }
        return 0;
    }

    @Override
    public int extractCoverage(A4Solution instance, Module world, HashMap<String,String> parameters) throws Err {
        return extractCoverage(instance, world);
    }

    public Sig getAlloySig() {
        return signature;
    }

    @Override
    public TargetingConstraint getTargetingConstraint() {
        if (coverage.get("|s| = 0") == Construct.Coverage.NOTCOVERED) {
            return new TargetingConstraint("{ no " + name + "}", label, label, "|s| = 0");
        }
        if (coverage.get("|s| = 1") == Construct.Coverage.NOTCOVERED) {
            return new TargetingConstraint("{ one " + name + " }", label, label, "|s| = 1");
        }
        if (coverage.get("|s| > 1") == Construct.Coverage.NOTCOVERED) {
            return new TargetingConstraint("{!{ lone " + name + "}}", label, label, "|s| > 1");
        }
        return null;
    }
}
