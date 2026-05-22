package com.example.myapplication;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

public class FragmentShow extends Fragment {
    ListView listView;
    DatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_show, container, false);
        listView = v.findViewById(R.id.listView);
        dbHelper = new DatabaseHelper(getContext());
        refresh();
        return v;
    }

    public void refresh() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME, null, null, null, null, null, null);

        if (cursor.getCount() < 20) {
            Toast.makeText(getContext(), "База отсутствует (<20 записей)", Toast.LENGTH_LONG).show();
        }

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getContext(),
                R.layout.item_note, cursor,
                new String[]{DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_DESC},
                new int[]{R.id.tvId, R.id.tvDesc}, 0);
        listView.setAdapter(adapter);
    }

    @Override
    public void onResume() { super.onResume(); refresh(); }
}
