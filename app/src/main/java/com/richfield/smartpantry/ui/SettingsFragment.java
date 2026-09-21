package com.richfield.smartpantry.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.richfield.smartpantry.R;
import com.richfield.smartpantry.db.RecipeRepository;
import com.richfield.smartpantry.util.Prefs;

// Settings screen. Every change is saved straight away and the other screens
// read it back when they come to the front.
public class SettingsFragment extends Fragment {

    private Prefs prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefs = new Prefs(requireContext());

        SwitchMaterial switchExpiry = view.findViewById(R.id.switchExpiryAlerts);
        switchExpiry.setChecked(prefs.isExpiryAlertsEnabled());
        switchExpiry.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean checked) {
                prefs.setExpiryAlertsEnabled(checked);
            }
        });

        SwitchMaterial switchAlmost = view.findViewById(R.id.switchAlmostThere);
        switchAlmost.setChecked(prefs.isAlmostThereEnabled());
        switchAlmost.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean checked) {
                prefs.setAlmostThereEnabled(checked);
            }
        });

        RadioGroup radioUnits = view.findViewById(R.id.radioUnits);
        radioUnits.check(Prefs.UNITS_IMPERIAL.equals(prefs.getUnitSystem())
                ? R.id.radioImperial : R.id.radioMetric);
        radioUnits.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                prefs.setUnitSystem(checkedId == R.id.radioImperial
                        ? Prefs.UNITS_IMPERIAL : Prefs.UNITS_METRIC);
            }
        });

        MaterialButton buttonReset = view.findViewById(R.id.buttonResetRecipes);
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View clicked) {
                new RecipeRepository(requireContext()).resetRecipes();
                Toast.makeText(requireContext(), R.string.settings_reset_done,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
