package alloyfl.coverage.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.mit.csail.sdg.alloy4.Pair;
import edu.mit.csail.sdg.alloy4.Pos;
import edu.mit.csail.sdg.translator.A4Solution;
import parser.ast.nodes.Node;
import parser.etc.NodeMap;

public class UnsatAnalyzer {

    public static Collection<Node> findHighlightedNodes(NodeMap nodeMap, A4Solution sol) {
        if (sol.satisfiable()) {
            throw new RuntimeException("Cannot find highlighted nodes from a satisfiable solution.");
        }
        List<Node> highlightedNodes = new ArrayList<>();
        Map<Object,Node> alloy2aparser = nodeMap.getS2D();
        Pair<Set<Pos>,Set<Pos>> nodePair = sol.highLevelCore();
        for (Object alloyNode : nodePair.a) {
            // The node the unsat core reports is inside the analyzed model/file.
            // Could be null if the model invokes some API outside the file.
            if (alloyNode != null && alloy2aparser.containsKey(alloyNode)) {
                highlightedNodes.add(alloy2aparser.get(alloyNode));
            }
        }
        for (Object alloyNode : nodePair.b) {
            // The node the unsat core reports is inside the analyzed model/file.
            // Could be null if the model invokes some API outside the file.
            if (alloyNode != null && alloy2aparser.containsKey(alloyNode)) {
                highlightedNodes.add(alloy2aparser.get(alloyNode));
            }
        }
        //    printUnsatNodePos(sol);
        return highlightedNodes;
    }

    public static void printUnsatNodePos(A4Solution sol) {
        Pair<Set<Pos>,Set<Pos>> posPair = sol.highLevelCore();
        for (Pos pos : posPair.a) {
            System.out.println(pos);
        }
        for (Pos pos : posPair.b) {
            System.out.println(pos);
        }
    }
}
