
package com.mitv.app

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var player: ExoPlayer
    private lateinit var playerView: PlayerView
    private lateinit var channelList: LinearLayout
    private lateinit var status: TextView

    private val channels = mutableListOf<Channel>()

    data class Channel(
        val name: String,
        val url: String,
        val category: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = android.graphics.Color.rgb(8, 12, 30)
        window.navigationBarColor = android.graphics.Color.BLACK

        createInterface()
        loadChannels()
    }

    private fun createInterface() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(android.graphics.Color.rgb(8, 12, 30))
            setPadding(16, 16, 16, 16)
        }

        val title = TextView(this).apply {
            text = "MI TV"
            textSize = 30f
            gravity = Gravity.CENTER
            setTextColor(android.graphics.Color.CYAN)
        }

        status = TextView(this).apply {
            text = "Conectando al servidor..."
            textSize = 14f
            setTextColor(android.graphics.Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(8, 12, 8, 12)
        }

        playerView = PlayerView(this).apply {
            useController = true
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                220
            )
        }

        channelList = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val scroll = ScrollView(this).apply {
            addView(channelList)
        }

        root.addView(title)
        root.addView(status)
        root.addView(playerView)
        root.addView(scroll, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0,
            1f
        ))

        setContentView(root)

        player = ExoPlayer.Builder(this).build()
        playerView.player = player
    }

    private fun loadChannels() {
        thread {
            try {
                val connection = URL(
                    BuildConfig.API_BASE + "?action=channels"
                ).openConnection() as HttpURLConnection

                connection.requestMethod = "GET"
                connection.connectTimeout = 15000
                connection.readTimeout = 15000

                val response = connection.inputStream.bufferedReader().use {
                    it.readText()
                }

                connection.disconnect()

                val json = JSONObject(response)
                val array = json.optJSONArray("channels")

                channels.clear()

                if (array != null) {
                    for (i in 0 until array.length()) {
                        val item = array.getJSONObject(i)

                        val name = item.optString("name")
                        val url = item.optString("url")
                        val category = item.optString("category", "General")

                        if (name.isNotBlank() && url.isNotBlank()) {
                            channels.add(Channel(name, url, category))
                        }
                    }
                }

                runOnUiThread {
                    status.text = "Canales encontrados: ${channels.size}"
                    showChannels()
                }

            } catch (e: Exception) {
                runOnUiThread {
                    status.text = "Error de conexión: ${e.message}"
                }
            }
        }
    }

    private fun showChannels() {
        channelList.removeAllViews()

        if (channels.isEmpty()) {
            val empty = TextView(this).apply {
                text = "No hay canales disponibles"
                textSize = 17f
                setTextColor(android.graphics.Color.WHITE)
                setPadding(12, 24, 12, 24)
            }
            channelList.addView(empty)
            return
        }

        channels.forEach { channel ->
            val button = Button(this).apply {
                text = "${channel.name}  •  ${channel.category}"
                textSize = 15f
                setTextColor(android.graphics.Color.WHITE)
                setBackgroundColor(android.graphics.Color.rgb(24, 38, 70))
                isAllCaps = false

                setOnClickListener {
                    playChannel(channel.url)
                    status.text = "Reproduciendo: ${channel.name}"
                }
            }

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 6, 0, 6)

            channelList.addView(button, params)
        }
    }

    private fun playChannel(url: String) {
        try {
            val mediaItem = MediaItem.fromUri(url)
            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
        } catch (e: Exception) {
            status.text = "No se pudo reproducir el canal"
        }
    }

    override fun onStop() {
        super.onStop()
        if (::player.isInitialized) player.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::player.isInitialized) player.release()
    }
}
