package com.richfield.smartpantry.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.richfield.smartpantry.R;
import com.richfield.smartpantry.model.MatchResult;
import com.richfield.smartpantry.model.Recipe;

import java.util.ArrayList;
import java.util.List;

// Fills the suggested recipes list. It uses two kinds of row, a heading and a
// recipe, so the strict suggestions and the almost there recipes can share one
// scrolling list while staying clearly apart.
public class RecipeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_RECIPE = 1;

    // one line of the list is either a heading or a recipe, never both
    private static class Row {
        final String header;
        final MatchResult result;

        Row(String header, MatchResult result) {
            this.header = header;
            this.result = result;
        }
    }

    private final List<Row> rows = new ArrayList<>();
    private final OnRecipeClickListener listener;

    public RecipeAdapter(OnRecipeClickListener listener) {
        this.listener = listener;
    }

    // almostThere is null when the user switched that section off in settings
    public void submit(Context context, List<MatchResult> cookable,
                       List<MatchResult> almostThere) {
        rows.clear();

        if (cookable != null && !cookable.isEmpty()) {
            rows.add(new Row(context.getString(R.string.suggested_header), null));
            for (MatchResult result : cookable) {
                rows.add(new Row(null, result));
            }
        }
        if (almostThere != null && !almostThere.isEmpty()) {
            rows.add(new Row(context.getString(R.string.almost_there_header), null));
            for (MatchResult result : almostThere) {
                rows.add(new Row(null, result));
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return rows.get(position).header != null ? TYPE_HEADER : TYPE_RECIPE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            return new HeaderViewHolder(
                    inflater.inflate(R.layout.item_section_header, parent, false));
        }
        return new RecipeViewHolder(inflater.inflate(R.layout.item_recipe, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Row row = rows.get(position);
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind(row.header);
        } else {
            ((RecipeViewHolder) holder).bind(row.result);
        }
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {

        private final TextView textHeader;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            textHeader = itemView.findViewById(R.id.textHeader);
        }

        void bind(String header) {
            textHeader.setText(header);
        }
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder {

        private final TextView textName;
        private final TextView textMeta;
        private final TextView textNote;
        private final ImageView imageStatus;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textRecipeName);
            textMeta = itemView.findViewById(R.id.textRecipeMeta);
            textNote = itemView.findViewById(R.id.textRecipeNote);
            imageStatus = itemView.findViewById(R.id.imageStatus);
        }

        void bind(MatchResult result) {
            Context context = itemView.getContext();
            final Recipe recipe = result.getRecipe();

            textName.setText(recipe.getName());
            textMeta.setText(context.getString(R.string.recipe_meta,
                    recipe.getIngredients().size(), recipe.getMinutes(), recipe.getServings()));

            if (result.isCookable()) {
                textNote.setVisibility(View.GONE);
                imageStatus.setImageResource(R.drawable.ic_check);
            } else {
                textNote.setVisibility(View.VISIBLE);
                textNote.setText(context.getString(R.string.missing_one, result.getMissingNames()));
                imageStatus.setImageResource(R.drawable.ic_close);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onRecipeClick(recipe);
                }
            });
        }
    }
}
