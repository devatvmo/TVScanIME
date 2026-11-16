package com.example.tvscanime;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import fi.iki.elonen.NanoHTTPD;
import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.json.JSONObject;
import java.net.InetSocketAddress;
import java.security.SecureRandom;
import java.util.Locale;

public final class LanBridge {
    public interface Listener { void onText(String text); void onAction(String action); }
    private static LanBridge instance;
    public static synchronized LanBridge get(Context c) {
        if (instance == null) instance = new LanBridge(c.getApplicationContext());
        return instance;
    }
    private final Context context;
    private final String token = randomToken();
    private Httpd http;
    private Ws ws;
    private Listener listener;
    private String currentText = "";
    private LanBridge(Context c) { context = c; }

    public synchronized void setListener(Listener l) { listener = l; }
    public synchronized String getCurrentText() { return currentText; }
    public synchronized void setCurrentText(String s) {
        currentText = s == null ? "" : s;
        broadcast("{\"type\":\"sync\",\"value\":" + JSONObject.quote(currentText) + "}");
    }
    public synchronized void start() {
        try {
            if (http == null) { http = new Httpd(); http.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false); }
            if (ws == null) { ws = new Ws(new InetSocketAddress(8765)); ws.start(); }
        } catch (Exception e) { e.printStackTrace(); }
    }
    public synchronized String getUrl() {
        String ip = getIp();
        return "http://" + ip + ":8766/?t=" + token;
    }
    private String getIp() {
        try {
            WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            int ip = wm.getConnectionInfo().getIpAddress();
            return Formatter.formatIpAddress(ip);
        } catch (Exception e) { return "127.0.0.1"; }
    }
    private void broadcast(String s) {
        try { if (ws != null) for (WebSocket c : ws.getConnections()) c.send(s); } catch (Exception ignored) {}
    }
    private static String randomToken() {
        byte[] b = new byte[16]; new SecureRandom().nextBytes(b);
        StringBuilder s = new StringBuilder();
        for (byte x : b) s.append(String.format(Locale.US, "%02x", x));
        return s.toString();
    }
    private class Httpd extends NanoHTTPD {
        Httpd() { super(8766); }
        @Override public Response serve(IHTTPSession session) {
            String q = session.getParms().get("t");
            if (!token.equals(q)) return newFixedLengthResponse(Response.Status.FORBIDDEN, "text/plain", "Pairing token required");
            return newFixedLengthResponse(Response.Status.OK, "text/html; charset=utf-8", PhonePage.HTML);
        }
    }
    private class Ws extends WebSocketServer {
        Ws(InetSocketAddress a) { super(a); }
        @Override public void onOpen(WebSocket c, ClientHandshake h) {}
        @Override public void onClose(WebSocket c, int code, String reason, boolean remote) {}
        @Override public void onMessage(WebSocket c, String msg) {
            try {
                JSONObject j = new JSONObject(msg);
                if (!token.equals(j.optString("token"))) { c.close(); return; }
                String type = j.optString("type");
                if ("text".equals(type)) {
                    String value = j.optString("value", "");
                    synchronized (LanBridge.this) { currentText = value; if (listener != null) listener.onText(value); }
                } else if ("action".equals(type)) {
                    if (listener != null) listener.onAction(j.optString("value"));
                }
            } catch (Exception ignored) {}
        }
        @Override public void onError(WebSocket c, Exception e) {}
        @Override public void onStart() {}
    }
}
