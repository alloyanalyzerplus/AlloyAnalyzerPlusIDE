package aunit.coverage;

import java.util.HashMap;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.ast.Expr;
import edu.mit.csail.sdg.ast.Module;
import edu.mit.csail.sdg.parser.CompUtil;
import edu.mit.csail.sdg.translator.A4Solution;

public class RelMultiplicity extends Construct {

    Expr   relDecl;
    String coverageCheck;

    public RelMultiplicity(String prettyPrintName, String coverageCheck, Expr relDecl) {
        super(prettyPrintName);
        this.prettyPrintName = prettyPrintName;
        this.relDecl = relDecl;
        this.coverageCheck = coverageCheck;

        coverage.put("f = true", Construct.Coverage.NOTCOVERED);
        coverage.put("f = false", Construct.Coverage.NOTCOVERED);

        prettyPrintOrder = new String[] {
                                         "f = true", "f = false"
        };


    }

    @Override
    public int extractCoverage(A4Solution instance, Module world) throws Err {
        int numCriteriaCovered = 0;
        if (coverage.get("f = true") == Construct.Coverage.NOTCOVERED) {
            if ((boolean) instance.eval(CompUtil.parseOneExpression_fromString(world, coverageCheck))) {
                markCovered("f = true");
                numCriteriaCovered++;
            }
        }

        if (coverage.get("f = false") == Construct.Coverage.NOTCOVERED) {
            if ((boolean) instance.eval(CompUtil.parseOneExpression_fromString(world, "!{ " + coverageCheck + "}"))) {
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

    public Expr getAlloyExpr() {
        return relDecl;
    }

    @Override
    public TargetingConstraint getTargetingConstraint() {
        if (coverage.get("f = true") == Construct.Coverage.NOTCOVERED) {
            return new TargetingConstraint("{" + coverageCheck + "}", origin, name, "f = true");
        }
        if (coverage.get("f = false") == Construct.Coverage.NOTCOVERED) {
            return new TargetingConstraint("{!{" + coverageCheck + "}}", origin, name, "f = false");
        }
        return null;
    }
}
