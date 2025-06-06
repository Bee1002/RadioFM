package com.example.radiofm

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class MainActivity : AppCompatActivity() {

    private lateinit var player: ExoPlayer
    private var isPlaying = false
    private lateinit var playPauseButton: ImageButton
    private lateinit var songTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playPauseButton = findViewById(R.id.playPauseButton)
        songTitle = findViewById(R.id.songTitle)

        setupPlayer()

        MetadataExtractor.fetchSongTitle("http://icecast.omroep.nl/radio2-bb-mp3") { title ->
            runOnUiThread {
                songTitle.text = title
            }
        }

        playPauseButton.setOnClickListener {
            if (isPlaying) {
                player.pause()
                playPauseButton.setImageResource(android.R.drawable.ic_media_play)
            } else {
                player.play()
                playPauseButton.setImageResource(android.R.drawable.ic_media_pause)
            }
            isPlaying = !isPlaying
        }
    }

    private fun setupPlayer() {
        player = ExoPlayer.Builder(this).build().apply {
            val mediaItem = MediaItem.fromUri("https://playerservices.streamtheworld.com/api/livestream-redirect/CADENASER.mp3")
            setMediaItem(mediaItem)
            prepare()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}
