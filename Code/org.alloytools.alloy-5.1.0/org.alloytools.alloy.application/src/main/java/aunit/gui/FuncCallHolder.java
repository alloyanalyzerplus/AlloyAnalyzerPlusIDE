package aunit.gui;

import java.util.ArrayList;

import javax.swing.tree.DefaultMutableTreeNode;

public class FuncCallHolder {
	ArrayList<String> explicit;
	ArrayList<String> implicit;
	int test;
	
	public FuncCallHolder(int test) {
		this.test = test;
		explicit = new ArrayList<String>();
		implicit = new ArrayList<String>();
	}
	
	public void addExplicitCall(String call) { explicit.add(call); }
	public void addImplicitCall(String call) { implicit.add(call); }
	
	public String getCallString(){
		if(explicit.size() == 0) { return "<html><b>Function Calls:</b> none</html>"; }
		String calls = "";
		calls += "<html><b>Function Calls</b></html>\n";
		for(String call : explicit){
			calls += call + "\n";
		}
		return calls;
	}
	
}
