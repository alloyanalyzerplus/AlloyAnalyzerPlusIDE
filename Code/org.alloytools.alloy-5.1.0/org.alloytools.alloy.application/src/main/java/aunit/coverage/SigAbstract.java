package aunit.coverage;

import java.util.HashMap;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4.Pos;
import edu.mit.csail.sdg.ast.Module;
import edu.mit.csail.sdg.ast.Sig;
import edu.mit.csail.sdg.parser.CompUtil;
import edu.mit.csail.sdg.translator.A4Solution;

public class SigAbstract extends Construct {

    Sig    signature;
    Pos    pos;
    String extendedSigs;

    public SigAbstract(String prettyPrintName, String name, Pos pos, Sig signature) {
        super(name);
        this.prettyPrintName = prettyPrintName;
        this.signature = signature;
        this.pos = pos;
        coverage.put("f = true", Construct.Coverage.NOTCOVERED);
        coverage.put("f = false", Construct.Coverage.NOTCOVERED);
        prettyPrintOrder = new String[] {
                                         "f = true", "f = false"
        };
        extendedSigs = signature.label.substring(signature.label.lastIndexOf("/") + 1);
    }

    public void addExtendedSig(String name) {
        extendedSigs += " - " + name;
    }

    @Override
    public int extractCoverage(A4Solution instance, Module world) throws Err {
        int numCriteriaCovered = 0;
        if (coverage.get("f = true") == Construct.Coverage.NOTCOVERED) {
            if ((boolean) instance.eval(CompUtil.parseOneExpression_fromString(world, "{ no {" + extendedSigs + "}}"))) {
                markCovered("f = true");
                numCriteriaCovered++;
            }
        }

        if (coverage.get("f = false") == Construct.Coverage.NOTCOVERED) {
            if ((boolean) instance.eval(CompUtil.parseOneExpression_fromString(world, "{ some {" + extendedSigs + "}}"))) {
                markCovered("f = false");
                numCriteriaCovered++;
            }
        }
        return numCriteriaCovered;
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
        if (coverage.get("f = true") == Construct.Coverage.NOTCOVERED) {
            return new TargetingConstraint("{ no { " + extendedSigs + "}}", origin, name, "f = true");
        }

        if (coverage.get("f = false") == Construct.Coverage.NOTCOVERED) {
            return new TargetingConstraint("{ some { " + extendedSigs + "}}", origin, name, "f = false");
        }
        return null;
    }
}
