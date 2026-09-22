# Smart Pantry Manager

An Android app written in Java that suggests recipes you can cook using only the
ingredients you already have at home. A recipe is only suggested when every
single ingredient it needs is in your pantry, so there is no shopping trip and
nothing goes to waste at the back of the cupboard.

Mobile App Development 700, Richfield Graduate Institute of Technology.
Shaazia, student number 402412521.

## What it does

| Screen | What it is for |
| --- | --- |
| My Pantry | Lists everything you have with quantity, unit and an optional expiry date. Add, edit and delete from here. |
| Add / Edit Ingredient | A validated form. The same screen handles adding and editing. |
| Suggested Recipes | Only the recipes you can cook right now, plus a separate "Almost there" section for recipes missing exactly one ingredient. |
| Recipe Detail | The full ingredient list and method, with a tick next to what you have and a cross next to what you don't. |
| Settings | Expiring soon alerts, the almost there section, preferred units, and a button to restore the built in recipes. |

Twenty recipes are loaded into the database the first time the app runs.

## The matching rule

The rule lives in `RecipeMatcher.java` and works like this: a recipe only
appears under Suggested Recipes when every ingredient it needs is in the pantry
in at least the quantity the recipe asks for. A recipe needing five ingredients
where you have four is never suggested. It can only show up in the separate
almost there section.

Matching is not a plain text comparison, because people don't type ingredients
the same way twice:

* `IngredientNormalizer` cleans up names. It lower cases them, strips
  punctuation and accents, drops words that describe an ingredient without
  changing it (fresh, chopped, large), turns plurals into singulars, and maps
  known synonyms onto one name. So Tomatoes, tomato and fresh chopped tomatoes
  are all the same ingredient, and aubergine matches eggplant.
* `UnitConverter` compares quantities across units. Every unit belongs to a
  dimension (weight, volume or a count of things) with a factor to a base unit,
  so 1 kg of rice at home covers a recipe that wants 250 g. Grams and
  millilitres are never mixed up, because they are different dimensions.

When two units genuinely cannot be compared, like 1 can against 200 g, the app
accepts that you have the ingredient rather than inventing a conversion.

The rule is covered by JUnit tests in `app/src/test`. They run on the computer
without a phone or emulator.

## Why SQLite

The app uses SQLite through `SQLiteOpenHelper`, with the queries kept in two
repository classes.

1. The data is personal and local. A pantry belongs to one person on one phone.
   Nothing needs to be shared or synced, so a cloud database would add an
   account and a network dependency for no benefit.
2. The app has to work offline. Standing in the kitchen deciding what to cook
   should not need signal. SQLite reads from a file in the app's own storage, so
   it works in aeroplane mode.
3. Recipes and their ingredient lines are a one to many relationship, which a
   relational table handles naturally.
4. PostgreSQL would need a REST API and a server for what is a single user data
   set sitting on one device.

### Tables

```
pantry_items                  recipes                    recipe_ingredients
------------                  -------                    ------------------
_id          INTEGER PK       _id       INTEGER PK       _id        INTEGER PK
name         TEXT             name      TEXT             recipe_id  INTEGER FK
name_key     TEXT             category  TEXT             name       TEXT
quantity     REAL             minutes   INTEGER          name_key   TEXT
unit         TEXT             servings  INTEGER          quantity   REAL
expiry_date  TEXT (nullable)  steps     TEXT             unit       TEXT
```

`name_key` holds the cleaned up version of the name, so the matcher does not
have to work it out again every time it reads a row.

All four CRUD operations are in `PantryRepository`: insert, getAll and getById,
update, and delete. Because the data is in a database file, everything survives
the app being closed or the phone restarting.

## Not included

No Google Maps, no mapping SDK, no GPS or location features, and no payments.
The app requests no Android permissions at all and works entirely offline.

## Running it

You need Android Studio, JDK 11 or newer (Android Studio ships with one), and
Android SDK 34. The minimum supported device is API 24, Android 7.0.

1. Clone the repository:
   ```
   git clone https://github.com/Shaazia1/smart-pantry-manager.git
   ```
2. In Android Studio choose File then Open, and pick the `SmartPantryManager`
   folder. Let the Gradle sync finish. The first sync downloads Gradle and the
   AndroidX libraries, so it needs an internet connection once.
3. Plug in a phone with USB debugging turned on, or start an emulator running
   API 34.
4. Press Run.

The database is created and the recipes are loaded on first launch. The pantry
starts empty, so add a few ingredients and open the Recipes tab to see the
matching work.

To run the tests, right click `app/src/test/java` in Android Studio and choose
Run tests, or use `gradlew test` from the terminal.

## Project layout

```
app/src/main/java/com/richfield/smartpantry/
  adapter/   PantryAdapter, RecipeAdapter          RecyclerView adapters
  data/      RecipeSeeder                          the 20 built in recipes
  db/        DatabaseHelper, PantryContract,       the SQLite layer
             PantryRepository, RecipeRepository
  logic/     RecipeMatcher, IngredientNormalizer,  the matching rule
             UnitConverter
  model/     PantryItem, Recipe, Ingredient,       plain data classes
             MatchResult
  ui/        MainActivity, PantryListFragment,     the screens
             SuggestedRecipesFragment, SettingsFragment,
             AddEditIngredientActivity, RecipeDetailActivity
  util/      Prefs, DateUtils, TextFormat          small helpers
```

`MainActivity` holds the three tabs with a bottom navigation bar and swaps
fragments into a container. The Add/Edit and Recipe Detail screens are separate
activities, started with an intent that carries the row id as an extra.
