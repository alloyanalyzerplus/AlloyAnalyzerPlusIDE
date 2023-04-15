package arepair.synthesizer.hole;

import static arepair.generator.etc.Contants.EQ;
import static arepair.generator.etc.Contants.IN;
import static arepair.generator.etc.Contants.NEQ;
import static arepair.generator.etc.Contants.NIN;

import com.google.common.collect.ImmutableList;

public class BinaryFormulaCmpHole extends Hole {

    public BinaryFormulaCmpHole() {
        this.fragments = ImmutableList.of(EQ, NEQ, IN, NIN);
        this.value = EQ;
    }

    @Override
    public String toString() {
        updated = false;
        return " " + value + " ";
    }
}
