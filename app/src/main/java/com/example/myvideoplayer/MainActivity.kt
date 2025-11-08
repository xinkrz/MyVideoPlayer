package com.example.myvideoplayer

import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myvideoplayer.databinding.ActivityMainBinding
import com.example.myvideoplayer.ui.video.VideoAdapter
import com.example.myvideoplayer.ui.video.VideoModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val videoList = mutableListOf<VideoModel>()

    private val folderPicker =
        registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
            uri?.let {
                // 可选：持久化访问权限，下次直接使用
                contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                loadVideosFromFolder(it)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 点击选择目录按钮
        binding.selectFolderButton.setOnClickListener {
            folderPicker.launch(null)
        }
    }

    /** 从用户选择的目录中加载视频 */
    private fun loadVideosFromFolder(uri: Uri) {
        videoList.clear()

        val pickedDir = DocumentFile.fromTreeUri(this, uri)
        pickedDir?.listFiles()?.forEach { file ->
            if (file.isFile && file.name?.endsWith(".mp4") == true) {
                val title = file.name ?: "未知视频"
                val path = file.uri.toString()
                val duration = getVideoDuration(this, file.uri)
                videoList.add(VideoModel(title, path, duration))
            }
        }

        binding.videoRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.videoRecyclerView.adapter = VideoAdapter(this, videoList)
    }

    /** 获取视频时长 */
    private fun getVideoDuration(context: Context, uri: Uri): Long {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, uri)
        val duration =
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0
        retriever.release()
        return duration
    }


    //自动扫描所有视频文件
//    private fun loadVideos() {
//        val uri: Uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
//        val projection = arrayOf(
//            MediaStore.Video.Media.TITLE,
//            MediaStore.Video.Media.DATA,
//            MediaStore.Video.Media.DURATION
//        )
//
//        val cursor: Cursor? = contentResolver.query(uri, projection, null, null, null)
//        cursor?.use {
//            val titleIndex = it.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)
//            val dataIndex = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
//            val durationIndex = it.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
//
//            while (it.moveToNext()) {
//                val title = it.getString(titleIndex)
//                val path = it.getString(dataIndex)
//                val duration = it.getLong(durationIndex)
//                videoList.add(VideoModel(title, path, duration))
//            }
//        }
//
//        binding.videoRecyclerView.layoutManager = LinearLayoutManager(this)
//        binding.videoRecyclerView.adapter = VideoAdapter(this, videoList)
//    }
}