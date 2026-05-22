package com.example.myapplication;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

public class FragmentUpdate extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_update, container, false);

        final EditText etId = v.findViewById(R.id.etId);
        final EditText etDesc = v.findViewById(R.id.etDesc);
        Button btnUpdate = v.findViewById(R.id.btnUpdate);
        final DatabaseHelper dbHelper = new DatabaseHelper(getContext());

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put(DatabaseHelper.COLUMN_DESC, etDesc.getText().toString());
                db.update(DatabaseHelper.TABLE_NAME, cv, DatabaseHelper.COLUMN_ID + " = ?", new String[]{etId.getText().toString()});
                Toast.makeText(getContext(), "Обновлено", Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }
}