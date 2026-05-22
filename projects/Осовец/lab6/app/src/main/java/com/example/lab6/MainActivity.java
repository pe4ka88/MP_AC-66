package com.example.lab6;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ImageView imagePreview;
    private VideoView videoView;
    private LinearLayout audioControls;
    private TextView selectedFileText;
    private TextView apiDataText;
    private TextView externalStatusText;
    private EditText apiQueryInput;
    private EditText externalUrlInput;
    private Button apiArtworkButton;
    private Button apiAudioButton;
    private Button apiVideoButton;

    private MediaPlayer audioPlayer;

    private String apiArtworkUrl;
    private String apiAudioPreviewUrl;
    private String apiVideoPreviewUrl;
    private String apiSelectionLabel;

    private final ExecutorService apiExecutor = Executors.newSingleThreadExecutor();
    private static final String ITUNES_API_URL = "https://itunes.apple.com/search";
    private static final Pattern GOOGLE_DRIVE_FILE_ID_PATTERN = Pattern.compile("/file/d/([^/]+)");

    private final ActivityResultLauncher<String[]> openDocumentLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenDocument(), this::handleSelectedUri);

    private final ActivityResultLauncher<String[]> requestPermissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                boolean allGranted = true;
                for (String permission : requiredPermissions()) {
                    Boolean granted = result.get(permission);
                    if (granted == null || !granted) {
                        allGranted = false;
                        break;
                    }
                }

                if (!allGranted) {
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show();
                }

                launchPicker();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imagePreview = findViewById(R.id.imagePreview);
        videoView = findViewById(R.id.videoView);
        audioControls = findViewById(R.id.audioControls);
        selectedFileText = findViewById(R.id.tvSelectedFile);
        apiDataText = findViewById(R.id.tvApiData);
        externalStatusText = findViewById(R.id.tvExternalStatus);
        apiQueryInput = findViewById(R.id.etApiQuery);
        externalUrlInput = findViewById(R.id.etExternalUrl);

        Button selectFileButton = findViewById(R.id.btnSelectFile);
        Button showTaskButton = findViewById(R.id.btnShowTask);
        Button openAboutButton = findViewById(R.id.btnOpenAbout);
        Button authorActionButton = findViewById(R.id.btnAuthorAction);
        Button audioStartButton = findViewById(R.id.btnAudioStart);
        Button audioPauseButton = findViewById(R.id.btnAudioPause);
        Button audioStopButton = findViewById(R.id.btnAudioStop);
        Button loadApiButton = findViewById(R.id.btnLoadApi);
        Button openExternalAutoButton = findViewById(R.id.btnOpenExternalAuto);
        Button openExternalImageButton = findViewById(R.id.btnOpenExternalImage);
        Button openExternalAudioButton = findViewById(R.id.btnOpenExternalAudio);
        Button openExternalVideoButton = findViewById(R.id.btnOpenExternalVideo);
        apiArtworkButton = findViewById(R.id.btnApiArtwork);
        apiAudioButton = findViewById(R.id.btnApiAudio);
        apiVideoButton = findViewById(R.id.btnApiVideo);

        selectFileButton.setOnClickListener(v -> ensurePermissionsAndPickFile());
        showTaskButton.setOnClickListener(v -> showTaskDialog());
        openAboutButton.setOnClickListener(v ->
            startActivity(new Intent(MainActivity.this, AboutActivity.class)));
        authorActionButton.setOnClickListener(v ->
                Toast.makeText(this, R.string.author_action_toast, Toast.LENGTH_SHORT).show());

        audioStartButton.setOnClickListener(v -> startAudio());
        audioPauseButton.setOnClickListener(v -> pauseAudio());
        audioStopButton.setOnClickListener(v -> resetAudioToStart());

        openExternalAutoButton.setOnClickListener(v -> playExternalFromInput(ExternalPlaybackMode.AUTO));
        openExternalImageButton.setOnClickListener(v -> playExternalFromInput(ExternalPlaybackMode.FORCE_IMAGE));
        openExternalAudioButton.setOnClickListener(v -> playExternalFromInput(ExternalPlaybackMode.FORCE_AUDIO));
        openExternalVideoButton.setOnClickListener(v -> playExternalFromInput(ExternalPlaybackMode.FORCE_VIDEO));

        loadApiButton.setOnClickListener(v -> loadExternalApiData());
        apiArtworkButton.setOnClickListener(v -> showApiArtwork());
        apiAudioButton.setOnClickListener(v -> playApiAudioPreview());
        apiVideoButton.setOnClickListener(v -> playApiVideoPreview());

        setApiActionButtonsEnabled(false);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
    }

    private void ensurePermissionsAndPickFile() {
        if (hasAllRequiredPermissions()) {
            launchPicker();
            return;
        }

        Toast.makeText(this, R.string.permission_required, Toast.LENGTH_LONG).show();
        requestPermissionsLauncher.launch(requiredPermissions());
    }

    private void launchPicker() {
        openDocumentLauncher.launch(new String[]{"image/*", "audio/*", "video/*"});
    }

    private boolean hasAllRequiredPermissions() {
        String[] permissions = requiredPermissions();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private String[] requiredPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            return new String[]{
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_MEDIA_VIDEO
            };
        }
        return new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
    }

    private void handleSelectedUri(Uri uri) {
        if (uri == null) {
            return;
        }

        try {
            getContentResolver().takePersistableUriPermission(
                    uri,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
            );
        } catch (SecurityException ignored) {
            // Some providers do not allow persistable permission flags.
        }

        String fileName = getDisplayName(uri);
        selectedFileText.setText(getString(R.string.selected_file_format, fileName));

        String mimeType = getContentResolver().getType(uri);
        String extension = getFileExtension(fileName);

        if (isImageType(mimeType, extension)) {
            showImage(uri);
            return;
        }

        if (isAudioType(mimeType, extension)) {
            prepareAudio(uri);
            return;
        }

        if (isVideoType(mimeType, extension)) {
            showVideo(uri);
            return;
        }

        clearMediaViews();
        String typeLabel = mimeType == null ? getString(R.string.empty_type) : mimeType;
        Toast.makeText(this, getString(R.string.unsupported_file, typeLabel), Toast.LENGTH_LONG).show();
    }

    private void showImage(@NonNull Uri uri) {
        releaseAudioPlayer();
        videoView.stopPlayback();
        videoView.setVisibility(VideoView.GONE);
        audioControls.setVisibility(LinearLayout.GONE);

        imagePreview.setImageURI(uri);
        imagePreview.setVisibility(ImageView.VISIBLE);
    }

    private void prepareAudio(@NonNull Uri uri) {
        imagePreview.setVisibility(ImageView.GONE);
        videoView.stopPlayback();
        videoView.setVisibility(VideoView.GONE);

        releaseAudioPlayer();
        audioPlayer = MediaPlayer.create(this, uri);
        if (audioPlayer == null) {
            audioControls.setVisibility(LinearLayout.GONE);
            Toast.makeText(this,
                    getString(R.string.unsupported_file, getString(R.string.empty_type)),
                    Toast.LENGTH_LONG).show();
            return;
        }

        audioControls.setVisibility(LinearLayout.VISIBLE);
        Toast.makeText(this, R.string.audio_prepared, Toast.LENGTH_SHORT).show();
    }

    private void showVideo(@NonNull Uri uri) {
        imagePreview.setVisibility(ImageView.GONE);
        releaseAudioPlayer();
        audioControls.setVisibility(LinearLayout.GONE);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        videoView.setOnPreparedListener(mp -> {
            videoView.start();
            mediaController.show(0);
        });

        videoView.setOnErrorListener((mp, what, extra) -> {
            Log.e(TAG, "Video error what=" + what + " extra=" + extra);
            Toast.makeText(this,
                    getString(R.string.video_error, what, extra),
                    Toast.LENGTH_LONG).show();
            return true;
        });

        videoView.setVideoURI(uri);
        videoView.setVisibility(VideoView.VISIBLE);
        videoView.requestFocus();
        Toast.makeText(this, R.string.video_prepared, Toast.LENGTH_SHORT).show();
    }

    private void clearMediaViews() {
        imagePreview.setVisibility(ImageView.GONE);
        videoView.stopPlayback();
        videoView.setVisibility(VideoView.GONE);
        releaseAudioPlayer();
        audioControls.setVisibility(LinearLayout.GONE);
    }

    private void startAudio() {
        if (audioPlayer != null) {
            audioPlayer.start();
        }
    }

    private void pauseAudio() {
        if (audioPlayer != null && audioPlayer.isPlaying()) {
            audioPlayer.pause();
        }
    }

    private void resetAudioToStart() {
        if (audioPlayer == null) {
            return;
        }

        if (audioPlayer.isPlaying()) {
            audioPlayer.pause();
        }
        audioPlayer.seekTo(0);
    }

    private void releaseAudioPlayer() {
        if (audioPlayer != null) {
            audioPlayer.release();
            audioPlayer = null;
        }
    }

    private String getDisplayName(@NonNull Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (nameIndex >= 0 && cursor.moveToFirst()) {
                String name = cursor.getString(nameIndex);
                cursor.close();
                return name;
            }
            cursor.close();
        }
        return uri.getLastPathSegment() == null ? "unknown" : uri.getLastPathSegment();
    }

    private String getFileExtension(@NonNull String fileName) {
        int dot = fileName.lastIndexOf('.');
        if (dot == -1 || dot == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    private boolean isImageType(String mimeType, @NonNull String ext) {
        if (mimeType != null && mimeType.startsWith("image/")) {
            return true;
        }
        return ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png") || ext.equals("webp") || ext.equals("gif");
    }

    private boolean isAudioType(String mimeType, @NonNull String ext) {
        if (mimeType != null && mimeType.startsWith("audio/")) {
            return true;
        }
        return ext.equals("mp3") || ext.equals("wav") || ext.equals("m4a") || ext.equals("ogg") || ext.equals("aac");
    }

    private boolean isVideoType(String mimeType, @NonNull String ext) {
        if (mimeType != null && mimeType.startsWith("video/")) {
            return true;
        }
        return ext.equals("mp4") || ext.equals("3gp") || ext.equals("webm") || ext.equals("mkv");
    }

    private void showTaskDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.task_dialog_title)
                .setMessage(getString(R.string.task_dialog_text))
                .setPositiveButton(R.string.dialog_ok, null)
                .show();
    }

    private void playExternalFromInput(@NonNull ExternalPlaybackMode mode) {
        String rawUrl = externalUrlInput.getText() == null
                ? ""
                : externalUrlInput.getText().toString().trim();

        if (TextUtils.isEmpty(rawUrl)) {
            Toast.makeText(this, R.string.external_empty_url, Toast.LENGTH_SHORT).show();
            return;
        }

        externalStatusText.setText(R.string.external_status_resolving);
        apiExecutor.execute(() -> {
            try {
                String resolvedUrl = resolveExternalSourceUrl(rawUrl);
                ExternalMediaType mediaType = mode == ExternalPlaybackMode.AUTO
                        ? detectRemoteMediaType(resolvedUrl)
                        : mode.forcedType;

                if (mediaType == ExternalMediaType.UNKNOWN) {
                    throw new IllegalStateException(getString(R.string.external_unknown_type));
                }

                runOnUiThread(() -> playResolvedExternal(rawUrl, resolvedUrl, mediaType));
            } catch (Exception ex) {
                runOnUiThread(() -> {
                    String message = ex.getMessage() == null ? getString(R.string.empty_type) : ex.getMessage();
                    externalStatusText.setText(getString(R.string.external_status_error, message));
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void playResolvedExternal(
            @NonNull String sourceUrl,
            @NonNull String resolvedUrl,
            @NonNull ExternalMediaType mediaType
    ) {
        selectedFileText.setText(getString(R.string.external_selected, trimForDisplay(sourceUrl)));

        switch (mediaType) {
            case IMAGE:
                externalStatusText.setText(R.string.external_status_image);
                showExternalImage(resolvedUrl);
                break;
            case AUDIO:
                externalStatusText.setText(R.string.external_status_audio);
                prepareAudioFromRemoteUrl(resolvedUrl);
                break;
            case VIDEO:
                externalStatusText.setText(R.string.external_status_video);
                showVideo(Uri.parse(resolvedUrl));
                break;
            default:
                externalStatusText.setText(getString(R.string.external_status_error, getString(R.string.external_unknown_type)));
                break;
        }
    }

    private void showExternalImage(@NonNull String imageUrl) {
        releaseAudioPlayer();
        videoView.stopPlayback();
        videoView.setVisibility(VideoView.GONE);
        audioControls.setVisibility(LinearLayout.GONE);
        imagePreview.setVisibility(ImageView.VISIBLE);
        imagePreview.setImageDrawable(null);

        apiExecutor.execute(() -> {
            try {
                Bitmap bitmap = loadBitmapFromUrl(imageUrl);
                runOnUiThread(() -> imagePreview.setImageBitmap(bitmap));
            } catch (Exception ex) {
                runOnUiThread(() -> {
                    externalStatusText.setText(getString(R.string.external_status_error, getString(R.string.external_image_error)));
                    Toast.makeText(this, R.string.external_image_error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private Bitmap loadBitmapFromUrl(@NonNull String imageUrl) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(imageUrl).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Lab14App");
        connection.setConnectTimeout(8000);
        connection.setReadTimeout(12000);

        int code = connection.getResponseCode();
        if (code < 200 || code >= 300) {
            connection.disconnect();
            throw new IllegalStateException("HTTP " + code);
        }

        InputStream stream = connection.getInputStream();
        Bitmap bitmap = BitmapFactory.decodeStream(stream);
        if (stream != null) {
            stream.close();
        }
        connection.disconnect();

        if (bitmap == null) {
            throw new IllegalStateException(getString(R.string.external_image_error));
        }
        return bitmap;
    }

    private void prepareAudioFromRemoteUrl(@NonNull String audioUrl) {
        imagePreview.setVisibility(ImageView.GONE);
        videoView.stopPlayback();
        videoView.setVisibility(VideoView.GONE);
        audioControls.setVisibility(LinearLayout.GONE);

        releaseAudioPlayer();

        MediaPlayer remotePlayer = new MediaPlayer();
        remotePlayer.setOnPreparedListener(mp -> {
            audioControls.setVisibility(LinearLayout.VISIBLE);
            Toast.makeText(this, R.string.audio_prepared, Toast.LENGTH_SHORT).show();
        });
        remotePlayer.setOnErrorListener((mp, what, extra) -> {
            Log.e(TAG, "Audio error what=" + what + " extra=" + extra);
            releaseAudioPlayer();
            Toast.makeText(this, R.string.external_audio_error, Toast.LENGTH_LONG).show();
            return true;
        });

        try {
            remotePlayer.setDataSource(audioUrl);
            remotePlayer.prepareAsync();
            audioPlayer = remotePlayer;
            Toast.makeText(this, R.string.external_audio_loading, Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            remotePlayer.release();
            audioPlayer = null;
            externalStatusText.setText(getString(R.string.external_status_error, getString(R.string.external_audio_error)));
            Toast.makeText(this, R.string.external_audio_error, Toast.LENGTH_LONG).show();
        }
    }

    private ExternalMediaType detectRemoteMediaType(@NonNull String mediaUrl) {
        String extension = extractUrlExtension(mediaUrl);

        if (isImageType(null, extension)) {
            return ExternalMediaType.IMAGE;
        }
        if (isAudioType(null, extension)) {
            return ExternalMediaType.AUDIO;
        }
        if (isVideoType(null, extension)) {
            return ExternalMediaType.VIDEO;
        }

        String contentType = fetchRemoteContentType(mediaUrl);
        if (!TextUtils.isEmpty(contentType)) {
            String normalized = contentType.toLowerCase(Locale.ROOT);
            int semicolon = normalized.indexOf(';');
            if (semicolon >= 0) {
                normalized = normalized.substring(0, semicolon).trim();
            }

            if (normalized.startsWith("image/")) {
                return ExternalMediaType.IMAGE;
            }
            if (normalized.startsWith("audio/")) {
                return ExternalMediaType.AUDIO;
            }
            if (normalized.startsWith("video/")) {
                return ExternalMediaType.VIDEO;
            }
        }

        return ExternalMediaType.UNKNOWN;
    }

    private String fetchRemoteContentType(@NonNull String mediaUrl) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(mediaUrl).openConnection();
            connection.setRequestMethod("HEAD");
            connection.setRequestProperty("User-Agent", "Lab14App");
            connection.setConnectTimeout(7000);
            connection.setReadTimeout(7000);
            int code = connection.getResponseCode();

            if (code >= 200 && code < 400) {
                String contentType = connection.getContentType();
                if (!TextUtils.isEmpty(contentType)) {
                    return contentType;
                }
            }
        } catch (Exception ignored) {
            // Some providers do not support HEAD requests.
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        HttpURLConnection fallbackConnection = null;
        try {
            fallbackConnection = (HttpURLConnection) new URL(mediaUrl).openConnection();
            fallbackConnection.setRequestMethod("GET");
            fallbackConnection.setRequestProperty("User-Agent", "Lab14App");
            fallbackConnection.setRequestProperty("Range", "bytes=0-0");
            fallbackConnection.setConnectTimeout(7000);
            fallbackConnection.setReadTimeout(7000);
            int code = fallbackConnection.getResponseCode();

            if (code >= 200 && code < 400) {
                String contentType = fallbackConnection.getContentType();
                if (!TextUtils.isEmpty(contentType)) {
                    return contentType;
                }
            }
        } catch (Exception ignored) {
            // Keep unknown type if provider blocks metadata requests.
        } finally {
            if (fallbackConnection != null) {
                fallbackConnection.disconnect();
            }
        }

        return "";
    }

    private String extractUrlExtension(@NonNull String mediaUrl) {
        Uri uri = Uri.parse(mediaUrl);

        String fileNameParam = uri.getQueryParameter("filename");
        if (!TextUtils.isEmpty(fileNameParam)) {
            return getFileExtension(fileNameParam);
        }

        String segment = uri.getLastPathSegment();
        if (TextUtils.isEmpty(segment)) {
            return "";
        }
        return getFileExtension(segment);
    }

    private String resolveExternalSourceUrl(@NonNull String rawUrl) throws Exception {
        String trimmed = rawUrl.trim();
        Uri uri = Uri.parse(trimmed);
        String scheme = uri.getScheme();

        if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
            throw new IllegalArgumentException(getString(R.string.external_invalid_url));
        }

        if (isYandexDiskPublicUrl(uri)) {
            return resolveYandexDiskDirectUrl(trimmed);
        }

        if (isGoogleDriveUrl(uri)) {
            return resolveGoogleDriveDirectUrl(uri);
        }

        return trimmed;
    }

    private boolean isYandexDiskPublicUrl(@NonNull Uri uri) {
        String host = uri.getHost();
        if (TextUtils.isEmpty(host)) {
            return false;
        }

        String normalizedHost = host.toLowerCase(Locale.ROOT);
        if (normalizedHost.contains("cloud-api.yandex.net")
                || normalizedHost.startsWith("downloader.")
                || normalizedHost.contains("storage.yandexcloud.net")) {
            return false;
        }

        return normalizedHost.contains("disk.yandex.")
                || normalizedHost.equals("yadi.sk")
                || normalizedHost.endsWith(".yadi.sk");
    }

    private boolean isGoogleDriveUrl(@NonNull Uri uri) {
        String host = uri.getHost();
        if (TextUtils.isEmpty(host)) {
            return false;
        }
        String normalizedHost = host.toLowerCase(Locale.ROOT);
        return normalizedHost.contains("drive.google.com") || normalizedHost.contains("docs.google.com");
    }

    private String resolveYandexDiskDirectUrl(@NonNull String publicUrl) throws Exception {
        String endpoint = "https://cloud-api.yandex.net/v1/disk/public/resources/download?public_key="
                + URLEncoder.encode(publicUrl, StandardCharsets.UTF_8.name());

        JSONObject json = new JSONObject(fetchHttpText(endpoint));
        String directUrl = json.optString("href", "");
        if (TextUtils.isEmpty(directUrl)) {
            throw new IllegalStateException(getString(R.string.external_yandex_error));
        }
        return directUrl;
    }

    private String resolveGoogleDriveDirectUrl(@NonNull Uri uri) {
        String fileId = extractGoogleDriveFileId(uri);
        if (TextUtils.isEmpty(fileId)) {
            throw new IllegalStateException(getString(R.string.external_google_error));
        }

        String resourceKey = uri.getQueryParameter("resourcekey");
        StringBuilder directUrl = new StringBuilder("https://drive.google.com/uc?export=download&id=")
                .append(Uri.encode(fileId));

        if (!TextUtils.isEmpty(resourceKey)) {
            directUrl.append("&resourcekey=").append(Uri.encode(resourceKey));
        }

        return directUrl.toString();
    }

    private String extractGoogleDriveFileId(@NonNull Uri uri) {
        String idQuery = uri.getQueryParameter("id");
        if (!TextUtils.isEmpty(idQuery)) {
            return idQuery;
        }

        String fullUrl = uri.toString();
        Matcher matcher = GOOGLE_DRIVE_FILE_ID_PATTERN.matcher(fullUrl);
        if (matcher.find()) {
            return matcher.group(1);
        }

        List<String> segments = uri.getPathSegments();
        for (int i = 0; i < segments.size() - 1; i++) {
            if ("d".equals(segments.get(i))) {
                return segments.get(i + 1);
            }
        }

        return "";
    }

    private String trimForDisplay(@NonNull String text) {
        int max = 80;
        if (text.length() <= max) {
            return text;
        }
        return text.substring(0, max - 1) + "…";
    }

    private void loadExternalApiData() {
        String query = apiQueryInput.getText() == null
                ? ""
                : apiQueryInput.getText().toString().trim();

        if (TextUtils.isEmpty(query)) {
            Toast.makeText(this, R.string.api_empty_query, Toast.LENGTH_SHORT).show();
            return;
        }

        setApiActionButtonsEnabled(false);
        apiArtworkUrl = null;
        apiAudioPreviewUrl = null;
        apiVideoPreviewUrl = null;
        apiSelectionLabel = null;

        apiDataText.setText(R.string.api_loading);
        apiExecutor.execute(() -> {
            try {
                ApiSelection selection = searchItunesMedia(query);
                runOnUiThread(() -> applyApiSelection(selection));
            } catch (Exception ex) {
                runOnUiThread(() -> apiDataText.setText(getString(R.string.api_error, ex.getMessage())));
            }
        });
    }

    private ApiSelection searchItunesMedia(@NonNull String query) throws Exception {
        String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8.name());

        String songsUrl = ITUNES_API_URL
                + "?term=" + encoded
                + "&entity=song&limit=6&country=US";

        String videosUrl = ITUNES_API_URL
                + "?term=" + encoded
                + "&entity=musicVideo&limit=3&country=US";

        String songsResponse = fetchHttpText(songsUrl);
        JSONArray songs = new JSONObject(songsResponse).optJSONArray("results");

        JSONArray videos = new JSONObject(fetchHttpText(videosUrl)).optJSONArray("results");

        if (songs == null || songs.length() == 0) {
            throw new IllegalStateException(getString(R.string.api_no_results));
        }

        JSONObject firstSong = songs.getJSONObject(0);
        String artist = firstSong.optString("artistName", "-");
        String track = firstSong.optString("trackName", "-");
        String kind = firstSong.optString("kind", "song");

        String artwork = firstSong.optString("artworkUrl100", "");
        if (!TextUtils.isEmpty(artwork)) {
            artwork = artwork.replace("100x100bb", "600x600bb");
        }

        String audioPreview = firstSong.optString("previewUrl", "");

        String videoPreview = "";
        if (videos != null && videos.length() > 0) {
            JSONObject firstVideo = videos.getJSONObject(0);
            videoPreview = firstVideo.optString("previewUrl", "");
        }

        List<String> lines = new ArrayList<>();
        lines.add(getString(R.string.api_source_line));
        lines.add(getString(R.string.api_results_for, query));

        int limit = Math.min(songs.length(), 5);
        for (int i = 0; i < limit; i++) {
            JSONObject item = songs.getJSONObject(i);
            lines.add(getString(
                    R.string.api_track_format,
                    i + 1,
                    item.optString("artistName", "-"),
                    item.optString("trackName", "-"),
                    item.optString("kind", "song")
            ));
        }

        lines.add(TextUtils.isEmpty(videoPreview)
                ? getString(R.string.api_video_status_no)
                : getString(R.string.api_video_status_yes));

        return new ApiSelection(
                String.join("\n\n", lines),
                artwork,
                audioPreview,
                videoPreview,
                artist + " - " + track + " (" + kind + ")"
        );
    }

    private String fetchHttpText(@NonNull String endpoint) throws Exception {
        URL url = new URL(endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("User-Agent", "Lab14App");
        connection.setConnectTimeout(8000);
        connection.setReadTimeout(8000);

        int code = connection.getResponseCode();
        InputStream stream = (code >= 200 && code < 300)
                ? connection.getInputStream()
                : connection.getErrorStream();

        String response = readAll(stream);
        connection.disconnect();

        if (code < 200 || code >= 300) {
            throw new IllegalStateException("HTTP " + code);
        }
        return response;
    }

    private void applyApiSelection(@NonNull ApiSelection selection) {
        apiArtworkUrl = selection.artworkUrl;
        apiAudioPreviewUrl = selection.audioPreviewUrl;
        apiVideoPreviewUrl = selection.videoPreviewUrl;
        apiSelectionLabel = selection.label;

        apiDataText.setText(selection.resultText);
        setApiActionButtonsEnabled(true);
        selectedFileText.setText(getString(R.string.api_preview_selected, selection.label));
    }

    private void setApiActionButtonsEnabled(boolean enabled) {
        if (apiArtworkButton != null) {
            apiArtworkButton.setEnabled(enabled);
        }
        if (apiAudioButton != null) {
            apiAudioButton.setEnabled(enabled);
        }
        if (apiVideoButton != null) {
            apiVideoButton.setEnabled(enabled);
        }
    }

    private void showApiArtwork() {
        if (TextUtils.isEmpty(apiArtworkUrl)) {
            Toast.makeText(this, R.string.api_no_artwork, Toast.LENGTH_SHORT).show();
            return;
        }

        releaseAudioPlayer();
        videoView.stopPlayback();
        videoView.setVisibility(VideoView.GONE);
        audioControls.setVisibility(LinearLayout.GONE);
        imagePreview.setVisibility(ImageView.VISIBLE);

        final String artworkUrl = apiArtworkUrl;
        apiExecutor.execute(() -> {
            try {
                InputStream stream = new URL(artworkUrl).openStream();
                Bitmap bitmap = BitmapFactory.decodeStream(stream);
                if (stream != null) {
                    stream.close();
                }

                if (bitmap == null) {
                    throw new IllegalStateException(getString(R.string.api_artwork_error));
                }

                runOnUiThread(() -> imagePreview.setImageBitmap(bitmap));
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, R.string.api_artwork_error, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void playApiAudioPreview() {
        if (TextUtils.isEmpty(apiAudioPreviewUrl)) {
            Toast.makeText(this, R.string.api_no_audio, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!TextUtils.isEmpty(apiSelectionLabel)) {
            selectedFileText.setText(getString(R.string.api_preview_selected, apiSelectionLabel));
        }
        prepareAudioFromRemoteUrl(apiAudioPreviewUrl);
    }

    private void playApiVideoPreview() {
        if (TextUtils.isEmpty(apiVideoPreviewUrl)) {
            Toast.makeText(this, R.string.api_no_video, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!TextUtils.isEmpty(apiSelectionLabel)) {
            selectedFileText.setText(getString(R.string.api_preview_selected, apiSelectionLabel));
        }

        Uri videoUri = Uri.parse(apiVideoPreviewUrl);
        Intent openVideoIntent = new Intent(Intent.ACTION_VIEW, videoUri)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // API clips are opened externally to avoid device-specific VideoView decoder artifacts.
        videoView.stopPlayback();
        videoView.setVisibility(VideoView.GONE);

        try {
            startActivity(Intent.createChooser(openVideoIntent, getString(R.string.api_open_video_chooser)));
        } catch (Exception ex) {
            Log.w(TAG, "External player unavailable", ex);
            Toast.makeText(this, R.string.api_open_video_external_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private String readAll(InputStream stream) throws Exception {
        if (stream == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        return sb.toString();
    }

    private enum ExternalMediaType {
        IMAGE,
        AUDIO,
        VIDEO,
        UNKNOWN
    }

    private enum ExternalPlaybackMode {
        AUTO(ExternalMediaType.UNKNOWN),
        FORCE_IMAGE(ExternalMediaType.IMAGE),
        FORCE_AUDIO(ExternalMediaType.AUDIO),
        FORCE_VIDEO(ExternalMediaType.VIDEO);

        final ExternalMediaType forcedType;

        ExternalPlaybackMode(ExternalMediaType forcedType) {
            this.forcedType = forcedType;
        }
    }

    private static class ApiSelection {
        final String resultText;
        final String artworkUrl;
        final String audioPreviewUrl;
        final String videoPreviewUrl;
        final String label;

        ApiSelection(String resultText, String artworkUrl, String audioPreviewUrl, String videoPreviewUrl, String label) {
            this.resultText = resultText;
            this.artworkUrl = artworkUrl;
            this.audioPreviewUrl = audioPreviewUrl;
            this.videoPreviewUrl = videoPreviewUrl;
            this.label = label;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseAudioPlayer();
        videoView.pause();
    }

    @Override
    protected void onDestroy() {
        releaseAudioPlayer();
        videoView.stopPlayback();
        apiExecutor.shutdownNow();
        super.onDestroy();
    }
}