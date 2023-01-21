package pjwstk.s20124.prm_2.repository

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import pjwstk.s20124.prm_2.model.RssItem
import pjwstk.s20124.prm_2.model.RssSchema
import pjwstk.s20124.prm_2.utils.XmlParser
import java.net.URL
import java.util.concurrent.Executors

class RssRepository {

    var items: MutableLiveData<List<RssItem>> = MutableLiveData(emptyList())
    var url: String = "https://wiadomosci.gazeta.pl/pub/rss/wiadomosci_kraj.htm"
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    init {
        val myExecutor = Executors.newSingleThreadExecutor()
        val myHandler = Handler(Looper.getMainLooper())
        var rss: RssSchema? = null
        myExecutor.execute {
            rss = XmlParser.getInstance()?.parser?.readValue(URL(url), RssSchema::class.java)!!
            myHandler.post {
                val result = rss?.channel?.items
                auth = FirebaseAuth.getInstance()
                database = FirebaseDatabase.getInstance().reference
                database.child("rss").child(auth.currentUser?.uid!!).get().addOnSuccessListener {
                    it.getValue<List<String>>()
                        ?.stream()
                        ?.forEach {
                            result?.find { item -> item.guid == it }?.favourite = true
                        }
                }
                items.postValue(result!!)
            }
        }
    }
}
