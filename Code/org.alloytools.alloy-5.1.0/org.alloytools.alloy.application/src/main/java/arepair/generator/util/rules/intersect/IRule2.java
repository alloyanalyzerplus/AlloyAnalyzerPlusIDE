package arepair.generator.util.rules.intersect;

import static arepair.generator.util.Util.isSuperType;

import arepair.generator.util.rules.BinaryInfo;
import arepair.generator.util.rules.BinaryRule;

public class IRule2 extends BinaryRule {

    private IRule2(BinaryInfo binaryInfo) {
        super(binaryInfo);
    }

    public static IRule2 given(BinaryInfo binaryInfo) {
        return new IRule2(binaryInfo);
    }

    @Override
    public boolean isPruned() {
        // Remove the case where (A->A) & *a
        return (isSuperType(leftRel, inheritanceMap) || isSuperType(rightRel, inheritanceMap)) && pruningTypeMatches(leftRel.getTypes(), rightRel.getTypes());
    }
}
