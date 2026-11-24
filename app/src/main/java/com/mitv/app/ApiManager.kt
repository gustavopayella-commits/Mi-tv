
package com.mitv.app

import okhttp3.*
import org.json.JSONObject
import org.json.JSONArray
import java.io.IOException
import java.util.concurrent.TimeUnit

object ApiManager {

    private const val BASE_URL =
        "https://teve.totalh.net/tvserver/api/"

    private val cliente = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    interface Respuesta {
        fun correcto(datos: JSONObject)
        fun error(mensaje: String)
    }

    fun obtenerListas(respuesta: Respuesta) {

        val request = Request.Builder()
            .url(BASE_URL + "lists.php")
            .get()
            .build()

        cliente.newCall(request).enqueue(
            object : Callback {

                override fun onFailure(
                    call: Call,
                    e: IOException
                ) {
                    respuesta.error(
                        e.message ?: "Error de conexión"
                    )
                }

                override fun onResponse(
                    call: Call,
                    response: Response
                ) {
                    response.use {
                        try {
                            val contenido = it.body?.string()
                                ?: throw Exception("Respuesta vacía")

                            if (!it.isSuccessful) {
                                throw Exception(
                                    "Error HTTP ${it.code}"
                                )
                            }

                            val json = JSONObject(contenido)
                            respuesta.correcto(json)

                        } catch (e: Exception) {
                            respuesta.error(
                                e.message ?: "Error de lectura"
                            )
                        }
                    }
                }
            }
        )
    }

    fun agregarLista(
        datos: JSONObject,
        respuesta: Respuesta
    ) {
        enviar("lists.php", datos, respuesta)
    }

    fun editarLista(
        datos: JSONObject,
        respuesta: Respuesta
    ) {
        enviar("lists.php", datos, respuesta)
    }

    fun eliminarLista(
        id: String,
        respuesta: Respuesta
    ) {
        val datos = JSONObject()
        datos.put("id", id)

        enviar("lists.php", datos, respuesta, "DELETE")
    }

    private fun enviar(
        archivo: String,
        datos: JSONObject,
        respuesta: Respuesta,
        metodo: String = "POST"
    ) {

        val cuerpo = datos.toString().toRequestBody(
            "application/json; charset=utf-8".toMediaType()
        )

        val builder = Request.Builder()
            .url(BASE_URL + archivo)

        when (metodo) {
            "DELETE" -> builder.delete(cuerpo)
            else -> builder.post(cuerpo)
        }

        cliente.newCall(builder.build()).enqueue(
            object : Callback {

                override fun onFailure(
                    call: Call,
                    e: IOException
                ) {
                    respuesta.error(
                        e.message ?: "Error de conexión"
                    )
                }

                override fun onResponse(
                    call: Call,
                    response: Response
                ) {
                    response.use {
                        try {
                            val contenido = it.body?.string()
                                ?: throw Exception("Respuesta vacía")

                            if (!it.isSuccessful) {
                                throw Exception(
                                    "Error HTTP ${it.code}"
                                )
                            }

                            respuesta.correcto(
                                JSONObject(contenido)
                            )

                        } catch (e: Exception) {
                            respuesta.error(
                                e.message ?: "Error del servidor"
                            )
                        }
                    }
                }
            }
        )
    }
}
