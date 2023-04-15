package mualloy.rule;

import java.util.ArrayList;
import java.util.List;
import mualloy.util.MInfo;
import mualloy.util.MutantEquivalenceChecker;
import parser.ast.nodes.BinaryExpr;
import parser.ast.nodes.BinaryFormula;
import parser.ast.nodes.BinaryFormula.BinaryOp;
import parser.ast.nodes.BinaryFormula.BinaryOp.Group;
import parser.ast.nodes.ModelUnit;
import parser.ast.nodes.Paragraph;
import parser.ast.nodes.PredOrFun;
import parser.ast.visitor.CloneVisitor;
import parser.etc.MutationData;
import parser.exception.UnsupportedOptionException;
import parser.opt.Opt;
import parser.util.AlloyUtil;

/**
 * Binary Operator Replacement.
 */
public class BOR {

  public static List<MutationData> mutate(BinaryExpr binaryExpr, ModelUnit mu, Opt opt, MInfo mi) {
    List<MutationData> res = new ArrayList<>();
    if (mi.getType() == MInfo.MType.IGNORE) {
      return res;
    }
    BinaryExpr.BinaryOp original = binaryExpr.getOp();
    for (BinaryExpr.BinaryOp binaryOp : BinaryExpr.BinaryOp.values()) {
      // We allow converting set union to arithmetic plus and set difference to arithmetic minus.
      if (binaryOp == original || (binaryOp.getGroup() != original.getGroup() && !(
          original == BinaryExpr.BinaryOp.PLUS && binaryOp == BinaryExpr.BinaryOp.IPLUS) && !(
          original == BinaryExpr.BinaryOp.MINUS && binaryOp == BinaryExpr.BinaryOp.IMINUS))) {
        continue;
      }
      binaryExpr.setOp(binaryOp);
//            String mutatedPartAsString = binaryExpr.accept(opt.getPSV(), null);
      String mutatedNodePathAsString = AlloyUtil.computeNodePathAsString(binaryExpr, opt.getPSV());
      String mutant = mu.accept(opt.getPSV(), null);
      if (AlloyUtil.isValidModel(mutant)) {
        String equivModel;
        switch (mi.getType()) {
          case PRED:
          case FUN:
            PredOrFun mutatedPredOrFun = (PredOrFun) mi.getNode().accept(new CloneVisitor(), null);
            binaryExpr.setOp(original);
            equivModel = MutantEquivalenceChecker
                .buildEquivModel(mu, mutatedPredOrFun, opt.getScope());
            break;
          case FACT:
          case ASSERT:
            Paragraph mutatedFactOrAssertion = (Paragraph) mi.getNode()
                .accept(new CloneVisitor(), null);
            binaryExpr.setOp(original);
            equivModel = MutantEquivalenceChecker
                .buildEquivModel(mu, mutatedFactOrAssertion, opt.getScope());
            break;
          default:
            throw new UnsupportedOptionException(
                BOR.class.getSimpleName() + " for expression is not supported in " + mi.getType());
        }
        res.add(MutationData.of(mutatedNodePathAsString, mutant, MutantEquivalenceChecker
            .checkEquivalenceAndGenerateTest(equivModel, mi.getNode(), opt)));
      }
    }
    binaryExpr.setOp(original);
    return res;
  }

  public static List<MutationData> mutate(BinaryFormula binaryFormula, ModelUnit mu, Opt opt,
      MInfo mi) {
    List<MutationData> res = new ArrayList<>();
    if (mi.getType() == MInfo.MType.IGNORE) {
      return res;
    }
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
//            String mutatedPartAsString = binaryFormula.accept(opt.getPSV(), null);
      String mutatedNodePathAsString = AlloyUtil
          .computeNodePathAsString(binaryFormula, opt.getPSV());
      String mutant = mu.accept(opt.getPSV(), null);
      if (AlloyUtil.isValidModel(mutant)) {
        String equivModel;
        switch (mi.getType()) {
          case PRED:
          case FUN:
            PredOrFun mutatedPredOrFun = (PredOrFun) mi.getNode().accept(new CloneVisitor(), null);
            binaryFormula.setOp(original);
            equivModel = MutantEquivalenceChecker
                .buildEquivModel(mu, mutatedPredOrFun, opt.getScope());
            break;
          case FACT:
          case ASSERT:
            Paragraph mutatedFactOrAssertion = (Paragraph) mi.getNode()
                .accept(new CloneVisitor(), null);
            binaryFormula.setOp(original);
            equivModel = MutantEquivalenceChecker
                .buildEquivModel(mu, mutatedFactOrAssertion, opt.getScope());
            break;
          default:
            throw new UnsupportedOptionException(
                BOR.class.getSimpleName() + " for formula is not supported in " + mi.getType());
        }
        res.add(MutationData.of(mutatedNodePathAsString, mutant, MutantEquivalenceChecker
            .checkEquivalenceAndGenerateTest(equivModel, mi.getNode(), opt)));
      } else {
        System.out.println(mutant);
        System.out.println(
            "Left Int: " + binaryFormula.getLeft().getType().is_int() + ", Right Int: "
                + binaryFormula.getRight().getType().is_int());
      }
    }
    binaryFormula.setOp(original);
    return res;
  }
}
