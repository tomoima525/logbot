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

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.tomoima.logbot.view.OverlayViewService


class LogbotLauncher : AppCompatActivity() {

    companion object {
        const val REQUEST_OVERLAY = 525
        /**
         * Start Overlaying View upon application
         */
        @JvmStatic
        fun startOverlay(activity: Activity) {
            val intent = Intent(activity, LogbotLauncher::class.java)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${Logbot.packageName}"))
            startActivityForResult(intent, REQUEST_OVERLAY)
        } else {
            launchOverLay()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_OVERLAY
                && resultCode == Activity.RESULT_OK
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && Settings.canDrawOverlays(this)) {
            launchOverLay()
        } else {
            Toast.makeText(this,
                    "Please grant a permission to use Logbot",
                    Toast.LENGTH_LONG
            ).show()
            finish()
        }

    }

    private fun launchOverLay() {
        startService(OverlayViewService.createIntent(this))
        Toast.makeText(this, "Logbot is turned on", Toast.LENGTH_LONG).show()
        finish()
    }
}
