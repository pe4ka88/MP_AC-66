package com.example.geo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geo.model.RouteItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RoutesAdapter extends RecyclerView.Adapter<RoutesAdapter.VH> {

    private final List<RouteItem> routes;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public RoutesAdapter(List<RouteItem> routes) {
        this.routes = routes;
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView placeText;
        TextView timeText;
        View iconMoving; // Например, ImageView с иконкой пешехода
        View iconStay;   // Иконка точки на карте

        public VH(View v) {
            super(v);
            placeText = v.findViewById(R.id.routePlace);
            timeText = v.findViewById(R.id.routeTime);
            // Добавь эти ID в свой layout item_route.xml
            iconMoving = v.findViewById(R.id.iconMoving);
            iconStay = v.findViewById(R.id.iconStay);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        RouteItem r = routes.get(position);

        // Настройка текста
        if (r.isMoving) {
            holder.placeText.setText(r.placeName);
            if (holder.iconMoving != null) holder.iconMoving.setVisibility(View.VISIBLE);
            if (holder.iconStay != null) holder.iconStay.setVisibility(View.GONE);
        } else {
            holder.placeText.setText(r.placeName);
            if (holder.iconMoving != null) holder.iconMoving.setVisibility(View.GONE);
            if (holder.iconStay != null) holder.iconStay.setVisibility(View.VISIBLE);
        }

        String start = timeFormat.format(new Date(r.start.timestamp));
        String end = timeFormat.format(new Date(r.end.timestamp));

        long durationMin = (r.end.timestamp - r.start.timestamp) / 60000;
        holder.timeText.setText(String.format("%s - %s (%d min)", start, end, durationMin));
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_route, parent, false);
        return new VH(v);
    }

    @Override
    public int getItemCount() { return routes.size(); }
}