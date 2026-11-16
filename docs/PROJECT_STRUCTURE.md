# Project structure

```text
TVScanIME/
├── .github/workflows/android-build.yml
├── app/
│   ├── build.gradle
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/example/tvscanime/
│       │   ├── MainActivity.java
│       │   ├── TvImeService.java
│       │   ├── LanBridge.java
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

The repository contains source/configuration only. Generated build output, APKs, signing keys, and ZIP archives are excluded. GitHub Actions builds a debug APK from source and stores it as a short-lived workflow artifact.
