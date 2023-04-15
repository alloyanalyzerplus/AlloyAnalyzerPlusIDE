package arepair.synthesizer.hole;

import static arepair.generator.etc.Contants.LONE;
import static arepair.generator.etc.Contants.NO;
import static arepair.generator.etc.Contants.ONE;
import static arepair.generator.etc.Contants.SOME;

import com.google.common.collect.ImmutableList;

/**
 * This class represents holes in the unary formula cardinality.
 */
public class UnaryFormulaCardHole extends Hole {

    public UnaryFormulaCardHole() {
        this.fragments = ImmutableList.of(SOME, NO, ONE, LONE);
        this.value = SOME;
    }

    @Override
    public String toString() {
        updated = false;
        return value + " ";
    }
}
