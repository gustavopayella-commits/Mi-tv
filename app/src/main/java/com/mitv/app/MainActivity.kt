
package com.mitv.app

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.content.Intent
import android.view.Gravity
import android.view.View
import android.widget.*

class MainActivity : Activity() {

    private val azul = Color.rgb(0, 145, 255)
    private val violeta = Color.rgb(150, 45, 255)
    private val fondo = Color.rgb(4, 8, 28)
    private val blanco = Color.WHITE

    private val categorias = listOf(
        "🇦🇷 TV Argentina",
        "🌎 Latinoamérica",
        "⚽ Deportes",
        "🎬 Películas",
        "📺 Series",
        "📰 Noticias",
        "▶ YouTube"
    )

    private val canales = listOf(
        "TN", "A24", "AMERICA TV",
        "TYC SPORTS", "ESPN", "HBO",
        "FOX SPORTS", "CINE", "YOUTUBE"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearPantalla()
    }

    private fun fondoRedondeado(
        color1: Int,
        color2: Int,
        radio: Float = 24f
    ): GradientDrawable {
        return GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            intArrayOf(color1, color2)
        ).apply {
            cornerRadius = radio
            setStroke(2, azul)
        }
    }

    private fun texto(
        contenido: String,
        tamano: Float,
        color: Int = blanco
    ): TextView {
        return TextView(this).apply {
            text = contenido
            textSize = tamano
            setTextColor(color)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER_VERTICAL
        }
    }

    private fun boton(
        nombre: String,
        accion: () -> Unit
    ): TextView {
        return TextView(this).apply {
            text = nombre
            textSize = 15f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(blanco)
            gravity = Gravity.CENTER
            background = fondoRedondeado(
                Color.rgb(8, 35, 100),
                Color.rgb(65, 15, 130)
            )
            setPadding(20, 16, 20, 16)
            setOnClickListener { accion() }
        }
    }

    private fun crearPantalla() {

        val principal = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(fondo)
            setPadding(16, 12, 16, 12)
        }

        val scroll = ScrollView(this)

        val contenido = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        scroll.addView(contenido)

        // ENCABEZADO

        val encabezado = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val logo = texto("▶ MI TV", 30f, Color.rgb(0, 190, 255))
        logo.setPadding(4, 12, 4, 12)

        encabezado.addView(
            logo,
            LinearLayout.LayoutParams(0, -2, 1f)
        )

        encabezado.addView(
            boton("⚙ ADMIN") {
                abrirAdmin()
            }
        )

        contenido.addView(encabezado)

        val subtitulo = texto(
            "IPTV  •  CINE  •  SERIES  •  DEPORTES",
            12f,
            Color.LTGRAY
        )

        contenido.addView(subtitulo)

        // BUSCADOR

        val buscador = EditText(this).apply {
            hint = "⌕  Buscar canales..."
            setHintTextColor(Color.LTGRAY)
            setTextColor(blanco)
            textSize = 15f
            singleLine = true
            setPadding(18, 15, 18, 15)
            background = fondoRedondeado(
                Color.rgb(10, 25, 65),
                Color.rgb(20, 15, 65)
            )
        }

        val margen = LinearLayout.LayoutParams(
            -1, -2
        ).apply {
            topMargin = 20
            bottomMargin = 18
        }

        contenido.addView(buscador, margen)

        // DESTACADO

        val destacado = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 22, 20, 22)
            background = fondoRedondeado(
                Color.rgb(0, 35, 105),
                Color.rgb(65, 10, 120),
                30f
            )
        }

        destacado.addView(
            texto("✦ MI TV PREMIUM", 14f, Color.CYAN)
        )

        destacado.addView(
            texto("TU ENTRETENIMIENTO\nEN UN SOLO LUGAR", 25f)
        )

        destacado.addView(
            texto(
                "Televisión • Películas • Deportes",
                14f,
                Color.LTGRAY
            )
        )

        val verTv = boton("▶  ABRIR REPRODUCTOR") {
            abrirReproductor("", "MI TV")
        }

        val margenBoton = LinearLayout.LayoutParams(
            -1, -2
        ).apply {
            topMargin = 18
        }

        destacado.addView(verTv, margenBoton)

        val margenDestacado = LinearLayout.LayoutParams(
            -1, -2
        ).apply {
            bottomMargin = 22
        }

        contenido.addView(destacado, margenDestacado)

        // CATEGORÍAS

        contenido.addView(
            texto("CATEGORÍAS", 21f, Color.CYAN)
        )

        val filaCategorias = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        val scrollCategorias = HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false
            addView(filaCategorias)
        }

        categorias.forEach { categoria ->

            val item = boton(categoria) {
                Toast.makeText(
                    this,
                    "Categoría: $categoria",
                    Toast.LENGTH_SHORT
                ).show()
            }

            val params = LinearLayout.LayoutParams(
                -2, 65
            ).apply {
                setMargins(5, 12, 5, 12)
            }

            filaCategorias.addView(item, params)
        }

        contenido.addView(scrollCategorias)

        // CANALES

        contenido.addView(
            texto("CANALES DESTACADOS", 21f, Color.CYAN)
        )

        val filaCanales = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        val scrollCanales = HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false
            addView(filaCanales)
        }

        canales.forEach { canal ->

            val tarjeta = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setPadding(12, 18, 12, 18)
                background = fondoRedondeado(
                    Color.rgb(5, 30, 85),
                    Color.rgb(35, 10, 90)
                )

                addView(
                    texto("▶", 30f, Color.CYAN).apply {
                        gravity = Gravity.CENTER
                    }
                )

                addView(
                    texto(canal, 14f).apply {
                        gravity = Gravity.CENTER
                    }
                )

                setOnClickListener {
                    abrirReproductor("", canal)
                }
            }

            val params = LinearLayout.LayoutParams(
                145, 115
            ).apply {
                setMargins(5, 14, 5, 18)
            }

            filaCanales.addView(tarjeta, params)
        }

        contenido.addView(scrollCanales)

        // ADMINISTRACIÓN

        contenido.addView(
            texto("MI TV PRO", 24f, Color.CYAN)
        )

        contenido.addView(
            texto(
                "Tu centro de entretenimiento",
                13f,
                Color.LTGRAY
            )
        )

        contenido.addView(
            boton("⚙ PANEL DE ADMINISTRACIÓN") {
                abrirAdmin()
            },
            LinearLayout.LayoutParams(-1, 65).apply {
                topMargin = 18
            }
        )

        principal.addView(
            scroll,
            LinearLayout.LayoutParams(-1, 0, 1f)
        )

        // MENÚ INFERIOR

        val menu = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(0, 12, 0, 8)
        }

        listOf("⌂ Inicio", "▣ TV", "🎬 Cine", "★ Favoritos").forEach {
            menu.addView(
                boton(it) {
                    Toast.makeText(
                        this,
                        it,
                        Toast.LENGTH_SHORT
                    ).show()
                },
                LinearLayout.LayoutParams(0, 55, 1f)
            )
        }

        principal.addView(menu)

        setContentView(principal)
    }

    private fun abrirAdmin() {
        startActivity(
            Intent(this, AdminActivity::class.java)
        )
    }

    private fun abrirReproductor(
        url: String,
        titulo: String
    ) {
        val intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra("url", url)
        intent.putExtra("titulo", titulo)
        startActivity(intent)
    }
}
