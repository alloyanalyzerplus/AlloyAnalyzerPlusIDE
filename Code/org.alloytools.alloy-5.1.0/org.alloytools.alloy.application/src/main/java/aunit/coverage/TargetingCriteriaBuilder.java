package aunit.coverage;

import java.util.ArrayList;
import java.util.HashMap;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4.ErrorSyntax;
import edu.mit.csail.sdg.alloy4.Pair;
import edu.mit.csail.sdg.ast.Decl;
import edu.mit.csail.sdg.ast.Expr;
import edu.mit.csail.sdg.ast.ExprBinary;
import edu.mit.csail.sdg.ast.ExprCall;
import edu.mit.csail.sdg.ast.ExprConstant;
import edu.mit.csail.sdg.ast.ExprHasName;
import edu.mit.csail.sdg.ast.ExprITE;
import edu.mit.csail.sdg.ast.ExprLet;
import edu.mit.csail.sdg.ast.ExprList;
import edu.mit.csail.sdg.ast.ExprQt;
import edu.mit.csail.sdg.ast.ExprUnary;
import edu.mit.csail.sdg.ast.Func;
import edu.mit.csail.sdg.ast.Module;
import edu.mit.csail.sdg.ast.Sig;
import edu.mit.csail.sdg.ast.Sig.Field;
import edu.mit.csail.sdg.ast.Sig.PrimSig;
import edu.mit.csail.sdg.translator.A4Solution;

public class TargetingCriteriaBuilder {

    ArrayList<SigParagraph>        signatures;
    HashMap<String,AlloyParagraph> paragraphs;
    ArrayList<String>              facts;
    String[]                       text;
    int                            numExp;
    public Module                  world;
    ArrayList<String>              names;

    public TargetingCriteriaBuilder(Module world, ArrayList<String> names) {
        this.world = world;
        signatures = new ArrayList<SigParagraph>();
        paragraphs = new HashMap<String,AlloyParagraph>();
        facts = new ArrayList<String>();
        this.names = new ArrayList<String>();
        this.names.addAll(names);
        numExp = 0;
    }

    public void findCriteria(String text) {
        this.text = text.split("\n");
        sigAndRelCriteria(); //Find all signatures and relations criteria

        String outerDomain = "";
        for (Func func : world.getAllFunc()) { //Find all expression/formula coverage per function
            if (!func.isVal && !func.label.toLowerCase().contains("run") && !func.label.toLowerCase().contains("check") && !func.label.toLowerCase().contains("open")) {
                Expr body = func.getBody();
                String origin = func.label.substring(func.label.lastIndexOf("/") + 1);
                if (names.contains(origin)) {
                    paragraphs.put(origin, new AlloyParagraph(origin));
                    if (body instanceof ExprConstant) {
                        if (body.toString().equals("true"))
                            paragraphs.get(origin).addFormula(new Formula("{}", outerDomain, body));
                    } else {
                        expAndFormulaCriteria(((ExprUnary) body).sub, origin, outerDomain);
                    }
                }
            }
        }

        for (Pair<String,Expr> fact : world.getAllFacts()) {
            if (!paragraphs.containsKey("Facts")) {
                paragraphs.put("Facts", new AlloyParagraph("Facts"));
                facts.add("Facts");
            }
            if (!(fact.b instanceof ExprConstant)) {
                if (fact.b instanceof ExprList) {
                    ExprList factsEL = (ExprList) fact.b;
                    for (Expr e : factsEL.args) {
                        outerDomain = "";
                        expAndFormulaCriteria(e, "Facts", outerDomain);
                    }
                } else {
                    outerDomain = "";
                    expAndFormulaCriteria(fact.b, "Facts", outerDomain);
                }
            }
        }
    }

    public void sigAndRelCriteria() {

        for (Sig sig : world.getAllSigs()) { //find every signature
            String sig_name = sig.label.substring(sig.label.lastIndexOf("/") + 1);
            if (names.contains(sig_name)) {
                SigParagraph sigP = new SigParagraph(sig_name, new Signature(sig_name, sig));
                ArrayList<String> relations = new ArrayList<String>();
                /**
                 * Add implicit facts: 1. Abstract sigs need to be |s| = 0 always 2. Multplicity
                 * constraints
                 **/
                if (sig.isAbstract != null) { //1. Handling abstract
                    SigAbstract abstractSig = new SigAbstract("abstract " + sig_name, "#{" + sig_name + "} = 0", sig.isAbstract, sig);
                    /** Find all signatures that extend **/
                    for (Sig extSig : world.getAllSigs()) {
                        if (!extSig.isTopLevel()) {
                            String parent = ((Sig.PrimSig) extSig).parent.label;
                            parent = parent.substring(parent.lastIndexOf("/") + 1);
                            if (parent.equals(sig_name)) {
                                abstractSig.addExtendedSig(extSig.label.substring(extSig.label.lastIndexOf("/") + 1));
                            }
                        }
                    }
                    sigP.addImplicitFact(abstractSig);
                }

                if (sig.isOne != null) { //2. Handling multiplicity constraints
                    sigP.addImplicitFact(new Formula("one " + sig_name, "one " + sig_name, sig.isOne));
                } else if (sig.isLone != null) {
                    sigP.addImplicitFact(new Formula("lone " + sig_name, "lone " + sig_name, sig.isLone));
                } else if (sig.isSome != null) {
                    sigP.addImplicitFact(new Formula("some " + sig_name, "some " + sig_name, sig.isSome));
                }

                for (Field field : sig.getFields()) { //find every relation
                    sigP.addRelation(new Relation(field.label, field));
                }

                for (Decl relationDecl : sig.getFieldDecls()) {
                    for (ExprHasName name : relationDecl.names) {
                        relations.add(name.label);
                        String multi = getText(relationDecl.expr);
                        if (multi.startsWith("lone")) {
                            String coverageCheck = "all atom: " + sig_name + " { lone atom." + name.label + " }";
                            sigP.addImplicitFact(new RelMultiplicity(name.label + ": " + multi, coverageCheck, relationDecl.expr));
                        } else if (multi.startsWith("one")) {
                            String coverageCheck = "all atom: " + sig_name + " { one atom." + name.label + " }";
                            sigP.addImplicitFact(new RelMultiplicity(name.label + ": " + multi, coverageCheck, relationDecl.expr));
                        }
                    }
                }
                signatures.add(sigP);

                for (Expr e : sig.getFacts()) {
                    if (!paragraphs.containsKey("Facts")) {
                        paragraphs.put("Facts", new AlloyParagraph("Facts"));
                        facts.add("Facts");
                    }
                    expAndFormulaCriteriaFromSigs(e, "Facts", "", sig_name, relations);
                }
            }
        }
    }

    public void expAndFormulaCriteria(Expr e, String origin, String outerDomain) {
        /* Used to flag which formulas are primitive booleans and thus add coverage */
        boolean primitiveBoolean = false;
        if (e.getHTML().contains("PrimitiveBoolean")) {
            primitiveBoolean = true;
        }

        if (e instanceof ExprBinary) {
            ExprBinary binExp = (ExprBinary) e;
            String name = getText(e);

            /* ExprBinary can be an expression or a formula, depending on the operator */
            switch (binExp.op) {
                case JOIN :
                case INTERSECT :
                case PLUS :
                case ARROW :
                case MINUS :
                    paragraphs.get(origin).addExpression(new Expression(name, numExp, outerDomain, binExp));
                    numExp++;
                    break;
                default :
                    if (primitiveBoolean)
                        paragraphs.get(origin).addFormula(new Formula(name, outerDomain, binExp));
            }

            if (!(binExp.op == ExprBinary.Op.JOIN)) {
                expAndFormulaCriteria(binExp.left, origin, outerDomain);
                expAndFormulaCriteria(binExp.right, origin, outerDomain);
            }
        } else if (e instanceof ExprQt) {

            /*
             * ExprQt quantFormula = (ExprQt) e; try { quantFormula = (ExprQt)
             * quantFormula.desugar(); } catch (ErrorSyntax e1) { // TODO Auto-generated
             * catch block e1.printStackTrace(); } String name = getText(e); String quant =
             * quantFormula.op.toString(); String var = ""; String comma = ""; for(int i =
             * 0; i < quantFormula.decls.size(); i++){ for(ExprHasName s :
             * quantFormula.decls.get(i).names){ var += comma + s.label; comma = ", "; } }
             * ExprUnary exprUnary = (ExprUnary)quantFormula.decls.get(0).expr; Expr
             * domainExpr = exprUnary.sub; String domain = getText(domainExpr); Expr body =
             * quantFormula.sub; String quantBody = getText(body);
             * if(quantBody.equals("true")){ } else { paragraphs.get(origin).addFormula(new
             * FormulaQt(name, quant, var, domain, quantBody, quantFormula, outerDomain)); }
             * paragraphs.get(origin).addExpression(new Expression(domain, numExp,
             * outerDomain, domainExpr)); numExp++; for(int i = 0; i <
             * quantFormula.decls.size(); i++){ String temp =
             * quantFormula.decls.get(i).expr.toString(); if(temp.startsWith("one")){ temp =
             * temp.substring(4); } for(ExprHasName s : quantFormula.decls.get(i).names){
             * outerDomain += "some " + s.label + " : " + temp + " | "; } }
             * expAndFormulaCriteria(quantFormula.sub, origin, outerDomain);
             */
            ExprQt quantFormula = (ExprQt) e;
            try {
                quantFormula = (ExprQt) quantFormula.desugar();
            } catch (ErrorSyntax e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            String name = getText(e);
            String quant = quantFormula.op.toString();
            String var = "";
            Expr body = quantFormula.sub;
            String quantBody = getText(body);

            if (quant.equals("comprehension")) {
                for (int decl = 0; decl < quantFormula.decls.size(); decl++) {
                    for (int v = 0; v < quantFormula.decls.get(decl).names.size(); v++) {
                        var = quantFormula.decls.get(decl).names.get(v).label;
                        ExprUnary exprUnary = (ExprUnary) quantFormula.decls.get(decl).expr;
                        Expr domainExpr = exprUnary.sub;
                        String domain = getText(domainExpr);

                        if (quantBody.equals("true")) {
                        } else {
                            if (decl == 0 && v == 0) {
                                paragraphs.get(origin).addExpression(new Expression(name, numExp, outerDomain, e));
                                numExp++;
                            }
                        }
                        paragraphs.get(origin).addExpression(new Expression(domain, numExp, outerDomain, domainExpr));
                        numExp++;

                        String temp = quantFormula.decls.get(decl).expr.toString();
                        if (temp.startsWith("one")) {
                            temp = temp.substring(4);
                        }
                        outerDomain += "some " + quantFormula.decls.get(decl).names.get(v).label + " : " + temp + " | ";
                    }
                }
            } else {
                for (int decl = 0; decl < quantFormula.decls.size(); decl++) {
                    for (int v = 0; v < quantFormula.decls.get(decl).names.size(); v++) {
                        var = quantFormula.decls.get(decl).names.get(v).label;

                        ExprUnary exprUnary = (ExprUnary) quantFormula.decls.get(decl).expr;
                        Expr domainExpr = exprUnary.sub;
                        String domain = getText(domainExpr);

                        String adtn_decls = "";
                        for (int c = v + 1; c < quantFormula.decls.get(decl).names.size(); c++) {
                            adtn_decls += quant + " " + quantFormula.decls.get(decl).names.get(c).label + " : " + domain + " | ";
                        }
                        for (int d = decl + 1; d < quantFormula.decls.size(); d++) {
                            ExprUnary exprUnary2 = (ExprUnary) quantFormula.decls.get(d).expr;
                            Expr domainExpr2 = exprUnary2.sub;
                            String domain2 = getText(domainExpr2);
                            for (int c = 0; c < quantFormula.decls.get(d).names.size(); c++) {
                                adtn_decls += quant + " " + quantFormula.decls.get(d).names.get(c).label + " : " + domain2 + " | ";
                            }
                        }

                        if (quantBody.equals("true")) {
                        } else {
                            if (decl != 0 && v != 0) {
                                name = quant + " " + var + " : " + domain + " | " + adtn_decls + body;
                            }
                            paragraphs.get(origin).addFormula(new FormulaQt(name, quant, var, domain, adtn_decls + quantBody, quantFormula, outerDomain));
                        }
                        paragraphs.get(origin).addExpression(new Expression(domain, numExp, outerDomain, domainExpr));
                        numExp++;

                        String temp = quantFormula.decls.get(decl).expr.toString();
                        if (temp.startsWith("one")) {
                            temp = temp.substring(4);
                        }
                        outerDomain += "some " + quantFormula.decls.get(decl).names.get(v).label + " : " + temp + " | ";
                    }
                }
            }
            expAndFormulaCriteria(quantFormula.sub, origin, outerDomain);

        } else if (e instanceof ExprUnary) {
            ExprUnary unExp = (ExprUnary) e;
            String name = getText(e);

            if (unExp.op.toString().equals("NOOP")) {
                expAndFormulaCriteria(unExp.sub, origin, outerDomain);
            } else {
                if (unExp.sub == null) {
                    paragraphs.get(origin).addExpression(new Expression(name, numExp, outerDomain, unExp));
                    numExp++;
                } else {
                    /* Check is UnaryExpr is a expression or formula */
                    switch (unExp.op) {
                        case TRANSPOSE :
                        case RCLOSURE :
                        case CLOSURE :
                        case CARDINALITY :
                            if (!unExp.op.toString().equals("NOOP")) {
                                paragraphs.get(origin).addExpression(new Expression(name, numExp, outerDomain, unExp));
                                numExp++;
                            }
                            break;
                        default :
                            if (primitiveBoolean)
                                paragraphs.get(origin).addFormula(new Formula(name, outerDomain, unExp));
                    }
                }
                expAndFormulaCriteria(unExp.sub, origin, outerDomain);
            }

        } else if (e instanceof ExprList) {
            ExprList listExp = (ExprList) e;
            String name = getText(e);
            if (primitiveBoolean)
                paragraphs.get(origin).addFormula(new Formula(name, outerDomain, listExp));

            for (Expr e1 : listExp.args) {
                expAndFormulaCriteria(e1, origin, outerDomain);
            }
        } else if (e instanceof ExprCall) {
            ExprCall callExp = (ExprCall) e;
            String name = getText(e);
            if (callExp.fun.isPred) {
                paragraphs.get(origin).addFormula(new Formula(name, outerDomain, callExp));
            } else if (!callExp.fun.isVal) {
                paragraphs.get(origin).addExpression(new Expression(name, numExp, outerDomain, callExp));
                numExp++;
            }
        } else if (e instanceof ExprITE) {
            /*
             * formula syntax == if 'condition' (subformula) else (subformula) - entire
             * formula added - send the condition and both subformulas to be broken down for
             * coverage
             */
            ExprITE exITE = (ExprITE) e;
            String name = getText(e);
            if (primitiveBoolean)
                paragraphs.get(origin).addFormula(new Formula(name, outerDomain, exITE));

            expAndFormulaCriteria(exITE.cond, origin, outerDomain);
            expAndFormulaCriteria(exITE.left, origin, outerDomain);
            expAndFormulaCriteria(exITE.right, origin, outerDomain);
        } else if (e instanceof ExprLet) {
            /*
             * formula syntax == "let 'var' = 'domain' | 'subformula'" - the entire let
             * formula added with T/F criteria - send the 'subformula' to be broken down for
             * coverage
             */
            ExprLet exLet = (ExprLet) e;
            String name = getText(e);
            if (primitiveBoolean)
                paragraphs.get(origin).addFormula(new Formula("let " + name, outerDomain, exLet));

            outerDomain += "let " + exLet.var.label + " = " + exLet.expr.toString() + " | ";
            expAndFormulaCriteria(exLet.sub, origin, outerDomain);
        } else if (e instanceof ExprConstant) {
            ExprConstant constant = (ExprConstant) e;
            if (constant.op.toString().equals("iden")) {
                paragraphs.get(origin).addExpression(new Expression(constant.op.toString(), numExp, outerDomain, constant));
                numExp++;
            }
        } else if (e instanceof PrimSig) {
            PrimSig sig = (PrimSig) e;
            if (sig.label.equals("univ")) {
                paragraphs.get(origin).addExpression(new Expression(sig.label, numExp, outerDomain, sig));
                numExp++;
            }
        }
        /*
         * No coverage criteria for: 1. ExprVar - variables do not currently contribute
         * to coverage 2.
         */
    }

    public void expAndFormulaCriteriaFromSigs(Expr e, String origin, String outerDomain, String sig, ArrayList<String> relations) {
        /* Used to flag which formulas are primitive booleans and thus add coverage */
        boolean primitiveBoolean = false;
        if (e.getHTML().contains("PrimitiveBoolean")) {
            primitiveBoolean = true;
        }

        if (e instanceof ExprBinary) {
            ExprBinary binExp = (ExprBinary) e;
            String name = getText(e);
            name = addContext(name, sig, relations);
            /* ExprBinary can be an expression or a formula, depending on the operator */
            switch (binExp.op) {
                case JOIN :
                case INTERSECT :
                case PLUS :
                case ARROW :
                case MINUS :
                    paragraphs.get(origin).addExpression(new Expression(name, numExp, outerDomain, binExp));
                    numExp++;
                    break;
                default :
                    if (primitiveBoolean)
                        paragraphs.get(origin).addFormula(new Formula(name, outerDomain, binExp));
            }

            if (!(binExp.op == ExprBinary.Op.JOIN)) {
                expAndFormulaCriteriaFromSigs(binExp.left, origin, outerDomain, sig, relations);
                expAndFormulaCriteriaFromSigs(binExp.right, origin, outerDomain, sig, relations);
            }
        } else if (e instanceof ExprQt) {

            ExprQt quantFormula = (ExprQt) e;
            try {
                quantFormula = (ExprQt) quantFormula.desugar();
            } catch (ErrorSyntax e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            String name = getText(e);
            name = addContext(name, sig, relations);
            String quant = quantFormula.op.toString();
            String var = "";
            Expr body = quantFormula.sub;
            String quantBody = getText(body);
            quantBody = addContext(quantBody, sig, relations);

            if (quant.equals("comprehension")) {
                for (int decl = 0; decl < quantFormula.decls.size(); decl++) {
                    for (int v = 0; v < quantFormula.decls.get(decl).names.size(); v++) {
                        var = quantFormula.decls.get(decl).names.get(v).label;

                        ExprUnary exprUnary = (ExprUnary) quantFormula.decls.get(decl).expr;
                        Expr domainExpr = exprUnary.sub;
                        String domain = getText(domainExpr);
                        domain = addContext(domain, sig, relations);

                        if (quantBody.equals("true")) {
                        } else {
                            if (decl == 0 && v == 0) {
                                paragraphs.get(origin).addExpression(new Expression(name, numExp, outerDomain, e));
                                numExp++;
                            }
                        }
                        paragraphs.get(origin).addExpression(new Expression(domain, numExp, outerDomain, domainExpr));
                        numExp++;

                        String temp = quantFormula.decls.get(decl).expr.toString();
                        if (temp.startsWith("one")) {
                            temp = temp.substring(4);
                        }
                        outerDomain += "some " + quantFormula.decls.get(decl).names.get(v).label + " : " + temp + " | ";
                        outerDomain = outerDomain.replaceAll(" this . ", sig + ".");
                    }
                }
            } else {
                for (int decl = 0; decl < quantFormula.decls.size(); decl++) {
                    for (int v = 0; v < quantFormula.decls.get(decl).names.size(); v++) {
                        var = quantFormula.decls.get(decl).names.get(v).label;

                        ExprUnary exprUnary = (ExprUnary) quantFormula.decls.get(decl).expr;
                        Expr domainExpr = exprUnary.sub;
                        String domain = getText(domainExpr);
                        domain = addContext(domain, sig, relations);
                        String adtn_decls = "";
                        for (int c = v + 1; c < quantFormula.decls.get(decl).names.size(); c++) {
                            adtn_decls += "9" + quant + " " + quantFormula.decls.get(decl).names.get(c).label + " : " + domain + " | ";
                        }
                        for (int d = decl + 1; d < quantFormula.decls.size(); d++) {
                            ExprUnary exprUnary2 = (ExprUnary) quantFormula.decls.get(d).expr;
                            Expr domainExpr2 = exprUnary2.sub;
                            String domain2 = getText(domainExpr2);
                            domain2 = addContext(domain2, sig, relations);
                            for (int c = 0; c < quantFormula.decls.get(d).names.size(); c++) {
                                adtn_decls += "" + quant + " " + quantFormula.decls.get(d).names.get(c).label + " : " + domain2 + " | ";
                            }
                        }

                        if (quantBody.equals("true")) {
                        } else {
                            if (decl != 0 && v != 0) {
                                name = quant + " " + var + " : " + domain + " | " + adtn_decls + body;
                            }
                            paragraphs.get(origin).addFormula(new FormulaQt(name, quant, var, domain, adtn_decls + quantBody, quantFormula, outerDomain));
                        }
                        paragraphs.get(origin).addExpression(new Expression(domain, numExp, outerDomain, domainExpr));
                        numExp++;

                        String temp = quantFormula.decls.get(decl).expr.toString();
                        if (temp.startsWith("one")) {
                            temp = temp.substring(4);
                        }
                        outerDomain += "some " + quantFormula.decls.get(decl).names.get(v).label + " : " + temp + " | ";
                        outerDomain = outerDomain.replaceAll(" this . ", sig + ".");
                    }
                }
            }



            expAndFormulaCriteriaFromSigs(quantFormula.sub, origin, outerDomain, sig, relations);

        } else if (e instanceof ExprUnary) {
            ExprUnary unExp = (ExprUnary) e;
            String name = getText(e);
            name = addContext(name, sig, relations);
            if (unExp.op.toString().equals("NOOP")) {
                expAndFormulaCriteriaFromSigs(unExp.sub, origin, outerDomain, sig, relations);
            } else {
                if (unExp.sub == null) {
                    paragraphs.get(origin).addExpression(new Expression(name, numExp, outerDomain, unExp));
                    numExp++;
                } else {
                    /* Check is UnaryExpr is a expression or formula */
                    switch (unExp.op) {
                        case TRANSPOSE :
                        case RCLOSURE :
                        case CLOSURE :
                        case CARDINALITY :
                            if (!unExp.op.toString().equals("NOOP")) {
                                paragraphs.get(origin).addExpression(new Expression(name, numExp, outerDomain, unExp));
                                numExp++;
                            }
                            break;
                        default :
                            if (primitiveBoolean)
                                paragraphs.get(origin).addFormula(new Formula(name, outerDomain, unExp));
                    }
                }
                expAndFormulaCriteriaFromSigs(unExp.sub, origin, outerDomain, sig, relations);
            }

        } else if (e instanceof ExprList) {
            ExprList listExp = (ExprList) e;
            String name = getText(e);
            name = addContext(name, sig, relations);
            if (primitiveBoolean)
                paragraphs.get(origin).addFormula(new Formula(name, outerDomain, listExp, ""));

            for (Expr e1 : listExp.args) {
                expAndFormulaCriteriaFromSigs(e1, origin, outerDomain, sig, relations);
            }
        } else if (e instanceof ExprCall) {
            ExprCall callExp = (ExprCall) e;
            String name = getText(e);
            name = addContext(name, sig, relations);
            if (callExp.fun.isPred) {
                paragraphs.get(origin).addFormula(new Formula(name, outerDomain, callExp, ""));
            } else if (!callExp.fun.isVal) {
                paragraphs.get(origin).addExpression(new Expression(name, numExp, outerDomain, callExp));
                numExp++;
            }
        } else if (e instanceof ExprITE) {
            /*
             * formula syntax == if 'condition' (subformula) else (subformula) - entire
             * formula added - send the condition and both subformulas to be broken down for
             * coverage
             */
            ExprITE exITE = (ExprITE) e;
            String name = getText(e);
            name = addContext(name, sig, relations);
            if (primitiveBoolean)
                paragraphs.get(origin).addFormula(new Formula(name, outerDomain, exITE, ""));

            expAndFormulaCriteriaFromSigs(exITE.cond, origin, outerDomain, sig, relations);
            expAndFormulaCriteriaFromSigs(exITE.left, origin, outerDomain, sig, relations);
            expAndFormulaCriteriaFromSigs(exITE.right, origin, outerDomain, sig, relations);
        } else if (e instanceof ExprLet) {
            /*
             * formula syntax == "let 'var' = 'domain' | 'subformula'" - the entire let
             * formula added with T/F criteria - send the 'subformula' to be broken down for
             * coverage
             */
            ExprLet exLet = (ExprLet) e;
            String name = getText(e);
            name = addContext(name, sig, relations);
            if (primitiveBoolean)
                paragraphs.get(origin).addFormula(new Formula("let " + name, outerDomain, exLet, ""));

            outerDomain += "let " + exLet.var.label + " = " + exLet.expr.toString() + " | ";
            outerDomain = outerDomain.replaceAll(" this . ", sig + ".");
            expAndFormulaCriteriaFromSigs(exLet.sub, origin, outerDomain, sig, relations);
        } else if (e instanceof ExprConstant) {
            ExprConstant constant = (ExprConstant) e;
            if (constant.op.toString().equals("iden")) {
                paragraphs.get(origin).addExpression(new Expression(constant.op.toString(), numExp, outerDomain, constant));
                numExp++;
            }
        } else if (e instanceof PrimSig) {
            PrimSig primSig = (PrimSig) e;
            if (primSig.label.equals("univ")) {
                paragraphs.get(origin).addExpression(new Expression(primSig.label, numExp, outerDomain, primSig));
                numExp++;
            }
        }
    }

    public String getText(Expr exp) {
        int start = exp.span().x;
        int end = exp.span().x2;
        int lineStart = exp.span().y;
        int lineEnd = exp.span().y2;
        String name = "";
        if (lineStart == lineEnd) {

            String temp = text[lineStart - 1] + " ";
            name = temp.substring(start - 1, end);
            if (temp.charAt(end) == ']') {
                name += "]";
            }
            if (name.trim().endsWith("{")) {
                name = name.substring(0, name.length() - 1);
            }
        } else {
            name = text[lineStart - 1].substring(start - 1) + "\n";
            for (int i = lineStart; i < lineEnd - 1; i++) {
                name += text[i] + "\n";
            }
            name += text[lineEnd - 1].substring(0, end);
        }
        name = name.replaceAll("\t", "");
        if (name.startsWith("pred")) {
            name = name.substring(name.indexOf("{"));
        }

        return name;
    }

    public HashMap<String,AlloyParagraph> getParagraphs() {
        return paragraphs;
    }

    public ArrayList<SigParagraph> getSigParagraphs() {
        return signatures;
    }

    public String printCoverageString() {
        String coverage = "";

        for (SigParagraph paragraph : signatures) {
            coverage += "*****Coverage for Signature " + paragraph.name + "*****\n";
            coverage += paragraph.printCoverage();
        }

        for (String origin : paragraphs.keySet()) {
            coverage += "*****Coverage for paragraph " + origin + "*****\n";
            coverage += paragraphs.get(origin).printCoverage();
        }

        return coverage;
    }


    public void printCoverage() {
        System.out.println(printCoverageString());
    }

    public void markInfeasible(TargetingConstraint target) {

        for (SigParagraph sigP : signatures) {
            if (target.paragraph.equals(sigP.name)) {
                if (sigP.sig.label.equals(target.object)) {
                    sigP.sig.markInfeasible(target.criteria);
                    return;
                }
                for (Relation rel : sigP.relations) {
                    if (rel.label.equals(target.object)) {
                        rel.markInfeasible(target.criteria);
                        return;
                    }
                }
            }
        }

        for (String paragraph : paragraphs.keySet()) {
            if (paragraph.equals(target.paragraph)) {
                for (Construct expOrForm : paragraphs.get(paragraph).getExpressionsAndFormulas()) {
                    if (expOrForm.name.equals(target.object)) {
                        expOrForm.markInfeasible(target.criteria);
                    }
                }
            }
        }
    }

    /* Method for calcuating coverage from a test case */
    public int markCoverage(A4Solution instance, TestCase testCase) throws Err {
        int cov = 0;
        /* First, explore signatures and relations */
        for (SigParagraph sigP : signatures) {
            cov += sigP.extractCoverage(instance, world);
        }

        /* Then, expressions and formulas in facts */
        for (String fact : facts) {
            for (Construct expOrForm : paragraphs.get(fact).getExpressionsAndFormulas()) {
                cov += expOrForm.extractCoverage(instance, world, new HashMap<String,String>());
            }
        }

        /* Then, expressions and formulas in all commands invoked by the test case */
        for (FunctionInvocation commandToExplore : testCase.explicitCommands) {
            String command = commandToExplore.getCommand();
            HashMap<String,String> parameters = commandToExplore.getParameters();
            for (Construct expOrForm : paragraphs.get(command).getExpressionsAndFormulas()) {
                cov += expOrForm.extractCoverage(instance, world, parameters);
            }
        }
        return cov;
    }


    public String addContext(String expression, String sig, ArrayList<String> relations) {
        String spaced = "";
        for (int i = 0; i < expression.length(); i++) {
            switch (expression.charAt(i)) {
                case '.' :
                case '~' :
                case '*' :
                case '(' :
                case ')' :
                case '[' :
                case ']' :
                case '!' :
                case '#' :
                case '&' :
                    spaced += expression.charAt(i) + " ";
                    break;
                case '+' :
                    if (expression.charAt(i + 1) == '+') {
                        spaced += expression.charAt(i) + expression.charAt(i + 1) + " ";
                        i++;
                    } else {
                        spaced += expression.charAt(i) + " ";
                    }
                    break;
                case '-' :
                    if (expression.charAt(i + 1) == '>') {
                        spaced += expression.charAt(i) + expression.charAt(i + 1) + " ";
                        i++;
                    } else {
                        spaced += expression.charAt(i) + " ";
                    }
                    break;
                case '=' :
                case ':' :
                    if (expression.charAt(i + 1) == '>') {
                        spaced += expression.charAt(i) + expression.charAt(i + 1) + " ";
                        i++;
                    } else {
                        spaced += expression.charAt(i);
                    }
                    break;
                case '<' :
                    if (expression.charAt(i + 1) == ':') {
                        spaced += expression.charAt(i) + expression.charAt(i + 1) + " ";
                        i++;
                    } else {
                        spaced += expression.charAt(i);
                    }
                    break;
                default :
                    spaced += expression.charAt(i);
                    break;
            }
        }

        expression = spaced;
        for (String relation : relations) {
            expression = expression.replaceAll(relation, "(" + sig + "." + relation + ")");
        }
        return expression;
    }


    public TargetingConstraint getNextTargetingConstraint() {
        /* Then, expressions and formulas in facts */
        for (String paragraph : paragraphs.keySet()) {
            for (Construct expOrForm : paragraphs.get(paragraph).getExpressionsAndFormulas()) {
                if (!expOrForm.isCovered()) {
                    return expOrForm.getTargetingConstraint();
                }
            }
        }

        for (SigParagraph sigP : signatures) {
            if (!sigP.sig.isCovered()) {
                return sigP.sig.getTargetingConstraint();
            }
        }

        for (SigParagraph sigP : signatures) {
            for (Relation rel : sigP.relations) {
                if (!rel.isCovered()) {
                    return rel.getTargetingConstraint();
                }
            }
        }


        return null;
    }
}
