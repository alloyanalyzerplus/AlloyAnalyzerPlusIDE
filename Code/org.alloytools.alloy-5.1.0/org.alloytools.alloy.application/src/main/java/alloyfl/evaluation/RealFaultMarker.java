package alloyfl.evaluation;

import edu.mit.csail.sdg.parser.CompModule;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import parser.ast.nodes.Assertion;
import parser.ast.nodes.BinaryExpr;
import parser.ast.nodes.BinaryFormula;
import parser.ast.nodes.Body;
import parser.ast.nodes.CallExpr;
import parser.ast.nodes.CallFormula;
import parser.ast.nodes.Check;
import parser.ast.nodes.ConstExpr;
import parser.ast.nodes.ExprOrFormula;
import parser.ast.nodes.Fact;
import parser.ast.nodes.FieldDecl;
import parser.ast.nodes.FieldExpr;
import parser.ast.nodes.Function;
import parser.ast.nodes.ITEExpr;
import parser.ast.nodes.ITEFormula;
import parser.ast.nodes.LetExpr;
import parser.ast.nodes.ListExpr;
import parser.ast.nodes.ListFormula;
import parser.ast.nodes.ModelUnit;
import parser.ast.nodes.ModuleDecl;
import parser.ast.nodes.Node;
import parser.ast.nodes.OpenDecl;
import parser.ast.nodes.ParamDecl;
import parser.ast.nodes.Predicate;
import parser.ast.nodes.QtExpr;
import parser.ast.nodes.QtFormula;
import parser.ast.nodes.Run;
import parser.ast.nodes.SigDecl;
import parser.ast.nodes.SigExpr;
import parser.ast.nodes.UnaryExpr;
import parser.ast.nodes.UnaryFormula;
import parser.ast.nodes.VarDecl;
import parser.ast.nodes.VarExpr;
import parser.ast.visitor.PrettyStringVisitor;
import parser.ast.visitor.VoidVisitorAdapter;
import parser.etc.Pair;
import parser.util.AlloyUtil;
import parser.util.Codec;

public class RealFaultMarker extends VoidVisitorAdapter<Object> {

  private static final PrettyStringVisitor PSV = new PrettyStringVisitor();
  private Map<String, Integer> exactFaultsIdentifiers;
  private List<String> underFaultsIdentifiers;
  private Map<Node, Integer> faultyNodeToFaultId;
  private Set<String> seen;

  public RealFaultMarker(Map<String, Integer> exactFaultsIdentifiers) {
    this.exactFaultsIdentifiers = exactFaultsIdentifiers;
    this.underFaultsIdentifiers = new ArrayList<>();
    this.faultyNodeToFaultId = new HashMap<>();
    this.seen = new HashSet<>();
  }

  public RealFaultMarker(Map<String, Integer> exactFaultsIdentifiers, List<String> underFaultsIdentifiers) {
    this.exactFaultsIdentifiers = exactFaultsIdentifiers;
    this.underFaultsIdentifiers = underFaultsIdentifiers;
    this.faultyNodeToFaultId = new HashMap<>();
    this.seen = new HashSet<>();
  }

  public Map<Node, Integer> getFaultyNodeToFaultId() {
    return faultyNodeToFaultId;
  }

  public void visit(ModelUnit n, Object arg) {
    checkNode(n);
    super.visit(n, arg);
  }

  public void visit(ModuleDecl n, Object arg) {
    checkNode(n);
    super.visit(n, arg);
  }

  public void visit(OpenDecl n, Object arg) {
    checkNode(n);
    super.visit(n, arg);
  }

  public void visit(SigDecl n, Object arg) {
    checkNode(n);
    super.visit(n, arg);
  }

  public void visit(FieldDecl n, Object arg) {
    checkNode(n);
    super.visit(n, arg);
  }

  public void visit(ParamDecl n, Object arg) {
    checkNode(n);
    super.visit(n, arg);
  }

  public void visit(VarDecl n, Object arg) {
    checkNode(n);
    super.visit(n, arg);
  }

  public void visit(ExprOrFormula n, Object arg) {
    checkNode(n);
    super.visit(n, arg);
  }

  public void visit(SigExpr n, Object arg) {
    checkNode(n);
    super.visit(n, arg);
  }

  public void visit(FieldExpr n, Object arg) {
    checkNode(n);
    super.visit(n, arg);
  }

  public void visit(VarExpr n, Object arg) {
    checkNode(n);
    super.visit(n, arg);
  }

  public void visit(UnaryExpr n, Object arg) {
    // Only check meaningful AST Node
    if (n.getOp() != UnaryExpr.UnaryOp.NOOP) {
      checkNode(n);
    }
    super.visit(n, arg);
  }

  public void visit(UnaryFormula n, Object arg) {
    checkNode(n);
    super.visit(n, arg);
  }

  public void visit(BinaryExpr n, Object arg) {
    checkNode(n);
    super.visit(n, arg);
  }

  public void visit(BinaryFormula n, Object arg) {
    checkNode(n);
    super.visit(n, arg);
  }

  public void visit(ListExpr n, Object arg) {
    checkNode(n);
    super.visit(n, arg);
  }

  public void visit(ListFormula n, Object arg) {
    checkNode(n);
    super.visit(n, arg);
  }

  public void visit(CallExpr n, Object arg) {
    checkNode(n);
    super.visit(n, arg);
  }

  public void visit(CallFormula n, Object arg) {
    checkNode(n);
    super.visit(n, arg);
  }

  public void visit(QtExpr n, Object arg) {
    checkNode(n);
    super.visit(n, arg);
  }

  public void visit(QtFormula n, Object arg) {
    checkNode(n);
    super.visit(n, arg);
  }

  public void visit(ITEExpr n, Object arg) {
    checkNode(n);
    super.visit(n, arg);
  }

  public void visit(ITEFormula n, Object arg) {
    checkNode(n);
    super.visit(n, arg);
  }

  public void visit(LetExpr n, Object arg) {
    checkNode(n);
    super.visit(n, arg);
  }

  public void visit(ConstExpr n, Object arg) {
    checkNode(n);
    super.visit(n, arg);
  }

  public void visit(Body n, Object arg) {
    checkNode(n);
    super.visit(n, arg);
  }

  public void visit(Predicate n, Object arg) {
    checkNode(n);
    super.visit(n, arg);
  }

  public void visit(Function n, Object arg) {
    checkNode(n);
    super.visit(n, arg);
  }

  public void visit(Fact n, Object arg) {
    checkNode(n);
    super.visit(n, arg);
  }

  public void visit(Assertion n, Object arg) {
    checkNode(n);
    super.visit(n, arg);
  }

  public void visit(Run n, Object arg) {
    checkNode(n);
    super.visit(n, arg);
  }

  public void visit(Check n, Object arg) {
    checkNode(n);
    super.visit(n, arg);
  }

  private void checkNode(Node node) {
    String nodeIdentifier = AlloyUtil.computeNodePathAsString(node, PSV);
//    String nodeIdentifier = node.accept(PSV, null);
//    System.out.println(nodeIdentifier);
//    System.out.println("===============");
    if (exactFaultsIdentifiers.containsKey(nodeIdentifier)) {
      if (seen.add(nodeIdentifier)) {
        faultyNodeToFaultId.put(node, exactFaultsIdentifiers.get(nodeIdentifier));
      }
    }
    if (underFaultsIdentifiers.stream().anyMatch(nodeIdentifier::contains)) {
      if (seen.add(nodeIdentifier)) {
        throw new UnsupportedOperationException("Does not have fault ID");
      }
    }
  }

  private static void markerFaultyNodes(RealFaultInfo realFaultInfo) {
    CompModule module = AlloyUtil.compileAlloyModule(realFaultInfo.getModelPath());
    ModelUnit mu = new ModelUnit(null, module);
//    System.out.println(mu.accept(PSV, null));
//    System.out.println("======================");
    RealFaultMarker realFaultMarker = new RealFaultMarker(realFaultInfo.getExactFaultIdentifiers());
    mu.accept(realFaultMarker, null);
    Map<Node, Integer> faultyNodes = realFaultMarker.getFaultyNodeToFaultId();
    if (faultyNodes.isEmpty()) {
      throw new RuntimeException("No faulty node find.");
    }
//    faultyNodeToFaultId.remove(2);
//    faultyNodeToFaultId.remove(3);
//    faultyNodeToFaultId.remove(4);
//    faultyNodeToFaultId.forEach(node -> System.out.println(
//        AlloyUtil.computeNodePathAsString(node, PSV) + "\n=========="));
    Codec.serialize(Pair.of(mu, faultyNodes), realFaultInfo.getFltPath());
  }

  public static void main(String... args) {
    markerFaultyNodes(RealFaultInfo.ADDR_FAULTY);
    markerFaultyNodes(RealFaultInfo.ARRAY_1);
    markerFaultyNodes(RealFaultInfo.ARRAY_2);
    markerFaultyNodes(RealFaultInfo.ARRAY_3);
    markerFaultyNodes(RealFaultInfo.ARRAY_4);
    markerFaultyNodes(RealFaultInfo.ARRAY_5);
    markerFaultyNodes(RealFaultInfo.ARRAY_6);
    markerFaultyNodes(RealFaultInfo.ARRAY_7);
    markerFaultyNodes(RealFaultInfo.ARRAY_8);
    markerFaultyNodes(RealFaultInfo.ARRAY_9);
    markerFaultyNodes(RealFaultInfo.ARRAY_10);
    markerFaultyNodes(RealFaultInfo.ARRAY_11);
    markerFaultyNodes(RealFaultInfo.BALANCED_BST_1);
    markerFaultyNodes(RealFaultInfo.BALANCED_BST_2);
    markerFaultyNodes(RealFaultInfo.BALANCED_BST_3);
    markerFaultyNodes(RealFaultInfo.BALANCED_BST_4);
    markerFaultyNodes(RealFaultInfo.BALANCED_BST_5);
    markerFaultyNodes(RealFaultInfo.BALANCED_BST_6);
    markerFaultyNodes(RealFaultInfo.BALANCED_BST_7);
    markerFaultyNodes(RealFaultInfo.BALANCED_BST_8);
    markerFaultyNodes(RealFaultInfo.BALANCED_BST_9);
    markerFaultyNodes(RealFaultInfo.BALANCED_BST_10);
    markerFaultyNodes(RealFaultInfo.BALANCED_BST_11);
    markerFaultyNodes(RealFaultInfo.BALANCED_BST_12);
    markerFaultyNodes(RealFaultInfo.BALANCED_BST_13);
    markerFaultyNodes(RealFaultInfo.BALANCED_BST_14);
    markerFaultyNodes(RealFaultInfo.BALANCED_BST_15);
    markerFaultyNodes(RealFaultInfo.BALANCED_BST_16);
    markerFaultyNodes(RealFaultInfo.BALANCED_BST_17);
    markerFaultyNodes(RealFaultInfo.BALANCED_BST_18);
    markerFaultyNodes(RealFaultInfo.BALANCED_BST_19);
    markerFaultyNodes(RealFaultInfo.BALANCED_BST_20);
    markerFaultyNodes(RealFaultInfo.BALANCED_BST_21);
    markerFaultyNodes(RealFaultInfo.BALANCED_BST_22);
    markerFaultyNodes(RealFaultInfo.BEMPL_FAULTY);
    markerFaultyNodes(RealFaultInfo.CD_1);
    markerFaultyNodes(RealFaultInfo.CD_2);
    markerFaultyNodes(RealFaultInfo.CD_3);
    markerFaultyNodes(RealFaultInfo.CTREE_FAULTY);
    markerFaultyNodes(RealFaultInfo.DLL_1);
    markerFaultyNodes(RealFaultInfo.DLL_2);
    markerFaultyNodes(RealFaultInfo.DLL_3);
    markerFaultyNodes(RealFaultInfo.DLL_4);
    markerFaultyNodes(RealFaultInfo.DLL_5);
    markerFaultyNodes(RealFaultInfo.DLL_6);
    markerFaultyNodes(RealFaultInfo.DLL_7);
    markerFaultyNodes(RealFaultInfo.DLL_8);
    markerFaultyNodes(RealFaultInfo.DLL_9);
    markerFaultyNodes(RealFaultInfo.DLL_10);
    markerFaultyNodes(RealFaultInfo.DLL_11);
    markerFaultyNodes(RealFaultInfo.DLL_12);
    markerFaultyNodes(RealFaultInfo.DLL_13);
    markerFaultyNodes(RealFaultInfo.DLL_14);
    markerFaultyNodes(RealFaultInfo.DLL_15);
    markerFaultyNodes(RealFaultInfo.DLL_16);
    markerFaultyNodes(RealFaultInfo.DLL_17);
    markerFaultyNodes(RealFaultInfo.DLL_18);
    markerFaultyNodes(RealFaultInfo.DLL_19);
    markerFaultyNodes(RealFaultInfo.DLL_20);
    markerFaultyNodes(RealFaultInfo.FARMER_FAULTY);
    markerFaultyNodes(RealFaultInfo.FSM_1);
    markerFaultyNodes(RealFaultInfo.FSM_2);
    markerFaultyNodes(RealFaultInfo.FSM_3);
    markerFaultyNodes(RealFaultInfo.FSM_4);
    markerFaultyNodes(RealFaultInfo.FSM_5);
    markerFaultyNodes(RealFaultInfo.FSM_6);
    markerFaultyNodes(RealFaultInfo.FSM_7);
    markerFaultyNodes(RealFaultInfo.FSM_8);
    markerFaultyNodes(RealFaultInfo.FSM_9);
    markerFaultyNodes(RealFaultInfo.GRADE_FAULTY);
    markerFaultyNodes(RealFaultInfo.OTHER_FAULTY);
    markerFaultyNodes(RealFaultInfo.STUDENT_1);
    markerFaultyNodes(RealFaultInfo.STUDENT_2);
    markerFaultyNodes(RealFaultInfo.STUDENT_3);
    markerFaultyNodes(RealFaultInfo.STUDENT_4);
    markerFaultyNodes(RealFaultInfo.STUDENT_5);
    markerFaultyNodes(RealFaultInfo.STUDENT_6);
    markerFaultyNodes(RealFaultInfo.STUDENT_7);
    markerFaultyNodes(RealFaultInfo.STUDENT_8);
    markerFaultyNodes(RealFaultInfo.STUDENT_9);
    markerFaultyNodes(RealFaultInfo.STUDENT_10);
    markerFaultyNodes(RealFaultInfo.STUDENT_11);
    markerFaultyNodes(RealFaultInfo.STUDENT_12);
    markerFaultyNodes(RealFaultInfo.STUDENT_13);
    markerFaultyNodes(RealFaultInfo.STUDENT_14);
    markerFaultyNodes(RealFaultInfo.STUDENT_15);
    markerFaultyNodes(RealFaultInfo.STUDENT_16);
    markerFaultyNodes(RealFaultInfo.STUDENT_17);
    markerFaultyNodes(RealFaultInfo.STUDENT_18);
    markerFaultyNodes(RealFaultInfo.STUDENT_19);
  }
}
