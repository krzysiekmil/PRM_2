package pjwstk.s20124.prm_2.utils

import com.ctc.wstx.stax.WstxInputFactory
import com.ctc.wstx.stax.WstxOutputFactory
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.XmlFactory
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.net.URL

class XmlParser {
    lateinit var parser: ObjectMapper
    init {
        val xmlFactory = XmlFactory.builder()
            .xmlInputFactory(WstxInputFactory())
            .xmlOutputFactory(WstxOutputFactory())
            .build();

        parser = XmlMapper(xmlFactory.xmlInputFactory, xmlFactory.xmlOutputFactory)
            .registerKotlinModule()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }


    companion object {
        private var INSTANCE: XmlParser? = null

        fun getInstance(): XmlParser? {
            if (INSTANCE == null) {
                synchronized(this) {
                    if (INSTANCE == null) {
                        INSTANCE = XmlParser()
                    }
                }
            }
            return INSTANCE;
        }
    }
}