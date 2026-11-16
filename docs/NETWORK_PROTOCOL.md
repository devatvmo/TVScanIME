# Local network protocol

The TV starts two local-network listeners when the IME service starts:

- HTTP port 8766 serves the mobile input page at `http://<TV-LAN-IP>:8766/`.
- WebSocket port 8765 accepts JSON messages from devices on the same reachable network.
- No pairing code, URL token, or WebSocket token is used. The QR code encodes the direct HTTP address displayed on the TV.

## Client to TV

Text update:

```json
{"type":"text","value":"hello"}
```

Action:

```json
{"type":"action","value":"confirm"}
```

Supported action values: `backspace`, `clear`, `confirm`, `dismiss`, `dpad_left`, `dpad_right`, `dpad_up`, `dpad_down`, and `back`.

## TV to client

Text synchronization:

```json
{"type":"sync","value":"hello"}
```

## Security notes

This prototype intentionally has no pairing code or authentication. Any device that can reach TCP ports 8765/8766 can open the input page and send input to the currently focused field. HTTP and WebSocket traffic are unencrypted. Use only on a trusted private LAN, and never expose these ports to the public internet. A production version should add authenticated sessions and TLS or another secure transport.
