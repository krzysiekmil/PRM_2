package pjwstk.s20124.prm_2.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class RssUserInfo(val username: String?, val favorites: List<String>?)
