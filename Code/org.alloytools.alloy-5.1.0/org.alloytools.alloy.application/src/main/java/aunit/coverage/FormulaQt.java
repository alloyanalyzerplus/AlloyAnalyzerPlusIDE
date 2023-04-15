package aunit.coverage;

import java.util.HashMap;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4.Pos;
import edu.mit.csail.sdg.ast.Expr;
import edu.mit.csail.sdg.ast.ExprVar;
import edu.mit.csail.sdg.ast.Module;
import edu.mit.csail.sdg.parser.CompUtil;
import edu.mit.csail.sdg.translator.A4Solution;

public class FormulaQt extends Construct {

    String quantifier;
    String domain;
    String body;
    String var;
    Expr   formula;

    public FormulaQt(String name, String quantifier, String var, String domain, String body, Expr formula, String outerDomains) {
        super(name);
        this.quantifier = quantifier;
        this.body = body;
        this.var = var;
        this.domain = domain;
        this.formula = formula;
        this.outerDomains = outerDomains;

        this.label = outerDomains + " " + name;
        highlight_pos.add(new Pos(formula.pos.filename, formula.pos.x, formula.pos.y, formula.pos.x2, formula.pos.y2));

        line_start = formula.span().y + "";
        if (formula.span().y != formula.span().y2)
            line_start += " - " + formula.span().y2;

        coverage.put("f = true", Construct.Coverage.NOTCOVERED);
        coverage.put("f = false", Construct.Coverage.NOTCOVERED);
        coverage.put("|d| = 0", Construct.Coverage.NOTCOVERED);
        coverage.put("|d| = 1 and f = true", Construct.Coverage.NOTCOVERED);
        coverage.put("|d| = 1 and f = false", Construct.Coverage.NOTCOVERED);
        coverage.put("|d| > 1 and f = true", Construct.Coverage.NOTCOVERED);
        coverage.put("|d| > 1 and f = false", Construct.Coverage.NOTCOVERED);
        coverage.put("|d| > 1 and f = true & false", Construct.Coverage.NOTCOVERED);

        prettyPrintOrder = new String[] {
                                         "f = true", "f = false", "|d| = 0", "|d| = 1 and f = true", "|d| = 1 and f = false", "|d| > 1 and f = true", "|d| > 1 and f = false", "|d| > 1 and f = true & false"
        };
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

        String parameterLets = "";
        for (String parameter : parameters.keySet()) {
            if (parameters.get(parameter).contains(" : "))
                parameterLets += "some " + parameter + parameters.get(parameter);
            else
                parameterLets += "let " + parameter + " = " + parameters.get(parameter) + " | ";
        }

        if (coverage.get("f = true") == Construct.Coverage.NOTCOVERED) {
            //System.out.println(parameterLets + outerDomains + name);
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

        String some = " some " + var + " : " + domain + " | ";
        String all = " all " + var + " : " + domain + " | ";

        if (coverage.get("|d| = 0") == Construct.Coverage.NOTCOVERED) {
            if ((boolean) instance.eval(CompUtil.parseOneExpression_fromString(world, parameterLets + outerDomains + " { no " + domain + "} "))) {
                markCovered("|d| = 0");
                numCriteriaCovered++;
            }
        }

        if (coverage.get("|d| = 1 and f = true") == Construct.Coverage.NOTCOVERED) {
            if ((boolean) instance.eval(CompUtil.parseOneExpression_fromString(world, parameterLets + outerDomains + " { one " + domain + "}"))) {
                if ((boolean) instance.eval(CompUtil.parseOneExpression_fromString(world, parameterLets + outerDomains + some + body))) {
                    markCovered("|d| = 1 and f = true");
                    numCriteriaCovered++;
                }
            }
        }

        if (coverage.get("|d| = 1 and f = false") == Construct.Coverage.NOTCOVERED) {
            if ((boolean) instance.eval(CompUtil.parseOneExpression_fromString(world, parameterLets + outerDomains + " { one " + domain + "}"))) {
                if ((boolean) instance.eval(CompUtil.parseOneExpression_fromString(world, parameterLets + outerDomains + some + "!( " + body + " )"))) {
                    markCovered("|d| = 1 and f = false");
                    numCriteriaCovered++;
                }
            }
        }

        if (coverage.get("|d| > 1 and f = true") == Construct.Coverage.NOTCOVERED) {
            if ((boolean) instance.eval(CompUtil.parseOneExpression_fromString(world, parameterLets + outerDomains + " ! lone {" + domain + "}"))) {
                if ((boolean) instance.eval(CompUtil.parseOneExpression_fromString(world, parameterLets + outerDomains + all + body))) {
                    markCovered("|d| > 1 and f = true");
                    numCriteriaCovered++;
                }
            }
        }

        if (coverage.get("|d| > 1 and f = false") == Construct.Coverage.NOTCOVERED) {
            if ((boolean) instance.eval(CompUtil.parseOneExpression_fromString(world, parameterLets + outerDomains + "  ! lone {" + domain + "}"))) {
                if ((boolean) instance.eval(CompUtil.parseOneExpression_fromString(world, parameterLets + outerDomains + all + "!( " + body + " )"))) {
                    markCovered("|d| > 1 and f = false");
                    numCriteriaCovered++;
                }
            }
        }

        if (coverage.get("|d| > 1 and f = true & false") == Construct.Coverage.NOTCOVERED) {
            if ((boolean) instance.eval(CompUtil.parseOneExpression_fromString(world, parameterLets + outerDomains + " ! lone {" + domain + "} "))) {
                if ((boolean) instance.eval(CompUtil.parseOneExpression_fromString(world, parameterLets + outerDomains + some + body))) {
                    if ((boolean) instance.eval(CompUtil.parseOneExpression_fromString(world, parameterLets + outerDomains + some + "!( " + body + " )"))) {
                        markCovered("|d| > 1 and f = true & false");
                        numCriteriaCovered++;
                    }
                }
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

        String some = " some " + var + " : " + domain + " | ";
        String all = " all " + var + " : " + domain + " | ";

        if (coverage.get("|d| = 0") == Construct.Coverage.NOTCOVERED) {
            return new TargetingConstraint("{" + outerDomains + " { no " + domain + " }}", origin, name, "|d| = 0");
        }
        if (coverage.get("|d| = 1 and f = true") == Construct.Coverage.NOTCOVERED) {
            return new TargetingConstraint("{" + outerDomains + " {{ one " + domain + "} and {" + some + body + "}}}", origin, name, "|d| = 1 and f = true");
        }
        if (coverage.get("|d| = 1 and f = false") == Construct.Coverage.NOTCOVERED) {
            return new TargetingConstraint("{" + outerDomains + " {{ one " + domain + "} and {" + some + "!{ " + body + "}}}}", origin, name, "|d| = 1 and f = false");
        }
        if (coverage.get("|d| > 1 and f = true") == Construct.Coverage.NOTCOVERED) {
            return new TargetingConstraint("{" + outerDomains + " {{!lone " + domain + "}  and {" + all + body + "}}}", origin, name, "|d| > 1 and f = true");
        }
        if (coverage.get("|d| > 1 and f = false") == Construct.Coverage.NOTCOVERED) {
            return new TargetingConstraint("{" + outerDomains + " {{!lone " + domain + "} and {" + all + "!{ " + body + "}}}}", origin, name, "|d| > 1 and f = false");
        }
        if (coverage.get("|d| > 1 and f = true & false") == Construct.Coverage.NOTCOVERED) {
            return new TargetingConstraint("{" + outerDomains + " {{!lone " + domain + "} and { " + some + body + "} and {" + some + "!{ " + body + "}}}}", origin, name, "|d| > 1 and f = true & false");
        }
        return null;
    }

    public void addPos(Pos pos2) {
        highlight_pos.add(pos2);
    }
}
