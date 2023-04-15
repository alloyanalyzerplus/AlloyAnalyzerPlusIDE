package arepair.generator.util.rules.join;

import static arepair.generator.etc.Card.ONE;
import static arepair.generator.etc.Card.SOME;
import static arepair.generator.etc.Contants.ARROW;

import arepair.generator.fragment.Expression;
import arepair.generator.util.rules.BinaryInfo;
import arepair.generator.util.rules.BinaryRule;

public class JRule2 extends BinaryRule {

    private JRule2(BinaryInfo binaryInfo) {
        super(binaryInfo);
    }

    public static JRule2 given(BinaryInfo binaryInfo) {
        return new JRule2(binaryInfo);
    }

    @Override
    public boolean isPruned() {
        if (opIsOr(leftRel.getOp(), ARROW)) {
            Expression leftSubRel = getChild(leftRel, 1);
            if (sameRelations(leftSubRel, rightRel) && rightRel.getArity() == 1 && (rightRel.getCards().get(0).equals(ONE) || rightRel.getCards().get(0).equals(SOME))) {
                return true;
            }
        }
        if (opIsOr(rightRel.getOp(), ARROW)) {
            Expression rightSubRel = getChild(rightRel, 0);
            if (sameRelations(leftRel, rightSubRel) && leftRel.getArity() == 1 && (leftRel.getCards().get(0).equals(ONE) || leftRel.getCards().get(0).equals(SOME))) {
                return true;
            }
        }
        return false;
    }
}
