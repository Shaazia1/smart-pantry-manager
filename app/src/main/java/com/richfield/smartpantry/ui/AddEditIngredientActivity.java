package com.richfield.smartpantry.ui;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.richfield.smartpantry.R;
import com.richfield.smartpantry.db.PantryRepository;
import com.richfield.smartpantry.model.PantryItem;
import com.richfield.smartpantry.util.DateUtils;
import com.richfield.smartpantry.util.Prefs;
import com.richfield.smartpantry.util.TextFormat;

import java.util.Calendar;

// Used for adding a new ingredient and for editing one that already exists.
// The pantry list sends the row id through the intent when it wants an edit,
// and leaves it out when the user tapped the + button.
public class AddEditIngredientActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "extra_item_id";
    private static final long NO_ID = -1L;
    private static final double MAX_QUANTITY = 10000;

    private PantryRepository repository;
    private Prefs prefs;

    private TextInputLayout layoutName;
    private TextInputLayout layoutQuantity;
    private TextInputLayout layoutUnit;
    private TextInputLayout layoutExpiry;
    private TextInputEditText editName;
    private TextInputEditText editQuantity;
    private TextInputEditText editExpiry;
    private AutoCompleteTextView dropdownUnit;

    private long itemId = NO_ID;
    private PantryItem existingItem;
    private String selectedExpiry;      // yyyy-MM-dd or null

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        repository = new PantryRepository(this);
        prefs = new Prefs(this);

        layoutName = findViewById(R.id.layoutName);
        layoutQuantity = findViewById(R.id.layoutQuantity);
        layoutUnit = findViewById(R.id.layoutUnit);
        layoutExpiry = findViewById(R.id.layoutExpiry);
        editName = findViewById(R.id.editName);
        editQuantity = findViewById(R.id.editQuantity);
        editExpiry = findViewById(R.id.editExpiry);
        dropdownUnit = findViewById(R.id.dropdownUnit);

        setUpUnitDropdown();
        setUpExpiryPicker();

        itemId = getIntent().getLongExtra(EXTRA_ITEM_ID, NO_ID);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(itemId == NO_ID ? R.string.add_ingredient : R.string.edit_ingredient);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (itemId != NO_ID) {
            loadExistingItem();
        }

        findViewById(R.id.buttonSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        // the delete button only makes sense when editing something that exists
        MaterialButton buttonDelete = findViewById(R.id.buttonDelete);
        buttonDelete.setVisibility(itemId == NO_ID ? View.GONE : View.VISIBLE);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDelete();
            }
        });
    }

    private void setUpUnitDropdown() {
        String[] units = getResources().getStringArray(R.array.units);
        dropdownUnit.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, units));
        dropdownUnit.setText(prefs.getDefaultUnit(), false);
    }

    // the expiry field can't be typed in, tapping it opens the date picker
    private void setUpExpiryPicker() {
        View.OnClickListener openPicker = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        };
        editExpiry.setOnClickListener(openPicker);
        layoutExpiry.setEndIconOnClickListener(openPicker);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        if (DateUtils.parse(selectedExpiry) != null) {
            calendar.setTime(DateUtils.parse(selectedExpiry));
        }

        DatePickerDialog dialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        Calendar chosen = Calendar.getInstance();
                        chosen.set(year, month, day);
                        selectedExpiry = DateUtils.toStorage(chosen);
                        editExpiry.setText(DateUtils.toDisplay(selectedExpiry));
                        layoutExpiry.setError(null);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        // food already in the pantry can't have expired before today
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dialog.show();
    }

    private void loadExistingItem() {
        existingItem = repository.getById(itemId);
        if (existingItem == null) {
            Toast.makeText(this, R.string.error_item_missing, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        editName.setText(existingItem.getName());
        editQuantity.setText(TextFormat.quantity(existingItem.getQuantity()));
        dropdownUnit.setText(existingItem.getUnit(), false);
        selectedExpiry = existingItem.getExpiryDate();
        editExpiry.setText(DateUtils.toDisplay(selectedExpiry));
    }

    private void save() {
        if (!validateForm()) {
            return;     // nothing is written to the database until the form is clean
        }

        String name = text(editName);
        double quantity = Double.parseDouble(text(editQuantity));
        String unit = dropdownUnit.getText().toString().trim();

        PantryItem item = existingItem == null ? new PantryItem() : existingItem;
        item.setName(name);
        item.setQuantity(quantity);
        item.setUnit(unit);
        item.setExpiryDate(selectedExpiry);

        if (item.isNew()) {
            repository.insert(item);
        } else {
            repository.update(item);
        }

        Toast.makeText(this, getString(R.string.saved_item, name), Toast.LENGTH_SHORT).show();
        finish();     // the pantry list reloads itself in onResume
    }

    // Checks every field and puts the message under the one that is wrong.
    private boolean validateForm() {
        boolean valid = true;
        layoutName.setError(null);
        layoutQuantity.setError(null);
        layoutUnit.setError(null);
        layoutExpiry.setError(null);

        String name = text(editName);
        if (name.isEmpty()) {
            layoutName.setError(getString(R.string.error_name_required));
            valid = false;
        } else if (name.length() < 2) {
            layoutName.setError(getString(R.string.error_name_too_short));
            valid = false;
        } else if (repository.existsByName(name, itemId)) {
            layoutName.setError(getString(R.string.error_duplicate));
            valid = false;
        }

        String quantityText = text(editQuantity);
        if (quantityText.isEmpty()) {
            layoutQuantity.setError(getString(R.string.error_quantity_required));
            valid = false;
        } else {
            try {
                double quantity = Double.parseDouble(quantityText);
                if (quantity <= 0) {
                    layoutQuantity.setError(getString(R.string.error_quantity_invalid));
                    valid = false;
                } else if (quantity > MAX_QUANTITY) {
                    layoutQuantity.setError(getString(R.string.error_quantity_too_large));
                    valid = false;
                }
            } catch (NumberFormatException e) {
                // catches things like "1.2.3" that the number keyboard still allows
                layoutQuantity.setError(getString(R.string.error_quantity_invalid));
                valid = false;
            }
        }

        if (dropdownUnit.getText().toString().trim().isEmpty()) {
            layoutUnit.setError(getString(R.string.error_unit_required));
            valid = false;
        }

        if (selectedExpiry != null && DateUtils.daysUntil(selectedExpiry) < 0) {
            layoutExpiry.setError(getString(R.string.error_expiry_past));
            valid = false;
        }

        if (!valid) {
            focusFirstError();
        }
        return valid;
    }

    private void focusFirstError() {
        if (layoutName.getError() != null) {
            editName.requestFocus();
        } else if (layoutQuantity.getError() != null) {
            editQuantity.requestFocus();
        } else if (layoutUnit.getError() != null) {
            dropdownUnit.requestFocus();
        }
    }

    private void confirmDelete() {
        final String name = existingItem == null ? "" : existingItem.getName();
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_delete_title)
                .setMessage(getString(R.string.confirm_delete_body, name))
                .setNegativeButton(R.string.action_cancel, null)
                .setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        repository.delete(itemId);
                        Toast.makeText(AddEditIngredientActivity.this,
                                getString(R.string.deleted_item, name),
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .show();
    }

    private String text(TextView view) {
        return view.getText() == null ? "" : view.getText().toString().trim();
    }
}
