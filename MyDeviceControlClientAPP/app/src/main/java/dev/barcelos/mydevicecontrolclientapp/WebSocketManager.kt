package dev.barcelos.mydevicecontrolclientapp

import android.os.Handler
import android.os.Looper
import okhttp3.*
import java.io.IOException

class WebSocketManager(private val serverUrl: String) {

    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null
    private val handler = Handler(Looper.getMainLooper())

    fun connect(onMessageReceived: (String) -> Unit) {
        val request = Request.Builder().url(serverUrl).build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                sendAuthentication()
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                handler.post { onMessageReceived(text) }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                // Tratar falhas de conexão
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                // Tratar fechamento de conexão
            }
        })
    }


    fun sendAuthentication() {
        val authMessage = "{\"token\": \"${BuildConfig.ANDROID_TOKEN}\"}"
        webSocket?.send(authMessage)
    }

    fun sendScreenData(data: ByteArray) {
        val screenMessage = "{\"type\": \"screen\", \"data\": \"${data.toString(Charsets.UTF_8)}\"}"
        webSocket?.send(screenMessage)
    }

    fun sendCommand(command: String) {
        val commandMessage = "{\"type\": \"command\", \"command\": \"$command\"}"
        webSocket?.send(commandMessage)
    }

    fun close() {
        webSocket?.close(1000, "Closing connection")
    }
}
