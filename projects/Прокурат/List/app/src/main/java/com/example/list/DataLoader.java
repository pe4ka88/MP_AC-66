package com.example.list;

import android.content.Context;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import java.lang.reflect.Type;
import java.util.List;

public class DataLoader {

    public interface DataLoadListener {
        void onSuccess(List<Item> itemList);
        void onError(String error);
    }

    private Context context;
    private RequestQueue queue;

    public DataLoader(Context context) {
        this.context = context;
        queue = Volley.newRequestQueue(context);
    }

    public void loadData(String url, final DataLoadListener listener) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Item>>(){}.getType();
                        List<Item> itemList = gson.fromJson(response.toString(), listType);
                        listener.onSuccess(itemList);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onError(error.getMessage());
                    }
                });
        queue.add(jsonArrayRequest);
    }
}