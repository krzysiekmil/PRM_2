package pjwstk.s20124.prm_2.viewModel

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.launch
import pjwstk.s20124.prm_2.RssApplication
import pjwstk.s20124.prm_2.repository.RssRepository
import java.util.*

class RssViewModel(application: RssApplication) : AndroidViewModel(application) {

    private var database = FirebaseDatabase.getInstance().reference
    private var auth = FirebaseAuth.getInstance()
    private var rssRepository: RssRepository = application.rssRepository
    val rssItemList = rssRepository.items
    val notification = rssRepository.notification

    fun fetch() {
        val list = rssItemList.value
        rssRepository.items.value = list
    }

    fun setAsRead(uuid: UUID) = viewModelScope.launch {
        val list = rssItemList.value
        list?.first { it.id == uuid }?.wasRead = true
        rssRepository.items.value = list
    }

    fun addToFavourites(uuid: UUID) = viewModelScope.launch {
        val list = rssItemList.value
        val item = list?.first { it.id == uuid }
        item?.favourite = true
        database.child("rss").child(auth.currentUser?.uid!!).get().onSuccessTask {
            it.exists()
            val toSave: ArrayList<String>
            if (it.exists()) {
                toSave = it.getValue<ArrayList<String>>()!!
                if (!toSave.contains(item?.guid))
                    toSave.add(item?.guid!!)
            } else {
                toSave = ArrayList(1)
                toSave.add(item?.guid!!)
            }
            database.child("rss").child(auth.currentUser?.uid!!).setValue(toSave)

        }.addOnFailureListener {
            val toSave = ArrayList<String>(1)
            toSave.add(item?.guid!!)
            database.child("rss").child(auth.currentUser?.uid!!).setValue(toSave)
        }
        rssRepository.items.value = list
    }

    fun removeFromFavourites(uuid: UUID) = viewModelScope.launch {
        val list = rssItemList.value
        val item = list?.first { it.id == uuid }
        item?.favourite = false
        database.child("rss").child(auth.currentUser?.uid!!).get().onSuccessTask {
            it.exists()
            val list: ArrayList<String>
            if (it.exists()) {
                list = it.getValue<ArrayList<String>>()!!
                if (!list.contains(item?.guid))
                    list.remove(item?.guid!!)
            } else {
                list = ArrayList(1)
                list.add(item?.guid!!)
            }
            database.child("rss").child(auth.currentUser?.uid!!).setValue(list)

        }
        rssRepository.items.value = list
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun setCurrentCountry(latitude: Double, longitude: Double, context: Context) {
        rssRepository.setCurrentCountry(latitude,longitude, context)
    }

}

class RssViewModelFactory(private val application: RssApplication) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RssViewModel::class.java)) {
            return RssViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}