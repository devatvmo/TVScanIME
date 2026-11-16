package com.example.tvscanime

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

class MainActivity : Activity() {
    private lateinit var bridge: LanBridge
    private lateinit var addressView: TextView
    private lateinit var qrView: ImageView
    private lateinit var networkHint: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bridge = LanBridge.get(this)
        bridge.start()

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(28), dp(24), dp(28), dp(28))
            setBackgroundColor(0xff101820.toInt())
        }

        root.addView(TextView(this).apply {
            text = "TVScanIME"
            textSize = 29f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        })
        root.addView(TextView(this).apply {
            text = "手机扫码，使用手机键盘输入电视文字"
            textSize = 16f
            setTextColor(0xffc9d4df.toInt())
            gravity = Gravity.CENTER
            setPadding(0, dp(8), 0, dp(18))
        })

        qrView = ImageView(this).apply {
            contentDescription = "电视局域网输入页面二维码"
            setBackgroundColor(Color.WHITE)
            setPadding(dp(8), dp(8), dp(8), dp(8))
        }
        root.addView(qrView, LinearLayout.LayoutParams(dp(224), dp(224)))

        addressView = TextView(this).apply {
            textSize = 17f
            typeface = Typeface.MONOSPACE
            setTextColor(0xff8fb7f0.toInt())
            gravity = Gravity.CENTER
            setPadding(0, dp(14), 0, dp(4))
        }
        root.addView(addressView)

        networkHint = TextView(this).apply {
            textSize = 13f
            setTextColor(0xffffcc80.toInt())
            gravity = Gravity.CENTER
            setPadding(0, dp(4), 0, dp(12))
        }
        root.addView(networkHint)

        root.addView(TextView(this).apply {
            text = "输入框测试"
            textSize = 15f
            setTextColor(Color.WHITE)
            setPadding(0, dp(8), 0, dp(6))
        })

        val testInput = EditText(this).apply {
            hint = "点击这里，测试电视输入法…"
            textSize = 17f
            setTextColor(Color.WHITE)
            setHintTextColor(0xff8293a8.toInt())
            minLines = 2
            gravity = Gravity.TOP or Gravity.START
            setPadding(dp(12), dp(12), dp(12), dp(12))
            background = GradientDrawable().apply {
                setColor(0xff1b2a3c.toInt())
                cornerRadius = dp(10).toFloat()
                setStroke(dp(1), 0xff49617d.toInt())
            }
        }
        root.addView(testInput, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dp(92)
        ))

        root.addView(Button(this).apply {
            text = "打开输入法设置"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
            }
        })
        root.addView(Button(this).apply {
            text = "选择当前输入法"
            setOnClickListener {
                (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
                    .showInputMethodPicker()
            }
        })

        root.addView(TextView(this).apply {
            text = "手机和电视需连接同一个 Wi-Fi。扫码页面无需配对码。"
            textSize = 12f
            setTextColor(0xff8293a8.toInt())
            gravity = Gravity.CENTER
            setPadding(0, dp(12), 0, 0)
        })

        val scroll = ScrollView(this).apply { addView(root) }
        setContentView(scroll)
        refreshConnectionInfo()
    }

    override fun onResume() {
        super.onResume()
        if (::bridge.isInitialized) refreshConnectionInfo()
    }

    private fun refreshConnectionInfo() {
        val url = bridge.getUrl()
        addressView.text = url
        qrView.setImageBitmap(makeQr(url, 420))
        networkHint.text = if (url.contains("127.0.0.1")) {
            "尚未检测到局域网地址，请先连接 Wi-Fi 或网线"
        } else {
            "手机浏览器可直接访问此地址"
        }
    }

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

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density + 0.5f).toInt()
}
