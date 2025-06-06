package com.example.radiofm

import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

object MetadataExtractor {
    fun fetchSongTitle(streamUrl: String, callback: (String) -> Unit) {
        thread {
            try {
                val url = URL(streamUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.setRequestProperty("Icy-MetaData", "1")
                connection.connect()

                val metaInt = connection.getHeaderField("icy-metaint")?.toIntOrNull() ?: 0
                if (metaInt > 0) {
                    val inputStream = connection.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    
                    // Saltar los datos de audio hasta llegar a los metadatos
                    reader.skip(metaInt.toLong())
                    
                    // Leer el byte de longitud de los metadatos
                    val length = reader.read() * 16
                    if (length > 0) {
                        val metadata = CharArray(length)
                        reader.read(metadata, 0, length)
                        val metadataString = String(metadata).trim()
                        
                        // Extraer el título de la canción del formato StreamTitle='Título - Artista';
                        val titleMatch = "StreamTitle='([^']*)'".toRegex().find(metadataString)
                        val title = titleMatch?.groupValues?.get(1) ?: "Sin título"
                        callback(title)
                    } else {
                        callback("Sin título")
                    }
                } else {
                    callback("Sin título")
                }
            } catch (e: Exception) {
                Log.e("Metadata", "Error al obtener metadata", e)
                callback("Sin título")
            }
        }
    }
}
