package arepair.generator.util.rules.intersect;

import static arepair.generator.etc.Contants.AMP;

import arepair.generator.util.rules.BinaryInfo;
import arepair.generator.util.rules.BinaryRule;

public class IRule1 extends BinaryRule {

    private IRule1(BinaryInfo binaryInfo) {
        super(binaryInfo);
    }

    public static IRule1 given(BinaryInfo binaryInfo) {
        return new IRule1(binaryInfo);
    }

    @Override
    public boolean isPruned() {
        return leftRel.getOp().equals(AMP);
    }
}
