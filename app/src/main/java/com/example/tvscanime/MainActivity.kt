package com.example.tvscanime

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
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

        val wideLayout = resources.configuration.screenWidthDp >= 720
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(36), dp(24), dp(36), dp(24))
            setBackgroundColor(0xff101820.toInt())
        }

        root.addView(TextView(this).apply {
            text = "TVScanIME"
            textSize = 30f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        })
        root.addView(TextView(this).apply {
            text = "手机扫码，使用手机键盘输入电视文字"
            textSize = 17f
            setTextColor(0xffc9d4df.toInt())
            gravity = Gravity.CENTER
            setPadding(0, dp(6), 0, dp(18))
        })

        val content = LinearLayout(this).apply {
            orientation = if (wideLayout) LinearLayout.HORIZONTAL else LinearLayout.VERTICAL
            gravity = Gravity.CENTER
        }

        val qrColumn = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(16), dp(8), dp(16), dp(8))
        }
        qrView = ImageView(this).apply {
            contentDescription = "电视局域网输入页面二维码"
            setBackgroundColor(Color.WHITE)
            setPadding(dp(9), dp(9), dp(9), dp(9))
            scaleType = ImageView.ScaleType.FIT_CENTER
        }
        val qrSize = if (wideLayout) 224 else 196
        qrColumn.addView(qrView, LinearLayout.LayoutParams(dp(qrSize), dp(qrSize)))

        addressView = TextView(this).apply {
            textSize = 17f
            typeface = Typeface.MONOSPACE
            setTextColor(0xff8fb7f0.toInt())
            gravity = Gravity.CENTER
            maxLines = 2
            setPadding(0, dp(12), 0, dp(4))
        }
        qrColumn.addView(addressView)
        networkHint = TextView(this).apply {
            textSize = 13f
            setTextColor(0xffffcc80.toInt())
            gravity = Gravity.CENTER
            setPadding(0, dp(4), 0, dp(8))
        }
        qrColumn.addView(networkHint)
        content.addView(qrColumn, if (wideLayout)
            LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
        else LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))

        val actions = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(28), dp(8), dp(28), dp(8))
        }
        actions.addView(TextView(this).apply {
            text = "使用方法"
            textSize = 22f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
        })
        actions.addView(TextView(this).apply {
            text = "1. 手机和电视连接同一个 Wi-Fi 或局域网\n2. 手机扫描左侧二维码\n3. 在手机网页输入，文字会同步到电视"
            textSize = 16f
            setTextColor(0xffc9d4df.toInt())
            setLineSpacing(dp(5).toFloat(), 1f)
            setPadding(0, dp(10), 0, dp(18))
        })

        val testButton = actionButton("测试输入法") { showImeTestDialog() }
        val pickerButton = actionButton("选择当前输入法") {
            (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).showInputMethodPicker()
        }
        val settingsButton = actionButton("打开输入法设置") {
            startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
        }
        actions.addView(testButton)
        actions.addView(pickerButton)
        actions.addView(settingsButton)
        actions.addView(TextView(this).apply {
            text = "十字键：方向键移动焦点 · OK 确认\n飞鼠：指针悬停高亮 · 单击操作 · 返回退出"
            textSize = 13f
            setTextColor(0xff8293a8.toInt())
            setPadding(0, dp(14), 0, 0)
        })
        content.addView(actions, if (wideLayout)
            LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
        else LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))

        if (wideLayout) {
            root.addView(content, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f
            ))
            setContentView(root)
        } else {
            val scroll = ScrollView(this).apply { addView(root) }
            root.addView(content)
            setContentView(scroll)
        }

        testButton.requestFocus()
        refreshConnectionInfo()
    }

    override fun onResume() {
        super.onResume()
        if (::bridge.isInitialized) refreshConnectionInfo()
    }

    private fun actionButton(label: String, action: () -> Unit): TextView {
        return TextView(this).apply {
            text = label
            textSize = 18f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            isFocusable = true
            isFocusableInTouchMode = true
            isClickable = true
            setTextColor(Color.WHITE)
            setPadding(dp(18), dp(13), dp(18), dp(13))
            background = actionBackground(false)
            setOnFocusChangeListener { view, focused ->
                view.background = actionBackground(focused)
                view.scaleX = if (focused) 1.035f else 1f
                view.scaleY = if (focused) 1.035f else 1f
            }
            setOnHoverListener { view, event ->
                val hovering = event.action == android.view.MotionEvent.ACTION_HOVER_ENTER ||
                    event.action == android.view.MotionEvent.ACTION_HOVER_MOVE
                val active = hovering || view.hasFocus()
                view.background = actionBackground(active)
                view.scaleX = if (active) 1.035f else 1f
                view.scaleY = if (active) 1.035f else 1f
                false
            }
            setOnClickListener { action() }
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.bottomMargin = dp(12)
            layoutParams = params
        }
    }

    private fun actionBackground(focused: Boolean) = GradientDrawable().apply {
        setColor(if (focused) 0xff28558a.toInt() else 0xff1b2a3c.toInt())
        cornerRadius = dp(12).toFloat()
        setStroke(dp(if (focused) 3 else 1),
            if (focused) 0xffa9d0ff.toInt() else 0xff49617d.toInt())
    }

    private fun showImeTestDialog() {
        val input = EditText(this).apply {
            hint = "在这里输入，检查电视输入法"
            textSize = 18f
            setSingleLine(true)
        }
        val dialog = AlertDialog.Builder(this)
            .setTitle("输入法测试")
            .setMessage("如果 TVScanIME 已设为当前输入法，点击输入框后应显示扫码面板。")
            .setView(input)
            .setPositiveButton("完成", null)
            .setNegativeButton("关闭", null)
            .create()
        dialog.setOnShowListener {
            input.requestFocus()
            input.post {
                (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
                    .showSoftInput(input, InputMethodManager.SHOW_IMPLICIT)
            }
        }
        dialog.show()
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
        val matrix: BitMatrix = MultiFormatWriter().encode(value, BarcodeFormat.QR_CODE, size, size)
        return Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).also { bitmap ->
            for (x in 0 until size) for (y in 0 until size) {
                bitmap.setPixel(x, y, if (matrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density + 0.5f).toInt()
}
