package arepair.generator.util.rules.union;

import static arepair.generator.etc.Contants.CARET;
import static arepair.generator.etc.Contants.STAR;

import arepair.generator.util.rules.BinaryInfo;
import arepair.generator.util.rules.BinaryRule;

public class URule5 extends BinaryRule {

    private URule5(BinaryInfo binaryInfo) {
        super(binaryInfo);
    }

    public static URule5 given(BinaryInfo binaryInfo) {
        return new URule5(binaryInfo);
    }

    @Override
    public boolean isPruned() {
        return opIsOr(leftRel.getOp(), STAR, CARET) && opIsOr(rightRel.getOp(), STAR, CARET) && sameRelations(getChild(leftRel, 0), getChild(rightRel, 0));
    }
}
