#  Spider Lily: Recreating Futon in Compose
**Spider Lily** is a modern recreation of the Futon application. It retains Futon's exact Material Design layout, navigation, sheets, and reading interfaces, but migrates the entire rendering engine to **Jetpack Compose** with a custom crimson red branding.

---
## 📂 1. The Directory Structure

This structure separates your app into independent, reusable modules. Inside the feature modules, we use **Clean Architecture** directories.
## Directory
spider-lily/                          # Root Project
│
├── app/                              # The single Main App module
│   ├── build.gradle.kts              # Imports Room, Hilt, Compose, RxJava, & QuickJS
│   └── src/main/
│       ├── AndroidManifest.xml
│       └── kotlin/                   # Kotlin Source Root
│           │
│           ├── eu/kanade/            # ⚠️ COPIED EXACTLY AS-IS (Tachiyomi Emulation)
│           │   └── tachiyomi/
│           │       ├── AppInfo.kt
│           │       ├── network/      # JavaScriptEngine.kt, NetworkHelper.kt, etc.
│           │       └── source/       # Source.kt, CatalogueSource.kt, etc.
│           │
│           └── com/app/spiderlily/   # Your Main Application Namespace
│               │
│               ├── SpiderLilyApp.kt  # Application entry point (Starts Hilt)
│               ├── MainActivity.kt   # Hosts NavHost (Compose Navigation Routing)
│               │
│               ├── mihon/            # ⚠️ COPIED & RENAMED PACKAGES (Mihon Loader)
│               │   ├── ChildFirstPathClassLoader.kt # Custom ClassLoader
│               │   ├── MihonExtensionLoader.kt      # Loads extension APKs
│               │   └── MihonExtensionManager.kt     # Installs & manages extensions
│               │
│               ├── core/             # SHARED SYSTEM CORE
│               │   ├── db/           # SQLite Database (MangaDatabase, entities, DAOs)
│               │   ├── network/      # App-wide API helpers & Retrofit provider
│               │   ├── prefs/        # SharedPreferences / Settings Storage
│               │   ├── nav/          # Navigation Routing Actions
│               │   └── designsystem/ # Universal UI widgets
│               │
│               └── features/         # VERTICAL FEATURE SLICES (Futon Screens)
│                   │
│                   ├── library/      # 1. Library Feature (Bookshelf Grid)
│                   │   ├── data/     # Loads local favorites using room DAO flows
│                   │   ├── domain/   # GetFavoritesUseCase.kt
│                   │   └── ui/       # LibraryScreen.kt & LibraryViewModel.kt
│                   │
│                   ├── details/      # 2. Details Feature (Manga info & Chapters)
│                   │   ├── data/     # Connects to Mihon Extensions for details
│                   │   ├── domain/   # FetchChapterListUseCase.kt
│                   │   └── ui/       # DetailsScreen.kt & DetailsViewModel.kt
│                   │
│                   └── reader/       # 3. Reader Feature (Standard & Webtoon scrolls)
│                       ├── data/     # Loads page image paths using extension engine
│                       ├── domain/   # GetChapterPagesUseCase.kt
│                       └── ui/       # ReaderScreen.kt & ReaderViewModel.kt
│
└── gradle/
    └── libs.versions.toml            # Holds libraries (Hilt, Compose, Room, RxJava, QuickJS)

---

## 📝 2. Easy-to-Understand Architectural Notes

If you are coming from **React**, keep these notes on your desk to quickly understand this structure.
### 📌 Note A: The "Props and State" rule in Compose
- **Composables (`Screen.kt`)** are like **React components**. They should be stateless. They receive their data from a state object and pass user click actions back up.
- **ViewModels (`ViewModel.kt`)** are like **React Hooks (`useState`)**. They hold the state. If the phone is rotated, the Composable is destroyed and rebuilt, but the ViewModel remains alive in memory, keeping your data safe.
### 📌 Note B: How Data flows (The DI Chain)
You don't need to import or instantiate databases and APIs inside your pages. **Hilt (DI)** automatically builds them and passes them into constructors down this line:
1. **Hilt** builds the database (`DatabaseModule.kt`).
2. Hilt injects the Database DAO into your **Repository** (`LibraryRepositoryImpl.kt`).
3. Hilt injects the Repository into your **Use Case** (`GetLibraryMangaUseCase.kt`).
4. Hilt injects the Use Case into your **ViewModel** (`LibraryViewModel.kt`).
5. Your **Compose Screen** simply calls `val viewModel = hiltViewModel()` to access everything automatically.
### 📌 Note C: Room Flows vs. API Suspend calls
- When fetching from the **Internet (MangaDex API)**, we use **`suspend fun`**. This is a one-time request (like a standard Axios `get` request).
- When fetching from the **Database (Room)**, we use **`Flow`**. This is an open subscription (like Firebase's `onSnapshot` database listener). If you favorite a manga, the database updates, the Flow emits the new list, and the screen automatically redraws.
### 📂 1. The Files to Copy
To get the extension system working, copy these directories and files from Futon directly into Spider Lily:
#### A. The Mihon Extension Runner (The Engine)
Copy the entire **`mihon/`** folder from `app/src/main/kotlin/io/github/landwarderer/futon/mihon` into your project's equivalent path (e.g. `com/app/spiderlily/mihon`):
- ChildFirstPathClassLoader.kt – The dynamic APK loader.
- MihonExtensionLoader.kt – Parses the downloaded extension APK files.
- MihonExtensionManager.kt – Manages install/uninstall states of extensions.
#### B. The Emulation Layer (CRITICAL ⚠️)
Copy the entire **`eu/`** folder from `app/src/main/kotlin/eu` into your project's source root (`src/main/kotlin/`):
- **Do NOT change the package names of these files!** Keep them as `eu.kanade.tachiyomi`.
- **Why:** Third-party extensions are compiled to look for the exact package name `eu.kanade.tachiyomi`. If you rename this folder to `com.app.spiderlily`, loaded extensions will crash instantly with `ClassNotFoundException`.
---
### 🛠️ 2. The 3 Things You Must Configure to Support the Copied Code
If you just copy the files, the project will not compile. You must add these three configurations:
#### A. Add RxJava and QuickJS to your Dependencies
Tachiyomi extensions were written years ago, so they use **RxJava** instead of Coroutines, and they use **QuickJS** to execute JavaScript (for passing Cloudflare bypasses).
Add these lines to your `app/build.gradle`:
```
groovy
dependencies {
    // Required to run the JavaScript engines in extensions
    implementation libs.quickjs.kt
    // Required because Tachiyomi extensions return RxJava Observables
    implementation libs.rxjava
    implementation libs.rxandroid
    }
```

#### B. Update imports in your files
When you copy the `mihon/` package files, open them and:
1. Change the package line at the top to: `package com.app.spiderlily.mihon`
2. Update any imports pointing to `io.github.landwarderer.futon.R` (Futon resources) to your own resource package `com.app.spiderlily.R`.
#### C. Setup the Hilt Provider
The dynamic extension manager needs context. In your DI module, tell Hilt how to provide the `MihonExtensionManager` as a Singleton:
kotlin
```
@Provides
@Singleton
fun provideMihonExtensionManager(
    @ApplicationContext context: Context
): MihonExtensionManager {
    return MihonExtensionManager(context)
}
```
## Critical 
### 1. The RxJava-to-Coroutines Bridge (Dependency)
Tachiyomi extensions return **RxJava Observables**, but your modern Compose UI and ViewModels use **Kotlin Coroutines and Flow**.
To connect them without writing messy adapter code, you must add the **RxJava Coroutines Bridge** library to your dependencies:
kotlin
```
// Add to build.gradle
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-rx2:1.7.3")
This lets you convert any RxJava call from an extension directly into a Flow or Coroutine suspend function using `.asFlow()` or `.await()`:
kotlin
```
// Example inside your repository
```
val chaptersFlow = extension.getChapterList(manga).asFlow()
```

---
### 2. ProGuard / R8 Rules (To prevent Release Build Crashes)
When you build the app for testing (Debug mode), everything will work. But the moment you compile a **Release APK**, the compiler (R8) will shrink the code and delete "unused" classes.
Because Hilt and the Mihon ClassLoader load classes dynamically via **reflection**, R8 will think these classes are unused and delete them, causing your release app to crash instantly.
You must add these lines to your app/proguard-rules.pro:
proguard
# Prevent R8 from deleting the Tachiyomi emulation classes
```
-keep class eu.kanade.tachiyomi.** { *; }
```
# Prevent R8 from deleting the Mihon loader classes

```
-keep class com.app.spiderlily.mihon.** { *; }
```
# Keep all classes that implement Tachiyomi Extension interfaces
```
-keep public class * implements eu.kanade.tachiyomi.source.Source { *; }
```

---
### 3. Cleartext Traffic Permission (In the Android Manifest)
MangaDex uses HTTPS, but many community manga websites (which extensions scrape) still run on **HTTP**.
By default, modern Android versions block unencrypted HTTP traffic. You must allow it in your `AndroidManifest.xml` or many extensions will fail to load images:
xml
```
<application
    android:name=".SpiderLilyApp"
    android:usesCleartextTraffic="true"  <!-- Add this line -->
    ...>
</application>
```
---
### 4. Downloading the Extensions (The Keiyoushi Repository)
Extensions are not built into the app; they must be downloaded. You need a way to fetch the list of extensions.
The community hosts all extension APKs on a repository index. You will need to make a network call to the official community index URL:
- `https://raw.githubusercontent.com/keiyoushi/extensions/repo/index.json`
Your app reads this JSON, displays the list of extensions (MangaDex, MangaLife, etc.) in a settings screen, downloads the selected `.apk` file to the phone's internal storage, and tells the `MihonExtensionLoader` to load it.
---
### 5. QuickJS Library (For Cloudflare Bypass)
Many manga sites protect themselves with Cloudflare DDoS protection. To bypass this, Tachiyomi extensions execute a small piece of JavaScript in the background.
This requires the **QuickJS** engine. Make sure you copy this line to your Gradle dependencies, or your app will crash the moment an extension hits a Cloudflare check:
kotlin
```
implementation("blocker.com.github.clquwu:quickjs-kt:0.1.0") // Or equivalent library
```



---
## 📝 3. Phase Adjustments & Architecture Notes

### 📌 Note D: App Namespace & Package Name
* The project base namespace is configured as `com.arcadelabs.spiderlily` (not `com.app.spiderlily`). Ensure all main files are written inside this package path to match the Gradle namespace configuration.

### 📌 Note E: Final-Phase MangaDex Fallback
* Build a clean native MangaDex client in the **final phase** as the Play Store-safe source path.
* This client must be implemented from scratch in Spider Lily code, not copied from Futon or any extension APK.
* Use the official MangaDex HTTPS API for search, manga details, chapters, cover art, and page-server resolution.
* Keep this source behind a repository interface, e.g. `MangaSourceRepository`, so the app can switch between:
  * `MangaDexRepository` for Play Store builds.
  * `MihonExtensionRepository` for non-Play builds that allow extension APK loading.
* Do not require `usesCleartextTraffic` for the MangaDex-only Play Store flavor because MangaDex uses HTTPS.
* The Play Store flavor should disable extension APK downloads, dynamic extension loading, and community scraper repositories to avoid store policy risk.
* The UI should treat MangaDex like any other source: Explore, search results, details, chapters, reader pages, favorites, history, and downloads should all use the same Compose screens and state models.

### 📌 Note F: RxJava 1.x Coroutine Bridge
* Tachiyomi extensions require RxJava 1.x (`rx.Observable`). To keep execution clean, we will implement a custom `RxBridge.kt` file instead of pulling in complex external coroutine-bridge libraries:
  ```kotlin
  suspend fun <T> rx.Observable<T>.awaitFirst(): T = suspendCancellableCoroutine { cont ->
      val subscription = this.first().subscribe(
          { cont.resume(it) },
          { cont.resumeWithException(it) }
      )
      cont.invokeOnCancellation { subscription.unsubscribe() }
  }
  ```

### 📌 Note G: Scoped Storage & Room Migrations
* **Downloads**: Save offline downloaded content inside app-scoped directories (`context.getExternalFilesDir(null)`) to prevent permission rejections in target Android APIs.
* **Database Updates**: Set Room database initialization with `fallbackToDestructiveMigration()` during the active development phase to easily handle table updates without crashes.
