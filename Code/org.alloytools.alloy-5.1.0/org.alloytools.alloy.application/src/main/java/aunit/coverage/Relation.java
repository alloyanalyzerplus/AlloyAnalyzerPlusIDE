package aunit.coverage;

import java.util.HashMap;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.ast.Module;
import edu.mit.csail.sdg.ast.Sig.Field;
import edu.mit.csail.sdg.parser.CompUtil;
import edu.mit.csail.sdg.translator.A4Solution;

public class Relation extends Construct {

    Field         relation;
    public String label;
    public String siglabel;

    public Relation(String name, Field relation) {
        super(name);
        this.relation = relation;
        label = name;
        coverage.put("|r| = 0", Construct.Coverage.NOTCOVERED);
        coverage.put("|r| = 1", Construct.Coverage.NOTCOVERED);
        coverage.put("|r| > 1", Construct.Coverage.NOTCOVERED);

        prettyPrintOrder = new String[] {
                                         "|r| = 0", "|r| = 1", "|r| > 1"
        };
    }

    @Override
    public int extractCoverage(A4Solution instance, Module world) throws Err {
        if (coverage.get("|r| = 0") == Construct.Coverage.NOTCOVERED) {
            if ((boolean) instance.eval(CompUtil.parseOneExpression_fromString(world, "( no " + name + " )"))) {
                markCovered("|r| = 0");
                return 1;
            }
        }
        if (coverage.get("|r| = 1") == Construct.Coverage.NOTCOVERED) {
            if ((boolean) instance.eval(CompUtil.parseOneExpression_fromString(world, "( one " + name + " )"))) {
                markCovered("|r| = 1");
                return 1;
            }
        }
        if (coverage.get("|r| > 1") == Construct.Coverage.NOTCOVERED) {
            if ((boolean) instance.eval(CompUtil.parseOneExpression_fromString(world, "(!{lone " + name + "})"))) {
                markCovered("|r| > 1");
                return 1;
            }
        }
        return 0;
    }

    @Override
    public int extractCoverage(A4Solution instance, Module world, HashMap<String,String> parameters) throws Err {
        return extractCoverage(instance, world);
    }

    public Field getAlloyRelation() {
        return relation;
    }

    @Override
    public TargetingConstraint getTargetingConstraint() {
        if (coverage.get("|r| = 0") == Construct.Coverage.NOTCOVERED) {
            return new TargetingConstraint("{ no " + name + "}", siglabel, label, "|r| = 0");
        }
        if (coverage.get("|r| = 1") == Construct.Coverage.NOTCOVERED) {
            return new TargetingConstraint("{ one " + name + " }", siglabel, label, "|r| = 1");
        }
        if (coverage.get("|r| > 1") == Construct.Coverage.NOTCOVERED) {
            return new TargetingConstraint("{!{ lone " + name + "}}", siglabel, label, "|r| > 1");
        }
        return null;
    }
}
