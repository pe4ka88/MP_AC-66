package com.example.lab4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListViewAdapter extends ArrayAdapter<JSONObject> {
    private int listLayout;
    private ArrayList<JSONObject> usersList;
    private Context context;
    private boolean isFullMode = true;

    public ListViewAdapter(Context context, int listLayout, ArrayList<JSONObject> usersList) {
        super(context, listLayout, usersList);
        this.context = context;
        this.listLayout = listLayout;
        this.usersList = usersList;
    }

    public void setFullMode(boolean fullMode) {
        this.isFullMode = fullMode;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(listLayout, parent, false);
        }

        TextView name = convertView.findViewById(R.id.textViewName);
        TextView email = convertView.findViewById(R.id.textViewEmail);
        TextView id = convertView.findViewById(R.id.textViewId);

        try {
            JSONObject user = usersList.get(position);
            name.setText(user.getString("name"));
            id.setText("ID: " + user.getString("id"));
            
            if (isFullMode) {
                email.setVisibility(View.VISIBLE);
                email.setText(user.getString("email"));
            } else {
                email.setVisibility(View.GONE);
            }
        } catch (JSONException je) {
            je.printStackTrace();
        }
        
        return convertView;
    }
}
