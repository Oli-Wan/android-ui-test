package com.example.AndroidUITest.activities.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import com.example.AndroidUITest.R;
import com.example.AndroidUITest.models.Mission;
import com.example.AndroidUITest.storage.CommandOpenHelper;
import com.example.AndroidUITest.storage.MissionOpenHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateMissionFragment extends Fragment implements View.OnClickListener {

    private Mission mission;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.update_mision, container, false);

        Button saveButton = (Button) view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);

        Spinner spinner = (Spinner) view.findViewById(R.id.typeSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.mission_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Bundle extras = getActivity().getIntent().getExtras();
        long missionId = extras.getLong("MISSION_ID");
        this.mission = new MissionOpenHelper(getActivity().getBaseContext()).get(missionId);

        bindDataWithView(view);

        return view;
    }

    private void bindDataWithView(View view) {
        EditText obsEdit = (EditText) view.findViewById(R.id.obsEdit);
        String observation = this.mission.getObservation();
        obsEdit.setText(observation);
    }

    private void bindViewWithData() {
        EditText obsEdit = (EditText) view.findViewById(R.id.obsEdit);
        Editable text = obsEdit.getText();
        mission.setObservation(text.toString());
    }

    @Override
    public void onClick(View view) {
        bindViewWithData();
        MissionOpenHelper missionOpenHelper = new MissionOpenHelper(getActivity().getBaseContext());
        Mission oldMission = missionOpenHelper.get(mission.getId());

        new CommandOpenHelper(getActivity().getBaseContext()).createCommandIfNeeded(oldMission, mission);

        missionOpenHelper.update(mission);
    }
}
