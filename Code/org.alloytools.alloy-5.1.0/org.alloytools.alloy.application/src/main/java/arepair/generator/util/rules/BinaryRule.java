package arepair.generator.util.rules;

import java.util.Map;

import arepair.generator.fragment.Expression;
import arepair.generator.fragment.Fragment;

public abstract class BinaryRule extends PruningRule {

    protected Fragment           op;
    protected Expression         leftRel;
    protected int                leftDepth;
    protected Expression         rightRel;
    protected int                rightDepth;
    protected Map<String,String> inheritanceMap;

    public BinaryRule(BinaryInfo binaryInfo) {
        this.op = binaryInfo.getOp();
        this.leftRel = binaryInfo.getLeftRel();
        this.leftDepth = binaryInfo.getLeftDepth();
        this.rightRel = binaryInfo.getRightRel();
        this.rightDepth = binaryInfo.getRightDepth();
        this.inheritanceMap = binaryInfo.getInheritanceMap();
    }
}
