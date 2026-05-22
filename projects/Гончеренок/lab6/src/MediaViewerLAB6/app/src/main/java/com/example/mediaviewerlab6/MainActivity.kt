package com.example.mediaviewerLAB6


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var btnSelectFile: Button
    private lateinit var tvFileName: TextView

    // Image
    private lateinit var imageContainer: FrameLayout
    private lateinit var ivImage: ImageView

    // Video
    private lateinit var videoContainer: FrameLayout
    private lateinit var videoView: VideoView
    private lateinit var btnPlayPauseVideo: Button
    private lateinit var btnStopVideo: Button

    // Audio
    private lateinit var audioContainer: LinearLayout
    private lateinit var tvAudioName: TextView
    private lateinit var btnPlayPauseAudio: Button
    private lateinit var btnStopAudio: Button
    private lateinit var seekBarAudio: SeekBar
    private lateinit var tvAudioProgress: TextView

    private var mediaPlayer: MediaPlayer? = null
    private var isAudioPlaying = false
    private var isVideoPaused = false

    private val handler = Handler(Looper.getMainLooper())
    private var updateSeekBarRunnable: Runnable? = null

    // Лаунчер для выбора файла
    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { handleSelectedFile(it) }
    }

    // Лаунчер для запроса разрешений
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.all { it }) {
            openFilePicker()
        } else {
            Toast.makeText(this, "Необходимы разрешения для доступа к файлам", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        btnSelectFile = findViewById(R.id.btnSelectFile)
        tvFileName = findViewById(R.id.tvFileName)

        // Image
        imageContainer = findViewById(R.id.imageContainer)
        ivImage = findViewById(R.id.ivImage)

        // Video
        videoContainer = findViewById(R.id.videoContainer)
        videoView = findViewById(R.id.videoView)
        btnPlayPauseVideo = findViewById(R.id.btnPlayPauseVideo)
        btnStopVideo = findViewById(R.id.btnStopVideo)

        // Audio
        audioContainer = findViewById(R.id.audioContainer)
        tvAudioName = findViewById(R.id.tvAudioName)
        btnPlayPauseAudio = findViewById(R.id.btnPlayPauseAudio)
        btnStopAudio = findViewById(R.id.btnStopAudio)
        seekBarAudio = findViewById(R.id.seekBarAudio)
        tvAudioProgress = findViewById(R.id.tvAudioProgress)
    }

    private fun setupListeners() {
        btnSelectFile.setOnClickListener {
            checkPermissionsAndOpenPicker()
        }

        // Video controls
        btnPlayPauseVideo.setOnClickListener {
            toggleVideoPlayPause()
        }

        btnStopVideo.setOnClickListener {
            stopVideo()
        }

        // Audio controls
        btnPlayPauseAudio.setOnClickListener {
            toggleAudioPlayPause()
        }

        btnStopAudio.setOnClickListener {
            stopAudio()
        }

        seekBarAudio.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun checkPermissionsAndOpenPicker() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO
            )
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        val allGranted = permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }

        if (allGranted) {
            openFilePicker()
        } else {
            permissionLauncher.launch(permissions)
        }
    }

    private fun openFilePicker() {
        filePickerLauncher.launch("*/*")
    }

    private fun handleSelectedFile(uri: Uri) {
        val fileName = getFileName(uri)
        tvFileName.text = fileName ?: "Неизвестный файл"

        val mimeType = contentResolver.getType(uri)

        hideAllContainers()

        when {
            mimeType?.startsWith("image/") == true -> {
                displayImage(uri)
            }
            mimeType?.startsWith("video/") == true -> {
                playVideo(uri)
            }
            mimeType?.startsWith("audio/") == true -> {
                playAudio(uri, fileName)
            }
            else -> {
                Toast.makeText(this, "Неподдерживаемый формат файла", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun hideAllContainers() {
        imageContainer.visibility = FrameLayout.GONE
        videoContainer.visibility = FrameLayout.GONE
        audioContainer.visibility = LinearLayout.GONE

        stopAudio()
        stopVideo()
    }

    private fun displayImage(uri: Uri) {
        imageContainer.visibility = FrameLayout.VISIBLE
        ivImage.setImageURI(uri)
    }

    private fun playVideo(uri: Uri) {
        videoContainer.visibility = FrameLayout.VISIBLE

        videoView.setVideoURI(uri)
        videoView.setOnPreparedListener {
            btnPlayPauseVideo.text = "Pause"
            videoView.start()
            isVideoPaused = false
        }

        videoView.setOnCompletionListener {
            btnPlayPauseVideo.text = "Play"
            isVideoPaused = true
        }
    }

    private fun toggleVideoPlayPause() {
        if (videoView.isPlaying) {
            videoView.pause()
            btnPlayPauseVideo.text = "Play"
            isVideoPaused = true
        } else {
            videoView.start()
            btnPlayPauseVideo.text = "Pause"
            isVideoPaused = false
        }
    }

    private fun stopVideo() {
        if (videoView.isPlaying) {
            videoView.stopPlayback()
        }
        videoView.setVideoURI(null)
        btnPlayPauseVideo.text = "Play"
        isVideoPaused = true
    }

    private fun playAudio(uri: Uri, fileName: String?) {
        audioContainer.visibility = LinearLayout.VISIBLE
        tvAudioName.text = fileName ?: "Аудио файл"

        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(applicationContext, uri)
                prepare()

                seekBarAudio.max = duration

                setOnCompletionListener {
                    btnPlayPauseAudio.text = "Play"
                    isAudioPlaying = false
                    seekBarAudio.progress = 0
                    stopSeekBarUpdate()
                }

                start()
                isAudioPlaying = true
                btnPlayPauseAudio.text = "Pause"
                startSeekBarUpdate()
            }
        } catch (e: IOException) {
            Toast.makeText(this, "Ошибка воспроизведения аудио", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleAudioPlayPause() {
        mediaPlayer?.let { player ->
            if (isAudioPlaying) {
                player.pause()
                btnPlayPauseAudio.text = "Play"
                isAudioPlaying = false
                stopSeekBarUpdate()
            } else {
                player.start()
                btnPlayPauseAudio.text = "Pause"
                isAudioPlaying = true
                startSeekBarUpdate()
            }
        }
    }

    private fun stopAudio() {
        mediaPlayer?.let {
            it.stop()
            it.release()
        }
        mediaPlayer = null
        btnPlayPauseAudio.text = "Play"
        isAudioPlaying = false
        seekBarAudio.progress = 0
        tvAudioProgress.text = "00:00 / 00:00"
        stopSeekBarUpdate()
    }

    private fun startSeekBarUpdate() {
        updateSeekBarRunnable = object : Runnable {
            override fun run() {
                mediaPlayer?.let {
                    if (isAudioPlaying) {
                        seekBarAudio.progress = it.currentPosition
                        tvAudioProgress.text = formatTime(it.currentPosition) + " / " + formatTime(it.duration)
                        handler.postDelayed(this, 100)
                    }
                }
            }
        }
        handler.post(updateSeekBarRunnable!!)
    }

    private fun stopSeekBarUpdate() {
        updateSeekBarRunnable?.let {
            handler.removeCallbacks(it)
        }
    }

    private fun formatTime(milliseconds: Int): String {
        val seconds = (milliseconds / 1000) % 60
        val minutes = (milliseconds / (1000 * 60)) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun getFileName(uri: Uri): String? {
        val cursor = contentResolver.query(uri, null, null, null, null)
        return cursor?.use {
            val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            it.moveToFirst()
            it.getString(nameIndex)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
        stopSeekBarUpdate()
    }
}