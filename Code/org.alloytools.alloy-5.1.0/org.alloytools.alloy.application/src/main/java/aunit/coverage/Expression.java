package aunit.coverage;

import java.util.HashMap;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4.Pos;
import edu.mit.csail.sdg.ast.Expr;
import edu.mit.csail.sdg.ast.ExprVar;
import edu.mit.csail.sdg.ast.Module;
import edu.mit.csail.sdg.parser.CompUtil;
import edu.mit.csail.sdg.translator.A4Solution;

public class Expression extends Construct {

    int  ID;
    Expr exp;

    public Expression(String name, int id, String outerDomains, Expr exp) {
        super(name);
        this.name = name;
        ID = id;
        this.exp = exp;
        highlight_pos.add(exp.span());
        line_start = exp.span().y + "";
        if (exp.span().y != exp.span().y2)
            line_start += " - " + exp.span().y2;
        this.outerDomains = outerDomains;
        this.prettyPrintName = outerDomains + name;
        coverage.put("|e| = 0", Construct.Coverage.NOTCOVERED);
        coverage.put("|e| = 1", Construct.Coverage.NOTCOVERED);
        coverage.put("|e| > 1", Construct.Coverage.NOTCOVERED);
        prettyPrintOrder = new String[] {
                                         "|e| = 0", "|e| = 1", "|e| > 1"
        };
    }

    public Expression(String name, int id, String outerDomains, Expr exp, Pos pos) {
        super(name);
        this.name = name;
        ID = id;
        this.exp = exp;
        highlight_pos.add(pos);
        line_start = pos.y + "";
        if (pos.y != pos.y2)
            line_start += " - " + pos.y2;
        this.outerDomains = outerDomains;
        this.prettyPrintName = outerDomains + name;
        coverage.put("|e| = 0", Construct.Coverage.NOTCOVERED);
        coverage.put("|e| = 1", Construct.Coverage.NOTCOVERED);
        coverage.put("|e| > 1", Construct.Coverage.NOTCOVERED);
        prettyPrintOrder = new String[] {
                                         "|e| = 0", "|e| = 1", "|e| > 1"
        };
    }

    @Override
    public int extractCoverage(A4Solution instance, Module world) throws Err {
        return extractCoverage(instance, world, new HashMap<String,String>());
    }

    @Override
    public int extractCoverage(A4Solution instance, Module world, HashMap<String,String> parameters) throws Err {

        for (ExprVar atoms : instance.getAllAtoms()) {
            world.addGlobal(atoms.label, atoms);
        }
        for (ExprVar skolem : instance.getAllSkolems()) {
            world.addGlobal(skolem.label, skolem);
        }

        String parameterLets = "";
        /*** Update this ***/
        for (String parameter : parameters.keySet()) {
            if (parameters.get(parameter).contains(" : "))
                parameterLets += "some " + parameter + parameters.get(parameter);
            else
                parameterLets += "let " + parameter + " = " + parameters.get(parameter) + " | ";
        }

        if (coverage.get("|e| = 0") == Construct.Coverage.NOTCOVERED) {
            //System.out.println(parameterLets + outerDomains + "# {" + name + "} = 0");
            if ((boolean) instance.eval(CompUtil.parseOneExpression_fromString(world, parameterLets + outerDomains + " no {" + name + "} "))) {
                markCovered("|e| = 0");
                return 1;
            }
        }
        if (coverage.get("|e| = 1") == Construct.Coverage.NOTCOVERED) {
            if ((boolean) instance.eval(CompUtil.parseOneExpression_fromString(world, parameterLets + outerDomains + " one {" + name + "} "))) {
                markCovered("|e| = 1");
                return 1;
            }
        }
        if (coverage.get("|e| > 1") == Construct.Coverage.NOTCOVERED) {
            if ((boolean) instance.eval(CompUtil.parseOneExpression_fromString(world, parameterLets + outerDomains + "! lone {" + name + "} "))) {
                markCovered("|e| > 1");
                return 1;
            }
        }

        return 0;

    }

    public Expr getAlloyExpression() {
        return exp;
    }

    @Override
    public TargetingConstraint getTargetingConstraint() {
        if (coverage.get("|e| = 0") == Construct.Coverage.NOTCOVERED) {
            return new TargetingConstraint(outerDomains + "{ no " + name + " }", origin, name, "|e| = 0");
        }
        if (coverage.get("|e| = 1") == Construct.Coverage.NOTCOVERED) {
            return new TargetingConstraint(outerDomains + "{ one " + name + " }", origin, name, "|e| = 1");
        }
        if (coverage.get("|e| > 1") == Construct.Coverage.NOTCOVERED) {
            return new TargetingConstraint(outerDomains + "{!{ lone " + name + " }}", origin, name, "|e| > 1");
        }
        return null;
    }

    public void addPos(Pos highlight) {
        highlight_pos.add(highlight);
    }
}
