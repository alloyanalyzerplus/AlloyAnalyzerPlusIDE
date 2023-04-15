package parser.util;

import parser.ast.nodes.Assertion;
import parser.ast.nodes.BinaryExpr;
import parser.ast.nodes.BinaryExprOrFormula;
import parser.ast.nodes.BinaryFormula;
import parser.ast.nodes.Body;
import parser.ast.nodes.Call;
import parser.ast.nodes.CallExpr;
import parser.ast.nodes.CallFormula;
import parser.ast.nodes.Check;
import parser.ast.nodes.Cmd;
import parser.ast.nodes.ConstExpr;
import parser.ast.nodes.ExprOrFormula;
import parser.ast.nodes.Fact;
import parser.ast.nodes.FieldDecl;
import parser.ast.nodes.FieldExpr;
import parser.ast.nodes.Function;
import parser.ast.nodes.ITEExpr;
import parser.ast.nodes.ITEExprOrFormula;
import parser.ast.nodes.ITEFormula;
import parser.ast.nodes.LetExpr;
import parser.ast.nodes.ListExpr;
import parser.ast.nodes.ListExprOrFormula;
import parser.ast.nodes.ListFormula;
import parser.ast.nodes.ModelUnit;
import parser.ast.nodes.ModuleDecl;
import parser.ast.nodes.Node;
import parser.ast.nodes.OpenDecl;
import parser.ast.nodes.Paragraph;
import parser.ast.nodes.ParamDecl;
import parser.ast.nodes.PredOrFun;
import parser.ast.nodes.Predicate;
import parser.ast.nodes.QtExpr;
import parser.ast.nodes.QtExprOrFormula;
import parser.ast.nodes.QtFormula;
import parser.ast.nodes.RelDecl;
import parser.ast.nodes.Run;
import parser.ast.nodes.SigDecl;
import parser.ast.nodes.SigExpr;
import parser.ast.nodes.UnaryExpr;
import parser.ast.nodes.UnaryExprOrFormula;
import parser.ast.nodes.UnaryFormula;
import parser.ast.nodes.VarDecl;
import parser.ast.nodes.VarExpr;

public class NodeUtil {
  public static boolean isAssertion(Node node) {
    return node instanceof Assertion;
  }

  public static boolean isBinaryExpr(Node node) {
    return node instanceof BinaryExpr;
  }

  public static boolean isBinaryExprOrFormula(Node node) {
    return node instanceof BinaryExprOrFormula;
  }

  public static boolean isBinaryFormula(Node node) {
    return node instanceof BinaryFormula;
  }

  public static boolean isBody(Node node) {
    return node instanceof Body;
  }

  public static boolean isCall(Node node) {
    return node instanceof Call;
  }

  public static boolean isCallExpr(Node node) {
    return node instanceof CallExpr;
  }

  public static boolean isCallFormula(Node node) {
    return node instanceof CallFormula;
  }

  public static boolean isCheck(Node node) {
    return node instanceof Check;
  }

  public static boolean isCmd(Node node) {
    return node instanceof Cmd;
  }

  public static boolean isConstExpr(Node node) {
    return node instanceof ConstExpr;
  }

  public static boolean isExprOrFormula(Node node) {
    return node instanceof ExprOrFormula;
  }

  public static boolean isFact(Node node) {
    return node instanceof Fact;
  }

  public static boolean isFieldDecl(Node node) {
    return node instanceof FieldDecl;
  }

  public static boolean isFieldExpr(Node node) {
    return node instanceof FieldExpr;
  }

  public static boolean isFunction(Node node) {
    return node instanceof Function;
  }

  public static boolean isITEExpr(Node node) {
    return node instanceof ITEExpr;
  }

  public static boolean isITEExprOrFormula(Node node) {
    return node instanceof ITEExprOrFormula;
  }

  public static boolean isITEFormula(Node node) {
    return node instanceof ITEFormula;
  }

  public static boolean isLetExpr(Node node) {
    return node instanceof LetExpr;
  }

  public static boolean isListExpr(Node node) {
    return node instanceof ListExpr;
  }

  public static boolean isListExprOrFormula(Node node) {
    return node instanceof ListExprOrFormula;
  }

  public static boolean isListFormula(Node node) {
    return node instanceof ListFormula;
  }

  public static boolean isModelUnit(Node node) {
    return node instanceof ModelUnit;
  }

  public static boolean isModuleDecl(Node node) {
    return node instanceof ModuleDecl;
  }

  public static boolean isOpenDecl(Node node) {
    return node instanceof OpenDecl;
  }

  public static boolean isParagraph(Node node) {
    return node instanceof Paragraph;
  }

  public static boolean isParamDecl(Node node) {
    return node instanceof ParamDecl;
  }

  public static boolean isPredicate(Node node) {
    return node instanceof Predicate;
  }

  public static boolean isPredOrFun(Node node) {
    return node instanceof PredOrFun;
  }

  public static boolean isQtExpr(Node node) {
    return node instanceof QtExpr;
  }

  public static boolean isQtExprOrFormula(Node node) {
    return node instanceof QtExprOrFormula;
  }

  public static boolean isQtFormula(Node node) {
    return node instanceof QtFormula;
  }

  public static boolean isRelDecl(Node node) {
    return node instanceof RelDecl;
  }

  public static boolean isRun(Node node) {
    return node instanceof Run;
  }

  public static boolean isSigDecl(Node node) {
    return node instanceof SigDecl;
  }

  public static boolean isSigExpr(Node node) {
    return node instanceof SigExpr;
  }

  public static boolean isUnaryExpr(Node node) {
    return node instanceof UnaryExpr;
  }

  public static boolean isUnaryExprOrFormula(Node node) {
    return node instanceof UnaryExprOrFormula;
  }

  public static boolean isUnaryFormula(Node node) {
    return node instanceof UnaryFormula;
  }

  public static boolean isVarDecl(Node node) {
    return node instanceof VarDecl;
  }

  public static boolean isVarExpr(Node node) {
    return node instanceof VarExpr;
  }
}
