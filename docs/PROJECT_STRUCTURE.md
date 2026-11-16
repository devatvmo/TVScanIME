# Project structure

```text
TVScanIME/
├── .github/workflows/android-build.yml
├── app/
│   ├── build.gradle
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/example/tvscanime/
│       │   ├── MainActivity.kt
│       │   ├── TvImeService.kt
│       │   ├── LanBridge.kt
│       │   └── PhonePage.java
│       └── res/xml/method.xml
├── docs/
│   ├── PROJECT_STRUCTURE.md
│   ├── NETWORK_PROTOCOL.md
│   └── TEST_PLAN.md
├── .gitignore
├── build.gradle
├── gradle.properties
├── settings.gradle
└── README.md
```

The TV-side Android application, input method service, and LAN bridge use Kotlin and Android framework APIs. The phone-side input page is HTML/CSS/JavaScript embedded in `PhonePage.java`; it runs in the phone browser and uses the phone's own keyboard.

The repository contains source/configuration only. Generated build output, APKs, signing keys, and ZIP archives are excluded. GitHub Actions builds a debug APK from source and stores it as a short-lived workflow artifact.
