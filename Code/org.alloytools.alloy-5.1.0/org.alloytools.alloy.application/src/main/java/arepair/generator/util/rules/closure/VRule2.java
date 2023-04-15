package arepair.generator.util.rules.closure;

import static arepair.generator.etc.Contants.ARROW;

import arepair.generator.util.rules.UnaryInfo;
import arepair.generator.util.rules.UnaryRule;

public class VRule2 extends UnaryRule {

    private VRule2(UnaryInfo unaryInfo) {
        super(unaryInfo);
    }

    public static VRule2 given(UnaryInfo unaryInfo) {
        return new VRule2(unaryInfo);
    }

    @Override
    public boolean isPruned() {
        return opIsOr(rel.getOp(), ARROW);
    }
}
