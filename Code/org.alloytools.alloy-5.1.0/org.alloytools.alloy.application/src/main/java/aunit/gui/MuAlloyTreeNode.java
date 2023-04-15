package aunit.gui;

public class MuAlloyTreeNode {

    public String name;
    public String command;
    public String mutant;
    public String testcase;
    public String valuation;

    public MuAlloyTreeNode(String name, String mutant, String testcase, String valuation, String command) {
        this.name = name;
        this.mutant = mutant;
        this.testcase = testcase;
        this.command = command;
        this.valuation = valuation;
    }
}
