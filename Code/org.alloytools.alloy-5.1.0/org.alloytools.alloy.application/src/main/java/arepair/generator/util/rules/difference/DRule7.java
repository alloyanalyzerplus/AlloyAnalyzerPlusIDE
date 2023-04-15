package arepair.generator.util.rules.difference;

import static arepair.generator.etc.Contants.CARET;
import static arepair.generator.etc.Contants.DOT;
import static arepair.generator.etc.Contants.STAR;

import arepair.generator.util.rules.BinaryInfo;
import arepair.generator.util.rules.BinaryRule;

public class DRule7 extends BinaryRule {

    private DRule7(BinaryInfo binaryInfo) {
        super(binaryInfo);
    }

    public static DRule7 given(BinaryInfo binaryInfo) {
        return new DRule7(binaryInfo);
    }

    @Override
    public boolean isPruned() {
        return opIsOr(rightRel.getOp(), STAR, CARET) && getChild(rightRel, 0).equals(duplicateNodesUnderOps(leftRel, DOT));
    }
}
