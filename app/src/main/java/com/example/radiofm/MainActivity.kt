package com.example.radiofm

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.google.android.material.button.MaterialButton
import com.google.android.material.slider.Slider
import com.google.android.material.appbar.MaterialToolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {

    private lateinit var player: ExoPlayer
    private var isPlaying = false
    private lateinit var playPauseButton: MaterialButton
    private lateinit var songTitle: TextView
    private lateinit var frequencyDisplay: TextView
    private lateinit var frequencySlider: Slider
    private lateinit var shareButton: MaterialButton
    private lateinit var favoriteButton: MaterialButton
    private lateinit var topAppBar: MaterialToolbar

    private val frequencyMap = mapOf(
        88.5f to "https://playerservices.streamtheworld.com/api/livestream-redirect/CADENASER.mp3",
        89.3f to "https://reggaeton.stream.laut.fm/reggaeton",
        90.3f to "https://reggae.stream.laut.fm/reggae",
        91.5f to "https://playerservices.streamtheworld.com/api/livestream-redirect/LOS40.mp3"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupPlayer()
        setupListeners()
        setupTopAppBar()
    }

    private fun initializeViews() {
        playPauseButton = findViewById(R.id.playPauseButton)
        songTitle = findViewById(R.id.songTitle)
        frequencyDisplay = findViewById(R.id.frequencyDisplay)
        frequencySlider = findViewById(R.id.frequencySlider)
        shareButton = findViewById(R.id.shareButton)
        favoriteButton = findViewById(R.id.favoriteButton)
        topAppBar = findViewById(R.id.topAppBar)
    }

    private fun setupPlayer() {
        player = ExoPlayer.Builder(this).build().apply {
            val mediaItem = MediaItem.fromUri(frequencyMap[88.5f] ?: "")
            setMediaItem(mediaItem)
            prepare()
        }
    }

    private fun setupListeners() {
        playPauseButton.setOnClickListener {
            togglePlayback()
        }

        frequencySlider.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                updateFrequency(value)
            }
        }

        shareButton.setOnClickListener {
            shareCurrentStation()
        }

        favoriteButton.setOnClickListener {
            toggleFavorite()
        }
    }

    private fun setupTopAppBar() {
        setSupportActionBar(topAppBar)
    }

    private fun togglePlayback() {
        if (isPlaying) {
            player.pause()
            playPauseButton.text = "Reproducir"
            playPauseButton.setIconResource(android.R.drawable.ic_media_play)
        } else {
            player.play()
            playPauseButton.text = "Pausar"
            playPauseButton.setIconResource(android.R.drawable.ic_media_pause)
        }
        isPlaying = !isPlaying
    }

    private fun updateFrequency(frequency: Float) {
        frequencyDisplay.text = String.format("%.1f MHz", frequency)
        val streamUrl = frequencyMap[frequency]
        if (streamUrl != null) {
            player.setMediaItem(MediaItem.fromUri(streamUrl))
            player.prepare()
            if (isPlaying) {
                player.play()
            }
            updateSongTitle(streamUrl)
        }
    }

    private fun updateSongTitle(streamUrl: String) {
        MetadataExtractor.fetchSongTitle(streamUrl) { title ->
            runOnUiThread {
                songTitle.text = title
            }
        }
    }

    private fun shareCurrentStation() {
        val frequency = frequencySlider.value
        val shareText = "¡Escuchando la radio en ${String.format("%.1f", frequency)} MHz!"
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(sendIntent, "Compartir vía"))
    }

    private fun toggleFavorite() {
        val frequency = frequencySlider.value
        // TODO: Implement favorite stations storage
        Toast.makeText(this, "Estación ${String.format("%.1f", frequency)} MHz añadida a favoritos", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_app_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                showSettings()
                true
            }
            R.id.action_about -> {
                showAbout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showSettings() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Configuración")
            .setMessage("Configuración de la aplicación")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showAbout() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Acerca de")
            .setMessage("Radio FM v1.0\nUna aplicación para escuchar radio en vivo")
            .setPositiveButton("OK", null)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}