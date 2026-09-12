package com.richfield.smartpantry.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.richfield.smartpantry.R;
import com.richfield.smartpantry.adapter.PantryAdapter;
import com.richfield.smartpantry.db.PantryRepository;
import com.richfield.smartpantry.model.PantryItem;

import java.util.List;

// Shows everything in the pantry. Adding and editing comes next, for now the
// list can be read and items can be deleted.
public class PantryListFragment extends Fragment implements PantryAdapter.OnItemActionListener {

    private PantryRepository repository;
    private PantryAdapter adapter;

    private RecyclerView recyclerView;
    private View emptyState;
    private TextView textItemCount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pantry, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new PantryRepository(requireContext());

        recyclerView = view.findViewById(R.id.recyclerPantry);
        emptyState = view.findViewById(R.id.emptyState);
        textItemCount = view.findViewById(R.id.textItemCount);

        adapter = new PantryAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = view.findViewById(R.id.fabAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View clicked) {
                Toast.makeText(requireContext(), R.string.coming_soon, Toast.LENGTH_SHORT).show();
            }
        });

        addTestItems();
    }

    // Reading in onResume means the list is refreshed when we come back from
    // another screen, not just when the fragment is first made.
    @Override
    public void onResume() {
        super.onResume();
        loadPantry();
    }

    private void loadPantry() {
        List<PantryItem> items = repository.getAll();
        adapter.submitList(items);

        boolean empty = items.isEmpty();
        emptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
        textItemCount.setVisibility(empty ? View.GONE : View.VISIBLE);
        textItemCount.setText(getString(R.string.item_count, items.size()));
    }

    @Override
    public void onDeleteRequested(final PantryItem item) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.confirm_delete_title)
                .setMessage(getString(R.string.confirm_delete_body, item.getName()))
                .setNegativeButton(R.string.action_cancel, null)
                .setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        repository.delete(item.getId());
                        loadPantry();
                        Toast.makeText(requireContext(),
                                getString(R.string.deleted_item, item.getName()),
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    // temporary, just so there is something in the list before the add screen
    // is built. Delete this once adding works.
    private void addTestItems() {
        if (repository.count() > 0) {
            return;
        }
        repository.insert(new PantryItem(-1, "Eggs", 6, "piece", null));
        repository.insert(new PantryItem(-1, "Milk", 500, "ml", null));
        repository.insert(new PantryItem(-1, "Rice", 1, "kg", null));
    }
}
