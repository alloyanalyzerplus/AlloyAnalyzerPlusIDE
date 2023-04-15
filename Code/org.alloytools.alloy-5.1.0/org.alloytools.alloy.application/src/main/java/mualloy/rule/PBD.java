package mualloy.rule;

import mualloy.util.MInfo;
import mualloy.util.MutantEquivalenceChecker;
import parser.ast.nodes.Body;
import parser.ast.nodes.ConstExpr;
import parser.ast.nodes.ExprOrFormula;
import parser.ast.nodes.Fact;
import parser.ast.nodes.ModelUnit;
import parser.ast.nodes.Paragraph;
import parser.ast.nodes.PredOrFun;
import parser.ast.nodes.Predicate;
import parser.ast.visitor.CloneVisitor;
import parser.etc.MutationData;
import parser.exception.UnsupportedOptionException;
import parser.opt.Opt;
import parser.util.AlloyUtil;

/**
 * Paragraph Body Deletion.
 */
public class PBD {

  public static MutationData mutate(Body body, ModelUnit mu, Opt opt, MInfo mi) {
    MutationData res = null;
    if (mi.getType() == MInfo.MType.IGNORE) {
      return res;
    }
    if (!(body.getParent() instanceof Predicate) && !(body.getParent() instanceof Fact)) {
      return res;
    }
    ExprOrFormula original = body.getBodyExpr();
    body.setBodyExpr(new ConstExpr(body));
    String mutatedNodePathAsString = AlloyUtil.computeNodePathAsString(body, opt.getPSV());
    String mutant = mu.accept(opt.getPSV(), null);
    if (AlloyUtil.isValidModel(mutant)) {
      String equivModel;
      switch (mi.getType()) {
        case PRED:
        case FUN:
          PredOrFun mutatedPredOrFun = (PredOrFun) mi.getNode().accept(new CloneVisitor(), null);
          body.setBodyExpr(original);
          equivModel = MutantEquivalenceChecker
              .buildEquivModel(mu, mutatedPredOrFun, opt.getScope());
          break;
        case FACT:
        case ASSERT:
          Paragraph mutatedFactOrAssertion = (Paragraph) mi.getNode()
              .accept(new CloneVisitor(), null);
          body.setBodyExpr(original);
          equivModel = MutantEquivalenceChecker
              .buildEquivModel(mu, mutatedFactOrAssertion, opt.getScope());
          break;
        default:
          throw new UnsupportedOptionException(
              PBD.class.getSimpleName() + " for body is not supported in " + mi.getType());
      }
      res = MutationData.of(mutatedNodePathAsString, mutant, MutantEquivalenceChecker
          .checkEquivalenceAndGenerateTest(equivModel, mi.getNode(), opt));
    }
    body.setBodyExpr(original);
    return res;
  }
}
