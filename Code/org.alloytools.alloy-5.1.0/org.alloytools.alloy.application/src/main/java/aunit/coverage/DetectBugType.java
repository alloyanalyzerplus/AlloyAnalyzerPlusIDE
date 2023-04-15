package aunit.coverage;


import java.util.ArrayList;

import edu.mit.csail.sdg.alloy4.A4Reporter;
import edu.mit.csail.sdg.alloy4.ConstList;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4.ErrorWarning;
import edu.mit.csail.sdg.ast.Command;
import edu.mit.csail.sdg.ast.Module;
import edu.mit.csail.sdg.translator.A4Options;
import edu.mit.csail.sdg.translator.A4Solution;
import edu.mit.csail.sdg.translator.TranslateAlloyToKodkod;

public class DetectBugType {

    boolean             overconstrained;
    boolean             underconstrained;
    Module              world;
    ArrayList<TestCase> testSuite;
    ArrayList<Command>  underRevealingCommands;
    ArrayList<Command>  overRevealingCommands;

    public DetectBugType(Module world, ArrayList<TestCase> testSuite) {
        this.world = world;
        this.testSuite = new ArrayList<TestCase>();
        this.testSuite.addAll(testSuite);
        underRevealingCommands = new ArrayList<Command>();
        overRevealingCommands = new ArrayList<Command>();
        overconstrained = false;
        underconstrained = false;
    }

    public void detectBugTypes() throws Err {
        A4Reporter rep = new A4Reporter() {

            @Override
            public void warning(ErrorWarning msg) {
                System.out.print("Relevance Warning:\n" + (msg.toString().trim()) + "\n\n");
                System.out.flush();
            }
        };

        A4Options options = new A4Options();
        options.solver = A4Options.SatSolver.SAT4J;

        ConstList<Command> commands = world.getAllCommands();
        for (int i = 0; i < commands.size(); i++) {
            A4Solution ai = TranslateAlloyToKodkod.execute_commandFromBook(rep, world.getAllReachableSigs(), commands.get(i), options);
            if (ai.satisfiable()) { //satisfiable
                if (commands.get(i).expects == 0) {
                    underconstrained = true;
                    underRevealingCommands.add(commands.get(i));
                } else if (commands.get(i).check) {
                    underconstrained = true;
                    underRevealingCommands.add(commands.get(i));
                }
            } else { //unsatisfiable 
                if (commands.get(i).expects == 1) {
                    overconstrained = true;
                    overRevealingCommands.add(commands.get(i));
                } else if (!commands.get(i).check) {
                    overconstrained = true;
                    overRevealingCommands.add(commands.get(i));
                }
            }
        }
    }

    public String getBugType() {
        String fault = "";
        if (overconstrained) {
            fault = "The model \"" + world.getModelName() + "\" is over-constrained";
        }
        if (underconstrained) {
            if (fault.length() == 0) {
                fault = "The model \"" + world.getModelName() + "\" is under-constrained";
            } else {
                fault += " and under-constrained";
            }
        }
        return fault + ".";
    }

}
