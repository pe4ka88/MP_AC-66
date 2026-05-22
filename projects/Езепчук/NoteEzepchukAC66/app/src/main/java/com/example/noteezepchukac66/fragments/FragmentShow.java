package com.example.noteezepchukac66.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.noteezepchukac66.R;
import com.example.noteezepchukac66.adapter.NotesAdapter;
import com.example.noteezepchukac66.db.NotesDBHelper;
import com.example.noteezepchukac66.model.Note;

public class FragmentShow extends Fragment {

    private RecyclerView recyclerView;
    private NotesAdapter adapter;
    private NotesDBHelper db;
    private SwipeRefreshLayout swipeRefresh;
    private TextView textEmpty;

    private LinearLayout actionPanel;
    private Button btnPin, btnDelete;

    private ArrayList<Note> notesList;

    public FragmentShow() {}

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_show, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewNotes);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        textEmpty = view.findViewById(R.id.textEmpty);

        // ===== ИЩЕМ ВНУТРИ ФРАГМЕНТА =====
        actionPanel = view.findViewById(R.id.actionPanel);
        btnPin = view.findViewById(R.id.btnPin);
        btnDelete = view.findViewById(R.id.btnDelete);

        db = new NotesDBHelper(requireContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        swipeRefresh.setOnRefreshListener(this::loadNotes);

        setupActionButtons();
        loadNotes();

        return view;
    }

    private void loadNotes() {
        notesList = db.getNotes();

        Collections.sort(notesList, (n1, n2) -> {
            if (n1.getIs_pined() != n2.getIs_pined()) {
                return Integer.compare(n2.getIs_pined(), n1.getIs_pined());
            }
            return Integer.compare(n2.getId(), n1.getId());
        });
        Log.i("FragmentShow", "Loaded notes count: " + notesList.size());

        adapter = new NotesAdapter(
                requireContext(),
                notesList,
                note -> {
                    if (adapter.getSelectedNotes().isEmpty()) {
                        FragmentUpdate fragmentEdit = new FragmentUpdate();
                        Bundle args = new Bundle();
                        args.putInt("id", note.getId());
                        args.putString("title", note.getTitle());
                        args.putString("description", note.getDescription());
                        fragmentEdit.setArguments(args);

                        requireActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container, fragmentEdit)
                                .addToBackStack(null)
                                .commit();
                    }
                },
                isActive -> actionPanel.setVisibility(isActive ? View.VISIBLE : View.GONE)
        );

        recyclerView.setAdapter(adapter);
        swipeRefresh.setRefreshing(false);

        textEmpty.setVisibility(notesList.isEmpty() ? View.VISIBLE : View.GONE);
    }
    public void logAllEmbeddings(List<Float> queryEmbedding) {

        ArrayList<Note> allNotes = db.getNotes();

        Log.i("FragmentShow", "===== EMBEDDING DEBUG START =====");
        Log.i("FragmentShow", "Total notes: " + allNotes.size());

        for (Note note : allNotes) {

            List<Float> emb = note.getEmbedding();

            if (emb == null) {
                Log.e("FragmentShow", "Note: " + note.getTitle() + " -> EMBEDDING IS NULL");
                continue;
            }

            if (emb.isEmpty()) {
                Log.e("FragmentShow", "Note: " + note.getTitle() + " -> EMBEDDING IS EMPTY");
                continue;
            }

            float norm = 0f;
            float sum = 0f;
            float min = Float.MAX_VALUE;
            float max = Float.MIN_VALUE;
            boolean hasInvalid = false;

            for (Float v : emb) {
                if (v == null || Float.isNaN(v) || Float.isInfinite(v)) {
                    hasInvalid = true;
                    break;
                }
                sum += v;
                norm += v * v;
                if (v < min) min = v;
                if (v > max) max = v;
            }

            norm = (float) Math.sqrt(norm);
            float avg = sum / emb.size();

            StringBuilder preview = new StringBuilder();
            int previewCount = Math.min(10, emb.size());
            for (int i = 0; i < previewCount; i++) {
                preview.append(emb.get(i));
                if (i < previewCount - 1) preview.append(", ");
            }

            float cosineVsQuery = queryEmbedding != null ? cosineSimilarity(queryEmbedding, emb) : Float.NaN;

            Log.i("FragmentShow",
                    "Note: " + note.getTitle() +
                            "\n size: " + emb.size() +
                            "\n norm: " + norm +
                            "\n avg: " + avg +
                            "\n min: " + min +
                            "\n max: " + max +
                            "\n invalidValues: " + hasInvalid +
                            "\n preview(10): [" + preview + "]" +
                            (queryEmbedding != null ? "\n cosine vs query: " + cosineVsQuery : "")
            );
        }

        Log.i("FragmentShow", "===== EMBEDDING DEBUG END =====");
    }
    private void setupActionButtons() {
        btnPin.setOnClickListener(v -> {
            ArrayList<Note> selected = adapter.getSelectedNotes();
            Log.i("FragmentShow", "Pin clicked. Selected notes: " + selected.size());

            for (Note n : selected) {
                int newState = n.getIs_pined() == 1 ? 0 : 1;
                db.updatePin(n.getId(), newState);
                Log.i("FragmentShow", "Note " + n.getTitle() + " pinned state set to " + newState);
            }

            adapter.clearSelection();
            actionPanel.setVisibility(View.GONE);
            loadNotes();
        });

        btnDelete.setOnClickListener(v -> {
            ArrayList<Note> selected = adapter.getSelectedNotes();
            Log.i("FragmentShow", "Delete clicked. Selected notes: " + selected.size());

            for (Note n : selected) {
                db.deleteNote(n.getId());
                Log.i("FragmentShow", "Deleted note: " + n.getTitle());
            }

            adapter.clearSelection();
            actionPanel.setVisibility(View.GONE);
            Toast.makeText(requireContext(), "Удалено", Toast.LENGTH_SHORT).show();
            loadNotes();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadNotes();
    }

    public void semanticSearch(List<Float> queryEmbedding) {

        if (queryEmbedding == null || queryEmbedding.isEmpty()) {
            Log.e("FragmentShow", "Query embedding is null or empty!");
            return;
        }

        Log.i("FragmentShow", "===== SEMANTIC SEARCH START =====");
        Log.i("FragmentShow", "Query embedding size: " + queryEmbedding.size());

        ArrayList<Note> allNotes = db.getNotes();
        ArrayList<Note> filteredNotes = new ArrayList<>();

        for (Note note : allNotes) {

            List<Float> emb = note.getEmbedding();
            if (emb == null || emb.isEmpty()) {
                Log.i("FragmentShow", "Skipping note: " + note.getTitle() + " (embedding missing)");
                continue;
            }

            float sim = cosineSimilarity(queryEmbedding, emb);
            note.setSimilarity(sim);

            Log.i("FragmentShow", "Note: " + note.getTitle() + ", similarity vs query: " + sim);

            if (sim > 0.45f) { // можно подкорректировать порог
                filteredNotes.add(note);
            }
        }

        Collections.sort(filteredNotes, (n1, n2) -> Float.compare(n2.getSimilarity(), n1.getSimilarity()));

        adapter = new NotesAdapter(requireContext(), filteredNotes, note -> {}, isActive -> {});
        recyclerView.setAdapter(adapter);
        textEmpty.setVisibility(filteredNotes.isEmpty() ? View.VISIBLE : View.GONE);

        Log.i("FragmentShow", "Filtered notes count: " + filteredNotes.size());
        Log.i("FragmentShow", "===== SEMANTIC SEARCH END =====");
    }

    private float cosineSimilarity(List<Float> a, List<Float> b) {
        if (a == null || b == null) return 0f;
        if (a.size() != b.size()) return 0f;

        float dot = 0f;
        float normA = 0f;
        float normB = 0f;

        for (int i = 0; i < a.size(); i++) {
            dot += a.get(i) * b.get(i);
            normA += a.get(i) * a.get(i);
            normB += b.get(i) * b.get(i);
        }

        if (normA == 0f || normB == 0f) return 0f;

        return (float) (dot / (Math.sqrt(normA) * Math.sqrt(normB)));
    }

}