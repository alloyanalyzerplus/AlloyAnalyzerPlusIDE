package arepair.generator.util.rules.difference;

import static arepair.generator.etc.Contants.AMP;

import arepair.generator.util.rules.BinaryInfo;
import arepair.generator.util.rules.BinaryRule;

public class DRule11 extends BinaryRule {

    private DRule11(BinaryInfo binaryInfo) {
        super(binaryInfo);
    }

    public static DRule11 given(BinaryInfo binaryInfo) {
        return new DRule11(binaryInfo);
    }

    @Override
    public boolean isPruned() {
        return opIsOr(leftRel.getOp(), AMP);
    }
}
