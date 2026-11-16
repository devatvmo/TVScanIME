package com.example.tvscanime;

import android.inputmethodservice.InputMethodService;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.Gravity;
import android.view.inputmethod.InputConnection;
import android.widget.*;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

public class TvImeService extends InputMethodService implements LanBridge.Listener {
    private TextView preview, status;
    private ImageView qr;
    private LanBridge bridge;

    @Override public void onCreate() {
        super.onCreate();
        bridge = LanBridge.get(this);
        bridge.setListener(this);
        bridge.start();
    }

    @Override public View onCreateInputView() {
        bridge.start();
        LinearLayout shell = new LinearLayout(this);
        shell.setOrientation(LinearLayout.HORIZONTAL);
        shell.setGravity(Gravity.CENTER_VERTICAL);
        shell.setPadding(28, 20, 30, 20);
        GradientDrawable bg = new GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            new int[]{0xf20d1725, 0xf2182a40});
        bg.setCornerRadius(dp(22));
        bg.setStroke(dp(1), 0x664e6b8d);
        shell.setBackground(bg);
        shell.setElevation(dp(12));

        LinearLayout qrCard = new LinearLayout(this);
        qrCard.setGravity(Gravity.CENTER);
        qrCard.setPadding(dp(9),dp(9),dp(9),dp(9));
        GradientDrawable qrBg = new GradientDrawable();
        qrBg.setColor(Color.WHITE); qrBg.setCornerRadius(dp(13));
        qrCard.setBackground(qrBg);
        qr = new ImageView(this);
        try { qr.setImageBitmap(makeQr(bridge.getUrl(), 196)); } catch(Exception ignored) {}
        qrCard.addView(qr, new LinearLayout.LayoutParams(dp(196),dp(196)));
        shell.addView(qrCard, new LinearLayout.LayoutParams(dp(214),dp(214)));

        LinearLayout right = new LinearLayout(this);
        right.setOrientation(LinearLayout.VERTICAL);
        right.setGravity(Gravity.CENTER_VERTICAL);
        right.setPadding(dp(24),0,0,0);

        TextView eyebrow = new TextView(this);
        eyebrow.setText("TV SCAN  /  REMOTE INPUT");
        eyebrow.setTextColor(0xff7fa9e8);
        eyebrow.setTextSize(11);
        eyebrow.setLetterSpacing(.12f);

        TextView title = new TextView(this);
        title.setText("扫码，开始输入");
        title.setTextSize(25);
        title.setTextColor(Color.WHITE);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setPadding(0,dp(5),0,dp(5));

        status = new TextView(this);
        status.setText("等待手机连接");
        status.setTextColor(0xff9fb2c9);
        status.setTextSize(14);

        preview = new TextView(this);
        preview.setText("手机扫码后，在网页中输入文字");
        preview.setTextColor(0xffe6edf7);
        preview.setTextSize(17);
        preview.setMaxLines(2);
        preview.setPadding(0,dp(13),0,dp(9));

        TextView hint = new TextView(this);
        hint.setText("手机与电视需连接同一 Wi-Fi");
        hint.setTextColor(0xff7d91aa);
        hint.setTextSize(12);

        right.addView(eyebrow); right.addView(title); right.addView(status);
        right.addView(preview); right.addView(hint);
        shell.addView(right,new LinearLayout.LayoutParams(0,-2,1));
        return shell;
    }

    private int dp(int v){return (int)(v*getResources().getDisplayMetrics().density+.5f);}
    private Bitmap makeQr(String value,int size)throws Exception{
        BitMatrix m=new MultiFormatWriter().encode(value,BarcodeFormat.QR_CODE,size,size);
        Bitmap b=Bitmap.createBitmap(size,size,Bitmap.Config.RGB_565);
        for(int x=0;x<size;x++)for(int y=0;y<size;y++)b.setPixel(x,y,m.get(x,y)?Color.BLACK:Color.WHITE);
        return b;
    }

    @Override public void onText(String text){
        new android.os.Handler(getMainLooper()).post(()->{
            if(preview!=null)preview.setText(text.isEmpty()?"等待输入…":text);
            InputConnection ic=getCurrentInputConnection();
            if(ic!=null){ic.beginBatchEdit();ic.setComposingText(text,1);ic.endBatchEdit();}
            if(status!=null)status.setText("手机已连接 · 实时同步中");
        });
    }
    @Override public void onAction(String action){
        new android.os.Handler(getMainLooper()).post(()->{
            InputConnection ic=getCurrentInputConnection();if(ic==null)return;
            switch(action){
                case "backspace":
                    CharSequence before=ic.getTextBeforeCursor(1,0);
                    if(before!=null&&before.length()>0)ic.deleteSurroundingText(1,0);
                    break;
                case "clear":ic.setComposingText("",1);ic.commitText("",1);bridge.setCurrentText("");break;
                case "confirm":ic.finishComposingText();bridge.setCurrentText("");if(preview!=null)preview.setText("已发送到电视");break;
                case "dismiss":requestHideSelf(0);break;
                case "dpad_left":sendDownUpKeyEvents(android.view.KeyEvent.KEYCODE_DPAD_LEFT);break;
                case "dpad_right":sendDownUpKeyEvents(android.view.KeyEvent.KEYCODE_DPAD_RIGHT);break;
                case "dpad_up":sendDownUpKeyEvents(android.view.KeyEvent.KEYCODE_DPAD_UP);break;
                case "dpad_down":sendDownUpKeyEvents(android.view.KeyEvent.KEYCODE_DPAD_DOWN);break;
                case "back":requestHideSelf(0);break;
            }
        });
    }
}
