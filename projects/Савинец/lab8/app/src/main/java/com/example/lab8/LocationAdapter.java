package com.example.geotracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class LocationAdapter extends BaseAdapter {

    private List<LocationPoint> locationList;
    private LayoutInflater inflater;

    public LocationAdapter(List<LocationPoint> locationList, LayoutInflater inflater) {
        this.locationList = locationList;
        this.inflater = inflater;
    }

    public void updateData(List<LocationPoint> newList) {
        this.locationList = newList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return locationList != null ? locationList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return locationList != null ? locationList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return locationList != null ? locationList.get(position).getId() : 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_location, parent, false);
            holder = new ViewHolder();
            holder.tvCoordinates = convertView.findViewById(R.id.tvCoordinates);
            holder.tvDateTime = convertView.findViewById(R.id.tvDateTime);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (locationList != null && position < locationList.size()) {
            LocationPoint point = locationList.get(position);
            holder.tvCoordinates.setText(String.format("%.4f, %.4f",
                    point.getLatitude(), point.getLongitude()));
            holder.tvDateTime.setText(point.getFormattedDate());
        }

        return convertView;
    }

    static class ViewHolder {
        TextView tvCoordinates;
        TextView tvDateTime;
    }
}