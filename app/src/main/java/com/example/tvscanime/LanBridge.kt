package com.example.tvscanime

import android.content.Context
import fi.iki.elonen.NanoHTTPD
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import org.json.JSONObject
import java.net.Inet4Address
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.util.Collections

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

    fun getUrl(): String = "http://${ipAddress()}:8766/"

    /** Finds an IPv4 address on an active LAN interface (Wi-Fi or Ethernet). */
    private fun ipAddress(): String {
        return try {
            val interfaces = NetworkInterface.getNetworkInterfaces() ?: return "127.0.0.1"
            val candidates = Collections.list(interfaces)
                .filter { it.isUp && !it.isLoopback }
                .flatMap { network ->
                    Collections.list(network.inetAddresses)
                        .filterIsInstance<Inet4Address>()
                        .filter { !it.isLoopbackAddress && !it.isLinkLocalAddress }
                        .map { network.name.lowercase() to it.hostAddress }
                }
            candidates.sortedBy { (name, _) ->
                when {
                    name.startsWith("wlan") -> 0
                    name.startsWith("eth") -> 1
                    name.startsWith("en") -> 2
                    name.startsWith("rmnet") -> 4
                    name.startsWith("tun") -> 5
                    else -> 3
                }
            }.firstOrNull()?.second ?: "127.0.0.1"
        } catch (_: Exception) {
            "127.0.0.1"
        }
    }

    private fun broadcast(message: String) {
        try {
            socket?.connections?.forEach { connection -> connection.send(message) }
        } catch (_: Exception) { }
    }

    private inner class HttpServer : NanoHTTPD("0.0.0.0", 8766) {
        override fun serve(session: IHTTPSession): Response {
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
