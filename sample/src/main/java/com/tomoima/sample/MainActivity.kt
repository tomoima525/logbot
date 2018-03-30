package com.tomoima.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import com.tomoima.logbot.core.Logbot
import com.tomoima.logbot.core.LogbotLauncher
import java.util.*

class MainActivity : AppCompatActivity() {
    private var count = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Logbot.setup(this)

        val launchLogbotButton = findViewById<Button>(R.id.launch_logbot)
        launchLogbotButton.setOnClickListener { _ ->
            LogbotLauncher.startOverlay(this)
        }

        val sendEvent = findViewById<Button>(R.id.send_event)
        sendEvent.setOnClickListener { _ ->
            Logbot.send("[$count] message %s", UUID.randomUUID())
            count++
        }
    }
}
