package com.annguyen.fakeroute.actions

import android.content.Context
import android.util.Xml
import com.annguyen.fakeroute.screens.Location
import org.xmlpull.v1.XmlSerializer
import java.io.File
import java.io.FileWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WriteToGPX(
    val list: List<Location>
) {
    suspend fun exe(context: Context) {
        if (list.size < 2) return
        val file = File(context?.filesDir, "gpxs")
        runCatching {
            val fileName = "route_${list.first().lat},${list.first().lng}_${list.last().lat},${list.last().lng}.gpx"
            if (!file.exists()) {
                file.mkdir()
            }
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = dateFormat.format(Date())

            val fileToWrite = File(file, fileName)
            val writer = FileWriter(fileToWrite)

            val xmlSerializer = Xml.newSerializer()
//            val xmlWriter = StringWriter()
            xmlSerializer.setOutput(writer)

            xmlSerializer.startDocument("UTF-8", true)

            xmlSerializer.startTag(null, "gpx")
            xmlSerializer.attribute(null, "version", "1.1")
            xmlSerializer.attribute(null, "creator", "FakeRoute")
            xmlSerializer.attribute(null, "xmlns", "http://www.topografix.com/GPX/1/1")
            xmlSerializer.attribute(null, "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance")
            xmlSerializer.attribute(null, "xsi:schemaLocation", "http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd")

            xmlSerializer.startTag(null, "trk")
            xmlSerializer.startTag(null, "name")
            xmlSerializer.text(fileName)
            xmlSerializer.endTag(null, "name")
            xmlSerializer.startTag(null, "desc")
            xmlSerializer.text("Track of locations")
            xmlSerializer.endTag(null, "desc")
            xmlSerializer.startTag(null, "time")
            xmlSerializer.text(date)
            xmlSerializer.endTag(null, "time")

            for (location in list) {
                xmlSerializer.startTag(null, "trkpt")
                xmlSerializer.attribute(null, "lat", location.lat.toString())
                xmlSerializer.attribute(null, "lon", location.lng.toString())

//                xmlSerializer.startTag(null, "ele")
//                xmlSerializer.text(location.altitude.toString())
//                xmlSerializer.endTag(null, "ele")

//                xmlSerializer.startTag(null, "time")
//                xmlSerializer.text(dateFormat.format(location.timestamp))
//                xmlSerializer.endTag(null, "time")

                xmlSerializer.endTag(null, "trkpt")
            }

            xmlSerializer.endTag(null, "trk")
            xmlSerializer.endTag(null, "gpx")
            xmlSerializer.endDocument()
            writer.close()
        }
    }
}