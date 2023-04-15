package arepair.generator.util.rules.transpose;

import static arepair.generator.etc.Contants.ARROW;
import static arepair.generator.etc.Contants.DOT;

import arepair.generator.util.rules.UnaryInfo;
import arepair.generator.util.rules.UnaryRule;

public class TRule3 extends UnaryRule {

    private TRule3(UnaryInfo unaryInfo) {
        super(unaryInfo);
    }

    public static TRule3 given(UnaryInfo unaryInfo) {
        return new TRule3(unaryInfo);
    }

    @Override
    public boolean isPruned() {
        return opIsOr(rel.getOp(), DOT) && (opIsOr(getChild(rel, 0), ARROW) || opIsOr(getChild(rel, 1), ARROW));
    }
}
