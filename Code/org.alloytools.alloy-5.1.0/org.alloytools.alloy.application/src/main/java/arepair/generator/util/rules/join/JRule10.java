package arepair.generator.util.rules.join;

import static arepair.generator.etc.Contants.ARROW;
import static arepair.generator.etc.Contants.MINUS;

import arepair.generator.util.rules.BinaryInfo;
import arepair.generator.util.rules.BinaryRule;

public class JRule10 extends BinaryRule {

    private JRule10(BinaryInfo binaryInfo) {
        super(binaryInfo);
    }

    public static JRule10 given(BinaryInfo binaryInfo) {
        return new JRule10(binaryInfo);
    }

    @Override
    public boolean isPruned() {
        return opIsOr(rightRel.getOp(), ARROW) && rightChildUnderLeftImbalancedOps(leftRel, MINUS, root -> sameRelations(getChild(rightRel, 0), getChild(root, 1))) || opIsOr(leftRel.getOp(), ARROW) && rightChildUnderLeftImbalancedOps(rightRel, MINUS, root -> sameRelations(getChild(leftRel, 1), getChild(root, 1)));
    }
}
