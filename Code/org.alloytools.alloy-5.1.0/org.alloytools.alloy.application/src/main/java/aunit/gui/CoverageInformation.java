package aunit.gui;

import java.util.ArrayList;

import javax.swing.JTree;

public class CoverageInformation {

    public String           paragraph;
    public ArrayList<JTree> coverageTrees;
    //ArrayList<String> identifiers;
    public int              totalCriteria;
    public int              covered;

    public CoverageInformation(String paragraph) {
        this.paragraph = paragraph;
        //coverageTrees = new HashMap<String, JTree>();
        coverageTrees = new ArrayList<JTree>();
    }

    public void add(String id, JTree covTree) {
        //identifiers.add(id);
        coverageTrees.add(covTree);
    }
}
