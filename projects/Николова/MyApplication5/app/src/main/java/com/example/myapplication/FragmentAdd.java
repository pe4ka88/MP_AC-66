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

public class FragmentAdd extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add, container, false);

        final EditText etDesc = v.findViewById(R.id.etDesc);
        Button btnAdd = v.findViewById(R.id.btnAdd);
        final DatabaseHelper dbHelper = new DatabaseHelper(getContext());

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put(DatabaseHelper.COLUMN_DESC, etDesc.getText().toString());
                db.insert(DatabaseHelper.TABLE_NAME, null, cv);
                Toast.makeText(getContext(), "Заметка добавлена", Toast.LENGTH_SHORT).show();
                etDesc.setText("");
            }
        });
        return v;
    }
}
