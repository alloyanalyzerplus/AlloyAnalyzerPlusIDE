package arepair.generator.util.rules.join;

import static arepair.generator.etc.Contants.TILDE;

import arepair.generator.util.rules.BinaryInfo;
import arepair.generator.util.rules.BinaryRule;

public class JRule3 extends BinaryRule {

    private JRule3(BinaryInfo binaryInfo) {
        super(binaryInfo);
    }

    public static JRule3 given(BinaryInfo binaryInfo) {
        return new JRule3(binaryInfo);
    }

    @Override
    public boolean isPruned() {
        return leftRel.getArity() == 1 && opIsOr(rightRel.getOp(), TILDE);
    }
}
