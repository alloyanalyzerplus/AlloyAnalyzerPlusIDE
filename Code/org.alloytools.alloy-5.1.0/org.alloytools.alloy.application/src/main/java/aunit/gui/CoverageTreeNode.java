package aunit.gui;

import java.util.Enumeration;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

public class CoverageTreeNode  {
	String coverage_color;
	String coverage_content;
	String line_start;
	int num_covered;
	int num_criteria;
	double coverage_percentage;
	
	public CoverageTreeNode(String coverage_color, String coverage_content, String line_start, int num_covered, int num_criteria){
		this.coverage_color = coverage_color;
		this.coverage_content = coverage_content;
		this.line_start = line_start;
		this.num_covered = num_covered;
		this.num_criteria = num_criteria;
		coverage_percentage = ((double) num_covered / num_criteria) * 100;
	}
}
