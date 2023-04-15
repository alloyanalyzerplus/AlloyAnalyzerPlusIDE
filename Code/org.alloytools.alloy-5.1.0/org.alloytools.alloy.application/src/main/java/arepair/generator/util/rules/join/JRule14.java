package arepair.generator.util.rules.join;

import static arepair.generator.etc.Contants.ARROW;

import arepair.generator.util.rules.BinaryInfo;
import arepair.generator.util.rules.BinaryRule;

public class JRule14 extends BinaryRule {

    private JRule14(BinaryInfo binaryInfo) {
        super(binaryInfo);
    }

    public static JRule14 given(BinaryInfo binaryInfo) {
        return new JRule14(binaryInfo);
    }

    @Override
    public boolean isPruned() {
        return (opIsOr(leftRel.getOp(), ARROW) && getChild(leftRel, 1).getArity() + rightRel.getArity() > 2) || (opIsOr(rightRel.getOp(), ARROW) && leftRel.getArity() + getChild(rightRel, 0).getArity() > 2);
    }
}
