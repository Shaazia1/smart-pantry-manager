package com.richfield.smartpantry.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.richfield.smartpantry.R;
import com.richfield.smartpantry.model.PantryItem;
import com.richfield.smartpantry.util.DateUtils;
import com.richfield.smartpantry.util.TextFormat;

import java.util.ArrayList;
import java.util.List;

// Puts the pantry items into the RecyclerView. onCreateViewHolder makes a row
// from item_pantry.xml, onBindViewHolder fills that row with one item.
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    // lets the fragment know which item a button belonged to
    public interface OnItemActionListener {
        void onEditRequested(PantryItem item);

        void onDeleteRequested(PantryItem item);
    }

    // items expiring within this many days get the amber warning chip
    private static final int WARNING_DAYS = 3;

    private final List<PantryItem> items = new ArrayList<>();
    private final OnItemActionListener listener;
    private boolean expiryAlertsEnabled = true;

    public PantryAdapter(OnItemActionListener listener) {
        this.listener = listener;
    }

    public void setExpiryAlertsEnabled(boolean enabled) {
        this.expiryAlertsEnabled = enabled;
        notifyDataSetChanged();
    }

    public void submitList(List<PantryItem> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // Holds the views of one row so findViewById only runs once per row
    class PantryViewHolder extends RecyclerView.ViewHolder {

        private final TextView textName;
        private final TextView textQuantity;
        private final TextView textExpiry;
        private final ImageButton buttonEdit;
        private final ImageButton buttonDelete;

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textName);
            textQuantity = itemView.findViewById(R.id.textQuantity);
            textExpiry = itemView.findViewById(R.id.textExpiry);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }

        // Green for a date that is still far off, amber when it is close and red
        // once it has passed. Nothing shows when no date was set.
        private void bindExpiry(PantryItem item) {
            Context context = itemView.getContext();
            int days = DateUtils.daysUntil(item.getExpiryDate());

            if (days == Integer.MAX_VALUE) {
                textExpiry.setVisibility(View.GONE);
                return;
            }
            textExpiry.setVisibility(View.VISIBLE);

            if (days < 0) {
                textExpiry.setText(R.string.expired);
                textExpiry.setBackgroundResource(R.drawable.bg_chip_red);
                textExpiry.setTextColor(ContextCompat.getColor(context, R.color.red_600));
            } else if (expiryAlertsEnabled && days == 0) {
                textExpiry.setText(R.string.expires_today);
                textExpiry.setBackgroundResource(R.drawable.bg_chip_amber);
                textExpiry.setTextColor(ContextCompat.getColor(context, R.color.amber_600));
            } else if (expiryAlertsEnabled && days <= WARNING_DAYS) {
                textExpiry.setText(context.getString(R.string.expires_in_days, days));
                textExpiry.setBackgroundResource(R.drawable.bg_chip_amber);
                textExpiry.setTextColor(ContextCompat.getColor(context, R.color.amber_600));
            } else {
                textExpiry.setText(context.getString(R.string.expires_on,
                        DateUtils.toDisplay(item.getExpiryDate())));
                textExpiry.setBackgroundResource(R.drawable.bg_chip_green);
                textExpiry.setTextColor(ContextCompat.getColor(context, R.color.green_700));
            }
        }

        void bind(final PantryItem item) {
            textName.setText(item.getName());
            textQuantity.setText(TextFormat.amount(item.getQuantity(), item.getUnit()));
            bindExpiry(item);

            buttonEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onEditRequested(item);
                }
            });

            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onDeleteRequested(item);
                }
            });

            // tapping the row itself opens the edit screen too
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onEditRequested(item);
                }
            });
        }
    }
}
