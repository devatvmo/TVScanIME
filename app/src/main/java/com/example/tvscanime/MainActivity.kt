package com.example.tvscanime

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(36, 36, 36, 36)
            setBackgroundColor(0xff101820.toInt())
        }
        box.addView(TextView(this).apply {
            text = "扫码输入法"
            textSize = 28f
            setTextColor(0xffffffff.toInt())
            gravity = Gravity.CENTER
        })
        box.addView(TextView(this).apply {
            text = "安装后请启用并设为默认输入法。\n电视和手机连接同一个 Wi-Fi。"
            textSize = 18f
            setTextColor(0xffc9d4df.toInt())
            gravity = Gravity.CENTER
            setPadding(0, 20, 0, 20)
        })
        box.addView(Button(this).apply {
            text = "打开输入法设置"
            setOnClickListener { startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)) }
        })
        box.addView(Button(this).apply {
            text = "选择当前输入法"
            setOnClickListener { startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)) }
        })
        setContentView(box)
    }
}
