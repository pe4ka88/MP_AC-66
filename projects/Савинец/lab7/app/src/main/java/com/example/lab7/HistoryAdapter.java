package com.example.lab7;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class HistoryAdapter extends BaseAdapter {

    private List<DatabaseHelper.HistoryItem> historyList;
    private LayoutInflater inflater;

    public HistoryAdapter(List<DatabaseHelper.HistoryItem> historyList, LayoutInflater inflater) {
        this.historyList = historyList;
        this.inflater = inflater;
    }

    @Override
    public int getCount() {
        return historyList != null ? historyList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return historyList != null ? historyList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return historyList != null ? historyList.get(position).getId() : 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_history, parent, false);
            holder = new ViewHolder();
            holder.tvTypeIcon = convertView.findViewById(R.id.tvTypeIcon);
            holder.tvFileName = convertView.findViewById(R.id.tvFileName);
            holder.tvTimestamp = convertView.findViewById(R.id.tvTimestamp);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (historyList != null && position < historyList.size()) {
            DatabaseHelper.HistoryItem item = historyList.get(position);

            holder.tvTypeIcon.setText(item.getTypeIcon());
            holder.tvFileName.setText(item.getFileName());
            holder.tvTimestamp.setText(item.getTimestamp());
        }

        return convertView;
    }

    static class ViewHolder {
        TextView tvTypeIcon;
        TextView tvFileName;
        TextView tvTimestamp;
    }
}