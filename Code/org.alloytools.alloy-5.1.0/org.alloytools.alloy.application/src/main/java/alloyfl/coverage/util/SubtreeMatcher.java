package alloyfl.coverage.util;

import static parser.etc.Names.DOLLAR;
import static parser.etc.Names.UNDERSCORE;
import static parser.util.AlloyUtil.getFirstNonNOOPChild;
import static parser.util.NodeUtil.isBinaryExpr;
import static parser.util.NodeUtil.isBinaryExprOrFormula;
import static parser.util.NodeUtil.isBinaryFormula;
import static parser.util.NodeUtil.isCall;
import static parser.util.NodeUtil.isConstExpr;
import static parser.util.NodeUtil.isFieldExpr;
import static parser.util.NodeUtil.isITEExprOrFormula;
import static parser.util.NodeUtil.isLetExpr;
import static parser.util.NodeUtil.isListExpr;
import static parser.util.NodeUtil.isListExprOrFormula;
import static parser.util.NodeUtil.isListFormula;
import static parser.util.NodeUtil.isQtExpr;
import static parser.util.NodeUtil.isQtExprOrFormula;
import static parser.util.NodeUtil.isQtFormula;
import static parser.util.NodeUtil.isSigExpr;
import static parser.util.NodeUtil.isUnaryExpr;
import static parser.util.NodeUtil.isUnaryExprOrFormula;
import static parser.util.NodeUtil.isUnaryFormula;
import static parser.util.NodeUtil.isVarExpr;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import parser.ast.nodes.Assertion;
import parser.ast.nodes.BinaryExpr;
import parser.ast.nodes.BinaryExprOrFormula;
import parser.ast.nodes.BinaryFormula;
import parser.ast.nodes.Body;
import parser.ast.nodes.Call;
import parser.ast.nodes.Check;
import parser.ast.nodes.Cmd;
import parser.ast.nodes.ConstExpr;
import parser.ast.nodes.ExprOrFormula;
import parser.ast.nodes.Fact;
import parser.ast.nodes.FieldExpr;
import parser.ast.nodes.Function;
import parser.ast.nodes.ITEExprOrFormula;
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
import parser.ast.nodes.VarExpr;
import parser.ast.visitor.PrettyStringVisitor;
import parser.etc.Pair;

/**
 * This class implements a subtree matching algorithm for AParser AST nodes.  Given two nodes at the
 * same level, if their descendants are same, then establish a mapping between them.
 *
 * The algorithm is mainly used for matching the original model and the newly created temporary
 * model with new tests and commands.  We only want to match the original model part of the
 * temporary model.
 */
public class SubtreeMatcher {

  /**
   * Source model to destination model AST nodes mapping.
   */
  private final Map<Node, Node> s2d;
  /**
   * Destination model to source model AST nodes mapping.
   */
  private final Map<Node, Node> d2s;

  private final PrettyStringVisitor psv;

  public SubtreeMatcher(PrettyStringVisitor psv) {
    this.s2d = new LinkedHashMap<>();
    this.d2s = new LinkedHashMap<>();
    this.psv = psv;
  }

  public SubtreeMatcher() {
    this(new PrettyStringVisitor());
  }

  public Map<Node, Node> getS2D() {
    return s2d;
  }

  public Map<Node, Node> getD2S() {
    return d2s;
  }

  /**
   * Match the source model and the target model.  We allow the order of open, predicate, function,
   * fact and assertion declarations to be different as long as they are syntactically same.
   */
  public boolean matches(ModelUnit src, ModelUnit dst) {
    // Match module declaration.
    boolean match = matches(src.getModuleDecl(), dst.getModuleDecl());
    // Match open declarations.
    // For open declarations, the file name is unique.
    match = match && src.getOpenDeclList().size() == dst.getOpenDeclList().size();
    List<Pair<OpenDecl, OpenDecl>> openPairs = findPairs(
        copy(src.getOpenDeclList()), copy(dst.getOpenDeclList()),
        OpenDecl::getFileName);
    for (Pair<OpenDecl, OpenDecl> openPair : openPairs) {
      // Only update match if match == true and the pair matches successfully.
      // The order matters as match && matches(...) will mutate the method call
      // if match is false.
      match = matches(openPair.a, openPair.b) && match;
    }
    // Match signatures.
    match = match && src.getSigDeclList().size() == dst.getSigDeclList().size();
    List<Pair<SigDecl, SigDecl>> sigPairs = findPairs(
        copy(src.getSigDeclList()), copy(dst.getSigDeclList()),
        sigDecl -> sigDecl.accept(psv, null));
    for (Pair<SigDecl, SigDecl> sigPair : sigPairs) {
      // Similar to open declarations.  See above.
      match = matches(sigPair.a, sigPair.b) && match;
    }
    // Match predicates.
    // For predicate declarations, the predicate string representation without name
    // is unique.  We do not use name because a predicate may be automatically generated
    // from run commands and the name depends on the order the commands are declared.
    match = match && (src.getPredDeclList().size() == dst.getPredDeclList().size());
    List<Pair<Predicate, Predicate>> predPairs = findPairs(
        copy(src.getPredDeclList()), copy(dst.getPredDeclList()),
        this::getUniqueKey);
    for (Pair<Predicate, Predicate> predPair : predPairs) {
      match = matches(predPair.a, predPair.b) && match;
    }
    // Match functions.
    // Similar to predicate.  See above.
    match = match && src.getFunDeclList().size() == dst.getFunDeclList().size();
    List<Pair<Function, Function>> funPairs = findPairs(
        copy(src.getFunDeclList()), copy(dst.getFunDeclList()),
        this::getUniqueKey);
    for (Pair<Function, Function> funPair : funPairs) {
      match = matches(funPair.a, funPair.b) && match;
    }
    // Match facts.
    match = match && src.getFactDeclList().size() == dst.getFactDeclList().size();
    List<Pair<Fact, Fact>> factPairs = findPairs(
        copy(src.getFactDeclList()), copy(dst.getFactDeclList()),
        this::getUniqueKey);
    for (Pair<Fact, Fact> factPair : factPairs) {
      match = matches(factPair.a, factPair.b) && match;
    }
    // Match assertions.
    match = match && src.getAssertDeclList().size() == dst.getAssertDeclList().size();
    List<Pair<Assertion, Assertion>> assertPairs = findPairs(
        copy(src.getAssertDeclList()), copy(dst.getAssertDeclList()),
        this::getUniqueKey);
    for (Pair<Assertion, Assertion> assertPair : assertPairs) {
      match = matches(assertPair.a, assertPair.b) && match;
    }
    // Match run commands.
    match = match && src.getRunCmdList().size() == dst.getRunCmdList().size();
    List<Pair<Run, Run>> runPairs = findPairs(
        copy(src.getRunCmdList()), copy(dst.getRunCmdList()),
        run -> run.accept(psv, null));
    for (Pair<Run, Run> runPair : runPairs) {
      match = matches(runPair.a, runPair.b) && match;
    }
    // Match check commands.
    match = match && src.getCheckCmdList().size() == dst.getCheckCmdList().size();
    List<Pair<Check, Check>> checkPairs = findPairs(
        copy(src.getCheckCmdList()), copy(dst.getCheckCmdList()),
        check -> check.accept(psv, null));
    for (Pair<Check, Check> checkPair : checkPairs) {
      match = matches(checkPair.a, checkPair.b) && match;
    }
    // If all pairs of descendants match exactly, add root to the map.
    if (match) {
      addToMap(src, dst);
    }
    return match;
  }

  public boolean matches(ModuleDecl src, ModuleDecl dst) {
    if (src.getModelName().equals(dst.getModelName())) {
      addToMap(src, dst);
      return true;
    }
    return false;
  }

  public boolean matches(OpenDecl src, OpenDecl dst) {
    if (src.getFileName().equals(dst.getFileName()) && src.getArguments().equals(dst.getArguments())
        && src.getAlias().equals(dst.getAlias())) {
      addToMap(src, dst);
      return true;
    }
    return false;
  }

  public boolean matches(SigDecl src, SigDecl dst) {
    if (!src.getName().equals(dst.getName())
        || src.isAbstract() != dst.isAbstract()
        || src.getMult() != dst.getMult()
        || src.isTopLevel() != dst.isTopLevel()
        || src.isSubsig() != dst.isSubsig()
        || src.hasSigFact() != dst.hasSigFact()) {
      return false;
    }
    // Parent can only be non-null if the sig is not top-level.  See SigDecl for details.
    if (!src.isTopLevel()) {
      if (!src.getParentName().equals(dst.getParentName())) {
        return false;
      }
    }
    if (src.getFieldList().size() != dst.getFieldList().size()) {
      return false;
    }
    for (int i = 0; i < src.getFieldList().size(); i++) {
      if (!matches(src.getFieldList().get(i), dst.getFieldList().get(i))) {
        return false;
      }
    }
    if (src.hasSigFact()) {
      if (!matches(src.getSigFact(), dst.getSigFact())) {
        return false;
      }
    }
    addToMap(src, dst);
    return true;
  }

  /**
   * Match two predicates or functions.  We can stop matching if any part of the two
   * predicates/functions does not match.
   */
  public boolean matches(PredOrFun src, PredOrFun dst) {
    if (!matches(src.getName(), dst.getName())) {
      return false;
    }
    List<ParamDecl> srcParamDecls = src.getParamList();
    List<ParamDecl> dstParamDecls = dst.getParamList();
    if (srcParamDecls.size() != dstParamDecls.size()) {
      return false;
    }
    for (int i = 0; i < src.getParamList().size(); i++) {
      if (!matches(src.getParamList().get(i), dst.getParamList().get(i))) {
        return false;
      }
    }
    if (!matches(src.getBody(), dst.getBody())) {
      return false;
    }
    addToMap(src, dst);
    return true;
  }

  /**
   * Match two facts or assertions.  We can stop matching if any part of facts/assertions does not
   * match.
   */
  public boolean matches(Paragraph src, Paragraph dst) {
    if (!matches(src.getName(), dst.getName()) || !matches(src.getBody(), dst.getBody())) {
      return false;
    }
    addToMap(src, dst);
    return true;
  }

  public boolean matches(Cmd src, Cmd dst) {
    if (!matches(src.getName(), dst.getName())
        || !src.getScopeAsString().equals(dst.getScopeAsString())
        || src.getExpect() != dst.getExpect()) {
      return false;
    }
    addToMap(src, dst);
    return true;
  }

  public boolean matches(RelDecl src, RelDecl dst) {
    if (src.isDisjoint() == dst.isDisjoint() && src.getNames().equals(dst.getNames())
        && matches(src.getExpr(), dst.getExpr())) {
      addToMap(src, dst);
      return true;
    }
    return false;
  }

  public boolean matches(Body src, Body dst) {
    if (matches(src.getBodyExpr(), dst.getBodyExpr())) {
      addToMap(src, dst);
      return true;
    }
    return false;
  }

  public boolean matches(ExprOrFormula src, ExprOrFormula dst) {
    // We do not match NOOP unary expressions.  Because some alloyfl.mutation operators may add or delete
    // NOOP unary expressions and make the new AST not exactly same as one would parse from plain
    // text.  For example, (., n, (NOOP, link)) may be mutated to (., n, (NOOP, (~, link))) but it
    // would be (., n, (NOOP, (~, (NOOP, link)))) if it is parsed from plain text.  We do not want
    // to change the alloyfl.mutation operator implementation so here we do not match NOOP.
    src = (ExprOrFormula) getFirstNonNOOPChild(src);
    dst = (ExprOrFormula) getFirstNonNOOPChild(dst);
    if (src.getClass() != dst.getClass()) {
      return false;
    }
    if (isBinaryExprOrFormula(src)) {
      return matches((BinaryExprOrFormula) src, (BinaryExprOrFormula) dst);
    }
    if (isSigExpr(src)) {
      return matches((SigExpr) src, (SigExpr) dst);
    }
    if (isCall(src)) {
      return matches((Call) src, (Call) dst);
    }
    if (isConstExpr(src)) {
      return matches((ConstExpr) src, (ConstExpr) dst);
    }
    if (isVarExpr(src)) {
      return matches((VarExpr) src, (VarExpr) dst);
    }
    if (isFieldExpr(src)) {
      return matches((FieldExpr) src, (FieldExpr) dst);
    }
    if (isITEExprOrFormula(src)) {
      return matches((ITEExprOrFormula) src, (ITEExprOrFormula) dst);
    }
    if (isUnaryExprOrFormula(src)) {
      return matches((UnaryExprOrFormula) src, (UnaryExprOrFormula) dst);
    }
    if (isListExprOrFormula(src)) {
      return matches((ListExprOrFormula) src, (ListExprOrFormula) dst);
    }
    if (isLetExpr(src)) {
      return matches((LetExpr) src, (LetExpr) dst);
    }
    if (isQtExprOrFormula(src)) {
      return matches((QtExprOrFormula) src, (QtExprOrFormula) dst);
    }
    // Should not be reached.
    return true;
  }

  public boolean matches(BinaryExprOrFormula src, BinaryExprOrFormula dst) {
    if (isBinaryExpr(src)) {
      if (((BinaryExpr) src).getOp() != ((BinaryExpr) dst).getOp()) {
        return false;
      }
    }
    if (isBinaryFormula(src)) {
      if (((BinaryFormula) src).getOp() != ((BinaryFormula) dst).getOp()) {
        return false;
      }
    }
    if (!matches(src.getLeft(), dst.getLeft()) || !matches(src.getRight(), dst.getRight())) {
      return false;
    }
    addToMap(src, dst);
    return true;
  }

  public boolean matches(SigExpr src, SigExpr dst) {
    if (src.getName().equals(dst.getName())) {
      addToMap(src, dst);
      return true;
    }
    return false;
  }

  public boolean matches(Call src, Call dst) {
    if (!src.getName().equals(dst.getName())) {
      return false;
    }
    List<ExprOrFormula> srcArgs = src.getArguments();
    List<ExprOrFormula> dstArgs = dst.getArguments();
    if (srcArgs.size() != dstArgs.size()) {
      return false;
    }
    for (int i = 0; i < srcArgs.size(); i++) {
      if (!matches(srcArgs.get(i), dstArgs.get(i))) {
        return false;
      }
    }
    addToMap(src, dst);
    return true;
  }

  public boolean matches(ConstExpr src, ConstExpr dst) {
    if (src.getValue().equals(dst.getValue())) {
      addToMap(src, dst);
      return true;
    }
    return false;
  }

  public boolean matches(VarExpr src, VarExpr dst) {
    if (src.getName().equals(dst.getName())) {
      addToMap(src, dst);
      return true;
    }
    return false;
  }

  public boolean matches(FieldExpr src, FieldExpr dst) {
    if (src.getName().equals(dst.getName())) {
      addToMap(src, dst);
      return true;
    }
    return false;
  }

  public boolean matches(ITEExprOrFormula src, ITEExprOrFormula dst) {
    if (matches(src.getCondition(), dst.getCondition())
        && matches(src.getThenClause(), dst.getThenClause())
        && matches(src.getElseClause(), dst.getElseClause())) {
      addToMap(src, dst);
      return true;
    }
    return false;
  }

  public boolean matches(UnaryExprOrFormula src, UnaryExprOrFormula dst) {
    if (isUnaryExpr(src)) {
      if (((UnaryExpr) src).getOp() != ((UnaryExpr) dst).getOp()) {
        return false;
      }
    }
    if (isUnaryFormula(src)) {
      if (((UnaryFormula) src).getOp() != ((UnaryFormula) dst).getOp()) {
        return false;
      }
    }

    if (!matches(src.getSub(), dst.getSub())) {
      return false;
    }
    addToMap(src, dst);
    return true;
  }

  public boolean matches(ListExprOrFormula src, ListExprOrFormula dst) {
    if (isListExpr(src)) {
      if (((ListExpr) src).getOp() != ((ListExpr) dst).getOp()) {
        return false;
      }
    }
    if (isListFormula(src)) {
      if (((ListFormula) src).getOp() != ((ListFormula) dst).getOp()) {
        return false;
      }
    }
    if (src.getArguments().size() != dst.getArguments().size()) {
      return false;
    }
    for (int i = 0; i < src.getArguments().size(); i++) {
      if (!matches(src.getArguments().get(i), dst.getArguments().get(i))) {
        return false;
      }
    }
    addToMap(src, dst);
    return true;
  }

  public boolean matches(LetExpr src, LetExpr dst) {
    if (matches(src.getVar(), dst.getVar())
        && matches(src.getBound(), dst.getBound())
        && matches(src.getBody(), dst.getBody())) {
      addToMap(src, dst);
      return true;
    }
    return false;
  }

  public boolean matches(QtExprOrFormula src, QtExprOrFormula dst) {
    if (isQtExpr(src)) {
      if (((QtExpr) src).getOp() != ((QtExpr) dst).getOp()) {
        return false;
      }
    }
    if (isQtFormula(src)) {
      if (((QtFormula) src).getOp() != ((QtFormula) dst).getOp()) {
        return false;
      }
    }
    if (src.getVarDecls().size() != dst.getVarDecls().size()) {
      return false;
    }
    for (int i = 0; i < src.getVarDecls().size(); i++) {
      if (!matches(src.getVarDecls().get(i), dst.getVarDecls().get(i))) {
        return false;
      }
    }
    if (!matches(src.getBody(), dst.getBody())) {
      return false;
    }
    addToMap(src, dst);
    return true;
  }

  public boolean matches(String srcName, String dstName) {
    return srcName.replaceAll("\\" + DOLLAR, UNDERSCORE)
        .equals(dstName.replaceAll("\\" +DOLLAR, UNDERSCORE));
  }

  private void addToMap(Node src, Node dst) {
    s2d.put(src, dst);
    d2s.put(dst, src);
  }

  private static <T> List<T> copy(List<T> list) {
    return new ArrayList<>(list);
  }

  @SafeVarargs
  private static <T> int minimumLength(List<T>... lists) {
    if (lists == null || lists.length == 0) {
      return 0;
    }
    int res = lists[0].size();
    for (List<T> list : lists) {
      res = Math.min(res, list.size());
    }
    return res;
  }

  private String getUniqueKey(Paragraph paragraph) {
    String originalName = paragraph.getName();
    paragraph.setName(""); // Cannot be null otherwise throw NullPointerException.
    String uniqueKey = paragraph.accept(psv, null);
    paragraph.setName(originalName);
    return uniqueKey;
  }

  private static <T, U extends Comparable<? super U>> List<Pair<T, T>>
  findPairs(List<T> list1, List<T> list2, java.util.function.Function<T, U> matchFun) {
    List<Pair<T, T>> pairs = new ArrayList<>();
    list1.sort(Comparator.comparing(matchFun));
    list2.sort(Comparator.comparing(matchFun));
    int i1 = 0, i2 = 0;
    while (i1 < list1.size() && i2 < list2.size()) {
      T srcElem = list1.get(i1);
      T dstElem = list2.get(i2);
      int cmpRes = matchFun.apply(srcElem).compareTo(matchFun.apply(dstElem));
      if (cmpRes < 0) { // srcElem is less than dstElem.
        i1 += 1;
      } else if (cmpRes > 0) { // srcElem is greater than dstElem.
        i2 += 1;
      } else { // srcElem is equal to dstElem.
        pairs.add(Pair.of(srcElem, dstElem));
        i1 += 1;
        i2 += 1;
      }
    }
    return pairs;
  }
}
