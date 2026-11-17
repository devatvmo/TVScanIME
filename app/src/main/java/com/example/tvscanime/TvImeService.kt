package com.example.tvscanime

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.inputmethodservice.InputMethodService
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

class TvImeService : InputMethodService(), LanBridge.Listener {
    private var preview: TextView? = null
    private var status: TextView? = null
    private var addressView: TextView? = null
    private var qrImage: ImageView? = null
    private lateinit var bridge: LanBridge
    private val mainHandler = Handler(Looper.getMainLooper())

    override fun onCreate() {
        super.onCreate()
        bridge = LanBridge.get(this)
        bridge.setListener(this)
        bridge.start()
    }

    override fun onCreateInputView(): View {
        bridge.start()

        val shell = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(28), dp(20), dp(30), dp(20))
            background = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(0xf20d1725.toInt(), 0xf2182a40.toInt())
            ).apply {
                cornerRadius = dp(22).toFloat()
                setStroke(dp(1), 0x664e6b8d)
            }
            elevation = dp(12).toFloat()
        }

        val qrCard = LinearLayout(this).apply {
            gravity = Gravity.CENTER
            setPadding(dp(9), dp(9), dp(9), dp(9))
            background = GradientDrawable().apply {
                setColor(Color.WHITE)
                cornerRadius = dp(13).toFloat()
            }
        }
        val qrView = ImageView(this).apply {
            try {
                setImageBitmap(makeQr(bridge.getUrl(), 196))
            } catch (_: Exception) { }
            contentDescription = "扫码连接手机输入页面"
        }
        qrImage = qrView
        qrCard.addView(qrView, LinearLayout.LayoutParams(dp(196), dp(196)))
        shell.addView(qrCard, LinearLayout.LayoutParams(dp(214), dp(214)))

        val right = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(24), 0, 0, 0)
        }

        right.addView(TextView(this).apply {
            text = "TV SCAN  /  REMOTE INPUT"
            setTextColor(0xff7fa9e8.toInt())
            textSize = 11f
            letterSpacing = .12f
        })
        right.addView(TextView(this).apply {
            text = "扫码，开始输入"
            textSize = 25f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            setPadding(0, dp(5), 0, dp(5))
        })
        status = TextView(this).apply {
            text = "等待手机连接"
            setTextColor(0xff9fb2c9.toInt())
            textSize = 14f
        }
        preview = TextView(this).apply {
            text = "手机扫码后，在网页中输入文字"
            setTextColor(0xffe6edf7.toInt())
            textSize = 17f
            maxLines = 2
            setPadding(0, dp(13), 0, dp(9))
        }
        right.addView(status)
        addressView = TextView(this).apply {
            text = bridge.getUrl()
            setTextColor(0xff8fb7f0.toInt())
            textSize = 13f
            setPadding(0, dp(5), 0, dp(2))
        }
        right.addView(addressView)
        right.addView(preview)
        right.addView(TextView(this).apply {
            text = "遥控器按返回键收起输入面板"
            setTextColor(0xff7d91aa.toInt())
            textSize = 12f
        })
        shell.addView(right, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        return shell
    }

    // TV emulators often report a hardware keyboard, which suppresses the IME panel by default.
    // This IME is designed to show its QR panel even when a hardware keyboard is present.
    override fun onEvaluateInputViewShown(): Boolean = true

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        bridge.start()
        val url = bridge.getUrl()
        addressView?.text = url
        try { qrImage?.setImageBitmap(makeQr(url, 196)) } catch (_: Exception) { }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            requestHideSelf(0)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        if (::bridge.isInitialized) bridge.setListener(null)
        super.onDestroy()
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density + 0.5f).toInt()

    private fun makeQr(value: String, size: Int): Bitmap {
        val matrix: BitMatrix = MultiFormatWriter().encode(
            value, BarcodeFormat.QR_CODE, size, size
        )
        return Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).also { bitmap ->
            for (x in 0 until size) {
                for (y in 0 until size) {
                    bitmap.setPixel(x, y, if (matrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
        }
    }

    override fun onText(text: String) {
        mainHandler.post {
            preview?.text = text.ifEmpty { "等待输入…" }
            val connection: InputConnection? = currentInputConnection
            if (connection != null) {
                connection.beginBatchEdit()
                connection.setComposingText(text, 1)
                connection.endBatchEdit()
            }
            status?.text = "手机已连接 · 实时同步中"
        }
    }

    override fun onAction(action: String) {
        mainHandler.post {
            val connection = currentInputConnection ?: return@post
            when (action) {
                "backspace" -> {
                    val before = connection.getTextBeforeCursor(1, 0)
                    if (!before.isNullOrEmpty()) connection.deleteSurroundingText(1, 0)
                }
                "clear" -> {
                    connection.setComposingText("", 1)
                    connection.commitText("", 1)
                    bridge.setCurrentText("")
                }
                "confirm" -> {
                    connection.finishComposingText()
                    bridge.setCurrentText("")
                    preview?.text = "已发送到电视"
                }
                "dismiss", "back" -> requestHideSelf(0)
                "dpad_left" -> sendDownUpKeyEvents(android.view.KeyEvent.KEYCODE_DPAD_LEFT)
                "dpad_right" -> sendDownUpKeyEvents(android.view.KeyEvent.KEYCODE_DPAD_RIGHT)
                "dpad_up" -> sendDownUpKeyEvents(android.view.KeyEvent.KEYCODE_DPAD_UP)
                "dpad_down" -> sendDownUpKeyEvents(android.view.KeyEvent.KEYCODE_DPAD_DOWN)
            }
        }
    }
}
