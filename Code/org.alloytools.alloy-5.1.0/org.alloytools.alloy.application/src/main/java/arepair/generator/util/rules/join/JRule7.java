package arepair.generator.util.rules.join;

import static arepair.generator.etc.Contants.CARET;
import static arepair.generator.util.Util.buildExpression;

import arepair.generator.util.rules.BinaryInfo;
import arepair.generator.util.rules.BinaryRule;

public class JRule7 extends BinaryRule {

    private JRule7(BinaryInfo binaryInfo) {
        super(binaryInfo);
    }

    public static JRule7 given(BinaryInfo binaryInfo) {
        return new JRule7(binaryInfo);
    }

    @Override
    public boolean isPruned() {
        return consecutiveNodesUnderOps(buildExpression(-1, op, leftRel, rightRel, inheritanceMap), op, (prev, cur) -> opIsOr(prev.getOp(), CARET) && sameRelations(getChild(prev, 0), cur));
    }
}
