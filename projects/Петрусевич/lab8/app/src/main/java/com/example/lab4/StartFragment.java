package com.example.lab4;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class StartFragment extends Fragment {

    private EditText mEditURL, etRowsPerPage;
    private TextInputLayout mTextInputLayout;
    private RadioGroup radioGroup;
    private ArrayList<String> sourceList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.start_view, container, false);

        radioGroup = view.findViewById(R.id.radioGroup);
        sourceList.add("https://drive.google.com/uc?id=1kOZj7iojuiEPGcpoBRjO6sFHiLN4nPKb");
        sourceList.add("https://drive.google.com/uc?id=1iIcSzxcs5eUKUKgc3E-RBA7RRyy4jiFu");

        Button downloadButton = view.findViewById(R.id.DwnBtn);
        mEditURL = view.findViewById(R.id.editTextURL);
        etRowsPerPage = view.findViewById(R.id.etRowsPerPage);
        mTextInputLayout = view.findViewById(R.id.textInputLayout);

        downloadButton.setOnClickListener(v -> {
            try {
                String jsonUrl;
                int checkedId = radioGroup.getCheckedRadioButtonId();
                
                if (checkedId == R.id.radioButton1) {
                    jsonUrl = sourceList.get(0);
                } else if (checkedId == R.id.radioButton2) {
                    jsonUrl = sourceList.get(1);
                } else {
                    jsonUrl = mEditURL.getText().toString();
                    if (jsonUrl.length() < 4 || !jsonUrl.startsWith("http")) {
                        mTextInputLayout.setError(getString(R.string.error));
                        return;
                    }
                }
                
                mTextInputLayout.setError(null);
                
                String rowsStr = etRowsPerPage.getText().toString();
                int rowsPerPage = rowsStr.isEmpty() ? 5 : Integer.parseInt(rowsStr);
                if (rowsPerPage <= 0) throw new NumberFormatException();

                Bundle bundle = new Bundle();
                bundle.putString("url", jsonUrl);
                bundle.putInt("rowsPerPage", rowsPerPage);

                ListFragment listFragment = new ListFragment();
                listFragment.setArguments(bundle);

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, listFragment)
                        .addToBackStack(null)
                        .commit();
                        
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Invalid number of rows", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
