package mualloy.rule;

import static parser.util.AlloyUtil.computeNodePathAsString;

import java.util.ArrayList;
import java.util.List;
import mualloy.util.MInfo;
import mualloy.util.MutantEquivalenceChecker;
import parser.ast.nodes.BinaryFormula;
import parser.ast.nodes.BinaryFormula.BinaryOp;
import parser.ast.nodes.Body;
import parser.ast.nodes.ExprOrFormula;
import parser.ast.nodes.ITEFormula;
import parser.ast.nodes.ListFormula;
import parser.ast.nodes.ModelUnit;
import parser.ast.nodes.Node;
import parser.ast.nodes.Paragraph;
import parser.ast.nodes.PredOrFun;
import parser.ast.nodes.UnaryFormula;
import parser.ast.nodes.UnaryFormula.UnaryOp;
import parser.ast.visitor.CloneVisitor;
import parser.etc.MutationData;
import parser.exception.UnsupportedOptionException;
import parser.opt.Opt;
import parser.util.AlloyUtil;

/**
 * List Operand Deletion.
 */
public class LOD {

  public static List<MutationData> mutate(ListFormula listFormula, ModelUnit mu, Opt opt,
      MInfo mi) {
    List<MutationData> res = new ArrayList<>();
    if (mi.getType() == MInfo.MType.IGNORE) {
      return res;
    }
    List<ExprOrFormula> arguments = listFormula.getArguments();
    int argSize = arguments.size();
    for (int i = 0; i < argSize; i++) {
      ExprOrFormula deletedExprOrFormula = arguments.remove(i);
      if (argSize == 2) {
        Node parent = listFormula.getParent();
        ExprOrFormula leftExprOrFormula = arguments.get(0);
        if (parent instanceof Body) {
          Body p = (Body) parent;
          p.setBodyExpr(leftExprOrFormula);
          // We assume the fault is at the body after mutation.
          String mutatedNodePathAsString = computeNodePathAsString(parent, opt.getPSV());
          String mutant = mu.accept(opt.getPSV(), null);
          if (AlloyUtil.isValidModel(mutant)) {
            String equivModel;
            switch (mi.getType()) {
              case PRED:
              case FUN:
                PredOrFun mutatedPredOrFun = (PredOrFun) mi.getNode()
                    .accept(new CloneVisitor(), null);
                p.setBodyExpr(listFormula);
                arguments.add(i, deletedExprOrFormula);
                equivModel = MutantEquivalenceChecker
                    .buildEquivModel(mu, mutatedPredOrFun, opt.getScope());
                break;
              case FACT:
              case ASSERT:
                Paragraph mutatedFactOrAssertion = (Paragraph) mi.getNode()
                    .accept(new CloneVisitor(), null);
                p.setBodyExpr(listFormula);
                arguments.add(i, deletedExprOrFormula);
                equivModel = MutantEquivalenceChecker
                    .buildEquivModel(mu, mutatedFactOrAssertion, opt.getScope());
                break;
              default:
                throw new UnsupportedOptionException(
                    LOD.class.getSimpleName() + " for formula is not supported in " + mi.getType());
            }
            res.add(MutationData.of(mutatedNodePathAsString, mutant, MutantEquivalenceChecker
                .checkEquivalenceAndGenerateTest(equivModel, mi.getNode(), opt)));
          } else { // Revert if the model does not compile.  This should not happen.
            p.setBodyExpr(listFormula);
            arguments.add(i, deletedExprOrFormula);
          }
        } else if (parent instanceof ListFormula) {
          ListFormula p = (ListFormula) parent;
          int idx = p.getArguments().indexOf(listFormula);
          p.getArguments().set(idx, leftExprOrFormula);
          // We assume the fault is at the left subformula because we know the left subformula
          // should be enhanced with the deleted subformula.
          String mutatedNodePathAsString = computeNodePathAsString(leftExprOrFormula, opt.getPSV());
          String mutant = mu.accept(opt.getPSV(), null);
          if (AlloyUtil.isValidModel(mutant)) {
            String equivModel;
            switch (mi.getType()) {
              case PRED:
              case FUN:
                PredOrFun mutatedPredOrFun = (PredOrFun) mi.getNode()
                    .accept(new CloneVisitor(), null);
                p.getArguments().set(idx, listFormula);
                arguments.add(i, deletedExprOrFormula);
                equivModel = MutantEquivalenceChecker
                    .buildEquivModel(mu, mutatedPredOrFun, opt.getScope());
                break;
              case FACT:
              case ASSERT:
                Paragraph mutatedFactOrAssertion = (Paragraph) mi.getNode()
                    .accept(new CloneVisitor(), null);
                p.getArguments().set(idx, listFormula);
                arguments.add(i, deletedExprOrFormula);
                equivModel = MutantEquivalenceChecker
                    .buildEquivModel(mu, mutatedFactOrAssertion, opt.getScope());
                break;
              default:
                throw new UnsupportedOptionException(
                    LOD.class.getSimpleName() + " for formula is not supported in " + mi.getType());
            }
            res.add(MutationData.of(mutatedNodePathAsString, mutant, MutantEquivalenceChecker
                .checkEquivalenceAndGenerateTest(equivModel, mi.getNode(), opt)));
          } else { // Revert if the model does not compile.  This should not happen.
            p.getArguments().set(idx, listFormula);
            arguments.add(i, deletedExprOrFormula);
          }
        } else if (parent instanceof BinaryFormula) {
          BinaryFormula p = (BinaryFormula) parent;
          if (p.getOp() == BinaryOp.IMPLIES || p.getOp() == BinaryOp.IFF) {
            if (p.getLeft() == listFormula) { // Left formula is the current list formula.
              p.setLeft(leftExprOrFormula);
              // We assume the fault is at the left subformula.
              String mutatedNodePathAsString = computeNodePathAsString(leftExprOrFormula,
                  opt.getPSV());
              String mutant = mu.accept(opt.getPSV(), null);
              if (AlloyUtil.isValidModel(mutant)) {
                String equivModel;
                switch (mi.getType()) {
                  case PRED:
                  case FUN:
                    PredOrFun mutatedPredOrFun = (PredOrFun) mi.getNode()
                        .accept(new CloneVisitor(), null);
                    p.setLeft(listFormula);
                    arguments.add(i, deletedExprOrFormula);
                    equivModel = MutantEquivalenceChecker
                        .buildEquivModel(mu, mutatedPredOrFun, opt.getScope());
                    break;
                  case FACT:
                  case ASSERT:
                    Paragraph mutatedFactOrAssertion = (Paragraph) mi.getNode()
                        .accept(new CloneVisitor(), null);
                    p.setLeft(listFormula);
                    arguments.add(i, deletedExprOrFormula);
                    equivModel = MutantEquivalenceChecker
                        .buildEquivModel(mu, mutatedFactOrAssertion, opt.getScope());
                    break;
                  default:
                    throw new UnsupportedOptionException(
                        LOD.class.getSimpleName() + " for formula is not supported in " + mi
                            .getType());
                }
                res.add(MutationData.of(mutatedNodePathAsString, mutant, MutantEquivalenceChecker
                    .checkEquivalenceAndGenerateTest(equivModel, mi.getNode(), opt)));
              } else { // Revert if the model does not compile.  This should not happen.
                p.setLeft(listFormula);
                arguments.add(i, deletedExprOrFormula);
              }
            } else { // Right formula is the current list formula.
              p.setRight(leftExprOrFormula);
              // We assume the fault is at the left subformula.
              String mutatedNodePathAsString = computeNodePathAsString(leftExprOrFormula,
                  opt.getPSV());
              String mutant = mu.accept(opt.getPSV(), null);
              if (AlloyUtil.isValidModel(mutant)) {
                String equivModel;
                switch (mi.getType()) {
                  case PRED:
                  case FUN:
                    PredOrFun mutatedPredOrFun = (PredOrFun) mi.getNode()
                        .accept(new CloneVisitor(), null);
                    p.setRight(listFormula);
                    arguments.add(i, deletedExprOrFormula);
                    equivModel = MutantEquivalenceChecker
                        .buildEquivModel(mu, mutatedPredOrFun, opt.getScope());
                    break;
                  case FACT:
                  case ASSERT:
                    Paragraph mutatedFactOrAssertion = (Paragraph) mi.getNode()
                        .accept(new CloneVisitor(), null);
                    p.setRight(listFormula);
                    arguments.add(i, deletedExprOrFormula);
                    equivModel = MutantEquivalenceChecker
                        .buildEquivModel(mu, mutatedFactOrAssertion, opt.getScope());
                    break;
                  default:
                    throw new UnsupportedOptionException(
                        LOD.class.getSimpleName() + " for formula is not supported in " + mi
                            .getType());
                }
                res.add(MutationData.of(mutatedNodePathAsString, mutant, MutantEquivalenceChecker
                    .checkEquivalenceAndGenerateTest(equivModel, mi.getNode(), opt)));
              } else { // Revert if the model does not compile.  This should not happen.
                p.setRight(listFormula);
                arguments.add(i, deletedExprOrFormula);
              }
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
            // We assume the fault is at the left subformula.
            String mutatedNodePathAsString = computeNodePathAsString(leftExprOrFormula,
                opt.getPSV());
            String mutant = mu.accept(opt.getPSV(), null);
            if (AlloyUtil.isValidModel(mutant)) {
              String equivModel;
              switch (mi.getType()) {
                case PRED:
                case FUN:
                  PredOrFun mutatedPredOrFun = (PredOrFun) mi.getNode()
                      .accept(new CloneVisitor(), null);
                  p.setCondition(listFormula);
                  arguments.add(i, deletedExprOrFormula);
                  equivModel = MutantEquivalenceChecker
                      .buildEquivModel(mu, mutatedPredOrFun, opt.getScope());
                  break;
                case FACT:
                case ASSERT:
                  Paragraph mutatedFactOrAssertion = (Paragraph) mi.getNode()
                      .accept(new CloneVisitor(), null);
                  p.setCondition(listFormula);
                  arguments.add(i, deletedExprOrFormula);
                  equivModel = MutantEquivalenceChecker
                      .buildEquivModel(mu, mutatedFactOrAssertion, opt.getScope());
                  break;
                default:
                  throw new UnsupportedOptionException(
                      LOD.class.getSimpleName() + " for formula is not supported in " + mi
                          .getType());
              }
              res.add(MutationData.of(mutatedNodePathAsString, mutant, MutantEquivalenceChecker
                  .checkEquivalenceAndGenerateTest(equivModel, mi.getNode(), opt)));
            } else { // Revert if the model does not compile.  This should not happen.
              p.setCondition(listFormula);
              arguments.add(i, deletedExprOrFormula);
            }
          } else if (p.getThenClause() == listFormula) {
            // Then formula is the current list formula.
            p.setThenClause(leftExprOrFormula);
            // We assume the fault is at the left subformula.
            String mutatedNodePathAsString = computeNodePathAsString(leftExprOrFormula,
                opt.getPSV());
            String mutant = mu.accept(opt.getPSV(), null);
            if (AlloyUtil.isValidModel(mutant)) {
              String equivModel;
              switch (mi.getType()) {
                case PRED:
                case FUN:
                  PredOrFun mutatedPredOrFun = (PredOrFun) mi.getNode()
                      .accept(new CloneVisitor(), null);
                  p.setThenClause(listFormula);
                  arguments.add(i, deletedExprOrFormula);
                  equivModel = MutantEquivalenceChecker
                      .buildEquivModel(mu, mutatedPredOrFun, opt.getScope());
                  break;
                case FACT:
                case ASSERT:
                  Paragraph mutatedFactOrAssertion = (Paragraph) mi.getNode()
                      .accept(new CloneVisitor(), null);
                  p.setThenClause(listFormula);
                  arguments.add(i, deletedExprOrFormula);
                  equivModel = MutantEquivalenceChecker
                      .buildEquivModel(mu, mutatedFactOrAssertion, opt.getScope());
                  break;
                default:
                  throw new UnsupportedOptionException(
                      LOD.class.getSimpleName() + " for formula is not supported in " + mi
                          .getType());
              }
              res.add(MutationData.of(mutatedNodePathAsString, mutant, MutantEquivalenceChecker
                  .checkEquivalenceAndGenerateTest(equivModel, mi.getNode(), opt)));
            } else { // Revert if the model does not compile.  This should not happen.
              p.setThenClause(listFormula);
              arguments.add(i, deletedExprOrFormula);
            }
          } else {
            // Else formula is the current list formula.
            p.setElseClause(leftExprOrFormula);
            // We assume the fault is at the left subformula.
            String mutatedNodePathAsString = computeNodePathAsString(leftExprOrFormula,
                opt.getPSV());
            String mutant = mu.accept(opt.getPSV(), null);
            if (AlloyUtil.isValidModel(mutant)) {
              String equivModel;
              switch (mi.getType()) {
                case PRED:
                case FUN:
                  PredOrFun mutatedPredOrFun = (PredOrFun) mi.getNode()
                      .accept(new CloneVisitor(), null);
                  p.setElseClause(listFormula);
                  arguments.add(i, deletedExprOrFormula);
                  equivModel = MutantEquivalenceChecker
                      .buildEquivModel(mu, mutatedPredOrFun, opt.getScope());
                  break;
                case FACT:
                case ASSERT:
                  Paragraph mutatedFactOrAssertion = (Paragraph) mi.getNode()
                      .accept(new CloneVisitor(), null);
                  p.setElseClause(listFormula);
                  arguments.add(i, deletedExprOrFormula);
                  equivModel = MutantEquivalenceChecker
                      .buildEquivModel(mu, mutatedFactOrAssertion, opt.getScope());
                  break;
                default:
                  throw new UnsupportedOptionException(
                      LOD.class.getSimpleName() + " for formula is not supported in " + mi
                          .getType());
              }
              res.add(MutationData.of(mutatedNodePathAsString, mutant, MutantEquivalenceChecker
                  .checkEquivalenceAndGenerateTest(equivModel, mi.getNode(), opt)));
            } else { // Revert if the model does not compile.  This should not happen.
              p.setElseClause(listFormula);
              arguments.add(i, deletedExprOrFormula);
            }
          }
        } else if (parent instanceof UnaryFormula
            && ((UnaryFormula) parent).getOp() == UnaryOp.NOT) {
          UnaryFormula p = (UnaryFormula) parent;
          p.setSub(leftExprOrFormula);
          // We assume the fault is at the body after mutation.
          String mutatedNodePathAsString = computeNodePathAsString(parent, opt.getPSV());
          String mutant = mu.accept(opt.getPSV(), null);
          if (AlloyUtil.isValidModel(mutant)) {
            String equivModel;
            switch (mi.getType()) {
              case PRED:
              case FUN:
                PredOrFun mutatedPredOrFun = (PredOrFun) mi.getNode()
                    .accept(new CloneVisitor(), null);
                p.setSub(listFormula);
                arguments.add(i, deletedExprOrFormula);
                equivModel = MutantEquivalenceChecker
                    .buildEquivModel(mu, mutatedPredOrFun, opt.getScope());
                break;
              case FACT:
              case ASSERT:
                Paragraph mutatedFactOrAssertion = (Paragraph) mi.getNode()
                    .accept(new CloneVisitor(), null);
                p.setSub(listFormula);
                arguments.add(i, deletedExprOrFormula);
                equivModel = MutantEquivalenceChecker
                    .buildEquivModel(mu, mutatedFactOrAssertion, opt.getScope());
                break;
              default:
                throw new UnsupportedOptionException(
                    LOD.class.getSimpleName() + " for formula is not supported in " + mi.getType());
            }
            res.add(MutationData.of(mutatedNodePathAsString, mutant, MutantEquivalenceChecker
                .checkEquivalenceAndGenerateTest(equivModel, mi.getNode(), opt)));
          } else { // Revert if the model does not compile.  This should not happen.
            p.setSub(listFormula);
            arguments.add(i, deletedExprOrFormula);
          }
        } else {
          throw new RuntimeException(
              "ListFormula's parent should never be of type " + parent.getClass());
        }
      } else { // Can safely delete any argument because the number of arguments is > 2.
        String mutatedNodePathAsString = computeNodePathAsString(listFormula, opt.getPSV());
        String mutant = mu.accept(opt.getPSV(), null);
        if (AlloyUtil.isValidModel(mutant)) {
          String equivModel;
          switch (mi.getType()) {
            case PRED:
            case FUN:
              PredOrFun mutatedPredOrFun = (PredOrFun) mi.getNode()
                  .accept(new CloneVisitor(), null);
              arguments.add(i, deletedExprOrFormula);
              equivModel = MutantEquivalenceChecker
                  .buildEquivModel(mu, mutatedPredOrFun, opt.getScope());
              break;
            case FACT:
            case ASSERT:
              Paragraph mutatedFactOrAssertion = (Paragraph) mi.getNode()
                  .accept(new CloneVisitor(), null);
              arguments.add(i, deletedExprOrFormula);
              equivModel = MutantEquivalenceChecker
                  .buildEquivModel(mu, mutatedFactOrAssertion, opt.getScope());
              break;
            default:
              throw new UnsupportedOptionException(
                  LOD.class.getSimpleName() + " for formula is not supported in " + mi.getType());
          }
          res.add(MutationData.of(mutatedNodePathAsString, mutant, MutantEquivalenceChecker
              .checkEquivalenceAndGenerateTest(equivModel, mi.getNode(), opt)));
        } else { // Revert if the model does not compile.  This should not happen.
          arguments.add(i, deletedExprOrFormula);
        }
      }
    }
    return res;
  }
}
