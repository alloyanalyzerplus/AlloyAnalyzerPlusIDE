package arepair.generator.util.rules.intersect;

import static arepair.generator.util.Util.buildExpression;

import arepair.generator.util.rules.BinaryInfo;
import arepair.generator.util.rules.BinaryRule;

public class IRule3 extends BinaryRule {

    private IRule3(BinaryInfo binaryInfo) {
        super(binaryInfo);
    }

    public static IRule3 given(BinaryInfo binaryInfo) {
        return new IRule3(binaryInfo);
    }

    @Override
    public boolean isPruned() {
        return !uniqueNodesUnderOps(buildExpression(-1, op, leftRel, rightRel, inheritanceMap), op);
    }
}
