package pjwstk.s20124.prm_2

import android.app.Application
import pjwstk.s20124.prm_2.repository.RssRepository

class RssApplication: Application(){
    val rssRepository = RssRepository()
}