package pjwstk.s20124.prm_2.viewModel

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import pjwstk.s20124.prm_2.RssApplication
import pjwstk.s20124.prm_2.repository.RssRepository
import java.util.UUID

class RssViewModel(application: RssApplication) : AndroidViewModel(application) {

    private var rssRepository: RssRepository = application.rssRepository
    val rssItemList = rssRepository.items

    fun setAsRead(uuid: UUID) = viewModelScope.launch {
        rssItemList.value?.first { it.id == uuid }?.wasRead = true
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