package com.example.taxiezepchukac66;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class DriverAdapter extends ArrayAdapter<RouteActivity.Driver> {

    public DriverAdapter(Context context, List<RouteActivity.Driver> drivers) {
        super(context, 0, drivers);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_driver, parent, false);
        }

        RouteActivity.Driver driver = getItem(position);

        ImageView img = convertView.findViewById(R.id.imgPhoto);
        TextView name = convertView.findViewById(R.id.tvName);
        TextView car = convertView.findViewById(R.id.tvCar);
        RatingBar rating = convertView.findViewById(R.id.ratingBar);

        img.setImageResource(driver.getPhotoRes());
        name.setText(driver.getName());
        car.setText(driver.getCar());
        rating.setRating(driver.getRating());

        return convertView;
    }
}
