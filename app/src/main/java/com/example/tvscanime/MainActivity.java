package com.example.tvscanime;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.*;

public class MainActivity extends Activity {
    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(1); box.setGravity(Gravity.CENTER); box.setPadding(36,36,36,36);
        box.setBackgroundColor(0xff101820);
        TextView title = new TextView(this);
        title.setText("扫码输入法"); title.setTextSize(28); title.setTextColor(0xffffffff);
        TextView desc = new TextView(this);
        desc.setText("安装后请启用并设为默认输入法。\n电视和手机连接同一个 Wi‑Fi。");
        desc.setTextSize(18); desc.setTextColor(0xffc9d4df); desc.setGravity(Gravity.CENTER);
        Button settings = new Button(this); settings.setText("打开输入法设置");
        settings.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)));
        Button enable = new Button(this); enable.setText("选择当前输入法");
        enable.setOnClickListener(v -> {
            try { startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SUBTYPE_SETTINGS)); }
            catch (Exception e) { startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)); }
        });
        box.addView(title); box.addView(desc); box.addView(settings); box.addView(enable);
        setContentView(box);
    }
}
