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
