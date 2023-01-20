package pjwstk.s20124.prm_2.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.google.type.Date
import java.time.LocalDateTime
import java.util.UUID

@JacksonXmlRootElement(localName = "rss")
data class RssSchema(
    @set: JacksonXmlProperty(isAttribute = true)
    var version: String?,
    @set: JacksonXmlProperty(localName = "channel")
    var channel: RssChanel?,

)

data class RssChanel(
    @set:JacksonXmlProperty(localName = "title")
    var title: String?,

    @set:JacksonXmlProperty(localName = "item")
    @JacksonXmlElementWrapper(useWrapping = false)
    var items: List<RssItem>,

    @set:JacksonXmlProperty(localName = "lastBuildDate")
    var lastBuildDate: String?
)

data class RssItem(
    @set:JacksonXmlProperty(localName = "title")
    var title: String,
    @set:JacksonXmlProperty(localName = "link")
    var link: String,
    @set:JacksonXmlProperty(localName = "description")
    var description: String,
    @set:JacksonXmlProperty(localName = "enclosure")
    var enclosure: RssEnclosure,
    @set:JacksonXmlProperty(localName = "guid")
    var guid: String,
    @set:JacksonXmlProperty(namespace = "dc", localName = "creator")
    var creator: String,
    @set:JacksonXmlProperty(namespace = "dc",localName = "date")
    var date: String,
    var id: UUID = UUID.randomUUID(),
    var wasRead: Boolean = false,
    var favourite: Boolean = false
)

data class RssEnclosure(
    @set:JacksonXmlProperty(isAttribute = true)
    var url: String,
    @set:JacksonXmlProperty(isAttribute = true)
    var length: String,
    @set:JacksonXmlProperty(isAttribute = true)
    var type: String

)
