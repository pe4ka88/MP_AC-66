package com.example.a7lab;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private MaterialButton btnImport;
    private TrackAdapter adapter;
    private List<Track> trackList;
    private SharedPreferences sharedPreferences;
    private Gson gson;
    private ActivityResultLauncher<String[]> importLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        tvEmpty = findViewById(R.id.tvEmpty);
        btnImport = findViewById(R.id.btnImport);

        sharedPreferences = getSharedPreferences("MusicPlayer", MODE_PRIVATE);
        gson = new Gson();
        trackList = loadTracks();

        adapter = new TrackAdapter(trackList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        updateUI();

        importLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenMultipleDocuments(),
                uris -> {
                    if (uris != null && !uris.isEmpty()) {
                        for (Uri uri : uris) {
                            getContentResolver().takePersistableUriPermission(uri,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION);

                            String fileName = getFileName(uri);
                            String uriString = uri.toString();

                            if (!isTrackExists(uriString)) {
                                trackList.add(new Track(fileName, uriString));
                            }
                        }
                        saveTracks();
                        adapter.notifyDataSetChanged();
                        updateUI();
                    }
                }
        );

        btnImport.setOnClickListener(v -> {
            importLauncher.launch(new String[]{"audio/*"});
        });
    }

    private boolean isTrackExists(String uri) {
        for (Track track : trackList) {
            if (track.getUri().equals(uri)) {
                return true;
            }
        }
        return false;
    }

    private String getFileName(Uri uri) {
        String result = "Неизвестно";
        try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex >= 0) {
                    result = cursor.getString(nameIndex);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private void saveTracks() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(trackList);
        editor.putString("tracks", json);
        editor.apply();
    }

    private List<Track> loadTracks() {
        String json = sharedPreferences.getString("tracks", null);
        if (json != null) {
            Type type = new TypeToken<List<Track>>(){}.getType();
            return gson.fromJson(json, type);
        }
        return new ArrayList<>();
    }

    private void updateUI() {
        if (trackList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        }
    }

    class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.TrackViewHolder> {
        private List<Track> tracks;

        public TrackAdapter(List<Track> tracks) {
            this.tracks = tracks;
        }

        @Override
        public TrackViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_track, parent, false);
            return new TrackViewHolder(view);
        }

        @Override
        public void onBindViewHolder(TrackViewHolder holder, int position) {
            Track track = tracks.get(position);
            holder.tvTrackName.setText(track.getName());
            holder.tvTrackPath.setText(track.getUri());

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
                intent.putExtra("track_index", position);
                intent.putExtra("tracks", gson.toJson(tracks));
                startActivity(intent);
            });

            holder.itemView.setOnLongClickListener(v -> {
                tracks.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, tracks.size());
                saveTracks();
                updateUI();
                Toast.makeText(MainActivity.this, "Трек удалён", Toast.LENGTH_SHORT).show();
                return true;
            });
        }

        @Override
        public int getItemCount() {
            return tracks.size();
        }

        class TrackViewHolder extends RecyclerView.ViewHolder {
            TextView tvTrackName;
            TextView tvTrackPath;

            public TrackViewHolder(View itemView) {
                super(itemView);
                tvTrackName = itemView.findViewById(R.id.tvTrackName);
                tvTrackPath = itemView.findViewById(R.id.tvTrackPath);
            }
        }
    }
}