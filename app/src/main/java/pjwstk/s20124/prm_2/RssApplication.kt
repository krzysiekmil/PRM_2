package pjwstk.s20124.prm_2

import android.Manifest
import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import pjwstk.s20124.prm_2.repository.RssRepository


class RssApplication: Application() {
    val rssRepository = RssRepository()

    override fun onCreate() {
        super.onCreate()

        val channel = NotificationChannel(
            "RSS_APP_NOTIFICATION_CHANEL",
            "RSS_APP",
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }
}