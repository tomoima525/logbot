package com.tomoima.logbot.core

import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager


object Logbot {

    private var localBroadcastManager: LocalBroadcastManager? = null

    internal var packageName: String? = null
        get() {
            if (field == null) {
                AssertionError("Logbot.setup() should be called to use Logbot")
            }
            return field
        }
        private set

    internal var logbotSettings: LogbotSettings? = null
        get() {
            if (field == null) {
                AssertionError("Logbot.setup() should be called to use Logbot")
            }
            return field
        }
        private set

    @JvmStatic
    fun setup(context: Context, logbotSettings: LogbotSettings = LogbotSettings()) {
        localBroadcastManager = LocalBroadcastManager.getInstance(context)
        packageName = context.packageName
        Logbot.logbotSettings = logbotSettings
    }

    @JvmStatic
    fun send(message: String, vararg args: Any?) {
        assert(localBroadcastManager)
        val sendLog = if (args.isNotEmpty()) {
            String.format(message, *args)
        } else message
        val intent = Intent(Consts.EVENT_SEND_LOG)
        intent.putExtra(Consts.INTENT_MESSAGE, sendLog)
        localBroadcastManager?.sendBroadcast(intent)
    }

    private fun assert(localBroadcastManager: LocalBroadcastManager?) {
        if (localBroadcastManager == null) {
            AssertionError("Logbot.setup() should be called to use Logbot")
        }
    }

}