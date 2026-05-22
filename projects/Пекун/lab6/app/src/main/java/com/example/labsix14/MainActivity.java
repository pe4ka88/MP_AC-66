package com.example.labsix14;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String TEST_MEDIA_DIR_NAME = "test_media";

    private final ActivityResultLauncher<String[]> openDocumentLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenDocument(), this::onFilePicked);

    private final ActivityResultLauncher<String[]> importDocumentsLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenMultipleDocuments(), this::onImportMediaPicked);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialButton chooseFileButton = findViewById(R.id.buttonChooseFile);
        MaterialButton aboutButton = findViewById(R.id.buttonAbout);
        MaterialButton importMediaButton = findViewById(R.id.buttonImportMedia);

        chooseFileButton.setOnClickListener(v -> openDocumentLauncher.launch(new String[]{
                "image/*",
                "audio/*",
                "video/*"
        }));

        aboutButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        importMediaButton.setOnClickListener(v -> importDocumentsLauncher.launch(new String[]{
                "image/*",
                "audio/*",
                "video/*"
        }));
    }

    private void onFilePicked(Uri uri) {
        if (uri == null) {
            Toast.makeText(this, R.string.error_no_file, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } catch (SecurityException e) {
            Log.d(TAG, "Persistable permission is not available", e);
        }

        String mimeType = getContentResolver().getType(uri);
        Log.d(TAG, "Picked uri: " + uri + ", mimeType=" + mimeType);

        if (TextUtils.isEmpty(mimeType) || (!mimeType.startsWith("image/")
                && !mimeType.startsWith("audio/")
                && !mimeType.startsWith("video/"))) {
            Toast.makeText(this, R.string.unsupported_file_message, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, ViewerActivity.class);
        intent.putExtra(ViewerActivity.EXTRA_FILE_URI, uri.toString());
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void onImportMediaPicked(List<Uri> uris) {
        if (uris == null || uris.isEmpty()) {
            Toast.makeText(this, R.string.import_empty_selection, Toast.LENGTH_SHORT).show();
            return;
        }

        File testMediaDir = new File(getFilesDir(), TEST_MEDIA_DIR_NAME);
        if (!testMediaDir.exists() && !testMediaDir.mkdirs()) {
            Toast.makeText(this, R.string.import_result_failed, Toast.LENGTH_LONG).show();
            return;
        }

        int copied = 0;
        for (Uri uri : uris) {
            try {
                getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } catch (SecurityException e) {
                Log.d(TAG, "Persistable permission is not available for import", e);
            }

            if (copyUriToDirectory(uri, testMediaDir)) {
                copied++;
            }
        }

        if (copied == uris.size()) {
            String msg = getString(R.string.import_result_ok, copied, testMediaDir.getAbsolutePath());
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        } else if (copied > 0) {
            Toast.makeText(this, getString(R.string.import_result_partial, copied, uris.size()), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, R.string.import_result_failed, Toast.LENGTH_LONG).show();
        }

        Log.d(TAG, "Imported media files: " + copied + " of " + uris.size() + " to " + testMediaDir.getAbsolutePath());
    }

    private boolean copyUriToDirectory(Uri uri, File targetDir) {
        String name = resolveDisplayName(uri);
        if (TextUtils.isEmpty(name)) {
            String mime = getContentResolver().getType(uri);
            String ext = "bin";
            if (!TextUtils.isEmpty(mime) && mime.contains("/")) {
                ext = mime.substring(mime.indexOf('/') + 1);
            }
            name = String.format(Locale.US, "media_%d.%s", System.currentTimeMillis(), ext);
        }

        File outputFile = new File(targetDir, name);
        int suffix = 1;
        while (outputFile.exists()) {
            outputFile = new File(targetDir, suffix + "_" + name);
            suffix++;
        }

        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            if (inputStream == null) {
                return false;
            }
            byte[] buffer = new byte[8192];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();
            return true;
        } catch (IOException e) {
            Log.d(TAG, "Import failed for uri: " + uri, e);
            return false;
        }
    }

    private String resolveDisplayName(Uri uri) {
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (index >= 0) {
                    return cursor.getString(index);
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "Failed to resolve display name", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }
}