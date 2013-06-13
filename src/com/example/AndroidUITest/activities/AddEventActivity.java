package com.example.AndroidUITest.activities;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.example.AndroidUITest.R;
import com.example.AndroidUITest.activities.fragments.DatePickerFragment;
import com.example.AndroidUITest.activities.fragments.TimePickerFragment;


public class AddEventActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event);

        Spinner typeSpinner = (Spinner) findViewById(R.id.typeSpinner);
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(getBaseContext(),
                R.array.event_types, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);


        Spinner vehicleSpinner = (Spinner) findViewById(R.id.vehicleSpinner);
        ArrayAdapter<CharSequence> vehiclesAdapter = ArrayAdapter.createFromResource(getBaseContext(),
                R.array.vehicle_types, android.R.layout.simple_spinner_item);
        vehiclesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicleSpinner.setAdapter(vehiclesAdapter);
    }

    public void pickStartTime(View view) {
        DialogFragment frag = new TimePickerFragment();
        frag.show(getFragmentManager(), "timePicker");
    }

    public void pickStartDate(View view) {
        DialogFragment frag = new DatePickerFragment();
        frag.show(getFragmentManager(), "datePicker");
    }

    public void pickEndTime(View view) {
        DialogFragment frag = new TimePickerFragment();
        frag.show(getFragmentManager(), "timePicker");
    }

    public void pickEndDate(View view) {
        DialogFragment frag = new DatePickerFragment();
        frag.show(getFragmentManager(), "datePicker");
    }
}