package alloyfl.himualloy.util;

import static mualloy.rule.LOR.canFlatten;
import static mualloy.rule.LOR.flattenListFormula;
import static mualloy.rule.LOR.revertListFormula;
import static parser.util.AlloyUtil.getFirstNonNOOPChild;

import alloyfl.himualloy.exception.InvalidNodeTypeException;
import alloyfl.himualloy.opt.HigherOrderMutantGeneratorOpt;
import edu.mit.csail.sdg.ast.Type;
import java.nio.file.Paths;
import java.util.List;
import parser.ast.nodes.BinaryExpr;
import parser.ast.nodes.BinaryFormula;
import parser.ast.nodes.BinaryFormula.BinaryOp;
import parser.ast.nodes.BinaryFormula.BinaryOp.Group;
import parser.ast.nodes.Body;
import parser.ast.nodes.ConstExpr;
import parser.ast.nodes.ExprOrFormula;
import parser.ast.nodes.Fact;
import parser.ast.nodes.FieldExpr;
import parser.ast.nodes.ITEExprOrFormula;
import parser.ast.nodes.ITEFormula;
import parser.ast.nodes.ListFormula;
import parser.ast.nodes.ModelUnit;
import parser.ast.nodes.Node;
import parser.ast.nodes.Predicate;
import parser.ast.nodes.QtFormula;
import parser.ast.nodes.SigDecl;
import parser.ast.nodes.UnaryExpr;
import parser.ast.nodes.UnaryFormula;
import parser.ast.nodes.VarDecl;
import parser.ast.nodes.VarExpr;
import parser.etc.Names;
import parser.etc.Pair;
import parser.util.AlloyUtil;
import parser.util.Codec;
import parser.util.FileUtil;

public class HigherOrderBacktracker {

  private static int COUNT = 1;

  public static void backtrack(ModelUnit mu, List<Node> mutationPoints, int start,
      List<Node> selectedPoints, List<String> names, HigherOrderMutantGeneratorOpt opt) {
    if (selectedPoints.size() == opt.getOrderNumber()) {
      String mutantAsString = mu.accept(opt.getPSV(), null);
      if (!AlloyUtil.isValidModel(mutantAsString) || (opt.getTestSuitePath() != null && TestRunner
          .runTestsFailFast(mutantAsString, opt.getTestSuitePath(), opt.getOptions()))) {
        return;
      }
      names.add(String.valueOf(COUNT));
      String uid = String.join(Names.UNDERSCORE, names);
      Codec.serialize(Pair.of(mu, selectedPoints),
          Paths.get(opt.getResultDirPath(), uid + Names.DOT_FLT).toString());
      FileUtil.writeText(mutantAsString,
          Paths.get(opt.getMutantDirPath(), uid + Names.DOT_ALS).toString(), false);
      names.remove(names.size() - 1);
      COUNT++;
    } else {
      for (int i = start; i < mutationPoints.size(); i++) {
        mutateAndBacktrack(mu, mutationPoints, i, selectedPoints, names, opt);
      }
    }
  }

  private static void mutateAndBacktrack(ModelUnit mu, List<Node> mutationPoints, int i,
      List<Node> selectedPoints, List<String> names, HigherOrderMutantGeneratorOpt opt) {
    Node node = mutationPoints.get(i);
    if (node instanceof SigDecl) {
      SigDecl sigDecl = (SigDecl) node;
      mor(sigDecl, mu, mutationPoints, i, selectedPoints, names, opt);
    } else if (node instanceof FieldExpr || node instanceof VarExpr) {
      ExprOrFormula exprOrFormula = (ExprOrFormula) node;
      uoi(exprOrFormula, mu, mutationPoints, i, selectedPoints, names, opt);
    } else if (node instanceof UnaryExpr) {
      UnaryExpr unaryExpr = (UnaryExpr) node;
      uor(unaryExpr, mu, mutationPoints, i, selectedPoints, names, opt);
      uoi(unaryExpr, mu, mutationPoints, i, selectedPoints, names, opt);
      uod(unaryExpr, mu, mutationPoints, i, selectedPoints, names, opt);
    } else if (node instanceof UnaryFormula) {
      UnaryFormula unaryFormula = (UnaryFormula) node;
      uor(unaryFormula, mu, mutationPoints, i, selectedPoints, names, opt);
      uod(unaryFormula, mu, mutationPoints, i, selectedPoints, names, opt);
    } else if (node instanceof BinaryExpr) {
      BinaryExpr binaryExpr = (BinaryExpr) node;
      bor(binaryExpr, mu, mutationPoints, i, selectedPoints, names, opt);
      uoi(binaryExpr, mu, mutationPoints, i, selectedPoints, names, opt);
      boe(binaryExpr, mu, mutationPoints, i, selectedPoints, names, opt);
    } else if (node instanceof BinaryFormula) {
      BinaryFormula binaryFormula = (BinaryFormula) node;
      bor(binaryFormula, mu, mutationPoints, i, selectedPoints, names, opt);
      boe(binaryFormula, mu, mutationPoints, i, selectedPoints, names, opt);
    } else if (node instanceof ListFormula) {
      ListFormula listFormula = (ListFormula) node;
      lor(listFormula, mu, mutationPoints, i, selectedPoints, names, opt);
      lod(listFormula, mu, mutationPoints, i, selectedPoints, names, opt);
    } else if (node instanceof QtFormula) {
      QtFormula qtFormula = (QtFormula) node;
      qor(qtFormula, mu, mutationPoints, i, selectedPoints, names, opt);
    } else if (node instanceof ITEFormula) {
      ITEFormula iteFormula = (ITEFormula) node;
      ieoe(iteFormula, mu, mutationPoints, i, selectedPoints, names, opt);
    } else if (node instanceof Body) {
      Body body = (Body) node;
      pbd(body, mu, mutationPoints, i, selectedPoints, names, opt);
    } else {
      throw new InvalidNodeTypeException(
          "Potential alloyfl.mutation points should not contain node type " + node.getClass()
              .getSimpleName());
    }
  }

  private static void mor(SigDecl sigDecl, ModelUnit mu, List<Node> mutationPoints, int i,
      List<Node> selectedPoints, List<String> names, HigherOrderMutantGeneratorOpt opt) {
    SigDecl.MULT original = sigDecl.getMult();
    for (SigDecl.MULT mult : SigDecl.MULT.values()) {
      if (mult == original) {
        continue;
      }
      sigDecl.setMult(mult);
//      String mutant = mu.accept(opt.getPSV(), null);
      // Check if the mutant compiles.
//      if (AlloyUtil.isValidModel(mutant)) {
      selectedPoints.add(sigDecl);
      names.add("MOR");
      backtrack(mu, mutationPoints, i + 1, selectedPoints, names, opt);
      names.remove(names.size() - 1);
      selectedPoints.remove(selectedPoints.size() - 1);
//      }
    }
    sigDecl.setMult(original);
  }

  private static void uoi(ExprOrFormula exprOrFormula, ModelUnit mu, List<Node> mutationPoints,
      int i, List<Node> selectedPoints, List<String> names, HigherOrderMutantGeneratorOpt opt) {
    if (exprOrFormula instanceof UnaryExpr
        && ((UnaryExpr) exprOrFormula).getOp() == UnaryExpr.UnaryOp.NOOP) {
      return;
    }
    if (AlloyUtil.isHomogeneous(exprOrFormula.getType())) {
      UnaryExpr parent = (UnaryExpr) exprOrFormula.getParent();
      for (UnaryExpr.UnaryOp unaryOp : UnaryExpr.UnaryOp.values()) {
        if (unaryOp.getGroup() != UnaryExpr.UnaryOp.Group.CLOSURE
            && unaryOp.getGroup() != UnaryExpr.UnaryOp.Group.TRANSPOSE) {
          continue;
        }
        if (exprOrFormula instanceof UnaryExpr && unaryOp == ((UnaryExpr) exprOrFormula).getOp()) {
          continue;
        }
        Type type = exprOrFormula.getType();
        if (unaryOp == UnaryExpr.UnaryOp.TRANSPOSE) {
          type = type.transpose();
        }
        UnaryExpr wrapper = new UnaryExpr(parent, type, exprOrFormula, unaryOp);
        parent.setSub(wrapper);
//        String mutant = mu.accept(opt.getPSV(), null);
//        if (AlloyUtil.isValidModel(mutant)) {
        selectedPoints.add(wrapper);
        names.add("UOI");
        backtrack(mu, mutationPoints, i + 1, selectedPoints, names, opt);
        names.remove(names.size() - 1);
        selectedPoints.remove(selectedPoints.size() - 1);
//        }
      }
      parent.setSub(exprOrFormula);
    }
  }

  private static void uor(UnaryExpr unaryExpr, ModelUnit mu, List<Node> mutationPoints, int i,
      List<Node> selectedPoints, List<String> names, HigherOrderMutantGeneratorOpt opt) {
    if (unaryExpr.getParent() instanceof VarDecl
        && unaryExpr.getOp().getGroup() == UnaryExpr.UnaryOp.Group.REL_DECL) {
      return;
    }
    UnaryExpr.UnaryOp original = unaryExpr.getOp();
    for (UnaryExpr.UnaryOp unaryOp : UnaryExpr.UnaryOp.values()) {
      if (unaryOp == original || unaryOp.getGroup() != original.getGroup()) {
        continue;
      }
      unaryExpr.setOp(unaryOp);
//      String mutant = mu.accept(opt.getPSV(), null);
//      if (AlloyUtil.isValidModel(mutant)) {
      selectedPoints.add(unaryExpr);
      names.add("UOR");
      backtrack(mu, mutationPoints, i + 1, selectedPoints, names, opt);
      names.remove(names.size() - 1);
      selectedPoints.remove(selectedPoints.size() - 1);
//      }
    }
    unaryExpr.setOp(original);
  }

  private static void uor(UnaryFormula unaryFormula, ModelUnit mu, List<Node> mutationPoints, int i,
      List<Node> selectedPoints, List<String> names, HigherOrderMutantGeneratorOpt opt) {
    UnaryFormula.UnaryOp original = unaryFormula.getOp();
    for (UnaryFormula.UnaryOp unaryOp : UnaryFormula.UnaryOp.values()) {
      if (unaryOp == original || unaryOp.getGroup() != original.getGroup()) {
        continue;
      }
      unaryFormula.setOp(unaryOp);
//      String mutant = mu.accept(opt.getPSV(), null);
//      if (AlloyUtil.isValidModel(mutant)) {
      selectedPoints.add(unaryFormula);
      names.add("UOR");
      backtrack(mu, mutationPoints, i + 1, selectedPoints, names, opt);
      names.remove(names.size() - 1);
      selectedPoints.remove(selectedPoints.size() - 1);
//      }
    }
    unaryFormula.setOp(original);
  }

  private static void uod(UnaryExpr unaryExpr, ModelUnit mu, List<Node> mutationPoints, int i,
      List<Node> selectedPoints, List<String> names, HigherOrderMutantGeneratorOpt opt) {
    UnaryExpr.UnaryOp original = unaryExpr.getOp();
    if (original.getGroup() == UnaryExpr.UnaryOp.Group.CLOSURE || (
        original.getGroup() == UnaryExpr.UnaryOp.Group.TRANSPOSE && AlloyUtil
            .isHomogeneous(unaryExpr.getType()))) {
      // Parent will be UnaryExpr whose op is NOOP。
      // We never use type so we don't need to set it.
      UnaryExpr parent = (UnaryExpr) unaryExpr.getParent();
      ExprOrFormula sub = unaryExpr.getSub();
      parent.setSub(sub);
      sub.setParent(parent);
//      String mutant = mu.accept(opt.getPSV(), null);
//      if (AlloyUtil.isValidModel(mutant)) {
      // sub could be NOOP UnaryExpr, which should not be the real fault.
      selectedPoints.add(getFirstNonNOOPChild(sub));
      names.add("UOD");
      backtrack(mu, mutationPoints, i + 1, selectedPoints, names, opt);
      names.remove(names.size() - 1);
      selectedPoints.remove(selectedPoints.size() - 1);
//      }
      parent.setSub(unaryExpr);
      sub.setParent(unaryExpr);
    }
  }

  private static void uod(UnaryFormula unaryFormula, ModelUnit mu, List<Node> mutationPoints, int i,
      List<Node> selectedPoints, List<String> names, HigherOrderMutantGeneratorOpt opt) {
    UnaryFormula.UnaryOp original = unaryFormula.getOp();
    if (original == UnaryFormula.UnaryOp.NOT) {
      // Parent will be UnaryExpr whose op is NOOP
      UnaryExpr parent = (UnaryExpr) unaryFormula.getParent();
      ExprOrFormula sub = unaryFormula.getSub();
      parent.setSub(sub);
      sub.setParent(parent);
//      String mutant = mu.accept(opt.getPSV(), null);
//      if (AlloyUtil.isValidModel(mutant)) {
      // sub could be NOOP UnaryExpr, which should not be the real fault.
      selectedPoints.add(getFirstNonNOOPChild(sub));
      names.add("UOD");
      backtrack(mu, mutationPoints, i + 1, selectedPoints, names, opt);
      names.remove(names.size() - 1);
      selectedPoints.remove(selectedPoints.size() - 1);
//      }
      parent.setSub(unaryFormula);
      sub.setParent(unaryFormula);
    }
  }

  private static void bor(BinaryExpr binaryExpr, ModelUnit mu, List<Node> mutationPoints, int i,
      List<Node> selectedPoints, List<String> names, HigherOrderMutantGeneratorOpt opt) {
    BinaryExpr.BinaryOp original = binaryExpr.getOp();
    for (BinaryExpr.BinaryOp binaryOp : BinaryExpr.BinaryOp.values()) {
      if (binaryOp == original || binaryOp.getGroup() != original.getGroup()) {
        continue;
      }
      binaryExpr.setOp(binaryOp);
//      String mutant = mu.accept(opt.getPSV(), null);
//      if (AlloyUtil.isValidModel(mutant)) {
      selectedPoints.add(binaryExpr);
      names.add("BOR");
      backtrack(mu, mutationPoints, i + 1, selectedPoints, names, opt);
      names.remove(names.size() - 1);
      selectedPoints.remove(selectedPoints.size() - 1);
//      }
    }
    binaryExpr.setOp(original);
  }

  private static void bor(BinaryFormula binaryFormula, ModelUnit mu, List<Node> mutationPoints,
      int i, List<Node> selectedPoints, List<String> names, HigherOrderMutantGeneratorOpt opt) {
    BinaryFormula.BinaryOp original = binaryFormula.getOp();
    for (BinaryFormula.BinaryOp binaryOp : BinaryFormula.BinaryOp.values()) {
      if (binaryOp == original) {
        continue;
      }
      if (original == BinaryOp.EQUALS || original == BinaryOp.NOT_EQUALS) {
        // If the original binary operator is "=" or "!=", we also try arithmetic comparisons like
        // "<", "<=", ">", ">=", ..., etc.
        if (binaryFormula.getLeft().getType().is_int() &&
            binaryFormula.getRight().getType().is_int()) {
          if (binaryOp != BinaryOp.EQUALS && binaryOp != BinaryOp.NOT_EQUALS &&
              binaryOp.getGroup() != Group.ARITHMETIC) {
            continue;
          }
        } else if (binaryOp.getGroup() != Group.SET_OPERATION) {
          continue;
        }
      } else if (original.getGroup() == Group.ARITHMETIC) {
        // If the original binary operator is arithmetic operators like "<", ">", etc., then we
        // also try "=" and "!=".
        if (binaryOp != BinaryOp.EQUALS && binaryOp != BinaryOp.NOT_EQUALS &&
            binaryOp.getGroup() != Group.ARITHMETIC) {
          continue;
        }
      } else if (binaryOp.getGroup() != original.getGroup()) {
        continue;
      }
      binaryFormula.setOp(binaryOp);
//      String mutant = mu.accept(opt.getPSV(), null);
//      if (AlloyUtil.isValidModel(mutant)) {
      selectedPoints.add(binaryFormula);
      names.add("BOR");
      backtrack(mu, mutationPoints, i + 1, selectedPoints, names, opt);
      names.remove(names.size() - 1);
      selectedPoints.remove(selectedPoints.size() - 1);
//      }
    }
    binaryFormula.setOp(original);
  }

  private static void boe(BinaryExpr binaryExpr, ModelUnit mu, List<Node> mutationPoints, int i,
      List<Node> selectedPoints, List<String> names, HigherOrderMutantGeneratorOpt opt) {
    if (binaryExpr.getOp().getGroup() != BinaryExpr.BinaryOp.Group.SET_OPERATION || binaryExpr
        .getOp().isCommutative()) {
      return;
    }
    ExprOrFormula originalLeft = binaryExpr.getLeft();
    ExprOrFormula originalRight = binaryExpr.getRight();
    binaryExpr.setLeft(originalRight);
    binaryExpr.setRight(originalLeft);
//    String mutant = mu.accept(opt.getPSV(), null);
//    if (AlloyUtil.isValidModel(mutant)) {
    selectedPoints.add(binaryExpr);
    names.add("BOE");
    backtrack(mu, mutationPoints, i + 1, selectedPoints, names, opt);
    names.remove(names.size() - 1);
    selectedPoints.remove(selectedPoints.size() - 1);
//    }
    binaryExpr.setLeft(originalLeft);
    binaryExpr.setRight(originalRight);
  }

  private static void boe(BinaryFormula binaryFormula, ModelUnit mu, List<Node> mutationPoints,
      int i, List<Node> selectedPoints, List<String> names, HigherOrderMutantGeneratorOpt opt) {
    if (binaryFormula.getOp().getGroup() != BinaryFormula.BinaryOp.Group.SET_OPERATION
        && binaryFormula.getOp().getGroup() != BinaryFormula.BinaryOp.Group.IMPLICATION
        || binaryFormula.getOp().isCommutative()) {
      return;
    }
    ExprOrFormula originalLeft = binaryFormula.getLeft();
    ExprOrFormula originalRight = binaryFormula.getRight();
    binaryFormula.setLeft(originalRight);
    binaryFormula.setRight(originalLeft);
//    String mutant = mu.accept(opt.getPSV(), null);
//    if (AlloyUtil.isValidModel(mutant)) {
    selectedPoints.add(binaryFormula);
    names.add("BOE");
    backtrack(mu, mutationPoints, i + 1, selectedPoints, names, opt);
    names.remove(names.size() - 1);
    selectedPoints.remove(selectedPoints.size() - 1);
//    }
    binaryFormula.setLeft(originalLeft);
    binaryFormula.setRight(originalRight);
  }

  private static void lor(ListFormula listFormula, ModelUnit mu, List<Node> mutationPoints, int i,
      List<Node> selectedPoints, List<String> names, HigherOrderMutantGeneratorOpt opt) {
    ListFormula.ListOp original = listFormula.getOp();
    for (ListFormula.ListOp listOp : ListFormula.ListOp.values()) {
      if (listOp == original) {
        continue;
      }
      boolean canFlattenParent = false;
      List<ExprOrFormula> parentChildren = null;
      List<ExprOrFormula> currentChildren = listFormula.getArguments();
      listFormula.setOp(listOp);
      flattenListFormula(listFormula);
      if (listFormula.getParent() instanceof ListFormula) {
        ListFormula parent = (ListFormula) listFormula.getParent();
        canFlattenParent = canFlatten(parent);
        parentChildren = parent.getArguments();
        flattenListFormula(parent);
      }
//      String mutant = mu.accept(opt.getPSV(), null);
//      if (AlloyUtil.isValidModel(mutant)) {
      selectedPoints.add(canFlattenParent ? listFormula.getParent() : listFormula);
      names.add("LOR");
      backtrack(mu, mutationPoints, i + 1, selectedPoints, names, opt);
      names.remove(names.size() - 1);
      selectedPoints.remove(selectedPoints.size() - 1);
//      }
      revertListFormula(listFormula, original, parentChildren, currentChildren);
    }
  }

  private static void lod(ListFormula listFormula, ModelUnit mu, List<Node> mutationPoints, int i,
      List<Node> selectedPoints, List<String> names, HigherOrderMutantGeneratorOpt opt) {
    List<ExprOrFormula> arguments = listFormula.getArguments();
    int argSize = arguments.size();
    for (int j = 0; j < argSize; j++) {
      ExprOrFormula deletedExprOrFormula = arguments.remove(j);
      if (argSize == 2) {
        Node parent = listFormula.getParent();
        ExprOrFormula leftExprOrFormula = arguments.get(0);
        if (parent instanceof Body) {
          Body p = (Body) parent;
          p.setBodyExpr(leftExprOrFormula);
          selectedPoints.add(parent);
          names.add("LOD");
          backtrack(mu, mutationPoints, i + 1, selectedPoints, names, opt);
          names.remove(names.size() - 1);
          selectedPoints.remove(selectedPoints.size() - 1);
          p.setBodyExpr(listFormula);
          arguments.add(j, deletedExprOrFormula);
        } else if (parent instanceof ListFormula) {
          ListFormula p = (ListFormula) parent;
          int idx = p.getArguments().indexOf(listFormula);
          if (idx == -1) {
            // It is possible that the current formula is no longer valid.
            // E.g. (a || b) && c, we pick "&&" for LOR and "||" for LOD, then "||" is merged with
            // the top level "||" after LOR operation.  In this case, we backtrack and ignore such
            // combination.
            arguments.add(j, deletedExprOrFormula);
            return;
          }
          p.getArguments().set(idx, leftExprOrFormula);
          selectedPoints.add(leftExprOrFormula);
          names.add("LOD");
          backtrack(mu, mutationPoints, i + 1, selectedPoints, names, opt);
          names.remove(names.size() - 1);
          selectedPoints.remove(selectedPoints.size() - 1);
          p.getArguments().set(idx, listFormula);
          arguments.add(j, deletedExprOrFormula);
        } else if (parent instanceof BinaryFormula) {
          BinaryFormula p = (BinaryFormula) parent;
          if (p.getOp() == BinaryOp.IMPLIES || p.getOp() == BinaryOp.IFF) {
            if (p.getLeft() == listFormula) { // Left formula is the current list formula.
              p.setLeft(leftExprOrFormula);
              selectedPoints.add(leftExprOrFormula);
              names.add("LOD");
              backtrack(mu, mutationPoints, i + 1, selectedPoints, names, opt);
              names.remove(names.size() - 1);
              selectedPoints.remove(selectedPoints.size() - 1);
              p.setLeft(listFormula);
              arguments.add(j, deletedExprOrFormula);
            } else { // Right formula is the current list formula.
              p.setRight(leftExprOrFormula);
              selectedPoints.add(leftExprOrFormula);
              names.add("LOD");
              backtrack(mu, mutationPoints, i + 1, selectedPoints, names, opt);
              names.remove(names.size() - 1);
              selectedPoints.remove(selectedPoints.size() - 1);
              p.setRight(listFormula);
              arguments.add(j, deletedExprOrFormula);
            }
          } else {
            throw new RuntimeException(
                "If ListFormula's parent is of type BinaryFormula, "
                    + "then the parent op is either => or <=>.  However, we find " + p.getOp());
          }
        } else if (parent instanceof ITEFormula) {
          ITEFormula p = (ITEFormula) parent;
          if (p.getCondition() == listFormula) {
            // Condition formula is the current list formula.
            p.setCondition(leftExprOrFormula);
            selectedPoints.add(leftExprOrFormula);
            names.add("LOD");
            backtrack(mu, mutationPoints, i + 1, selectedPoints, names, opt);
            names.remove(names.size() - 1);
            selectedPoints.remove(selectedPoints.size() - 1);
            p.setCondition(listFormula);
            arguments.add(j, deletedExprOrFormula);
          } else if (p.getThenClause() == listFormula) {
            // Then formula is the current list formula.
            p.setThenClause(leftExprOrFormula);
            selectedPoints.add(leftExprOrFormula);
            names.add("LOD");
            backtrack(mu, mutationPoints, i + 1, selectedPoints, names, opt);
            names.remove(names.size() - 1);
            selectedPoints.remove(selectedPoints.size() - 1);
            p.setThenClause(listFormula);
            arguments.add(j, deletedExprOrFormula);
          } else {
            // Else formula is the current list formula.
            p.setElseClause(leftExprOrFormula);
            selectedPoints.add(leftExprOrFormula);
            names.add("LOD");
            backtrack(mu, mutationPoints, i + 1, selectedPoints, names, opt);
            names.remove(names.size() - 1);
            selectedPoints.remove(selectedPoints.size() - 1);
            p.setElseClause(listFormula);
            arguments.add(j, deletedExprOrFormula);
          }
        } else {
          throw new RuntimeException(
              "ListFormula's parent should never be of type " + parent.getClass());
        }
      } else { // Can safely delete any argument because the number of arguments is > 2.
        selectedPoints.add(listFormula);
        names.add("LOD");
        backtrack(mu, mutationPoints, i + 1, selectedPoints, names, opt);
        names.remove(names.size() - 1);
        selectedPoints.remove(selectedPoints.size() - 1);
        arguments.add(j, deletedExprOrFormula);
      }
    }
  }

  private static void qor(QtFormula qtFormula, ModelUnit mu, List<Node> mutationPoints, int i,
      List<Node> selectedPoints, List<String> names, HigherOrderMutantGeneratorOpt opt) {
    QtFormula.Quantifier original = qtFormula.getOp();
    for (QtFormula.Quantifier qt : QtFormula.Quantifier.values()) {
      if (qt == original) {
        continue;
      }
      qtFormula.setOp(qt);
//      String mutant = mu.accept(opt.getPSV(), null);
//      if (AlloyUtil.isValidModel(mutant)) {
      selectedPoints.add(qtFormula);
      names.add("QOR");
      backtrack(mu, mutationPoints, i + 1, selectedPoints, names, opt);
      names.remove(names.size() - 1);
      selectedPoints.remove(selectedPoints.size() - 1);
//      }
    }
    qtFormula.setOp(original);
  }

  private static void ieoe(ITEExprOrFormula iteExpr, ModelUnit mu, List<Node> mutationPoints, int i,
      List<Node> selectedPoints, List<String> names, HigherOrderMutantGeneratorOpt opt) {
    ExprOrFormula originalThenClause = iteExpr.getThenClause();
    ExprOrFormula originalElseClause = iteExpr.getElseClause();
    iteExpr.setThenClause(originalElseClause);
    iteExpr.setElseClause(originalThenClause);
//    String mutant = mu.accept(opt.getPSV(), null);
//    if (AlloyUtil.isValidModel(mutant)) {
    selectedPoints.add(iteExpr);
    names.add("IEOE");
    backtrack(mu, mutationPoints, i + 1, selectedPoints, names, opt);
    names.remove(names.size() - 1);
    selectedPoints.remove(selectedPoints.size() - 1);
//    }
    iteExpr.setThenClause(originalThenClause);
    iteExpr.setElseClause(originalElseClause);
  }

  private static void pbd(Body body, ModelUnit mu, List<Node> mutationPoints, int i,
      List<Node> selectedPoints, List<String> names, HigherOrderMutantGeneratorOpt opt) {
    if (!(body.getParent() instanceof Predicate) && !(body.getParent() instanceof Fact)) {
      return;
    }
    ExprOrFormula original = body.getBodyExpr();
    body.setBodyExpr(new ConstExpr(body));
    selectedPoints.add(body);
    names.add("PBD");
    backtrack(mu, mutationPoints, i + 1, selectedPoints, names, opt);
    names.remove(names.size() - 1);
    selectedPoints.remove(selectedPoints.size() - 1);
    body.setBodyExpr(original);
  }
}
