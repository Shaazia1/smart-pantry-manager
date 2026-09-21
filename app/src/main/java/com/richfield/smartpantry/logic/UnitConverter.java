package com.richfield.smartpantry.logic;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

// Converts between units so quantities can still be compared when the pantry and
// the recipe were written differently, for example 1 kg of rice at home against
// 250 g needed by a recipe.
//
// Every unit belongs to a dimension (weight, volume or a count of things) and has
// a factor that turns it into that dimension's base unit: g, ml or one piece.
// Two quantities can only be compared when they share a dimension.
public class UnitConverter {

    public enum Dimension {
        WEIGHT, VOLUME, COUNT, UNKNOWN
    }

    private static final Map<String, Dimension> DIMENSIONS = new HashMap<>();
    private static final Map<String, Double> FACTORS = new HashMap<>();

    static {
        // weight, base unit is the gram
        add("mg", Dimension.WEIGHT, 0.001);
        add("g", Dimension.WEIGHT, 1);
        add("gram", Dimension.WEIGHT, 1);
        add("kg", Dimension.WEIGHT, 1000);
        add("kilogram", Dimension.WEIGHT, 1000);
        add("oz", Dimension.WEIGHT, 28.3495);
        add("ounce", Dimension.WEIGHT, 28.3495);
        add("lb", Dimension.WEIGHT, 453.592);
        add("pound", Dimension.WEIGHT, 453.592);

        // volume, base unit is the millilitre, a South African cup is 250 ml
        add("ml", Dimension.VOLUME, 1);
        add("millilitre", Dimension.VOLUME, 1);
        add("l", Dimension.VOLUME, 1000);
        add("litre", Dimension.VOLUME, 1000);
        add("liter", Dimension.VOLUME, 1000);
        add("tsp", Dimension.VOLUME, 5);
        add("teaspoon", Dimension.VOLUME, 5);
        add("tbsp", Dimension.VOLUME, 15);
        add("tablespoon", Dimension.VOLUME, 15);
        add("cup", Dimension.VOLUME, 250);

        // things you count rather than measure
        add("piece", Dimension.COUNT, 1);
        add("pc", Dimension.COUNT, 1);
        add("unit", Dimension.COUNT, 1);
        add("item", Dimension.COUNT, 1);
        add("whole", Dimension.COUNT, 1);
        add("slice", Dimension.COUNT, 1);
        add("clove", Dimension.COUNT, 1);
        add("can", Dimension.COUNT, 1);
        add("tin", Dimension.COUNT, 1);
        add("pinch", Dimension.COUNT, 1);
        add("handful", Dimension.COUNT, 1);
        add("", Dimension.COUNT, 1);
    }

    private static void add(String unit, Dimension dimension, double factor) {
        DIMENSIONS.put(unit, dimension);
        FACTORS.put(unit, factor);
    }

    // "Cups." becomes "cup"
    public static String normalizeUnit(String unit) {
        if (unit == null) {
            return "";
        }
        String text = unit.toLowerCase(Locale.ROOT).trim().replace(".", "");
        if (text.isEmpty() || DIMENSIONS.containsKey(text)) {
            return text;
        }
        if (text.endsWith("es") && DIMENSIONS.containsKey(text.substring(0, text.length() - 2))) {
            return text.substring(0, text.length() - 2);        // pinches -> pinch
        }
        if (text.endsWith("s") && DIMENSIONS.containsKey(text.substring(0, text.length() - 1))) {
            return text.substring(0, text.length() - 1);        // cups -> cup
        }
        return text;
    }

    public static Dimension dimensionOf(String unit) {
        Dimension dimension = DIMENSIONS.get(normalizeUnit(unit));
        return dimension == null ? Dimension.UNKNOWN : dimension;
    }

    // true when both units are known and measure the same kind of thing
    public static boolean isComparable(String first, String second) {
        Dimension a = dimensionOf(first);
        return a != Dimension.UNKNOWN && a == dimensionOf(second);
    }

    // Check isComparable first, this throws if the units don't match up.
    public static double convert(double quantity, String fromUnit, String toUnit) {
        if (!isComparable(fromUnit, toUnit)) {
            throw new IllegalArgumentException("cannot convert " + fromUnit + " to " + toUnit);
        }
        double from = FACTORS.get(normalizeUnit(fromUnit));
        double to = FACTORS.get(normalizeUnit(toUnit));
        return quantity * from / to;
    }
}
