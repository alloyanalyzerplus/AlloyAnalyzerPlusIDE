package arepair.generator.util.rules.join;

import static arepair.generator.etc.Contants.IDEN_EXPR;

import arepair.generator.util.rules.BinaryInfo;
import arepair.generator.util.rules.BinaryRule;

public class JRule15 extends BinaryRule {

    private JRule15(BinaryInfo binaryInfo) {
        super(binaryInfo);
    }

    public static JRule15 given(BinaryInfo binaryInfo) {
        return new JRule15(binaryInfo);
    }

    @Override
    public boolean isPruned() {
        return leftRel == IDEN_EXPR || rightRel == IDEN_EXPR;
    }
}
