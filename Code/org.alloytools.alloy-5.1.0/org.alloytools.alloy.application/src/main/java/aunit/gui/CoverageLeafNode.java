package aunit.gui;

/*Helps display the coverage tree for the "Coverage Results" tab.*/

public class CoverageLeafNode {
	String coverage_color;
	String coverage_content;
	
	public CoverageLeafNode(String coverage_color, String coverage_content){
		this.coverage_color = coverage_color;
		this.coverage_content = coverage_content;		
	}
}
