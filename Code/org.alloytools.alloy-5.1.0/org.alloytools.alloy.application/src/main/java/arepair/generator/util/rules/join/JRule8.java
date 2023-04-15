package arepair.generator.util.rules.join;

import static arepair.generator.etc.Contants.CARET;
import static arepair.generator.etc.Contants.STAR;
import static arepair.generator.util.Util.buildExpression;

import arepair.generator.util.rules.BinaryInfo;
import arepair.generator.util.rules.BinaryRule;

public class JRule8 extends BinaryRule {

    private JRule8(BinaryInfo binaryInfo) {
        super(binaryInfo);
    }

    public static JRule8 given(BinaryInfo binaryInfo) {
        return new JRule8(binaryInfo);
    }

    @Override
    public boolean isPruned() {
        return consecutiveNodesUnderOps(buildExpression(-1, op, leftRel, rightRel, inheritanceMap), op, (prev, cur) -> opIsOr(prev.getOp(), STAR, CARET) && opIsOr(cur.getOp(), STAR, CARET) && sameRelations(getChild(prev, 0), getChild(cur, 0)));
    }
}
