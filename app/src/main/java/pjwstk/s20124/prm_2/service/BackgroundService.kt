package pjwstk.s20124.prm_2.service

import android.app.IntentService
import android.app.Service
import android.content.Intent
import android.content.Context
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.ViewModelProvider
import pjwstk.s20124.prm_2.RssApplication
import pjwstk.s20124.prm_2.repository.RssRepository
import pjwstk.s20124.prm_2.viewModel.RssViewModel
import pjwstk.s20124.prm_2.viewModel.RssViewModelFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class BackgroundService : IntentService("BackgroundService") {

    private val rssRepository: RssRepository = RssRepository()


    @Deprecated("Deprecated in Java")
    override fun onHandleIntent(intent: Intent?) {
        val service = Executors.newSingleThreadScheduledExecutor()
        val handler = Handler(Looper.getMainLooper())
        service.scheduleAtFixedRate({
            handler.run {
                rssRepository.getRss()
            }
        }, 2, 2, TimeUnit.HOURS)
    }




}