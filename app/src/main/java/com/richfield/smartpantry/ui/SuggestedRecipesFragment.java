package com.richfield.smartpantry.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.richfield.smartpantry.R;
import com.richfield.smartpantry.adapter.RecipeAdapter;
import com.richfield.smartpantry.db.PantryRepository;
import com.richfield.smartpantry.db.RecipeRepository;
import com.richfield.smartpantry.logic.RecipeMatcher;
import com.richfield.smartpantry.model.MatchResult;
import com.richfield.smartpantry.model.PantryItem;
import com.richfield.smartpantry.model.Recipe;
import com.richfield.smartpantry.util.Prefs;

import java.util.List;

// Runs the strict rule against the pantry and lists only what can be cooked now.
// The matching is done again in onResume, so adding or removing one ingredient
// makes a recipe appear or disappear straight away.
public class SuggestedRecipesFragment extends Fragment
        implements RecipeAdapter.OnRecipeClickListener {

    private PantryRepository pantryRepository;
    private RecipeRepository recipeRepository;
    private Prefs prefs;
    private RecipeAdapter adapter;

    private RecyclerView recyclerView;
    private View emptyState;
    private TextView textSummary;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_suggested, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pantryRepository = new PantryRepository(requireContext());
        recipeRepository = new RecipeRepository(requireContext());
        prefs = new Prefs(requireContext());

        recyclerView = view.findViewById(R.id.recyclerRecipes);
        emptyState = view.findViewById(R.id.emptyState);
        textSummary = view.findViewById(R.id.textSummary);

        adapter = new RecipeAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshSuggestions();
    }

    private void refreshSuggestions() {
        List<PantryItem> pantry = pantryRepository.getAll();
        List<Recipe> recipes = recipeRepository.getAllWithIngredients();

        List<MatchResult> all = RecipeMatcher.matchAll(recipes, pantry);
        List<MatchResult> cookable = RecipeMatcher.cookable(all);

        // the almost there list is an extra, it never mixes into the suggestions
        List<MatchResult> almostThere = prefs.isAlmostThereEnabled()
                ? RecipeMatcher.almostThere(all)
                : null;

        adapter.submit(requireContext(), cookable, almostThere);

        boolean nothingToShow = cookable.isEmpty()
                && (almostThere == null || almostThere.isEmpty());
        emptyState.setVisibility(nothingToShow ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(nothingToShow ? View.GONE : View.VISIBLE);
        textSummary.setVisibility(nothingToShow ? View.GONE : View.VISIBLE);
        textSummary.setText(getString(R.string.match_summary, cookable.size(), recipes.size()));
    }

    // opens the detail screen, sending the recipe id with the intent
    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(requireContext(), RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
        startActivity(intent);
    }
}
