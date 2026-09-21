package com.richfield.smartpantry.logic;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

// Cleans up an ingredient name so that matching still works when the user types
// it slightly differently. "Tomatoes", "tomato" and "fresh chopped tomatoes"
// all come out as "tomato".
//
// The steps are: lower case and strip punctuation, drop words that describe the
// ingredient without changing it, make plurals singular, then map a few known
// synonyms onto one name.
//
// No Android classes are used here, which is what makes it testable with plain
// JUnit tests.
public class IngredientNormalizer {

    // words that describe an ingredient but don't change what it is
    private static final Set<String> FILLER_WORDS = new HashSet<>(Arrays.asList(
            "fresh", "freshly", "frozen", "dried", "chopped", "sliced", "diced", "grated",
            "minced", "crushed", "large", "small", "medium", "whole", "raw", "ripe",
            "plain", "organic", "cooked", "tinned", "canned", "packet", "packets",
            "of", "a", "an", "the", "some", "extra", "virgin", "free", "range", "lean"));

    // plurals the simple rules below would get wrong
    private static final Map<String, String> IRREGULAR_PLURALS = new HashMap<>();

    // different names for the same thing, keys are already cleaned and singular
    private static final Map<String, String> SYNONYMS = new HashMap<>();

    static {
        IRREGULAR_PLURALS.put("leaves", "leaf");
        IRREGULAR_PLURALS.put("loaves", "loaf");
        IRREGULAR_PLURALS.put("halves", "half");

        addSynonyms(new String[]{"aubergine", "brinjal"}, "eggplant");
        addSynonyms(new String[]{"courgette", "baby marrow"}, "zucchini");
        addSynonyms(new String[]{"capsicum", "green pepper", "red pepper",
                "yellow pepper", "sweet pepper"}, "bell pepper");
        addSynonyms(new String[]{"coriander", "dhania"}, "cilantro");
        addSynonyms(new String[]{"spring onion"}, "green onion");
        addSynonyms(new String[]{"prawn"}, "shrimp");
        addSynonyms(new String[]{"mince", "beef mince", "minced beef"}, "ground beef");
        addSynonyms(new String[]{"chilli", "chile"}, "chili");
        addSynonyms(new String[]{"mealie meal", "polenta", "cornmeal"}, "maize meal");
        addSynonyms(new String[]{"soya sauce"}, "soy sauce");
        addSynonyms(new String[]{"yoghurt", "natural yoghurt"}, "yogurt");

        // variations that shouldn't stop a recipe matching
        addSynonyms(new String[]{"red onion", "white onion", "brown onion",
                "yellow onion"}, "onion");
        addSynonyms(new String[]{"baby potato", "potatoe"}, "potato");
        addSynonyms(new String[]{"chicken breast", "chicken fillet", "chicken thigh",
                "chicken piece"}, "chicken");
        addSynonyms(new String[]{"olive oil", "sunflower oil", "vegetable oil",
                "canola oil", "oil"}, "cooking oil");
        addSynonyms(new String[]{"basmati rice", "white rice", "brown rice"}, "rice");
        addSynonyms(new String[]{"spaghetti", "macaroni", "penne", "fusilli"}, "pasta");
        addSynonyms(new String[]{"cake flour", "all purpose flour", "plain flour"}, "flour");
        addSynonyms(new String[]{"white sugar", "brown sugar", "castor sugar"}, "sugar");
        addSynonyms(new String[]{"cheddar", "cheddar cheese", "mozzarella",
                "mozzarella cheese", "gouda"}, "cheese");
        addSynonyms(new String[]{"full cream milk", "low fat milk"}, "milk");
        addSynonyms(new String[]{"white bread", "brown bread"}, "bread");
        addSynonyms(new String[]{"garlic clove", "clove garlic"}, "garlic");
    }

    private static void addSynonyms(String[] names, String canonical) {
        for (String name : names) {
            SYNONYMS.put(name, canonical);
        }
    }

    public static String normalize(String raw) {
        if (raw == null) {
            return "";
        }

        // lower case, strip accents, then anything that isn't a letter or digit
        String text = raw.toLowerCase(Locale.ROOT).trim();
        text = Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        text = text.replaceAll("[^a-z0-9 ]", " ").replaceAll("\\s+", " ").trim();
        if (text.isEmpty()) {
            return "";
        }

        StringBuilder cleaned = new StringBuilder();
        for (String word : text.split(" ")) {
            if (FILLER_WORDS.contains(word)) {
                continue;
            }
            if (cleaned.length() > 0) {
                cleaned.append(' ');
            }
            cleaned.append(singular(word));
        }

        // if the name was nothing but filler words, keep what we started with
        String key = cleaned.length() == 0 ? text : cleaned.toString();

        String synonym = SYNONYMS.get(key);
        return synonym != null ? synonym : key;
    }

    // true when two names mean the same ingredient
    public static boolean sameIngredient(String first, String second) {
        String a = normalize(first);
        return !a.isEmpty() && a.equals(normalize(second));
    }

    // Enough of an English singulariser for ingredient names.
    static String singular(String word) {
        String irregular = IRREGULAR_PLURALS.get(word);
        if (irregular != null) {
            return irregular;
        }
        if (word.length() <= 3 || !word.endsWith("s") || word.endsWith("ss")
                || word.endsWith("us") || word.endsWith("is")) {
            return word;
        }

        String result;
        if (word.endsWith("ies") && word.length() > 4) {
            result = word.substring(0, word.length() - 3) + "y";     // berries -> berry
        } else if (word.endsWith("oes") && word.length() > 4) {
            result = word.substring(0, word.length() - 2);           // tomatoes -> tomato
        } else if (word.endsWith("ches") || word.endsWith("shes")
                || word.endsWith("sses") || word.endsWith("xes")) {
            result = word.substring(0, word.length() - 2);           // dishes -> dish
        } else {
            result = word.substring(0, word.length() - 1);           // eggs -> egg
        }

        // guard against silly results like molasses -> molass
        if (result.length() < 3 || result.endsWith("ss")) {
            return word;
        }
        return result;
    }
}
