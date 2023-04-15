package arepair.generator.util.rules.transpose;

import static arepair.generator.etc.Contants.ARROW;

import arepair.generator.util.rules.UnaryInfo;
import arepair.generator.util.rules.UnaryRule;

public class TRule2 extends UnaryRule {

    private TRule2(UnaryInfo unaryInfo) {
        super(unaryInfo);
    }

    public static TRule2 given(UnaryInfo unaryInfo) {
        return new TRule2(unaryInfo);
    }

    @Override
    public boolean isPruned() {
        return opIsOr(rel.getOp(), ARROW);
    }
}
