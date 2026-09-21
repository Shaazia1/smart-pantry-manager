package com.richfield.smartpantry.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UnitConverterTest {

    @Test
    public void kilogramsConvertToGrams() {
        assertEquals(1000, UnitConverter.convert(1, "kg", "g"), 0.001);
        assertEquals(0.5, UnitConverter.convert(500, "g", "kg"), 0.001);
    }

    @Test
    public void spoonsAndCupsConvertToMillilitres() {
        assertEquals(15, UnitConverter.convert(1, "tbsp", "ml"), 0.001);
        assertEquals(250, UnitConverter.convert(1, "cup", "ml"), 0.001);
        assertEquals(1000, UnitConverter.convert(1, "l", "ml"), 0.001);
    }

    @Test
    public void pluralsAndCapitalsStillWork() {
        assertEquals(500, UnitConverter.convert(2, "Cups", "ml"), 0.001);
        assertEquals(3, UnitConverter.convert(3, "pieces", "piece"), 0.001);
    }

    @Test
    public void weightAndVolumeCannotBeCompared() {
        assertTrue(UnitConverter.isComparable("g", "kg"));
        assertTrue(UnitConverter.isComparable("ml", "cup"));
        assertFalse(UnitConverter.isComparable("g", "ml"));
        assertFalse(UnitConverter.isComparable("piece", "g"));
    }

    @Test
    public void unitsWeDoNotKnowAreNotComparable() {
        assertEquals(UnitConverter.Dimension.UNKNOWN, UnitConverter.dimensionOf("sack"));
        assertFalse(UnitConverter.isComparable("sack", "g"));
        assertFalse(UnitConverter.isComparable("sack", "sack"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void convertingBetweenDifferentKindsOfUnitFails() {
        UnitConverter.convert(1, "g", "ml");
    }
}
