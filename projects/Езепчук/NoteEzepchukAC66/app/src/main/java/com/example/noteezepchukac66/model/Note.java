package com.example.noteezepchukac66.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Note {

    private int id;
    private String title;
    private String description;
    private String date;
    private float similarity = 0f;
    private List<Float> embedding;

    private int is_pined; // 0 - обычная, 1 - закреплённая

    private static final String TAG = "Note";

    public Note(int id,
                String title,
                String description,
                String date,
                int is_pined,
                List<Float> embedding) {

        this.id = id;
        this.title = title != null ? title : "";
        this.description = description != null ? description : "";
        this.date = date;
        this.is_pined = is_pined;
        this.embedding = embedding != null ? embedding : new ArrayList<>();

        normalizeEmbedding();
    }

    // =====================
    // GETTERS
    // =====================

    public int getId() { return id; }

    public String getTitle() { return title; }

    public String getDescription() { return description; }

    public String getDate() { return date; }

    public int getIs_pined() { return is_pined; }

    public void setIs_pined(int is_pined) {
        this.is_pined = is_pined;
    }

    public List<Float> getEmbedding() {
        return embedding;
    }

    public void setEmbedding(List<Float> embedding) {
        this.embedding = embedding != null ? embedding : new ArrayList<>();
        normalizeEmbedding();
    }

    public float getSimilarity() { return similarity; }

    public void setSimilarity(float similarity) {
        this.similarity = similarity;
    }


    public String getFullText() {
        return "Заголовок: " + title +
                ". Описание: " + description;
    }

    public boolean hasValidEmbedding() {
        return embedding != null && !embedding.isEmpty();
    }


    private void normalizeEmbedding() {
        if (embedding == null || embedding.isEmpty()) return;

        float norm = 0f;
        for (Float v : embedding) {
            norm += v * v;
        }

        norm = (float) Math.sqrt(norm);
        if (norm == 0f) return;

        for (int i = 0; i < embedding.size(); i++) {
            embedding.set(i, embedding.get(i) / norm);
        }
    }


    public String embeddingToString() {
        if (embedding == null || embedding.isEmpty())
            return "[]";

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < embedding.size(); i++) {
            sb.append(embedding.get(i));
            if (i < embedding.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }


    public static List<Float> parseEmbedding(String embString) {

        List<Float> embedding = new ArrayList<>();

        if (embString == null || embString.trim().isEmpty()) {
            Log.i(TAG, "parseEmbedding: empty string");
            return embedding;
        }

        try {
            embString = embString.replace("[", "").replace("]", "");
            String[] parts = embString.split(",");

            for (String p : parts) {
                if (!p.trim().isEmpty()) {
                    embedding.add(Float.parseFloat(p.trim()));
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "parseEmbedding error", e);
        }

        Log.i(TAG, "parseEmbedding: size = " + embedding.size());
        return embedding;
    }
}