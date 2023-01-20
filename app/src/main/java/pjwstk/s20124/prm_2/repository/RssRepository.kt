package pjwstk.s20124.prm_2.repository

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import pjwstk.s20124.prm_2.model.RssItem
import pjwstk.s20124.prm_2.model.RssSchema
import pjwstk.s20124.prm_2.utils.XmlParser
import java.net.URL
import java.util.concurrent.Executors

class RssRepository {

    var items: MutableLiveData<List<RssItem>> = MutableLiveData(emptyList())
    var url: String = "https://wiadomosci.gazeta.pl/pub/rss/wiadomosci_kraj.htm"

    init {
        val myExecutor = Executors.newSingleThreadExecutor()
        val myHandler = Handler(Looper.getMainLooper())
        var rss: RssSchema? = null
        myExecutor.execute {
            rss = XmlParser.getInstance()?.parser?.readValue(URL(url), RssSchema::class.java)!!
            myHandler.post {
                items.postValue(rss?.channel?.items)
                }
            }
        }
    }
