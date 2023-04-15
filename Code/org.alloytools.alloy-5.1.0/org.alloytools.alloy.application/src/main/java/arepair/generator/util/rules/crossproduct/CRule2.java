package arepair.generator.util.rules.crossproduct;

import static arepair.generator.etc.Contants.DOT;

import arepair.generator.util.rules.BinaryInfo;
import arepair.generator.util.rules.BinaryRule;

public class CRule2 extends BinaryRule {

    private CRule2(BinaryInfo binaryInfo) {
        super(binaryInfo);
    }

    public static CRule2 given(BinaryInfo binaryInfo) {
        return new CRule2(binaryInfo);
    }

    @Override
    public boolean isPruned() {
        return opIsOr(leftRel.getOp(), DOT) || opIsOr(rightRel.getOp(), DOT);
    }
}
