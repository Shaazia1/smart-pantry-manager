package com.richfield.smartpantry.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

// Tests for the name clean up. These run on the computer, no phone needed.
public class IngredientNormalizerTest {

    @Test
    public void capitalsAndSpacesAreIgnored() {
        assertEquals("tomato", IngredientNormalizer.normalize("  Tomato "));
    }

    @Test
    public void pluralsBecomeSingular() {
        assertEquals("tomato", IngredientNormalizer.normalize("Tomatoes"));
        assertEquals("egg", IngredientNormalizer.normalize("eggs"));
        assertEquals("berry", IngredientNormalizer.normalize("Berries"));
    }

    @Test
    public void wordsThatOnlyDescribeTheIngredientAreDropped() {
        assertEquals("garlic", IngredientNormalizer.normalize("fresh chopped garlic"));
        assertEquals("onion", IngredientNormalizer.normalize("large onions"));
    }

    @Test
    public void differentNamesForTheSameThingMatch() {
        assertEquals("eggplant", IngredientNormalizer.normalize("Aubergine"));
        assertEquals("cooking oil", IngredientNormalizer.normalize("Olive Oil"));
        assertEquals("onion", IngredientNormalizer.normalize("red onions"));
    }

    @Test
    public void ingredientsItDoesNotKnowAreLeftAlone() {
        assertEquals("biltong", IngredientNormalizer.normalize("Biltong"));
    }

    @Test
    public void emptyAndNullAreSafe() {
        assertEquals("", IngredientNormalizer.normalize(null));
        assertEquals("", IngredientNormalizer.normalize("   "));
    }

    @Test
    public void sameIngredientComparesTwoNames() {
        assertTrue(IngredientNormalizer.sameIngredient("Tomatoes", "tomato"));
        assertFalse(IngredientNormalizer.sameIngredient("tomato", "potato"));
        assertFalse(IngredientNormalizer.sameIngredient("", ""));
    }
}
