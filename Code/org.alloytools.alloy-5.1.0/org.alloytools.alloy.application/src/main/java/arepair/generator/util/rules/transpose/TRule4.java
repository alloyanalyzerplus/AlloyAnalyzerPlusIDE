package arepair.generator.util.rules.transpose;

import static arepair.generator.etc.Contants.AMP;
import static arepair.generator.etc.Contants.DOT;
import static arepair.generator.etc.Contants.MINUS;
import static arepair.generator.etc.Contants.PLUS;
import static arepair.generator.etc.Contants.TILDE;

import arepair.generator.util.rules.UnaryInfo;
import arepair.generator.util.rules.UnaryRule;

public class TRule4 extends UnaryRule {

    private TRule4(UnaryInfo unaryInfo) {
        super(unaryInfo);
    }

    public static TRule4 given(UnaryInfo unaryInfo) {
        return new TRule4(unaryInfo);
    }

    @Override
    public boolean isPruned() {
        return opIsOr(rel.getOp(), PLUS, AMP, MINUS, DOT) && (opIsOr(getChild(rel, 0).getOp(), TILDE) || opIsOr(getChild(rel, 1).getOp(), TILDE));
    }
}
