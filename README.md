<div align="center">

# 🌺 Spider Lily

**A free and open-source manga reader for Android with built-in online content sources.**

![Android 6.0](https://img.shields.io/badge/android-6.0+-brightgreen) [![Sources count](https://img.shields.io/badge/dynamic/yaml?url=https%3A%2F%2Fraw.githubusercontent.com%2FKotatsu-Redo%2Fkotatsu-parsers-redo%2Frefs%2Fheads%2Fmaster%2F.github%2Fsummary.yaml&query=total&label=manga%20sources&color=%23E9321C)](https://github.com/Kotatsu-Redo/kotatsu-parsers-redo) [![License](https://img.shields.io/github/license/AppFuton/Futon)](https://github.com/AppFuton/Futon/blob/devel/LICENSE) [![GitHub Release](https://img.shields.io/github/v/release/appfuton/futon?sort=date&display_name=tag&style=flat&link=https%3A%2F%2Fgithub.com%2FAppFuton%2FFuton%2Freleases%2Flatest)](https://github.com/AppFuton/Futon/releases/latest) [![IzzyOnDroid Yearly Downloads](https://img.shields.io/badge/dynamic/json?url=https://dlstats.izzyondroid.org/iod-stats-collector/stats/basic/yearly/rolling.json&query=$.['com.arcadelabs.spiderlily']&label=IzzyOnDroid%20yearly%20downloads)](https://apt.izzysoft.de/packages/com.arcadelabs.spiderlily) [![F-Droid Version](https://img.shields.io/badge/F--Droid-%2311AB00.svg?logo=f-droid&logoColor=white)](https://f-droid.org/en/packages/com.arcadelabs.spiderlily/) [![Discord](https://img.shields.io/discord/1452862077134700628)](https://discord.gg/9sqBHXhwzz)

</div>

---

### ✨ Main Features

- 🌐 **Online Catalogues**: Browse 1200+ online manga, manhwa, and manhua sources out of the box.
- 🔌 **Extension Support**: Compatible with Tachiyomi / Keiyoushi extensions.
- 🔍 **Advanced Search & Filtering**: Search by title, author, genres, and rich status filters.
- 📁 **Custom Library Categories**: Organize favorites into user-defined categories.
- 📖 **Tailored Reading Modes**: Standard page-by-page view and Webtoon continuous scrolling with gesture controls.
- 📥 **Offline Downloads & CBZ Support**: Download chapters or open local `.cbz` / `.zip` archives.
- 📊 **Tracking Integrations**: Sync progress with Shikimori, AniList, MyAnimeList, and Kitsu.
- 🔔 **Chapter Notifications**: Automatic background checks for new chapter releases.
- 🔒 **Privacy Controls**: Incognito reading mode, password, and biometric app lock.
- ☁️ **Cloud & Local Sync**: Sync application data across your devices seamlessly.

---

### 📥 Downloads

Get Spider Lily from your preferred repository:

- **[IzzyOnDroid Repository](https://apt.izzysoft.de/packages/com.arcadelabs.spiderlily)**
- **[F-Droid Package](https://f-droid.org/packages/com.arcadelabs.spiderlily)**
- **[GitHub Latest Release](https://github.com/AppFuton/Futon/releases/latest)**

---

### 🛠️ Development Setup

#### Prerequisites
- **JDK 17** (Recommended: [Temurin 17](https://adoptium.net/temurin/releases/))
- **Android SDK** (Compile SDK 36, Target SDK 36, Minimum SDK 23)
- **Android Studio** (Recommended) or Gradle CLI

#### Build Commands

```bash
# Clone the repository
git clone https://github.com/AppFuton/Futon.git
cd Futon

# Debug build (for local testing)
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Nightly build
./gradlew assembleNightly
```

#### Running Verification & Tests

```bash
# Run unit tests
./gradlew test

# Run instrumented tests (device/emulator connected)
./gradlew connectedDebugAndroidTest

# Run lint & static checks
./gradlew check
```

---

### 🌐 Localization

Help translate Spider Lily into your language on [Weblate](https://hosted.weblate.org/engage/futon/).

---

### 🤝 Contributing

Pull requests and issue reports are welcome! Please check [CONTRIBUTING.md](./CONTRIBUTING.md) for contribution guidelines.

Join our community on [Discord](https://discord.gg/9sqBHXhwzz) for discussions, support, and announcements.

---

### 🔑 Certificate Fingerprints

```plaintext
EF:48:B2:2E:F2:C5:40:45:53:1F:6E:76:00:C2:7E:C3:D0:3B:71:22:1E:0B:05:FF:B6:8E:33:57:CF:8E:4D:40
```

---

### 📄 DMCA Disclaimer

The developers of this application do not host, store, or distribute any copyrighted media content. Spider Lily acts purely as a client-side interface for freely available public content on the internet. DMCA takedown requests should be directed to the respective content host website owners.

---

### ⚖️ License

Distributed under the **GNU General Public License v3.0 (GPLv3)**. See [LICENSE](./LICENSE) for details.

---

### 💖 Acknowledgments

Spider Lily is built upon the incredible open-source foundation of the **[Kotatsu](https://github.com/KotatsuApp/Kotatsu)** project. We extend our sincere gratitude to:
- The original **Kotatsu** developers and contributors
- The **Kotatsu-Redo** project for parser maintenance
- The **Keiyoushi** extension ecosystem maintainers
- All translators on Weblate helping localize the app
