package aunit.coverage;

import java.util.ArrayList;
import java.util.HashMap;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.ast.Module;
import edu.mit.csail.sdg.translator.A4Solution;

public class SigParagraph {

    String               name;
    Signature            sig;
    ArrayList<Relation>  relations;
    ArrayList<Construct> implicitFacts;

    public SigParagraph(String name, Signature sig) {
        this.name = name;
        this.sig = sig;
        relations = new ArrayList<Relation>();
        implicitFacts = new ArrayList<Construct>();
    }

    public void addRelation(Relation relation) {
        relation.siglabel = name;
        relations.add(relation);
    }

    public void addImplicitFact(Construct fact) {
        implicitFacts.add(fact);
    }

    public int extractCoverage(A4Solution instance, Module world) throws Err {
        int numCriteria = 0;
        numCriteria += sig.extractCoverage(instance, world);
        for (Relation rel : relations) {
            numCriteria += rel.extractCoverage(instance, world);
        }
        for (Construct f : implicitFacts) {
            numCriteria += f.extractCoverage(instance, world, new HashMap<String,String>());
        }
        return numCriteria;
    }

    public String printCoverage() {
        String coverage = "";
        coverage += sig.printCoverage();
        coverage += "**Relations in " + name + "**\n";
        for (Relation rel : relations) {
            coverage += rel.printCoverage();
        }
        coverage += "**Facts in " + name + "**\n";
        for (Construct fact : implicitFacts) {
            coverage += fact.printCoverage();
        }
        return coverage;
    }

    public Signature getSig() {
        return sig;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Relation> getRelations() {
        return relations;
    }

    public ArrayList<Construct> getImplicitFacts() {
        return implicitFacts;
    }
}
