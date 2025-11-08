package com.example.myvideoplayer

import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.MediaController
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myvideoplayer.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private var isFullscreen = true // 当前是否为全屏（横屏）

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 默认进入横屏
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        hideSystemUI()

        // 获取视频信息
        val videoPath = intent.getStringExtra("path") ?: return
        val title = intent.getStringExtra("title") ?: "视频播放"

        // 设置标题栏
        binding.videoTitle.text = title
        binding.titleBar.visibility = View.GONE // 横屏时隐藏标题栏

        // 播放视频
        val uri = Uri.parse(videoPath)
        val mediaController = MediaController(this)
        mediaController.setAnchorView(binding.videoView)
        binding.videoView.setMediaController(mediaController)
        binding.videoView.setVideoURI(uri)
        binding.videoView.requestFocus()
        binding.videoView.start()

        // 添加全屏切换按钮
        binding.btnFullscreen.setOnClickListener {
            toggleScreenOrientation()
        }

        // 返回按钮
        binding.btnBack.setOnClickListener {
            finish() // 直接返回上一个 Activity
        }

    }

    /** 切换横竖屏 **/
    private fun toggleScreenOrientation() {
        if (isFullscreen) {
            // 当前是全屏横屏 → 切换成竖屏
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            binding.btnFullscreen.setImageResource(R.drawable.ic_fullscreen)
            binding.titleBar.visibility = View.VISIBLE // 显示标题栏
            isFullscreen = false
        } else {
            // 当前是竖屏 → 切换成全屏横屏
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            binding.btnFullscreen.setImageResource(R.drawable.ic_fullscreen_exit)
            binding.titleBar.visibility = View.GONE // 隐藏标题栏
            isFullscreen = true
        }
    }

    /** 隐藏系统栏，兼容旧版本 **/
    private fun hideSystemUI() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            // Android 11+ (API 30+)
            window.setDecorFitsSystemWindows(false)
            val controller = window.insetsController
            controller?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            controller?.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            // Android 10 及以下版本
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    )
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }
}