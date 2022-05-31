package com.example.mediaplayer.presentation.ui.home

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.transition.Transition
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.RemoteViews
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat.stopForeground
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.descendants
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.offline.DownloadService.NOTIFICATION_SERVICE
import androidx.media3.exoplayer.offline.DownloadService.startForeground
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.PlayerNotificationManager
import androidx.media3.ui.PlayerNotificationManager.BitmapCallback
import androidx.media3.ui.PlayerNotificationManager.MediaDescriptionAdapter
import com.example.mediaplayer.MainActivity
import com.example.mediaplayer.R
import com.example.mediaplayer.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var player: ExoPlayer? = null
    private var playWhenReady = true
    private var currentItem = 0
    private var playbackPosition = 0L



    private val CHANNEL_ID = "id_notifications"
    private val CHANNEL_NAME = "my_notification"
    private val NOTIFICATION_ID = 123

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
        outState.putLong("position", playbackPosition)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState?.get("position") != null) {
            playbackPosition = savedInstanceState.get("position") as Long
        }
    }

    @SuppressLint("RemoteViewLayout")
    private fun initializePlayer() {
        var volumeInfo = true
        var fullScreen = true
        var currentPosition = 0L
        val matchParent = ConstraintLayout.LayoutParams.MATCH_PARENT

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
                    val secItem =
                        MediaItem.fromUri("https://youtu.be/CYYtLXfquy0")
                    exoPlayer.addMediaItem(secItem)

                    exoPlayer.playWhenReady = playWhenReady
                    exoPlayer.seekTo(currentItem, playbackPosition)
                    exoPlayer.prepare()
                    exoPlayer.play()

                    exoPlayer.addListener(object : Player.Listener {

                        override fun onIsPlayingChanged(isPlaying: Boolean) {
                            if (isPlaying){
                                notification()
                            }

                        }

                    })


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
//                            val currentOrientation = activity?.requestedOrientation
//                            if (currentOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
//                                activity?.requestedOrientation =
//                                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
//                            }

                            binding.videoView.layoutParams =
                                ConstraintLayout.LayoutParams(matchParent, matchParent)
                            btnFullScreen.setImageResource(R.drawable.ic_fullscreen_exit)
                            hideSystemUi()
                            currentPosition = exoPlayer.currentPosition
                            !fullScreen

                        } else {

                            showSystemUi()
                            val density = context?.resources?.displayMetrics?.density
                            binding.videoView.layoutParams = ConstraintLayout.LayoutParams(
                                matchParent,
                                (200 * density!!).toInt()
                            )
                            btnFullScreen.setImageResource(R.drawable.ic_fullscreen)
                            !fullScreen
                        }
                    }
                }
        }
//        if (!fullScreen){
//            OnBackPressedDispatcher{
//                showSystemUi()
//                val density = context?.resources?.displayMetrics?.density
//                binding.videoView.layoutParams = ConstraintLayout.LayoutParams(
//                    matchParent,
//                    (200 * density!!).toInt()
//                )
//                btnFullScreen?.setImageResource(R.drawable.ic_fullscreen)
//                !fullScreen
//            }
//        }





        }

    private fun notification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                lightColor = Color.GREEN
                enableLights(true)
                enableVibration(true)
            }
            val notificationManager = getSystemService(requireContext(), NotificationManager::class.java) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            val intent = Intent(requireContext(), MainActivity::class.java)

            val pendingIntent = PendingIntent.getActivity(requireContext(), 0, intent, 0)


            val notification = NotificationCompat.Builder(requireContext(),CHANNEL_ID)
                .setContentTitle("Player")
                .setContentText("Song Name")
                .setSmallIcon(R.drawable.ic_volume_off)
                .setContentIntent(pendingIntent)
                .build()

            notificationManager.notify(NOTIFICATION_ID,notification)

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



