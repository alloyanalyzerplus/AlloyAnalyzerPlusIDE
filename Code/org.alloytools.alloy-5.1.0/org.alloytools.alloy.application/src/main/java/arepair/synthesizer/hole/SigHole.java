package arepair.synthesizer.hole;

import static arepair.generator.etc.Contants.LONE;
import static arepair.generator.etc.Contants.ONE;
import static arepair.generator.etc.Contants.SET;
import static arepair.generator.etc.Contants.SOME;

import com.google.common.collect.ImmutableList;

/**
 * This class represents holes in the signature multiplicity.
 */
public class SigHole extends Hole {

    public SigHole() {
        this.fragments = ImmutableList.of(ONE, SET, LONE, SOME);
        this.value = ONE;
    }


    @Override
    public String toString() {
        updated = false;
        if (value == SET) {
            return "";
        }
        return value + " ";
    }
}
