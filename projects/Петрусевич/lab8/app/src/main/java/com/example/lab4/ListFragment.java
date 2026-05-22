package com.example.lab4;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class ListFragment extends Fragment {

    private ArrayList<JSONObject> allItems = new ArrayList<>();
    private ArrayList<JSONObject> currentPageItems = new ArrayList<>();
    private ListView listView;
    private ProgressBar progressBar;
    private TextView tvPageInfo;
    private ListViewAdapter adapter;
    
    private int rowsPerPage = 5;
    private int currentPage = 0;
    private boolean isFullView = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        listView = view.findViewById(R.id.listView);
        progressBar = view.findViewById(R.id.progressBar);
        tvPageInfo = view.findViewById(R.id.tvPageInfo);
        
        Button btnPrev = view.findViewById(R.id.btnPrev);
        Button btnNext = view.findViewById(R.id.btnNext);
        Button btnToggle = view.findViewById(R.id.btnToggleView);
        Button goButton = view.findViewById(R.id.goBtn);
        EditText etSearch = view.findViewById(R.id.editTextNumber);

        if (getArguments() != null) {
            String url = getArguments().getString("url");
            rowsPerPage = getArguments().getInt("rowsPerPage", 5);
            loadJSONFromURL(url);
        }

        btnPrev.setOnClickListener(v -> {
            if (currentPage > 0) {
                currentPage--;
                updateDisplay();
            }
        });

        btnNext.setOnClickListener(v -> {
            if ((currentPage + 1) * rowsPerPage < allItems.size()) {
                currentPage++;
                updateDisplay();
            }
        });

        btnToggle.setOnClickListener(v -> {
            isFullView = !isFullView;
            if (adapter != null) adapter.setFullMode(isFullView);
        });

        goButton.setOnClickListener(v -> {
            try {
                int searchId = Integer.parseInt(etSearch.getText().toString());
                int foundPos = -1;
                for (int i = 0; i < allItems.size(); i++) {
                    if (allItems.get(i).getInt("id") == searchId) {
                        foundPos = i;
                        break;
                    }
                }
                
                if (foundPos != -1) {
                    currentPage = foundPos / rowsPerPage;
                    updateDisplay();
                    listView.setSelection(foundPos % rowsPerPage);
                } else {
                    Toast.makeText(getContext(), "ID not found", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), "Error in search", Toast.LENGTH_SHORT).show();
            }
        });

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            DetailsFragment detailsFragment = new DetailsFragment();
            Bundle bundle = new Bundle();
            bundle.putString("item_data", currentPageItems.get(position).toString());
            detailsFragment.setArguments(bundle);

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, detailsFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void loadJSONFromURL(String url) {
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject object = new JSONObject(EncodingToUTF8(response));
                        JSONArray jsonArray = object.getJSONArray("users");
                        allItems = getArrayListFromJSONArray(jsonArray);
                        currentPage = 0;
                        updateDisplay();
                    } catch (JSONException e) {
                        Toast.makeText(getContext(), "JSON Parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Network error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                });
        Volley.newRequestQueue(getContext()).add(stringRequest);
    }

    private void updateDisplay() {
        currentPageItems.clear();
        int start = currentPage * rowsPerPage;
        int end = Math.min(start + rowsPerPage, allItems.size());
        
        for (int i = start; i < end; i++) {
            currentPageItems.add(allItems.get(i));
        }

        if (adapter == null) {
            adapter = new ListViewAdapter(getContext(), R.layout.row, currentPageItems);
            listView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

        int totalPages = (int) Math.ceil((double) allItems.size() / rowsPerPage);
        tvPageInfo.setText(String.format("Page: %d/%d (Total: %d)", currentPage + 1, totalPages, allItems.size()));
    }

    private ArrayList<JSONObject> getArrayListFromJSONArray(JSONArray jsonArray) {
        ArrayList<JSONObject> aList = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                aList.add(jsonArray.getJSONObject(i));
            }
        } catch (JSONException e) { e.printStackTrace(); }
        return aList;
    }

    public static String EncodingToUTF8(String response) {
        try {
            byte[] code = response.getBytes("ISO-8859-1");
            return new String(code, "UTF-8");
        } catch (UnsupportedEncodingException e) { return response; }
    }
}
