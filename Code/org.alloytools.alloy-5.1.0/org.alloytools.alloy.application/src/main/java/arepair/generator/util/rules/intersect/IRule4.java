package arepair.generator.util.rules.intersect;

import static arepair.generator.etc.Contants.ARROW;

import arepair.generator.util.rules.BinaryInfo;
import arepair.generator.util.rules.BinaryRule;

public class IRule4 extends BinaryRule {

    private IRule4(BinaryInfo binaryInfo) {
        super(binaryInfo);
    }

    public static IRule4 given(BinaryInfo binaryInfo) {
        return new IRule4(binaryInfo);
    }

    @Override
    public boolean isPruned() {
        return opIsOr(leftRel.getOp(), ARROW) && opIsOr(rightRel.getOp(), ARROW) && (sameRelations(getChild(leftRel, 0), getChild(rightRel, 0)) || sameRelations(getChild(leftRel, 1), getChild(rightRel, 1)));
    }
}
