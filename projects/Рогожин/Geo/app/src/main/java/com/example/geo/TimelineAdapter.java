package com.example.geo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.geo.analytics.MovementSegment;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.VH> {

    private final List<MovementSegment> list;

    private final SimpleDateFormat timeFormat =
            new SimpleDateFormat("HH:mm", Locale.getDefault());

    public TimelineAdapter(List<MovementSegment> list) {
        this.list = list;
    }

    static class VH extends RecyclerView.ViewHolder {

        TextView place;
        TextView time;
        TextView type;

        VH(View v) {
            super(v);

            place = v.findViewById(R.id.placeText);
            time = v.findViewById(R.id.timeText);
            type = v.findViewById(R.id.typeText);
        }
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_timeline, parent, false);

        return new VH(view);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {

        MovementSegment segment = list.get(position);

        String start = timeFormat.format(segment.startTime);
        String end = timeFormat.format(segment.endTime);

        holder.time.setText(start + " - " + end);

        // Используем getter placeName
        String placeText = segment.getPlace();

        // Если есть конечная точка сегмента, показываем её в скобках
        if (segment.endLatitude != 0 && segment.endLongitude != 0) {
            placeText += String.format(Locale.getDefault(),
                    " (%.5f, %.5f)", segment.endLatitude, segment.endLongitude);
        }

        holder.place.setText(placeText.isEmpty() ? "Unknown place" : placeText);

        switch (segment.type) {

            case STOP:
                holder.type.setText("Stay");
                break;

            case WALK:
                holder.type.setText(
                        "Walk • " +
                                String.format(Locale.getDefault(),
                                        "%.2f km",
                                        segment.distance / 1000f)
                );
                break;

            case CAR:
                holder.type.setText(
                        "Drive • " +
                                String.format(Locale.getDefault(),
                                        "%.2f km",
                                        segment.distance / 1000f)
                );
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}