package com.example.tvscanime

import android.content.Context
import android.net.wifi.WifiManager
import android.text.format.Formatter
import fi.iki.elonen.NanoHTTPD
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import org.json.JSONObject
import java.net.InetSocketAddress
import java.security.SecureRandom

class LanBridge private constructor(context: Context) {
    interface Listener {
        fun onText(text: String)
        fun onAction(action: String)
    }

    companion object {
        @Volatile private var instance: LanBridge? = null
        fun get(context: Context): LanBridge = instance ?: synchronized(this) {
            instance ?: LanBridge(context.applicationContext).also { instance = it }
        }
    }

    private val appContext = context.applicationContext
    private val token = ByteArray(16).also { SecureRandom().nextBytes(it) }
        .joinToString("") { "%02x".format(it) }
    @Volatile private var listener: Listener? = null
    @Volatile private var http: HttpServer? = null
    @Volatile private var socket: SocketServer? = null
    @Volatile private var currentText: String = ""

    fun setListener(value: Listener?) { listener = value }

    fun getCurrentText(): String = currentText

    fun setCurrentText(value: String?) {
        currentText = value.orEmpty()
        broadcast(JSONObject().put("type", "sync").put("value", currentText).toString())
    }

    @Synchronized
    fun start() {
        try {
            if (http == null) http = HttpServer().also {
                it.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false)
            }
            if (socket == null) socket = SocketServer(InetSocketAddress("0.0.0.0", 8765)).also {
                it.start()
            }
        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    fun getUrl(): String = "http://${ipAddress()}:8766/?t=$token"

    @Suppress("DEPRECATION")
    private fun ipAddress(): String = try {
        val wifi = appContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        Formatter.formatIpAddress(wifi.connectionInfo.ipAddress).takeIf {
            it != "0.0.0.0" && it != "127.0.0.1"
        } ?: "127.0.0.1"
    } catch (_: Exception) { "127.0.0.1" }

    private fun broadcast(message: String) {
        try {
            socket?.connections?.forEach { connection -> connection.send(message) }
        } catch (_: Exception) { }
    }

    private inner class HttpServer : NanoHTTPD("0.0.0.0", 8766) {
        override fun serve(session: IHTTPSession): Response {
            val supplied = session.parameters["t"]?.firstOrNull()
            if (supplied != token) {
                return newFixedLengthResponse(
                    Response.Status.FORBIDDEN, "text/plain", "Pairing token required"
                )
            }
            return newFixedLengthResponse(
                Response.Status.OK, "text/html; charset=utf-8", PhonePage.HTML
            )
        }
    }

    private inner class SocketServer(address: InetSocketAddress) : WebSocketServer(address) {
        override fun onOpen(connection: WebSocket, handshake: ClientHandshake) = Unit
        override fun onClose(connection: WebSocket, code: Int, reason: String, remote: Boolean) = Unit

        override fun onMessage(connection: WebSocket, message: String) {
            try {
                val data = JSONObject(message)
                if (data.optString("token") != token) {
                    connection.close()
                    return
                }
                when (data.optString("type")) {
                    "text" -> {
                        val value = data.optString("value", "")
                        currentText = value
                        listener?.onText(value)
                    }
                    "action" -> listener?.onAction(data.optString("value"))
                }
            } catch (_: Exception) { }
        }

        override fun onError(connection: WebSocket?, error: Exception) {
            error.printStackTrace()
        }
        override fun onStart() = Unit
    }
}
