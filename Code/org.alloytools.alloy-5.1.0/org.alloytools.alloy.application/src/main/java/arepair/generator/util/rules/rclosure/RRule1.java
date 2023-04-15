package arepair.generator.util.rules.rclosure;

import static arepair.generator.etc.Contants.CARET;
import static arepair.generator.etc.Contants.STAR;

import arepair.generator.util.rules.UnaryInfo;
import arepair.generator.util.rules.UnaryRule;

public class RRule1 extends UnaryRule {

    private RRule1(UnaryInfo unaryInfo) {
        super(unaryInfo);
    }

    public static RRule1 given(UnaryInfo unaryInfo) {
        return new RRule1(unaryInfo);
    }

    @Override
    public boolean isPruned() {
        return opIsOr(rel.getOp(), STAR, CARET);
    }
}
