package com.example.myvideoplayer

import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myvideoplayer.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val videoPath = intent.getStringExtra("path") ?: return
        val title = intent.getStringExtra("title") ?: "视频播放"

        supportActionBar?.title = title

        val uri = Uri.parse(videoPath)
        binding.videoView.setVideoURI(uri)
        binding.videoView.setMediaController(MediaController(this))
        binding.videoView.requestFocus()
        binding.videoView.start()

    }
}