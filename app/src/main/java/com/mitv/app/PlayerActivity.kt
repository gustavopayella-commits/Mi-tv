
package com.mitv.app

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class PlayerActivity : Activity() {

    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView

    private val fondo = Color.rgb(4, 8, 25)
    private val azul = Color.rgb(0, 180, 255)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val titulo = intent.getStringExtra("titulo") ?: "MI TV"
        val url = intent.getStringExtra("url") ?: ""

        crearInterfaz(titulo)

        if (url.isNotBlank()) {
            reproducir(url)
        } else {
            mostrarMensaje(
                "Seleccioná un canal o cargá una lista IPTV."
            )
        }
    }

    private fun crearInterfaz(titulo: String) {

        val principal = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(fondo)
            setPadding(12, 12, 12, 12)
        }

        val encabezado = TextView(this).apply {
            text = "◀  MI TV PRO"
            textSize = 23f
            setTextColor(azul)
            gravity = Gravity.CENTER_VERTICAL
            setPadding(8, 15, 8, 15)
            setOnClickListener { finish() }
        }

        principal.addView(encabezado)

        val nombre = TextView(this).apply {
            text = titulo
            textSize = 19f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(8, 15, 8, 15)
        }

        principal.addView(nombre)

        playerView = PlayerView(this).apply {
            useController = true
            setShowBuffering(
                PlayerView.SHOW_BUFFERING_WHEN_PLAYING
            )
            setBackgroundColor(Color.BLACK)
            layoutParams = LinearLayout.LayoutParams(
                -1, 0, 1f
            )
        }

        principal.addView(playerView)

        val estado = TextView(this).apply {
            text = "MI TV • REPRODUCTOR MULTIMEDIA"
            textSize = 13f
            setTextColor(azul)
            gravity = Gravity.CENTER
            setPadding(8, 16, 8, 16)
        }

        principal.addView(estado)

        val volver = Button(this).apply {
            text = "VOLVER AL INICIO"
            setOnClickListener { finish() }
        }

        principal.addView(volver)

        setContentView(principal)
    }

    private fun reproducir(url: String) {

        try {
            player?.release()

            player = ExoPlayer.Builder(this).build().also {

                playerView.player = it

                it.addListener(object : Player.Listener {

                    override fun onPlayerError(
                        error: PlaybackException
                    ) {
                        mostrarMensaje(
                            "No se pudo reproducir este enlace. " +
                            "Verificá la conexión y el formato."
                        )
                    }

                    override fun onPlaybackStateChanged(
                        playbackState: Int
                    ) {
                        when (playbackState) {
                            Player.STATE_BUFFERING ->
                                mostrarMensaje("Cargando transmisión...")

                            Player.STATE_READY ->
                                mostrarMensaje("Reproduciendo")

                            Player.STATE_ENDED ->
                                mostrarMensaje("Transmisión finalizada")
                        }
                    }
                })

                val mediaItem = MediaItem.fromUri(url)

                it.setMediaItem(mediaItem)
                it.prepare()
                it.playWhenReady = true
            }

        } catch (e: Exception) {
            mostrarMensaje("Error al iniciar el reproductor.")
        }
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(
            this,
            mensaje,
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onStop() {
        super.onStop()
        player?.pause()
    }

    override fun onDestroy() {
        playerView.player = null
        player?.release()
        player = null
        super.onDestroy()
    }
}
