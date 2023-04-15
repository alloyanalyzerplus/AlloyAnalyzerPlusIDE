package arepair.synthesizer.hole;

import java.util.List;

import arepair.generator.fragment.Expression;

public class ExprHole extends Hole {

    public void setFragments(List<Expression> fragments) {
        this.fragments = fragments;
        this.value = null;
    }

    @Override
    public String toString() {
        updated = false;
        return value.getValue();
    }
}
