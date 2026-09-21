package com.richfield.smartpantry.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.richfield.smartpantry.R;
import com.richfield.smartpantry.model.MatchResult;
import com.richfield.smartpantry.model.Recipe;

import java.util.ArrayList;
import java.util.List;

// Fills the suggested recipes list. Works the same way as the pantry adapter,
// only the row shows a recipe instead of an ingredient.
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    private final List<MatchResult> results = new ArrayList<>();
    private final OnRecipeClickListener listener;

    public RecipeAdapter(OnRecipeClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<MatchResult> newResults) {
        results.clear();
        if (newResults != null) {
            results.addAll(newResults);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        holder.bind(results.get(position));
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder {

        private final TextView textName;
        private final TextView textMeta;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textRecipeName);
            textMeta = itemView.findViewById(R.id.textRecipeMeta);
        }

        void bind(MatchResult result) {
            Context context = itemView.getContext();
            final Recipe recipe = result.getRecipe();

            textName.setText(recipe.getName());
            textMeta.setText(context.getString(R.string.recipe_meta,
                    recipe.getIngredients().size(), recipe.getMinutes(), recipe.getServings()));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onRecipeClick(recipe);
                }
            });
        }
    }
}
