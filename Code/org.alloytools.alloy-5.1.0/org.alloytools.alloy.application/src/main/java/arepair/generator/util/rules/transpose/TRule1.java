package arepair.generator.util.rules.transpose;

import static arepair.generator.etc.Contants.CARET;
import static arepair.generator.etc.Contants.STAR;
import static arepair.generator.etc.Contants.TILDE;

import arepair.generator.util.rules.UnaryInfo;
import arepair.generator.util.rules.UnaryRule;

public class TRule1 extends UnaryRule {

    private TRule1(UnaryInfo unaryInfo) {
        super(unaryInfo);
    }

    public static TRule1 given(UnaryInfo unaryInfo) {
        return new TRule1(unaryInfo);
    }

    @Override
    public boolean isPruned() {
        return opIsOr(rel.getOp(), STAR, CARET, TILDE);
    }
}
