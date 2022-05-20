package com.example.mediaplayer.presentation.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.common.util.Util
import androidx.media3.datasource.DefaultDataSource
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    private fun initializePlayer() {
        val btnMute = view?.findViewById<AppCompatImageView>(R.id.volume_control)
        val btnPlay = view?.findViewById<AppCompatImageView>(R.id.btn_exo_play)
        val btnNext = view?.findViewById<AppCompatImageView>(R.id.btn_exo_next)
        val btnPrevious = view?.findViewById<AppCompatImageView>(R.id.btn_exo_previous)
        val btnForward = view?.findViewById<AppCompatImageView>(R.id.btn_exo_fast_forward)
        val btnRewind = view?.findViewById<AppCompatImageView>(R.id.btn_exo_fast_rewind)
        var volumeInfo = true
        var play = true
        var currentPosition = 0L

//        val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()
//
//        val mediaSourceFactory: MediaSource.Factory =
//            DefaultMediaSourceFactory(dataSourceFactory)
//
//        var player: ExoPlayer? = ExoPlayer.Builder(requireContext())
//            .setMediaSourceFactory(mediaSourceFactory)
//            .build()


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
                            btnMute.setImageResource(R.drawable.ic_volume_up)
                            exoPlayer.volume = 1F
                            !volumeInfo
                        } else {
                            btnMute.setImageResource(R.drawable.ic_volume_off)
                            exoPlayer.volume = 0F
                            !volumeInfo
                        }
                    }
                    btnPlay?.setOnClickListener {
                        play = if (play) {
                            btnPlay.setImageResource(R.drawable.ic_play_circle)
                            exoPlayer.pause()
                            currentPosition = exoPlayer.currentPosition
                            !play
                        } else {
                            btnPlay.setImageResource(R.drawable.ic_pause_circle)
                            exoPlayer.play()
                            exoPlayer.seekTo(currentPosition)
                            !play
                        }
                    }
                    btnNext?.setOnClickListener {
                            exoPlayer.seekToNextMediaItem()
                    }
                    btnPrevious?.setOnClickListener {
                            exoPlayer.seekToPreviousMediaItem()
                    }
                    btnForward?.setOnClickListener {
                       val position =  exoPlayer.currentPosition
                        exoPlayer.seekTo(position+10000)
                    }
                    btnRewind?.setOnClickListener {
                        val position =  exoPlayer.currentPosition
                        exoPlayer.seekTo(position-10000)
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
        hideSystemUi()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}