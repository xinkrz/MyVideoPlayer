package com.example.myvideoplayer.ui.video

import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myvideoplayer.PlayerActivity
import com.example.myvideoplayer.databinding.ItemVideoBinding
import java.util.concurrent.TimeUnit

class VideoAdapter(private val context: Context, private val videos: List<VideoModel>) :
    RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {
    inner class VideoViewHolder(val binding: ItemVideoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VideoViewHolder {
        val binding = ItemVideoBinding.inflate(LayoutInflater.from(context), parent, false)
        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: VideoViewHolder,
        position: Int
    ) {
        val video = videos[position]
        holder.binding.videoTitle.text = video.title
        holder.binding.videoDuration.text = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(video.duration),
                TimeUnit.MILLISECONDS.toSeconds(video.duration) % 60
        )

        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, Uri.parse(video.path))
        val bitmap = retriever.getFrameAtTime(1_000_000) // 1s位置截图
        holder.binding.videoThumbnail.setImageBitmap(bitmap)
        retriever.release()

        holder.itemView.setOnClickListener {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra("path", video.path)
            intent.putExtra("title", video.title)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return videos.size
    }
}