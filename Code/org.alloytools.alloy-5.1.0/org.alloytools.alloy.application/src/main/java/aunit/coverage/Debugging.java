package aunit.coverage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import edu.mit.csail.sdg.alloy4.A4Reporter;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4.ErrorWarning;
import edu.mit.csail.sdg.ast.Command;
import edu.mit.csail.sdg.parser.CompModule;
import edu.mit.csail.sdg.parser.CompUtil;
import edu.mit.csail.sdg.translator.A4Options;
import edu.mit.csail.sdg.translator.A4Solution;
import edu.mit.csail.sdg.translator.TranslateAlloyToKodkod;

public class Debugging {

    public static void main(String[] args) throws Err, IOException {

        A4Reporter rep = new A4Reporter() {

            @Override
            public void warning(ErrorWarning msg) {
                System.out.print("Relevance Warning:\n" + (msg.toString().trim()) + "\n\n");
                System.out.flush();
            }
        };

        CompModule world = CompUtil.parseEverything_fromFile(rep, null, "check.als");
        A4Options options = new A4Options();
        options.solver = A4Options.SatSolver.SAT4J;

        String line = "";
        String coverageModel = "";
        int factNum = 0;
        String oldModel = "";
        try (BufferedReader br = new BufferedReader(new FileReader("check.als"))) {
            boolean record = false;
            while ((line = br.readLine()) != null) {
                oldModel += line + "\n";
                if (line.contains("(")) {
                    if (line.contains("pred")) {
                        coverageModel += line.substring(0, line.indexOf("{") + 1);
                        line = line.substring(line.indexOf("{") + 1);
                        line = line.replaceAll("\\(", "{");
                        line = line.replaceAll("\\)", "}");
                        coverageModel += line + "\n";
                    } else {
                        line = line.replaceAll("\\(", "{");
                        line = line.replaceAll("\\)", "}");
                        coverageModel += line + "\n";
                    }
                } else {
                    coverageModel += line + "\n";
                }
            }
        }

        PrintWriter writer = new PrintWriter("bareModel.als", "UTF-8");
        writer.println(coverageModel);
        writer.close();
        CompModule worldCoverage = CompUtil.parseEverything_fromFile(rep, null, "bareModel.als");

        //ccb.world = worldNoFacts;
        CoverageCriteriaBuilder ccb = new CoverageCriteriaBuilder(worldCoverage);
        ccb.findCriteria(oldModel);
        ccb.world = world;


        //  


        TestCase check = new TestCase("", "Acyclic");


        Command onlyValuation = new Command(false, 3, 3, 3, world.parseOneExpressionFromString("no Node no link no header"));
        A4Solution result = TranslateAlloyToKodkod.execute_command(rep, world.getAllReachableSigs(), onlyValuation, options);

        ccb.markCoverage(result, check);
        //ccb.printCoverage();
    }

}
