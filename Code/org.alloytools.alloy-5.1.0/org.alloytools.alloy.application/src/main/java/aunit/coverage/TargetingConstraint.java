package aunit.coverage;

public class TargetingConstraint {
	public String constraint;
	public String paragraph;
	public String criteria;
	public String object;
	
	public TargetingConstraint(String constraint, String paragraph, String object, String criteria){
		this.constraint = constraint;
		this.paragraph = paragraph;
		this.criteria = criteria;
		this.object = object;
	}
}
