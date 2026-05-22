package com.example.labsix14;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.io.IOException;

public class ViewerActivity extends AppCompatActivity {

    public static final String EXTRA_FILE_URI = "extra_file_uri";

    private static final String TAG = "ViewerActivity";
    private static final String KEY_URI = "key_uri";
    private static final String KEY_MIME = "key_mime";
    private static final String KEY_AUDIO_POS = "key_audio_pos";
    private static final String KEY_VIDEO_POS = "key_video_pos";
    private static final String KEY_AUDIO_PLAYING = "key_audio_playing";
    private static final String KEY_VIDEO_PLAYING = "key_video_playing";

    private ImageView imageView;
    private VideoView videoView;
    private MaterialButton audioPlayPauseButton;
    private MaterialButton fullscreenVideoButton;
    private TextView viewerTitle;
    private TextView viewerDetails;

    private MediaPlayer mediaPlayer;
    private Uri selectedUri;
    private String mimeType;

    private int audioPositionMs;
    private int videoPositionMs;
    private boolean shouldResumeAudio;
    private boolean shouldResumeVideo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);

        imageView = findViewById(R.id.imagePreview);
        videoView = findViewById(R.id.videoPreview);
        audioPlayPauseButton = findViewById(R.id.buttonAudioPlayPause);
        fullscreenVideoButton = findViewById(R.id.buttonFullscreenVideo);
        viewerTitle = findViewById(R.id.textViewerTitle);
        viewerDetails = findViewById(R.id.textViewerDetails);

        audioPlayPauseButton.setOnClickListener(v -> toggleAudioPlayback());
        fullscreenVideoButton.setOnClickListener(v -> openFullscreenVideo());

        if (savedInstanceState != null) {
            selectedUri = savedInstanceState.getParcelable(KEY_URI);
            mimeType = savedInstanceState.getString(KEY_MIME);
            audioPositionMs = savedInstanceState.getInt(KEY_AUDIO_POS, 0);
            videoPositionMs = savedInstanceState.getInt(KEY_VIDEO_POS, 0);
            shouldResumeAudio = savedInstanceState.getBoolean(KEY_AUDIO_PLAYING, false);
            shouldResumeVideo = savedInstanceState.getBoolean(KEY_VIDEO_PLAYING, false);
        } else {
            String uriString = getIntent().getStringExtra(EXTRA_FILE_URI);
            if (!TextUtils.isEmpty(uriString)) {
                selectedUri = Uri.parse(uriString);
            }
        }

        if (selectedUri == null) {
            Toast.makeText(this, R.string.error_no_file, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (TextUtils.isEmpty(mimeType)) {
            mimeType = resolveMimeType(this, selectedUri);
        }

        Log.d(TAG, "Selected uri: " + selectedUri + ", mimeType=" + mimeType);
        handleFileByMimeType();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_URI, selectedUri);
        outState.putString(KEY_MIME, mimeType);

        if (mediaPlayer != null) {
            outState.putInt(KEY_AUDIO_POS, mediaPlayer.getCurrentPosition());
            outState.putBoolean(KEY_AUDIO_PLAYING, mediaPlayer.isPlaying());
        } else {
            outState.putInt(KEY_AUDIO_POS, audioPositionMs);
            outState.putBoolean(KEY_AUDIO_PLAYING, shouldResumeAudio);
        }

        if (videoView != null) {
            outState.putInt(KEY_VIDEO_POS, videoView.getCurrentPosition());
            outState.putBoolean(KEY_VIDEO_PLAYING, videoView.isPlaying());
        } else {
            outState.putInt(KEY_VIDEO_POS, videoPositionMs);
            outState.putBoolean(KEY_VIDEO_PLAYING, shouldResumeVideo);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (videoView != null) {
            videoPositionMs = videoView.getCurrentPosition();
            shouldResumeVideo = videoView.isPlaying();
            videoView.pause();
        }
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            shouldResumeAudio = true;
            audioPositionMs = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
            updateAudioButtonState();
        }
    }

    @Override
    protected void onDestroy() {
        releaseMediaPlayer();
        super.onDestroy();
    }

    private void handleFileByMimeType() {
        hideAllContent();

        if (TextUtils.isEmpty(mimeType)) {
            showUnsupportedFile();
            return;
        }

        if (mimeType.startsWith("image/")) {
            showImage();
        } else if (mimeType.startsWith("audio/")) {
            setupAudioPlayer();
        } else if (mimeType.startsWith("video/")) {
            setupVideoPlayer();
        } else {
            showUnsupportedFile();
        }
    }

    private void hideAllContent() {
        imageView.setVisibility(ImageView.GONE);
        videoView.setVisibility(VideoView.GONE);
        fullscreenVideoButton.setVisibility(ImageView.GONE);
        findViewById(R.id.audioCard).setVisibility(ImageView.GONE);
        audioPlayPauseButton.setEnabled(false);
    }

    private void showImage() {
        viewerTitle.setText(R.string.viewer_mode_image);
        viewerDetails.setText(getString(R.string.viewer_file_info, mimeType));

        imageView.setVisibility(ImageView.VISIBLE);
        imageView.setImageURI(selectedUri);
    }

    private void setupAudioPlayer() {
        viewerTitle.setText(R.string.viewer_mode_audio);
        viewerDetails.setText(getString(R.string.viewer_file_info, mimeType));

        findViewById(R.id.audioCard).setVisibility(ImageView.VISIBLE);
        audioPlayPauseButton.setEnabled(false);

        releaseMediaPlayer();
        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(this, selectedUri);
            mediaPlayer.setOnPreparedListener(mp -> {
                audioPlayPauseButton.setEnabled(true);
                if (audioPositionMs > 0) {
                    mp.seekTo(audioPositionMs);
                }
                if (shouldResumeAudio) {
                    mp.start();
                }
                updateAudioButtonState();
                Log.d(TAG, "Audio prepared");
            });
            mediaPlayer.setOnCompletionListener(mp -> {
                shouldResumeAudio = false;
                audioPositionMs = 0;
                updateAudioButtonState();
            });
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            Log.d(TAG, "Audio setup failed", e);
            Toast.makeText(this, R.string.error_open_media, Toast.LENGTH_SHORT).show();
            releaseMediaPlayer();
        }
    }

    private void setupVideoPlayer() {
        viewerTitle.setText(R.string.viewer_mode_video);
        viewerDetails.setText(getString(R.string.viewer_file_info, mimeType));

        videoView.setVisibility(VideoView.VISIBLE);
        fullscreenVideoButton.setVisibility(ImageView.VISIBLE);

        android.widget.MediaController mediaController = new android.widget.MediaController(this);
        mediaController.setAnchorView(videoView);

        videoView.setMediaController(mediaController);
        videoView.setVideoURI(selectedUri);
        videoView.setOnPreparedListener(mp -> {
            if (videoPositionMs > 0) {
                videoView.seekTo(videoPositionMs);
            }
            if (shouldResumeVideo) {
                videoView.start();
            }
            Log.d(TAG, "Video prepared");
        });
        videoView.setOnCompletionListener(mp -> {
            shouldResumeVideo = false;
            videoPositionMs = 0;
        });
        videoView.requestFocus();
    }

    private void openFullscreenVideo() {
        if (selectedUri == null) {
            return;
        }

        Intent intent = new Intent(this, VideoFullscreenActivity.class);
        intent.putExtra(VideoFullscreenActivity.EXTRA_VIDEO_URI, selectedUri.toString());
        intent.putExtra(VideoFullscreenActivity.EXTRA_VIDEO_POSITION_MS, videoView.getCurrentPosition());
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void toggleAudioPlayback() {
        if (mediaPlayer == null) {
            return;
        }

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            audioPositionMs = mediaPlayer.getCurrentPosition();
            shouldResumeAudio = false;
        } else {
            mediaPlayer.start();
            shouldResumeAudio = true;
        }

        updateAudioButtonState();
    }

    private void updateAudioButtonState() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            audioPlayPauseButton.setText(R.string.pause_audio);
        } else {
            audioPlayPauseButton.setText(R.string.play_audio);
        }
    }

    private void showUnsupportedFile() {
        viewerTitle.setText(R.string.unsupported_file_title);
        viewerDetails.setText(R.string.unsupported_file_message);
        Toast.makeText(this, R.string.unsupported_file_message, Toast.LENGTH_SHORT).show();
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
            } catch (Exception ignored) {
            }
            mediaPlayer = null;
        }
    }

    private static String resolveMimeType(Context context, Uri uri) {
        String detected = context.getContentResolver().getType(uri);
        if (!TextUtils.isEmpty(detected)) {
            return detected;
        }

        String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
        if (!TextUtils.isEmpty(extension)) {
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
        }

        return null;
    }
}
