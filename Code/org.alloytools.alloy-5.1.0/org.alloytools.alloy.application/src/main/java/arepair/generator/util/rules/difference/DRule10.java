package arepair.generator.util.rules.difference;

import static arepair.generator.etc.Card.ONE;
import static arepair.generator.etc.Contants.ARROW;
import static arepair.generator.etc.Contants.STAR;

import arepair.generator.util.rules.BinaryInfo;
import arepair.generator.util.rules.BinaryRule;

public class DRule10 extends BinaryRule {

    private DRule10(BinaryInfo binaryInfo) {
        super(binaryInfo);
    }

    public static DRule10 given(BinaryInfo binaryInfo) {
        return new DRule10(binaryInfo);
    }

    @Override
    public boolean isPruned() {
        return opIsOr(leftRel.getOp(), ARROW) && opIsOr(rightRel.getOp(), STAR) && sameRelations(getChild(leftRel, 0), getChild(leftRel, 1)) && getChild(leftRel, 0).getArity() == 1 && getChild(leftRel, 0).getCards().get(0).equals(ONE);
    }
}
