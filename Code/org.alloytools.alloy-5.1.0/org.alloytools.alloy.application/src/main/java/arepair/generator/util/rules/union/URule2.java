package arepair.generator.util.rules.union;

import static arepair.generator.util.Util.isSuperType;

import arepair.generator.util.rules.BinaryInfo;
import arepair.generator.util.rules.BinaryRule;

public class URule2 extends BinaryRule {

    private URule2(BinaryInfo binaryInfo) {
        super(binaryInfo);
    }

    public static URule2 given(BinaryInfo binaryInfo) {
        return new URule2(binaryInfo);
    }

    @Override
    public boolean isPruned() {
        return isSuperType(leftRel, inheritanceMap) || isSuperType(rightRel, inheritanceMap);
    }
}
