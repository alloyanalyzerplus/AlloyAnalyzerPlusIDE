package aunit.coverage;

import java.util.ArrayList;
import java.util.HashMap;

import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4.Pos;
import edu.mit.csail.sdg.ast.Module;
import edu.mit.csail.sdg.translator.A4Solution;

/*Starting base for: Signature, Relation, Expression and Formula*/
public abstract class Construct {

    String                   name;
    String                   type;
    public String            origin;
    public String            label;
    public String            prettyPrintName;
    HashMap<String,Coverage> coverage;
    int                      num_covered;
    String[]                 prettyPrintOrder;
    ArrayList<Pos>           highlight_pos;
    public String            line_start;
    String                   outerDomains;

    public static enum Coverage {
                                 COVERED,
                                 NOTCOVERED,
                                 INFEASIBLE;
    }

    public Construct(String name) {
        this.name = name;
        label = name;
        prettyPrintName = name;
        num_covered = 0;
        coverage = new HashMap<String,Coverage>();
        highlight_pos = new ArrayList<Pos>();
        line_start = "";
    }

    public boolean isCovered() {
        return num_covered == coverage.size();
    }

    public boolean noCoverage() {
        return (num_covered == 0);
    }

    public int getNumCovered() {
        return num_covered;
    }

    public int getNumCriteria() {
        return coverage.size();
    }

    public HashMap<String,Coverage> getCurrentCoverage() {
        return coverage;
    }

    public String[] getPrettyPrintOrder() {
        return prettyPrintOrder;
    }

    public ArrayList<Pos> getHighlightPos() {
        return highlight_pos;
    }

    public void resetCoverage() {
        num_covered = 0;
        for (String criteria : prettyPrintOrder) {
            coverage.put(criteria, Coverage.NOTCOVERED);
        }
    }

    public String printCoverage() {
        String print = "";
        print += prettyPrintName + "\n";
        for (String criteria : prettyPrintOrder) {
            print += criteria + ": ";
            switch (coverage.get(criteria)) {
                case COVERED :
                    print += "covered";
                    break;
                case NOTCOVERED :
                    print += "NOT covered";
                    break;
                case INFEASIBLE :
                    print += "INFEASIBLE";
                    break;
            }
            print += "\n";
        }
        return print;
    }

    public void markCovered(String criteria) {
        if (coverage.get(criteria) != Coverage.COVERED) {
            coverage.put(criteria, Coverage.COVERED);
            num_covered++;
        }
    }

    public void markInfeasible(String criteria) {
        coverage.put(criteria, Coverage.INFEASIBLE);
        num_covered++;
    }

    public abstract int extractCoverage(A4Solution instance, Module world) throws Err;

    public abstract int extractCoverage(A4Solution instance, Module world, HashMap<String,String> parameters) throws Err;

    public abstract TargetingConstraint getTargetingConstraint();
}
