package arepair.generator.util.rules.difference;

import static arepair.generator.etc.Contants.PLUS;

import arepair.generator.util.rules.BinaryInfo;
import arepair.generator.util.rules.BinaryRule;

public class DRule3 extends BinaryRule {

    private DRule3(BinaryInfo binaryInfo) {
        super(binaryInfo);
    }

    public static DRule3 given(BinaryInfo binaryInfo) {
        return new DRule3(binaryInfo);
    }

    @Override
    public boolean isPruned() {
        return opIsOr(rightRel.getOp(), PLUS);
    }
}
