package com.example.lab7.ui;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.lab7.R;

public final class UiHelpers {

    private UiHelpers() {
    }

    public static void bindAuthorButton(Activity activity) {
        Button button = activity.findViewById(R.id.authorActionButton);
        if (button != null) {
            button.setOnClickListener(v -> Toast.makeText(
                    activity,
                    activity.getString(R.string.author_signature),
                    Toast.LENGTH_SHORT
            ).show());
        }
    }

    public static void bindBack(Activity activity) {
        View back = activity.findViewById(R.id.btnBack);
        if (back != null) {
            back.setOnClickListener(v -> activity.finish());
        }
    }
}
