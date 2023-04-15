package arepair.generator.util.rules;

import arepair.generator.fragment.Expression;
import arepair.generator.fragment.Fragment;

public abstract class UnaryRule extends PruningRule {

    protected Fragment   op;
    protected Expression rel;

    public UnaryRule(UnaryInfo unaryInfo) {
        this.op = unaryInfo.getOp();
        this.rel = unaryInfo.getRel();
    }
}
