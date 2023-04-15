package arepair.generator.util.rules.difference;

import static arepair.generator.etc.Contants.CARET;
import static arepair.generator.etc.Contants.STAR;

import arepair.generator.util.rules.BinaryInfo;
import arepair.generator.util.rules.BinaryRule;

public class DRule8 extends BinaryRule {

    private DRule8(BinaryInfo binaryInfo) {
        super(binaryInfo);
    }

    public static DRule8 given(BinaryInfo binaryInfo) {
        return new DRule8(binaryInfo);
    }

    @Override
    public boolean isPruned() {
        return opIsOr(leftRel.getOp(), STAR, CARET) && opIsOr(rightRel.getOp(), STAR, CARET)
        // Remove the case where *a - ^a
               && (!opIsOr(leftRel.getOp(), STAR) || !opIsOr(rightRel.getOp(), CARET)) && sameRelations(getChild(leftRel, 0), getChild(rightRel, 0));
    }
}
