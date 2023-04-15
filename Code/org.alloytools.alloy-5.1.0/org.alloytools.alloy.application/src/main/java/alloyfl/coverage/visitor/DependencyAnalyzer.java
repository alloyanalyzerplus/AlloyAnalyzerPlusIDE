package alloyfl.coverage.visitor;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
import parser.ast.nodes.CallExpr;
import parser.ast.nodes.CallFormula;
import parser.ast.nodes.ConstExpr;
import parser.ast.nodes.FieldDecl;
import parser.ast.nodes.FieldExpr;
import parser.ast.nodes.Function;
import parser.ast.nodes.LetExpr;
import parser.ast.nodes.ModelUnit;
import parser.ast.nodes.Node;
import parser.ast.nodes.Paragraph;
import parser.ast.nodes.Predicate;
import parser.ast.nodes.QtExpr;
import parser.ast.nodes.QtExprOrFormula;
import parser.ast.nodes.QtFormula;
import parser.ast.nodes.RelDecl;
import parser.ast.nodes.SigDecl;
import parser.ast.nodes.SigExpr;
import parser.ast.nodes.VarExpr;
import parser.ast.visitor.VoidVisitorAdapter;

/**
 * The class implements a visitor that takes an Alloy construct and returns a list of new constructs
 * to visit in the next round.  Note that this visitor is designed to visit high level Alloy
 * constructs, only including predicates, functions, facts and assertions.  It is not intended to
 * visit sub-formulas.  For example, in "all s: S | some s", if we visit "some s" then we lose the
 * declaration "s: S" and the visitor may give unexpected result.
 */
public class DependencyAnalyzer extends VoidVisitorAdapter<Object> {

  private static final Set<SigDecl> EMPTY_SET = Collections.unmodifiableSet(new HashSet<>());
  /**
   * Maps each sig/field name to a list of sig declarations it uses under the current scope.
   */
  private Map<String, Stack<Set<SigDecl>>> varsInScope;
  /**
   * Maps each paragraph name to the corresponding AST node.
   */
  private Map<String, Paragraph> name2paragraph;
  /**
   * Stores the signatures used for a visit.
   */
  private Set<SigDecl> usedSigs;
  /**
   * Stores the predicates, functions, facts and assertions used for a visit.
   */
  private Set<Paragraph> usedParagraphs;

  public DependencyAnalyzer(ModelUnit modelUnit) {
    this.varsInScope = findVarsInScope(modelUnit);
    this.name2paragraph = findParagraphMapping(modelUnit);
    this.usedSigs = new HashSet<>();
    this.usedParagraphs = new HashSet<>();
  }

  public Set<SigDecl> getUsedSigs() {
    return usedSigs;
  }

  public Set<Paragraph> getUsedParagraphs() {
    return usedParagraphs;
  }

  private Map<String, Stack<Set<SigDecl>>> findVarsInScope(ModelUnit modelUnit) {
    Map<String, Set<SigDecl>> name2decls = new HashMap<>();
    // Search the default variables in scope.
    // First round maps each keyword to its corresponding signature declaration.
    collectDepsForKeywords(modelUnit.getSigDeclList(), name2decls);
    // Second round maps each signature to its declaration and transitive
    // parent declarations.
    collectDepsForSigs(modelUnit.getSigDeclList(), name2decls);
    // Third round maps each field to its declared signature declaration
    // and all used signature declarations.
    collectDepsForFields(modelUnit.getSigDeclList(), name2decls);
    // Fourth round maps each function to its return type and all used
    // signature declarations.
    collectDepsForFunctions(modelUnit.getFunDeclList(), name2decls);
    // Construct variables in scope with stacks.
    return name2decls.entrySet().stream().collect(
        Collectors.toMap(Map.Entry::getKey, entry -> {
          Stack<Set<SigDecl>> stack = new Stack<>();
          stack.push(entry.getValue());
          return stack;
        }));
  }

  private void collectDepsForKeywords(List<SigDecl> sigDecls,
      Map<String, Set<SigDecl>> name2decls) {
    Set<SigDecl> universe = Collections.unmodifiableSet(new HashSet<>(sigDecls));
    name2decls.put("univ", universe);
    name2decls.put("iden", universe);
  }

  private void collectDepsForSigs(List<SigDecl> sigDecls, Map<String, Set<SigDecl>> name2decls) {
    Map<String, SigDecl> name2sig = sigDecls.stream()
        .collect(Collectors.toMap(SigDecl::getName, java.util.function.Function.identity()));
    for (String sigName : name2sig.keySet()) {
      String currentName = sigName;
      while (currentName != null) {
        SigDecl sigDecl = name2sig.get(currentName);
        // It is possible that a signature extends another signature declared in another file.
        // So sigDecl may be null, in which case we create a temporary sig decl with null parent.
        // TODO(kaiyuanw): This may still need more thorough consideration.
        if (sigDecl == null) {
          sigDecl = new SigDecl(null);
          sigDecl.setName(currentName);
          sigDecl.setFieldList(Collections.EMPTY_LIST);
        }
        if (!name2decls.containsKey(sigName)) {
          name2decls.put(sigName, new HashSet<>());
        }
        name2decls.get(sigName).add(sigDecl);
        currentName = sigDecl.getParentName();
      }
    }
  }

  private Set<SigDecl> findDependentSigs(Node node, Map<String, Set<SigDecl>> name2decls) {
    ExprCollector exprCollector = new ExprCollector();
    node.accept(exprCollector, null);
    return exprCollector.usedNames.stream()
        .filter(name2decls::containsKey)
        .map(name2decls::get)
        .flatMap(Collection::stream)
        .collect(Collectors.toSet());
  }

  private Set<SigDecl> findDependentSigs(Node node) {
    ExprCollector exprCollector = new ExprCollector();
    node.accept(exprCollector, null);
    return exprCollector.usedNames.stream()
        .filter(varsInScope::containsKey)
        .map(varsInScope::get)
        .map(Stack::peek)
        .flatMap(Collection::stream)
        .collect(Collectors.toSet());
  }

  private void collectDepsForFields(List<SigDecl> sigDecls, Map<String, Set<SigDecl>> name2decls) {
    for (SigDecl sigDecl : sigDecls) {
      for (FieldDecl fieldDecl : sigDecl.getFieldList()) {
        // Find all directly or indirectly referenced signatures.
        Set<SigDecl> decls = findDependentSigs(fieldDecl.getExpr(), name2decls);
        // Include the declared signature.
        decls.add(sigDecl);
        // We assume a good model should not have overlapping field names.
        // In Alloy, you can only use previous defined field in a field declaration.
        // The previously defined field must be declared in the same signature as
        // the current field.
        for (String fieldName : fieldDecl.getNames()) {
          name2decls.put(fieldName, decls);
        }
      }
    }
  }

  private void collectDepsForFunctions(List<Function> funDecls,
      Map<String, Set<SigDecl>> name2decls) {
    for (Function funDecl : funDecls) {
      // Find all directly or indirectly referenced signatures in the return type.
      Set<SigDecl> sigDecls = findDependentSigs(funDecl.getReturnType(), name2decls);
      // We assume a good model should not have overlapping function names.
      // In Alloy, you cannot have function's return type declared using another
      // function's return type.
      name2decls.put(funDecl.getName(), sigDecls);
    }
  }

  private Map<String, Paragraph> findParagraphMapping(ModelUnit modelUnit) {
    Map<String, Paragraph> name2paragraph = new HashMap<>();
    modelUnit.getPredDeclList()
        .forEach(predicate -> name2paragraph.put(predicate.getName(), predicate));
    modelUnit.getFunDeclList()
        .forEach(function -> name2paragraph.put(function.getName(), function));
    modelUnit.getFactDeclList().forEach(fact -> name2paragraph.put(fact.getName(), fact));
    modelUnit.getAssertDeclList()
        .forEach(assertion -> name2paragraph.put(assertion.getName(), assertion));
    return name2paragraph;
  }

  public void clearUsedParagraphs() {
    usedParagraphs = new HashSet<>();
  }

  // For SigDecl:
  // We should never visit a signature declaration if invoked from outside of a signature
  // declaration node.
  // No need to visit field list or sig fact if they are not used.
  // E.g. sig S { r: T } sig T {} run { some S }, in this case only S is used
  // and we do not want to highlight T.  So we do not visit field declaration.

  // For FieldDecl, ParamDecl and VarDecl:
  // We should never visit a field declaration if invoked from outside of a field declaration node.
  // We want to handle parameter declarations directly in predicate/function declarations.
  // We want to handle variable declarations directly in expression/formula declarations.
  // So we can backtrack and update variables in scope.

  @Override
  public void visit(SigExpr n, Object arg) {
    usedSigs.addAll(getUsedSigs(n.getName()));
  }

  @Override
  public void visit(FieldExpr n, Object arg) {
    usedSigs.addAll(getUsedSigs(n.getName()));
  }

  @Override
  public void visit(VarExpr n, Object arg) {
    usedSigs.addAll(getUsedSigs(n.getName()));
  }

  @Override
  public void visit(ConstExpr n, Object arg) {
    // In case if we use iden.
    usedSigs.addAll(getUsedSigs(n.getValue()));
  }

  @Override
  public void visit(CallExpr n, Object arg) {
    // We assume APIs from other models are correct.  For example, if we invoke ord/first[]
    // or ord/next[], then we don't need to traverse those functions.
    if (name2paragraph.containsKey(n.getName())) {
      usedParagraphs.add(name2paragraph.get(n.getName()));
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(CallFormula n, Object arg) {
    // We assume APIs from other models are correct.  For example, if we invoke ord/first[]
    // or ord/next[], then we don't need to traverse those functions.
    if (name2paragraph.containsKey(n.getName())) {
      usedParagraphs.add(name2paragraph.get(n.getName()));
    }
    super.visit(n, arg);
  }

  @Override
  public void visit(QtExpr n, Object arg) {
    visitQtExprOrFormula(n, arg);
  }

  @Override
  public void visit(QtFormula n, Object arg) {
    visitQtExprOrFormula(n, arg);
  }

  private void visitQtExprOrFormula(QtExprOrFormula n, Object arg) {
    // We should create a new variable and hide the variables with the same name.
    // In the meanwhile, we want to update all used signature declarations in this
    // parameter declaration.
    n.getVarDecls().forEach(this::addToVarsInScope);
    n.getBody().accept(this, arg);
    // Backtrack.
    n.getVarDecls().forEach(this::removeFromVarsInScope);
  }

  @Override
  public void visit(LetExpr n, Object arg) {
    // Let expression only contains a single variable name.
    VarExpr varExpr = n.getRealVar();
    Set<SigDecl> sigDecls = findDependentSigs(n.getBound());
    // We do not count the dependent signatures as used signatures
    // unless the variable(s) is used later.
    // usedSigs.addAll(sigDecls);
    addToVarsInScope(varExpr.getName(), sigDecls);
    n.getBody().accept(this, arg);
    // Backtrack.
    removeFromVarsInScope(varExpr.getName());
  }

  @Override
  public void visit(Predicate n, Object arg) {
    // We should create a new variable and hide the variables with the same name.
    // In the meanwhile, we want to update all used signature declarations in this
    // parameter declaration.
    n.getParamList().forEach(this::addToVarsInScope);
    n.getBody().accept(this, arg);
    // Backtrack.
    n.getParamList().forEach(this::removeFromVarsInScope);
  }

  @Override
  public void visit(Function n, Object arg) {
    n.getParamList().forEach(this::addToVarsInScope);
    n.getReturnType().accept(this, arg);
    n.getBody().accept(this, arg);
    n.getParamList().forEach(this::removeFromVarsInScope);
  }

  private void addToVarsInScope(String name, Set<SigDecl> sigDecls) {
    if (!varsInScope.containsKey(name)) {
      varsInScope.put(name, new Stack<>());
    }
    varsInScope.get(name).push(sigDecls);
  }

  private void addToVarsInScope(RelDecl n) {
    Set<SigDecl> sigDecls = findDependentSigs(n.getExpr());
    // We do not count the dependent signatures as used signatures
    // unless the variable(s) is used later.
    // usedSigs.addAll(sigDecls);
    n.getNames().forEach(name -> addToVarsInScope(name, sigDecls));
  }

  private void removeFromVarsInScope(String name) {
    varsInScope.get(name).pop();
    if (varsInScope.get(name).empty()) {
      varsInScope.remove(name);
    }
  }

  private void removeFromVarsInScope(RelDecl n) {
    n.getNames().forEach(this::removeFromVarsInScope);
  }

  /**
   * Get the used signatures for the given variable name.  If the variable is masked with another
   * variable with the same name, then return the used signatures of the inner most variable.
   */
  private Set<SigDecl> getUsedSigs(String name) {
    if (!varsInScope.containsKey(name)) { // invoke special keywords like none, univ.
      return EMPTY_SET;
    }
    return varsInScope.get(name).peek();
  }

  /**
   * Collect all leaves that possibly refer to signatures.
   */
  private class ExprCollector extends VoidVisitorAdapter<Object> {

    private Set<String> usedNames;

    ExprCollector() {
      this.usedNames = new HashSet<>();
    }

    @Override
    public void visit(SigExpr n, Object arg) {
      usedNames.add(n.getName());
    }

    @Override
    public void visit(FieldExpr n, Object arg) {
      usedNames.add(n.getName());
    }

    @Override
    public void visit(VarExpr n, Object arg) {
      usedNames.add(n.getName());
    }

    @Override
    public void visit(ConstExpr n, Object arg) {
      // In case the declaration type involves iden.
      usedNames.add(n.getValue());
    }
  }
}
