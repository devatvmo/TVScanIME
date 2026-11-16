package com.example.tvscanime;

final class PhonePage {
static final String HTML = """
<!doctype html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1,viewport-fit=cover">
<meta name="theme-color" content="#0b1220">
<title>TV Scan · 电视输入</title>
<style>
:root{color-scheme:dark;--bg:#0b1220;--panel:#121d2d;--line:#26364b;--muted:#91a2b8;--white:#f5f8fc;--blue:#4f8cff;--cyan:#6ee7e0}
*{box-sizing:border-box}
body{margin:0;min-height:100vh;background:radial-gradient(ellipse at 80% -10%,#1b3760 0,transparent 42%),var(--bg);color:var(--white);font:16px/1.5 system-ui,-apple-system,"Segoe UI",sans-serif;padding:calc(22px + env(safe-area-inset-top)) 18px calc(24px + env(safe-area-inset-bottom))}
main{width:min(100%,560px);margin:0 auto}
.brand{display:flex;align-items:center;gap:11px;margin:4px 0 26px}
.mark{width:42px;height:42px;border-radius:14px;background:linear-gradient(145deg,#65a3ff,#5369e8);display:grid;place-items:center;box-shadow:0 8px 24px #3d75dc55}
.mark svg{width:23px;height:23px}
.brand-name{font-weight:750;letter-spacing:.2px;font-size:17px}
.brand-sub{font-size:12px;color:var(--muted);margin-top:1px}
h1{font-size:25px;line-height:1.25;letter-spacing:-.5px;margin:0 0 7px}
.lead{color:var(--muted);font-size:14px;margin:0 0 22px}
.editor{background:linear-gradient(160deg,#17263a,#111b2a);border:1px solid #30445d;border-radius:22px;padding:13px;box-shadow:0 18px 48px #0005}
.editor-head{display:flex;align-items:center;justify-content:space-between;padding:2px 3px 11px}
.field-label{font-size:13px;font-weight:650;color:#dce7f5}
.live{font-size:11px;color:#8ea4bd;display:flex;align-items:center;gap:6px}
.dot{width:7px;height:7px;border-radius:50%;background:#66788c}
.dot.on{background:#4adea5;box-shadow:0 0 0 4px #4adea51a}
textarea{display:block;width:100%;height:34vh;min-height:210px;max-height:390px;resize:vertical;border:0;border-radius:14px;background:#0b1421;color:#f7faff;padding:16px;font:19px/1.65 system-ui,sans-serif;outline:none;box-shadow:inset 0 0 0 1px #24364d}
textarea:focus{box-shadow:inset 0 0 0 1px #5b91f5,0 0 0 3px #5b91f522}
textarea::placeholder{color:#60738a}
.meta{display:flex;justify-content:space-between;color:#71849c;font-size:11px;padding:9px 3px 1px}
.actions{display:grid;grid-template-columns:1fr 1fr 1.35fr;gap:9px;margin-top:12px}
button{border:1px solid #34465c;background:#1a2a3d;color:#eaf1fa;border-radius:13px;min-height:49px;padding:10px 8px;font:600 14px system-ui;cursor:pointer;transition:transform .12s,filter .12s}
button:active{transform:scale(.97);filter:brightness(1.15)}
button.primary{border:0;background:linear-gradient(135deg,#5798ff,#466de7);color:white;box-shadow:0 8px 20px #3c70e744}
.tools{display:grid;grid-template-columns:repeat(3,1fr);gap:9px;margin-top:9px}
.tools button{background:transparent;border-color:#26384e;color:#aebed1;min-height:43px;font-weight:550}
.foot{display:flex;align-items:center;justify-content:center;gap:7px;color:#6e829b;font-size:11px;margin:20px 0 0}
@media(min-height:850px){textarea{height:390px}}
</style>
</head>
<body><main>
<header class="brand">
 <div class="mark"><svg viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="5" width="18" height="14" rx="2.5"/><path d="M7 9h.01M11 9h.01M15 9h.01M7 13h.01M11 13h.01M15 13h.01M9 16h6"/></svg></div>
 <div><div class="brand-name">TV Scan</div><div class="brand-sub">REMOTE INPUT · LOCAL NETWORK</div></div>
</header>
<h1>让输入，简单一点。</h1>
<p class="lead">在这里输入文字，内容会实时同步到电视。</p>
<section class="editor">
 <div class="editor-head"><span class="field-label">输入内容</span><span class="live"><i class="dot" id="dot"></i><span id="status">正在连接电视…</span></span></div>
 <textarea id="t" placeholder="输入中文或 English…" autocomplete="off" autocapitalize="off" spellcheck="false"></textarea>
 <div class="meta"><span>支持手机系统键盘与语音输入</span><span id="count">0 字</span></div>
</section>
<div class="actions">
 <button onclick="act('backspace')">⌫　退格</button>
 <button onclick="act('clear')">清空</button>
 <button class="primary" onclick="act('confirm')">发送到电视　↗</button>
</div>
<div class="tools">
 <button onclick="act('dpad_left')">← 左移</button>
 <button onclick="act('dismiss')">收起键盘</button>
 <button onclick="act('dpad_right')">右移 →</button>
</div>
<div class="foot"><span>⌁</span> 仅在本地网络传输 · 无需安装手机 App</div>
</main>
<script>
const token=new URLSearchParams(location.search).get('t'),ta=document.getElementById('t'),statusEl=document.getElementById('status'),dot=document.getElementById('dot'),count=document.getElementById('count');
let ws,timer,connected=false;
function connect(){ws=new WebSocket('ws://'+location.hostname+':8765);
 ws.onopen=()=>{connected=true;statusEl.textContent='已连接';dot.classList.add('on');send('text',ta.value)};
 ws.onclose=()=>{connected=false;statusEl.textContent='连接断开，重连中';dot.classList.remove('on');setTimeout(connect,1500)};
 ws.onerror=()=>{statusEl.textContent='连接异常'};
 ws.onmessage=e=>{try{const m=JSON.parse(e.data);if(m.type==='sync'&&m.value!==ta.value){ta.value=m.value;updateCount()}}catch(_){}};
}
function send(type,value){if(ws&&ws.readyState===1)ws.send(JSON.stringify({type,value,token}))}
function updateCount(){count.textContent=ta.value.length+' 字'}
function act(v){
 if(v==='backspace'){const a=ta.selectionStart,b=ta.selectionEnd;if(a!==b)ta.setRangeText('',a,b,'end');else if(a>0)ta.setRangeText('',a-1,a,'end');send('text',ta.value);updateCount();ta.focus();return}
 if(v==='clear'){ta.value='';send('action','clear');send('text','');updateCount();ta.focus();return}
 send('action',v);
 if(v==='confirm'){ta.value='';send('text','');updateCount();ta.blur()}
}
ta.addEventListener('input',()=>{updateCount();clearTimeout(timer);timer=setTimeout(()=>send('text',ta.value),35)});
connect();
</script>
</body></html>
""";
}
