/*
 * Copyright (C) 2018 Tomoaki Imai
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    /**
     * Setup manager. This method should be called in the beginning to use Logbot
     */
    @JvmStatic
    fun setup(context: Context, logbotSettings: LogbotSettings = LogbotSettings()) {
        localBroadcastManager = LocalBroadcastManager.getInstance(context)
        packageName = context.packageName
        Logbot.logbotSettings = logbotSettings
    }

    /**
     * Send message to Logbot and show on Viewer.
     * @param message it can be either just string or a format string
     * @params args arguments if a format string is used (optional)
     */
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