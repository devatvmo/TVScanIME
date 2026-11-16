# Local network protocol

The TV starts two local-network listeners when the IME service starts:

- HTTP port 8766 serves the mobile input page only when the URL contains the current pairing token (`?t=...`).
- WebSocket port 8765 accepts JSON messages. A client must send the same token in each message.

## Client to TV

Text update:

```json
{"type":"text","value":"hello","token":"<pairing-token>"}
```

Action:

```json
{"type":"action","value":"confirm","token":"<pairing-token>"}
```

Supported action values: `backspace`, `clear`, `confirm`, `dismiss`, `dpad_left`, `dpad_right`, `dpad_up`, `dpad_down`, and `back`.

## TV to client

Text synchronization:

```json
{"type":"sync","value":"hello"}
```

## Security notes

This is a prototype for a trusted home LAN. HTTP and WebSocket traffic are unencrypted, and the token is a pairing secret rather than end-to-end encryption. Anyone who obtains the QR URL/token and can reach the TV's LAN ports may be able to interact with the session. Do not expose ports 8765/8766 to the public internet. Production use should add TLS or a secure transport, token rotation, connection limits, and stricter origin/session validation.
