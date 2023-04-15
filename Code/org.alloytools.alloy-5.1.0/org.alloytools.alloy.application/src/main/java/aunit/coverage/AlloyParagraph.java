package aunit.coverage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**Captures: Predicates, Functions and Assertions**/
public class AlloyParagraph {
	String name;
	ArrayList<Construct> expressionsAndFormulas;
	HashMap<String, String> parameters;
	
	public AlloyParagraph(String name){
		this.name = name;
		expressionsAndFormulas = new ArrayList<Construct>();
		parameters = new HashMap<String, String>();
	}
	
	public void addExpression(Expression exp){
		exp.origin = name;
		boolean duplicate = false;
		for(Construct compare_exp : expressionsAndFormulas){
			if(compare_exp instanceof Expression){	
				if(exp.name.equals(compare_exp.name)){
					if(exp.outerDomains.equals(((Expression) compare_exp).outerDomains)){
						compare_exp.line_start += ", " + exp.highlight_pos.get(0).y;
						if(exp.highlight_pos.get(0).y != exp.highlight_pos.get(0).y2)
							compare_exp.line_start += " - " + exp.highlight_pos.get(0).y2;
						((Expression) compare_exp).addPos(exp.highlight_pos.get(0));
						duplicate = true;
					}
				}
			}
		}
		if(!duplicate) { expressionsAndFormulas.add(exp); }
	}
	
	public void addFormula(Construct form){
		form.origin = name;
		boolean duplicate = false;
		for(Construct compare_form : expressionsAndFormulas){
			if(compare_form instanceof Formula && form instanceof Formula){	
				if(form.name.equals(compare_form.name)){
					if(form.outerDomains.equals(compare_form.outerDomains)){
						compare_form.line_start += ", " + ((Formula)compare_form).getAlloyFormula().pos.y;
						if(((Formula)compare_form).getAlloyFormula().span().y != ((Formula)compare_form).getAlloyFormula().span().y)
							compare_form.line_start += " - " + ((Formula)compare_form).getAlloyFormula().span().y;
						((Formula) compare_form).addPos(form.highlight_pos.get(0));
						duplicate = true;
					}
				}
			}
			else if(compare_form instanceof FormulaQt && form instanceof FormulaQt ){	
				if(form.name.equals(compare_form.name)){
					if(form.outerDomains.equals(compare_form.outerDomains)){
						compare_form.line_start += ", " + ((FormulaQt)compare_form).getAlloyFormula().pos.y;
						if(((FormulaQt)compare_form).getAlloyFormula().span().y != ((FormulaQt)compare_form).getAlloyFormula().span().y)
							compare_form.line_start += " - " + ((FormulaQt)compare_form).getAlloyFormula().span().y;
						((FormulaQt) compare_form).addPos(form.highlight_pos.get(0));
						duplicate = true;
					}
				}
			}
		}
		if(!duplicate) { expressionsAndFormulas.add(form); }
	}
	
	public String getName() { return name; }
	public ArrayList<Construct> getExpressionsAndFormulas() { return expressionsAndFormulas; }
	
	public String printCoverage(){
		String coverage = "";
		for(Construct construct : expressionsAndFormulas){
			coverage += construct.printCoverage();
		}
		return coverage;
	}

}
