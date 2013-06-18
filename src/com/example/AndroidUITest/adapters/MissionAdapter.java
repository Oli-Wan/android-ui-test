package com.example.AndroidUITest.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.AndroidUITest.R;
import com.example.AndroidUITest.models.Mission;

import java.util.List;

public class MissionAdapter extends ArrayAdapter<Mission> {
    private final List<Mission> values;
    private final Context context;

    public MissionAdapter(Context context, List<Mission> objects) {
        super(context, R.layout.mission_item, objects);
        this.values = objects;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.mission_item, parent, false);
        TextView idView = (TextView) rowView.findViewById(R.id.id);
        TextView obsView = (TextView) rowView.findViewById(R.id.obs);
        Mission currentMission = values.get(position);
        String id = Long.toString(currentMission.getId());
        idView.setText(id);
        obsView.setText(currentMission.getObservation());
        return rowView;
    }
}
