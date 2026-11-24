
package com.mitv.app

import org.json.JSONObject

data class Canal(
    val nombre: String,
    val url: String,
    val grupo: String = "General",
    val logo: String = "",
    val tipo: String = "AUTO"
)

object PlaylistManager {

    fun leerM3U(contenido: String): List<Canal> {

        val canales = mutableListOf<Canal>()

        var nombre = ""
        var grupo = "General"
        var logo = ""

        contenido.lineSequence().forEach { lineaOriginal ->

            val linea = lineaOriginal.trim()

            if (linea.startsWith("#EXTINF", true)) {

                val titulo = linea.substringAfterLast(",").trim()

                nombre = titulo.ifBlank {
                    "Canal sin nombre"
                }

                grupo = extraerAtributo(
                    linea,
                    "group-title"
                ) ?: "General"

                logo = extraerAtributo(
                    linea,
                    "tvg-logo"
                ) ?: ""

            } else if (
                linea.isNotEmpty() &&
                !linea.startsWith("#") &&
                nombre.isNotEmpty()
            ) {

                val tipo = detectarTipo(linea)

                canales.add(
                    Canal(
                        nombre = nombre,
                        url = linea,
                        grupo = grupo,
                        logo = logo,
                        tipo = tipo
                    )
                )

                nombre = ""
                grupo = "General"
                logo = ""
            }
        }

        return canales
    }

    private fun extraerAtributo(
        linea: String,
        atributo: String
    ): String? {

        val patron = Regex(
            """$atributo="([^"]*)"""",
            RegexOption.IGNORE_CASE
        )

        return patron.find(linea)
            ?.groupValues
            ?.getOrNull(1)
    }

    fun detectarTipo(url: String): String {

        val direccion = url.lowercase()

        return when {
            direccion.contains(".m3u8") -> "M3U8"
            direccion.contains(".m3u") -> "M3U"
            direccion.contains(".ts") -> "TS"
            direccion.contains(".mp4") -> "MP4"
            direccion.contains("youtube.com") -> "YOUTUBE"
            direccion.contains("youtu.be") -> "YOUTUBE"
            else -> "AUTO"
        }
    }

    fun crearXtreamUrl(
        servidor: String,
        usuario: String,
        password: String
    ): String {

        return servidor.trimEnd('/') +
            "/get.php?username=$usuario" +
            "&password=$password&type=m3u_plus&output=ts"
    }

    fun validarUrl(url: String): Boolean {

        return url.startsWith("http://") ||
            url.startsWith("https://")
    }

    fun convertirCanalJson(
        canal: Canal
    ): JSONObject {

        return JSONObject().apply {
            put("name", canal.nombre)
            put("url", canal.url)
            put("group", canal.grupo)
            put("logo", canal.logo)
            put("type", canal.tipo)
        }
    }

    fun filtrarCanales(
        canales: List<Canal>,
        busqueda: String
    ): List<Canal> {

        if (busqueda.isBlank()) return canales

        return canales.filter {
            it.nombre.contains(
                busqueda,
                ignoreCase = true
            ) ||
            it.grupo.contains(
                busqueda,
                ignoreCase = true
            )
        }
    }

    fun obtenerCategorias(
        canales: List<Canal>
    ): List<String> {

        return canales
            .map { it.grupo }
            .distinct()
            .sorted()
    }
}
