package alloyfl.mutation.visitor;

import java.util.Map;
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
import parser.ast.nodes.UnaryExpr.UnaryOp;
import parser.ast.nodes.UnaryFormula;
import parser.ast.nodes.VarDecl;
import parser.ast.nodes.VarExpr;
import parser.ast.visitor.GenericVisitor;

/**
 * Count the number of descendants for each node, inclusive.
 */
public class DescendantCounter implements GenericVisitor<Integer, Map<Node, Integer>> {

  private boolean inSigDecl;

  public DescendantCounter() {
    this.inSigDecl = false;
  }

  @Override
  public Integer visit(ModelUnit n, Map<Node, Integer> countMap) {
    int modelDeclCount = n.getModuleDecl().accept(this, countMap);
    int openDeclCount = n.getOpenDeclList().stream()
        .mapToInt(openDecl -> openDecl.accept(this, countMap)).sum();
    int sigDeclCount = n.getSigDeclList().stream()
        .mapToInt(signature -> signature.accept(this, countMap)).sum();
    int predDeclCount = n.getPredDeclList().stream()
        .mapToInt(predicate -> predicate.accept(this, countMap)).sum();
    int funDeclCount = n.getFunDeclList().stream()
        .mapToInt(function -> function.accept(this, countMap)).sum();
    int factDeclCount = n.getFactDeclList().stream()
        .mapToInt(fact -> fact.accept(this, countMap)).sum();
    int assertDeclCount = n.getAssertDeclList().stream()
        .mapToInt(assertion -> assertion.accept(this, countMap)).sum();
    int runDeclCount = n.getRunCmdList().stream()
        .mapToInt(run -> run.accept(this, countMap)).sum();
    int checkDeclCount = n.getCheckCmdList().stream()
        .mapToInt(check -> check.accept(this, countMap)).sum();
    int res = modelDeclCount + openDeclCount + sigDeclCount + predDeclCount + funDeclCount
        + factDeclCount + assertDeclCount + runDeclCount + checkDeclCount + 1;
    countMap.put(n, res);
    return res;
  }

  @Override
  public Integer visit(ModuleDecl n, Map<Node, Integer> countMap) {
    // We do not count module declaration.
    countMap.put(n, 0);
    return 0;
  }

  @Override
  public Integer visit(OpenDecl n, Map<Node, Integer> countMap) {
    countMap.put(n, 1);
    return 1;
  }

  @Override
  public Integer visit(SigDecl n, Map<Node, Integer> countMap) {
    inSigDecl = true;
    int fieldDeclCount = n.getFieldList().stream()
        .mapToInt(field -> field.accept(this, countMap)).sum();
    int sigFactDeclCount = n.hasSigFact() ? n.getSigFact().accept(this, countMap) : 0;
    int res = fieldDeclCount + sigFactDeclCount + 1;
    countMap.put(n, res);
    inSigDecl = false;
    return res;
  }

  @Override
  public Integer visit(FieldDecl n, Map<Node, Integer> countMap) {
    int exprCount = n.getExpr().accept(this, countMap);
    int res = exprCount + 1;
    countMap.put(n, res);
    return res;
  }

  @Override
  public Integer visit(ParamDecl n, Map<Node, Integer> countMap) {
    int exprCount = n.getExpr().accept(this, countMap);
    int res = exprCount + 1;
    countMap.put(n, res);
    return res;
  }

  @Override
  public Integer visit(VarDecl n, Map<Node, Integer> countMap) {
    int exprCount = n.getExpr().accept(this, countMap);
    int res = exprCount + 1;
    countMap.put(n, res);
    return res;
  }

  @Override
  public Integer visit(ExprOrFormula n, Map<Node, Integer> countMap) {
    return n.accept(this, countMap);
  }

  @Override
  public Integer visit(SigExpr n, Map<Node, Integer> countMap) {
    countMap.put(n, 1);
    return 1;
  }

  @Override
  public Integer visit(FieldExpr n, Map<Node, Integer> countMap) {
    countMap.put(n, 1);
    return 1;
  }

  @Override
  public Integer visit(VarExpr n, Map<Node, Integer> countMap) {
    countMap.put(n, 1);
    return 1;
  }

  @Override
  public Integer visit(UnaryExpr n, Map<Node, Integer> countMap) {
    int subCount = n.getSub().accept(this, countMap);
    if (n.getOp() == UnaryOp.NOOP) {
      // We do not count NOOP unary expressions.
      countMap.put(n, subCount);
      return subCount;
    }
    int res = subCount + 1;
    countMap.put(n, res);
    return res;
  }

  @Override
  public Integer visit(UnaryFormula n, Map<Node, Integer> countMap) {
    int subCount = n.getSub().accept(this, countMap);
    int res = subCount + 1;
    countMap.put(n, res);
    return res;
  }

  @Override
  public Integer visit(BinaryExpr n, Map<Node, Integer> countMap) {
    if (inSigDecl) {
      if (n.getLeft() instanceof VarExpr) {
        String value = ((VarExpr) n.getLeft()).getName();
        if (value.equals("this")) {
          // We do not count left child and this node in this case.
          // E.g.
          // sig Book {
          //	entry: set Name,
          //	listed: entry ->set Listing
          // }
          // entry -> set Listing is actually this.entry -> set Listing.
          int rightCount = n.getRight().accept(this, countMap);
          countMap.put(n, rightCount);
          return rightCount;
        }
      }
    }
    int leftCount = n.getLeft().accept(this, countMap);
    int rightCount = n.getRight().accept(this, countMap);
    int res = leftCount + rightCount + 1;
    countMap.put(n, res);
    return res;
  }

  @Override
  public Integer visit(BinaryFormula n, Map<Node, Integer> countMap) {
    int leftCount = n.getLeft().accept(this, countMap);
    int rightCount = n.getRight().accept(this, countMap);
    int res = leftCount + rightCount + 1;
    countMap.put(n, res);
    return res;
  }

  @Override
  public Integer visit(ListExpr n, Map<Node, Integer> countMap) {
    int argsCount = n.getArguments().stream()
        .mapToInt(expr -> expr.accept(this, countMap)).sum();
    int res = argsCount + 1;
    countMap.put(n, res);
    return res;
  }

  @Override
  public Integer visit(ListFormula n, Map<Node, Integer> countMap) {
    int argsCount = n.getArguments().stream()
        .mapToInt(arg -> arg.accept(this, countMap)).sum();
    int res = argsCount + 1;
    countMap.put(n, res);
    return res;
  }

  @Override
  public Integer visit(CallExpr n, Map<Node, Integer> countMap) {
    int argsCount = n.getArguments().stream()
        .mapToInt(arg -> arg.accept(this, countMap)).sum();
    int res = argsCount + 1;
    countMap.put(n, res);
    return res;
  }

  @Override
  public Integer visit(CallFormula n, Map<Node, Integer> countMap) {
    int argsCount = n.getArguments().stream()
        .mapToInt(arg -> arg.accept(this, countMap)).sum();
    int res = argsCount + 1;
    countMap.put(n, res);
    return res;
  }

  @Override
  public Integer visit(QtExpr n, Map<Node, Integer> countMap) {
    int varDeclCount = n.getVarDecls().stream()
        .mapToInt(varDecl -> varDecl.accept(this, countMap)).sum();
    int bodyCount = n.getBody().accept(this, countMap);
    int res = varDeclCount + bodyCount + 1;
    countMap.put(n, res);
    return res;
  }

  @Override
  public Integer visit(QtFormula n, Map<Node, Integer> countMap) {
    int varDeclCount = n.getVarDecls().stream()
        .mapToInt(varDecl -> varDecl.accept(this, countMap)).sum();
    int bodyCount = n.getBody().accept(this, countMap);
    int res = varDeclCount + bodyCount + 1;
    countMap.put(n, res);
    return res;
  }

  @Override
  public Integer visit(ITEExpr n, Map<Node, Integer> countMap) {
    int conditionCount = n.getCondition().accept(this, countMap);
    int thenCount = n.getThenClause().accept(this, countMap);
    int elseCount = n.getElseClause().accept(this, countMap);
    int res = conditionCount + thenCount + elseCount + 1;
    countMap.put(n, res);
    return res;
  }

  @Override
  public Integer visit(ITEFormula n, Map<Node, Integer> countMap) {
    int conditionCount = n.getCondition().accept(this, countMap);
    int thenCount = n.getThenClause().accept(this, countMap);
    int elseCount = n.getElseClause().accept(this, countMap);
    int res = conditionCount + thenCount + elseCount + 1;
    countMap.put(n, res);
    return res;
  }

  @Override
  public Integer visit(LetExpr n, Map<Node, Integer> countMap) {
    int varDeclCount = n.getVar().accept(this, countMap);
    int boundCount = n.getBound().accept(this, countMap);
    int bodyCount = n.getBody().accept(this, countMap);
    int res = varDeclCount + boundCount + bodyCount + 1;
    countMap.put(n, res);
    return res;
  }

  @Override
  public Integer visit(ConstExpr n, Map<Node, Integer> countMap) {
    countMap.put(n, 1);
    return 1;
  }

  @Override
  public Integer visit(Body n, Map<Node, Integer> countMap) {
    int exprCount = n.getBodyExpr().accept(this, countMap);
    int res = exprCount + 1;
    countMap.put(n, res);
    return res;
  }

  @Override
  public Integer visit(Predicate n, Map<Node, Integer> countMap) {
    int paramDeclCount = n.getParamList().stream()
        .mapToInt(parameter -> parameter.accept(this, countMap)).sum();
    int bodyCount = n.getBody().accept(this, countMap);
    int res = paramDeclCount + bodyCount + 1;
    countMap.put(n, res);
    return res;
  }

  @Override
  public Integer visit(Function n, Map<Node, Integer> countMap) {
    int paramDeclCount = n.getParamList().stream()
        .mapToInt(parameter -> parameter.accept(this, countMap)).sum();
    int returnCount = n.getReturnType().accept(this, countMap);
    int bodyCount = n.getBody().accept(this, countMap);
    int res = paramDeclCount + returnCount + bodyCount + 1;
    countMap.put(n, res);
    return res;
  }

  @Override
  public Integer visit(Fact n, Map<Node, Integer> countMap) {
    int bodyCount = n.getBody().accept(this, countMap);
    int res = bodyCount + 1;
    countMap.put(n, res);
    return res;
  }

  @Override
  public Integer visit(Assertion n, Map<Node, Integer> countMap) {
    int bodyCount = n.getBody().accept(this, countMap);
    int res = bodyCount + 1;
    countMap.put(n, res);
    return res;
  }

  @Override
  public Integer visit(Run n, Map<Node, Integer> countMap) {
    countMap.put(n, 1);
    return 1;
  }

  @Override
  public Integer visit(Check n, Map<Node, Integer> countMap) {
    countMap.put(n, 1);
    return 1;
  }
}
