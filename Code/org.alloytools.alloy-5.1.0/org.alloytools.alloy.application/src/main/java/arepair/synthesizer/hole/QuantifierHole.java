package arepair.synthesizer.hole;

import static arepair.generator.etc.Contants.ALL;
import static arepair.generator.etc.Contants.LONE;
import static arepair.generator.etc.Contants.NO;
import static arepair.generator.etc.Contants.ONE;
import static arepair.generator.etc.Contants.SOME;

import com.google.common.collect.ImmutableList;

public class QuantifierHole extends Hole {

    public QuantifierHole() {
        // Maybe we do not need lone and one for quantifiers.
        this.fragments = ImmutableList.of(ALL, SOME, NO, ONE, LONE);
        this.value = ALL;
    }

    @Override
    public String toString() {
        updated = false;
        return value + " ";
    }
}
