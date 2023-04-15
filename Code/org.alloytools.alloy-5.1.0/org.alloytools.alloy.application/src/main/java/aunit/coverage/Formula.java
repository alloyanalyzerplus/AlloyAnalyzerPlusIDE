package aunit.coverage;

import java.util.HashMap;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4.Pos;
import edu.mit.csail.sdg.ast.Expr;
import edu.mit.csail.sdg.ast.ExprBinary;
import edu.mit.csail.sdg.ast.ExprCall;
import edu.mit.csail.sdg.ast.ExprUnary;
import edu.mit.csail.sdg.ast.ExprVar;
import edu.mit.csail.sdg.ast.Module;
import edu.mit.csail.sdg.parser.CompUtil;
import edu.mit.csail.sdg.translator.A4Solution;

public class Formula extends Construct {

    Expr       formula;
    public Pos pos;
    int        level;

    public Formula(String prettyPrintName, String name, Pos pos) {
        super(name);
        this.prettyPrintName = prettyPrintName;
        this.formula = null;
        this.outerDomains = "";
        this.pos = pos;

        coverage.put("f = true", Construct.Coverage.NOTCOVERED);
        coverage.put("f = false", Construct.Coverage.NOTCOVERED);

        prettyPrintOrder = new String[] {
                                         "f = true", "f = false"
        };
    }


    public Formula(String name, String outerDomains, Expr formula) {
        super(name);
        this.name = name;
        this.formula = formula;
        this.outerDomains = outerDomains;

        pos = null;
        coverage.put("f = true", Construct.Coverage.NOTCOVERED);
        coverage.put("f = false", Construct.Coverage.NOTCOVERED);
        prettyPrintOrder = new String[] {
                                         "f = true", "f = false"
        };
    }

    public Formula(String name, String outerDomains, Expr formula, String op) {
        super(name);
        this.name = name;
        this.formula = formula;
        this.outerDomains = outerDomains;
        pos = null;
        coverage.put("f = true", Construct.Coverage.NOTCOVERED);
        coverage.put("f = false", Construct.Coverage.NOTCOVERED);
        prettyPrintName = outerDomains + " " + name;
        prettyPrintOrder = new String[] {
                                         "f = true", "f = false"
        };

        if (formula instanceof ExprBinary) {
            Pos oldPos = formula.pos();
            ExprBinary exprBinary = (ExprBinary) formula;

            switch (exprBinary.op) {
                case IMPLIES :
                    if (op.equals("=>")) {
                        highlight_pos.add(new Pos(oldPos.filename, oldPos.x, oldPos.y, oldPos.x + exprBinary.op.toString().length() - 1, oldPos.y2));
                    } else {
                        highlight_pos.add(new Pos(oldPos.filename, oldPos.x, oldPos.y, oldPos.x + 7, oldPos.y2));
                    }
                    break;
                case AND :
                    if (op.equals("&&")) {
                        highlight_pos.add(new Pos(oldPos.filename, oldPos.x, oldPos.y, oldPos.x + exprBinary.op.toString().length(), oldPos.y2));
                    } else {
                        highlight_pos.add(new Pos(oldPos.filename, oldPos.x, oldPos.y, oldPos.x + 7, oldPos.y2));
                    }
                    break;
                default :
                    highlight_pos.add(new Pos(oldPos.filename, oldPos.x, oldPos.y, oldPos.x + exprBinary.op.toString().length() - 1, oldPos.y2));
                    break;
            }
        } else if (formula instanceof ExprCall) {
            Pos oldPos = formula.span();
            highlight_pos.add(new Pos(oldPos.filename, oldPos.x, oldPos.y, oldPos.x2, oldPos.y2));
        } else if (formula instanceof ExprUnary) {
            ExprUnary exprUnary = (ExprUnary) formula;
            Pos oldPos = formula.pos();

            switch (exprUnary.op) {
                case NOT :
                    if (op.equals("!")) {
                        highlight_pos.add(new Pos(oldPos.filename, oldPos.x, oldPos.y, oldPos.x2, oldPos.y2));
                    } else {
                        highlight_pos.add(new Pos(oldPos.filename, oldPos.x, oldPos.y, oldPos.x + 2, oldPos.y2));
                    }
                    break;
                default :
                    highlight_pos.add(new Pos(oldPos.filename, oldPos.x, oldPos.y, oldPos.x2, oldPos.y2));
                    break;
            }
        } else {
            Pos oldPos = formula.pos();
            highlight_pos.add(new Pos(oldPos.filename, oldPos.x, oldPos.y, oldPos.x2, oldPos.y2));
        }

        line_start = formula.span().y + "";
        if (formula.span().y != formula.span().y2)
            line_start += " - " + formula.span().y2;
    }

    @Override
    public int extractCoverage(A4Solution instance, Module world) throws Err {
        return extractCoverage(instance, world, new HashMap<String,String>());
    }

    @Override
    public int extractCoverage(A4Solution instance, Module world, HashMap<String,String> parameters) throws Err {
        int numCriteriaCovered = 0;

        for (ExprVar atoms : instance.getAllAtoms()) {
            world.addGlobal(atoms.label, atoms);
        }
        for (ExprVar skolem : instance.getAllSkolems()) {
            world.addGlobal(skolem.label, skolem);
        }

        /*** Update This ***/
        String parameterLets = "";
        for (String parameter : parameters.keySet()) {
            if (parameters.get(parameter).contains(" : "))
                parameterLets += "some " + parameter + parameters.get(parameter);
            else
                parameterLets += "let " + parameter + " = " + parameters.get(parameter) + " | ";
        }

        if (coverage.get("f = true") == Construct.Coverage.NOTCOVERED) {
            if ((boolean) instance.eval(CompUtil.parseOneExpression_fromString(world, parameterLets + outerDomains + name))) {
                markCovered("f = true");
                numCriteriaCovered++;
            }
        }

        if (coverage.get("f = false") == Construct.Coverage.NOTCOVERED) {
            if ((boolean) instance.eval(CompUtil.parseOneExpression_fromString(world, parameterLets + outerDomains + "!( " + name + " )"))) {
                markCovered("f = false");
                numCriteriaCovered++;
            }
        }
        return numCriteriaCovered;
    }

    public Expr getAlloyFormula() {
        return formula;
    }

    @Override
    public TargetingConstraint getTargetingConstraint() {
        if (coverage.get("f = true") == Construct.Coverage.NOTCOVERED) {
            return new TargetingConstraint("{" + outerDomains + name + "}", origin, name, "f = true");
        }
        if (coverage.get("f = false") == Construct.Coverage.NOTCOVERED) {
            return new TargetingConstraint("{" + outerDomains + "!{ " + name + "}}", origin, name, "f = false");
        }
        return null;
    }

    public void addPos(Pos pos2) {
        highlight_pos.add(pos2);
    }
}
