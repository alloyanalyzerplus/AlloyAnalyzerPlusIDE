package arepair.synthesizer.hole;

import static arepair.generator.etc.Contants.LONE;
import static arepair.generator.etc.Contants.ONE;
import static arepair.generator.etc.Contants.SET;
import static arepair.generator.etc.Contants.SOME;

import com.google.common.collect.ImmutableList;

/**
 * This class represents holes in the unary expression cardinality.
 */
public class UnaryExprCardHole extends Hole {

    public UnaryExprCardHole() {
        this.fragments = ImmutableList.of(ONE, SET, LONE, SOME);
        this.value = ONE;
    }

    @Override
    public String toString() {
        updated = false;
        return value + " ";
    }
}
