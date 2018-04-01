package com.tomoima.logbot.view

import android.animation.ValueAnimator
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageView
import androidx.animation.doOnEnd
import androidx.animation.doOnStart
import com.tomoima.logbot.R
import com.tomoima.logbot.core.Consts
import com.tomoima.logbot.core.Logbot






class OverlayViewService : Service() {

    companion object {
        fun createIntent(context: Context): Intent = Intent(context, OverlayViewService::class.java)
    }

    private val overlayView: View by lazy {
        LayoutInflater.from(this).inflate(R.layout.view_overlay, null)
    }

    private val windowManager: WindowManager by lazy {
        getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    private val messageReceiver: MessageReceiver by lazy {
        MessageReceiver(this)
    }

    private val logAdapter =
            LogAdapter(Logbot.logbotSettings?.bufferSize
                    ?: Consts.DEFAULT_LOG_BUFFER)

    private lateinit var recyclerView: RecyclerView
    private lateinit var shrinkButton: ImageView
    private lateinit var expandButton: ImageView

    override fun onBind(Intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(messageReceiver, IntentFilter(Consts.EVENT_SEND_LOG))

        val params = createWindowLayoutParams()
        params.gravity = Gravity.TOP or Gravity.LEFT
        params.x = 0
        params.y = 100
        params.width = resources.getDimensionPixelSize(R.dimen.overlay_width)
        params.height = resources.getDimensionPixelSize(R.dimen.overlay_height)

        windowManager.addView(overlayView, params)

        overlayView.findViewById<ImageView>(R.id.close).setOnClickListener { _ -> stopSelf() }
        overlayView.findViewById<ImageView>(R.id.clear_log).setOnClickListener { _ -> clearLog() }
        overlayView.findViewById<ImageView>(R.id.share_log).setOnClickListener { _ -> shareLog() }

        shrinkButton = overlayView.findViewById(R.id.shrink)
        shrinkButton.setOnClickListener { _ -> shrink() }

        expandButton = overlayView.findViewById(R.id.expand)
        expandButton.setOnClickListener { _ -> expand() }

        recyclerView = overlayView.findViewById(R.id.log_list)
        recyclerView.adapter = logAdapter

        overlayView.setOnTouchListener(object : View.OnTouchListener {
            private var lastAction: Int = 0
            private var initialX: Int = 0
            private var initialY: Int = 0
            private var initialTouchX: Float = 0.toFloat()
            private var initialTouchY: Float = 0.toFloat()

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params.x
                        initialY = params.y

                        initialTouchX = event.rawX
                        initialTouchY = event.rawY

                        lastAction = event.action
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        if (lastAction == MotionEvent.ACTION_DOWN) {
                            // TODO: add pluggable action here
                        }
                        lastAction = event.action
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        params.x = initialX + (event.rawX - initialTouchX).toInt()
                        params.y = initialY + (event.rawY - initialTouchY).toInt()

                        //Update the layout with new X & Y coordinate
                        windowManager.updateViewLayout(overlayView, params)
                        lastAction = event.action
                        return true
                    }
                }
                return false
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        windowManager.removeView(overlayView)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver)
    }

    fun addLog(log: String) {
        logAdapter.addLog(log)
        recyclerView.smoothScrollToPosition(logAdapter.itemCount -1)
    }

    private fun clearLog() = logAdapter.clearLog()

    private fun shareLog() {
        val message = logAdapter.getFullFormattedLogs()
        val actionIntent = Intent()
        actionIntent.action = Intent.ACTION_SEND
        actionIntent.type = "text/plain"
        actionIntent.putExtra(Intent.EXTRA_TEXT, message)
        actionIntent.putExtra(Intent.EXTRA_SUBJECT, "Log")
        startActivity(actionIntent)
    }

    private fun shrink() {
        val newHeight = resources.getDimensionPixelSize(R.dimen.margin_36dp)
        val slideAnimator = ValueAnimator
                .ofInt(overlayView.height, newHeight)
                .setDuration(300)
        slideAnimator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            overlayView.layoutParams.height = value
            windowManager.updateViewLayout(overlayView, overlayView.layoutParams)
        }
        slideAnimator.doOnEnd {
            shrinkButton.visibility = View.GONE
            expandButton.visibility = View.VISIBLE
        }
        slideAnimator.start()
    }

    private fun expand() {
        val newHeight = resources.getDimensionPixelSize(R.dimen.overlay_height)
        val slideAnimator = ValueAnimator
                .ofInt(overlayView.height, newHeight)
                .setDuration(300)
        slideAnimator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            overlayView.layoutParams.height = value
            windowManager.updateViewLayout(overlayView, overlayView.layoutParams)
        }
        slideAnimator.doOnStart {
            shrinkButton.visibility = View.VISIBLE
            expandButton.visibility = View.GONE
        }
        slideAnimator.start()
    }

    private fun createWindowLayoutParams() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT)
    } else {
        @Suppress("DEPRECATION")
        WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT)
    }


    internal class MessageReceiver(private val overlayViewService: OverlayViewService)
        : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                val message = it.getStringExtra(Consts.INTENT_MESSAGE)
                overlayViewService.addLog(message)
            }
        }
    }
}