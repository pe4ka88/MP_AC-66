package com.example.myapplication;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

public class FragmentDel extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_del, container, false);

        final EditText etId = v.findViewById(R.id.etId);
        Button btnDel = v.findViewById(R.id.btnDel);
        final DatabaseHelper dbHelper = new DatabaseHelper(getContext());

        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                int rows = db.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.COLUMN_ID + " = ?", new String[]{etId.getText().toString()});
                if (rows > 0) Toast.makeText(getContext(), "Удалено", Toast.LENGTH_SHORT).show();
                etId.setText("");
            }
        });
        return v;
    }
}
