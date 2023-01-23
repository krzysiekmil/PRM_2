package pjwstk.s20124.prm_2.repository

import android.content.Context
import android.location.Geocoder
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.switchMap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import pjwstk.s20124.prm_2.R
import pjwstk.s20124.prm_2.model.RssItem
import pjwstk.s20124.prm_2.model.RssSchema
import pjwstk.s20124.prm_2.utils.XmlParser
import java.net.URL
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.util.concurrent.Executors

class RssRepository {

    val notification: MutableLiveData<Boolean> = MutableLiveData()
    var items: MutableLiveData<List<RssItem>> = MutableLiveData(emptyList())
    private var url: String = "https://wiadomosci.gazeta.pl/pub/rss/wiadomosci_kraj.htm"
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    @Volatile var lastPubDate: OffsetDateTime = OffsetDateTime.MIN

    init {
        getRss()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun setCurrentCountry(lat: Double, lot: Double, context: Context){
        Geocoder(context).getFromLocation(lat, lot, 1) {
            val address = it[0]
            url = when (address.countryCode) {
                "PL" -> "https://wiadomosci.gazeta.pl/pub/rss/wiadomosci_kraj.htm"
                else -> "https://wiadomosci.gazeta.pl/pub/rss/wiadomosci_swiat.htm"
            }
            getRss(true)
        }
    }

    fun getRss() {
        getRss(false)
    }

    private fun getRss(force: Boolean) {
        val myExecutor = Executors.newSingleThreadExecutor()
        val myHandler = Handler(Looper.getMainLooper())
        var rss: RssSchema

        myExecutor.execute {
            rss = XmlParser.getInstance()?.parser?.readValue(URL(url), RssSchema::class.java)!!
            myHandler.post {
                val result = rss.channel?.items ?: return@post
                val firstDate = OffsetDateTime.parse(result[0].date)

                if(force || (!firstDate.isBefore(lastPubDate) && !firstDate.isEqual(lastPubDate)))
                    updateValue(result)
                else {
                    return@post
                }

            }
        }
    }

    private fun updateValue(data: List<RssItem>){
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference


        if(auth.currentUser != null) {
            database.child("rss").child(auth.currentUser?.uid!!).get().addOnSuccessListener {
                it.getValue<List<String>>()
                    ?.stream()
                    ?.forEach {
                        data.find { item -> item.guid == it }?.favourite = true
                    }
            }
        }

        lastPubDate = OffsetDateTime.parse(data[0].date)
        items.postValue(data)
        notification.postValue(true)


    }
}
