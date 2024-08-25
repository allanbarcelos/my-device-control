package dev.barcelos.mydevicecontrolclientapp
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Button
import java.io.ByteArrayOutputStream
import java.io.IOException


import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.util.DisplayMetrics
import java.nio.ByteBuffer


import android.accessibilityservice.AccessibilityService
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.FrameLayout
import org.json.JSONObject

import android.graphics.Bitmap

import android.view.InputDevice

class MainActivity : Activity() {

    private lateinit var webSocketManager: WebSocketManager
    private val projectionManager by lazy { getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager }
    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val startButton: Button = findViewById(R.id.buttonStartService)
        startButton.setOnClickListener {
            startScreenCapture()
        }

        webSocketManager = WebSocketManager("ws://${BuildConfig.SERVER_API}:8080")
        webSocketManager.connect { message ->
            Log.d("WebSocket", "Received: $message")
        }
    }

    private fun startScreenCapture() {
        val captureIntent = projectionManager.createScreenCaptureIntent()
        startActivityForResult(captureIntent, SCREEN_CAPTURE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SCREEN_CAPTURE_REQUEST_CODE && resultCode == RESULT_OK) {
            mediaProjection = projectionManager.getMediaProjection(resultCode, data!!)
            setupVirtualDisplay()
        }
    }


    private fun setupVirtualDisplay() {
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(metrics)

        imageReader = ImageReader.newInstance(metrics.widthPixels, metrics.heightPixels, PixelFormat.RGBA_8888, 2)
        virtualDisplay = mediaProjection?.createVirtualDisplay(
            "ScreenCapture",
            metrics.widthPixels,
            metrics.heightPixels,
            metrics.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader?.surface,
            null,
            null
        )

        imageReader?.setOnImageAvailableListener({ reader ->
            val image = reader.acquireLatestImage()
            if (image != null) {
                val planes = image.planes
                val buffer: ByteBuffer = planes[0].buffer
                val pixelStride: Int = planes[0].pixelStride
                val rowStride: Int = planes[0].rowStride
                val rowPadding: Int = rowStride - pixelStride * image.width

                val bitmapWidth = image.width + rowPadding / pixelStride
                val bitmapHeight = image.height

                val byteArrayOutputStream = ByteArrayOutputStream()
                val bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
                bitmap.copyPixelsFromBuffer(buffer)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                val byteArray = byteArrayOutputStream.toByteArray()

                webSocketManager.sendScreenData(byteArray)
                image.close()
            }
        }, null)
    }

    private fun sendScreenshotToServer(imageData: ByteArray) {
        webSocketManager.sendScreenData(imageData)
    }


    private fun handleReceivedMessage(message: String) {
        try {
            val json = JSONObject(message)
            val command = json.getString("command")

            when (command) {
                "TOUCH" -> {
                    val x = json.getInt("x")
                    val y = json.getInt("y")
                    performTouch(x, y)
                }
                "SCROLL" -> {
                    val direction = json.getString("direction")
                    performScroll(direction)
                }
                "TYPE" -> {
                    val text = json.getString("text")
                    performTyping(text)
                }
                "KEYPRESS" -> {
                    val keyCode = json.getInt("keyCode")
                    performKeyPress(keyCode)
                }
                else -> {
                    Log.w("CommandHandler", "Comando desconhecido: $command")
                }
            }
        } catch (e: Exception) {
            Log.e("CommandHandler", "Erro ao interpretar o comando: $message", e)
        }
    }

    private fun performTouch(x: Int, y: Int) {
        // Executa um toque na posição (x, y)
        val downTime = System.currentTimeMillis()
        val eventTime = System.currentTimeMillis()
        val event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_DOWN, x.toFloat(), y.toFloat(), 0)
        val upEvent = MotionEvent.obtain(downTime, eventTime + 100, MotionEvent.ACTION_UP, x.toFloat(), y.toFloat(), 0)

        // Precisa de um serviço de acessibilidade ou injeção de evento para executar o toque
        injectMotionEvent(event)
        injectMotionEvent(upEvent)
    }

    private fun performScroll(direction: String) {
        // Executa uma rolagem (scroll) para cima ou para baixo
        when (direction) {
            "UP" -> {
                // Código para rolar para cima
                scrollUp()
            }
            "DOWN" -> {
                // Código para rolar para baixo
                scrollDown()
            }
            else -> Log.w("ScrollHandler", "Direção desconhecida: $direction")
        }
    }

    private fun performTyping(text: String) {
        // Envia texto para um campo de texto
        for (char in text) {
            val keyEvent = KeyEvent(KeyEvent.ACTION_DOWN, char.toInt())
            injectKeyEvent(keyEvent)
        }
    }

    private fun performKeyPress(keyCode: Int) {
        // Pressiona uma tecla específica (exemplo: botão Voltar)
        val keyEvent = KeyEvent(KeyEvent.ACTION_DOWN, keyCode)
        injectKeyEvent(keyEvent)
    }

    private fun injectKeyEvent(event: KeyEvent) {
        // Implementação para injetar um KeyEvent no sistema (exige permissões ou serviços especiais)
    }

    private fun scrollUp() {
        // Cria um MotionEvent que simula a rolagem para cima
        val downTime = System.currentTimeMillis()
        val eventTime = System.currentTimeMillis()

        val scrollUpEvent = MotionEvent.obtain(
            downTime, eventTime, MotionEvent.ACTION_SCROLL, 0f, -10f, 0
        )
        scrollUpEvent.setSource(InputDevice.SOURCE_CLASS_POINTER)

        injectMotionEvent(scrollUpEvent)
    }

    private fun scrollDown() {
        // Cria um MotionEvent que simula a rolagem para baixo
        val downTime = System.currentTimeMillis()
        val eventTime = System.currentTimeMillis()

        val scrollDownEvent = MotionEvent.obtain(
            downTime, eventTime, MotionEvent.ACTION_SCROLL, 0f, 10f, 0
        )
        scrollDownEvent.setSource(InputDevice.SOURCE_CLASS_POINTER)

        injectMotionEvent(scrollDownEvent)
    }

    private fun injectMotionEvent(event: MotionEvent) {
        // Injetar o MotionEvent no sistema
        // Esse método precisa ser implementado para enviar o evento ao sistema ou ao serviço de acessibilidade
        // Exemplo: se estiver usando um serviço de acessibilidade, você pode usar `dispatchGenericMotionEvent(event)`
        dispatchGenericMotionEvent(event)
    }

    companion object {
        private const val SCREEN_CAPTURE_REQUEST_CODE = 1
    }
}
