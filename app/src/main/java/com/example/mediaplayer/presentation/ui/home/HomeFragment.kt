package com.example.mediaplayer.presentation.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.example.mediaplayer.R
import com.example.mediaplayer.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var player: ExoPlayer? = null
    private var playWhenReady = true
    private var currentItem = 0
    private var playbackPosition = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("position",playbackPosition)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
       if (savedInstanceState?.get("position")!=null) {
           playbackPosition = savedInstanceState.get("position") as Long
       }
    }

    private fun initializePlayer() {
        var volumeInfo = true
        var fullScreen = true
        var currentPosition = 0L
       val matchParent =  ConstraintLayout.LayoutParams.MATCH_PARENT

        val btnMute = view?.findViewById<AppCompatImageView>(R.id.volume_control)
       val btnFullScreen = view?.findViewById<AppCompatImageView>(R.id.fullscreen)

        player = context?.let {
            val trackSelector = DefaultTrackSelector(it).apply {
                setParameters(buildUponParameters().setMaxVideoSizeSd())
            }
            ExoPlayer.Builder(it)
                .setTrackSelector(trackSelector)
                .build()
                .also { exoPlayer ->
                    binding.videoView.player = exoPlayer
                    val mediaItem =
                        MediaItem.fromUri("https://storage.googleapis.com/exoplayer-test-media-0/play.mp3")
                    exoPlayer.setMediaItem(mediaItem)
                    val secondItem =
                        MediaItem.fromUri("https://videocdn.bodybuilding.com/video/mp4/62000/62792m.mp4")
                    exoPlayer.addMediaItem(secondItem)

                    exoPlayer.playWhenReady = playWhenReady
                    exoPlayer.seekTo(currentItem, playbackPosition)
                    exoPlayer.prepare()
                    exoPlayer.play()


                    btnMute?.setOnClickListener {
                        volumeInfo = if (volumeInfo) {
                            btnMute.setImageResource(R.drawable.ic_volume_off)
                            exoPlayer.volume = 0F
                            !volumeInfo
                        } else {
                            btnMute.setImageResource(R.drawable.ic_volume_up)
                            exoPlayer.volume = 1F
                            !volumeInfo
                        }
                    }
                    btnFullScreen?.setOnClickListener {
                        fullScreen = if (fullScreen) {
                            btnFullScreen.setImageResource(R.drawable.ic_fullscreen_exit)
                            hideSystemUi()
                            binding.videoView.layoutParams = ConstraintLayout.LayoutParams(matchParent,matchParent)
                            currentPosition = exoPlayer.currentPosition
                            !fullScreen
                        } else {

                           showSystemUi()
                            val density = context?.resources?.displayMetrics?.density
                            binding.videoView.layoutParams = ConstraintLayout.LayoutParams(matchParent,(200* density!!).toInt())
                            btnFullScreen.setImageResource(R.drawable.ic_fullscreen)
                            !fullScreen
                        }
                    }
                }
        }
    }

    private fun releasePlayer() {
        player?.let { exoPlayer ->
            playbackPosition = exoPlayer.currentPosition
            currentItem = exoPlayer.currentMediaItemIndex
            playWhenReady = exoPlayer.playWhenReady
            exoPlayer.release()
        }
        player = null
    }

    public override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initializePlayer()
        }
    }

    public override fun onResume() {
        super.onResume()

        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer()
        }
    }

    public override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }

    public override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }

    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        activity?.let { WindowCompat.setDecorFitsSystemWindows(it.window, false) }
        activity?.let {
            WindowInsetsControllerCompat(it.window, binding.videoView).let { controller ->
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }

    @SuppressLint("InlinedApi")
    private fun showSystemUi() {
        activity?.let { WindowCompat.setDecorFitsSystemWindows(it.window, true) }
        activity?.let {
            WindowInsetsControllerCompat(it.window, binding.videoView).let { controller ->
                controller.show(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}