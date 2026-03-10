<h1 align="center">SpaceX Launches 🇨🇷</h1>
<p align="center">
  <a href="https://es.wikipedia.org/wiki/Costa_Rica"><img alt="Made in Costa Rica" src="https://img.shields.io/badge/Made%20in-%20Costa%20Rica-blue.svg?logo=data:image/svg%2bxml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIGlkPSJmbGFnLWljb25zLWNyIiB2aWV3Qm94PSIwIDAgNjQwIDQ4MCI+CiAgPGcgZmlsbC1ydWxlPSJldmVub2RkIiBzdHJva2Utd2lkdGg9IjFwdCI+CiAgICA8cGF0aCBmaWxsPSIjMDAwMGI0IiBkPSJNMCAwaDY0MHY0ODBIMHoiLz4KICAgIDxwYXRoIGZpbGw9IiNmZmYiIGQ9Ik0wIDc1LjRoNjQwdjMyMi4zSDB6Ii8+CiAgICA8cGF0aCBmaWxsPSIjZDkwMDAwIiBkPSJNMCAxNTcuN2g2NDB2MTU3LjdIMHoiLz4KICA8L2c+Cjwvc3ZnPgo="/></a>
  <a href="https://github.com/kevinah95/spacex/releases"><img alt="GitHub release (with filter)" src="https://img.shields.io/github/v/release/kevinah95/SpaceX?style=flat&label=Release&color=7F52FF"/></a>
  <a href="https://kotlinlang.org/"><img alt="Kotlin Version" src="https://img.shields.io/badge/Kotlin-2.3.10-%237F52FF.svg?logo=kotlin"/></a><!-- gradle/libs.versions.toml -->
  <br>
  <a href="https://github.com/JetBrains/compose-multiplatform/releases/tag/v1.10.2"><img alt="Compose Multiplatform" src="https://img.shields.io/badge/Compose%20Multiplatform-v1.10.2-%237F52FF"/></a><!-- gradle/libs.versions.toml -->
</p>

<p align="center">  
KMP app showcasing SpaceX launches, targeting Android, iOS, and Desktop (JVM).
</p>

## 📌 Description

KMP app showcasing SpaceX launches, built with Kotlin Multiplatform, Compose Multiplatform, Koin, and more.
The app features a launch list, details screen, and user profile with authentication. It demonstrates clean
architecture principles and modern Android/iOS/Desktop development practices.

## 📦 Project Structure

This is a Kotlin Multiplatform project targeting Android, iOS, and Desktop (JVM).

* [/androidApp](./androidApp) is the Android application entry point. This module uses Android Gradle Plugin 9
  and depends on both `composeApp` and `shared` modules. Add Android-specific configurations, resources, and
  the main Activity here.

* [/composeApp](./composeApp/src) is a Kotlin Multiplatform library that contains the shared UI code built with
  Compose Multiplatform. This code runs on Android, iOS, and Desktop. It contains several subfolders:
  - [commonMain](./composeApp/src/commonMain/kotlin) is for Compose UI code that's common for all targets.
  - [iosMain](./composeApp/src/iosMain/kotlin) is for iOS-specific UI code. For example, if you want to use
    Apple's CoreCrypto or other iOS-specific APIs in your UI layer, this is the right place.
  - [androidMain](./composeApp/src/androidMain/kotlin) is for Android-specific UI code.

* [/shared](./shared/src) is for business logic and data that will be shared between all targets. This includes
  networking, database, and domain logic. The most important subfolder is [commonMain](./shared/src/commonMain/kotlin).
  If preferred, you can add platform-specific code to [androidMain](./shared/src/androidMain/kotlin),
  [iosMain](./shared/src/iosMain/kotlin), and [jvmMain](./shared/src/jvmMain/kotlin) folders.

* [/desktopApp](./desktopApp) contains the Desktop (JVM) application entry point for Compose Desktop.

* [/iosApp](./iosApp/iosApp) contains the iOS application entry point. Even though you're sharing your UI with
  Compose Multiplatform, you need this SwiftUI wrapper as the entry point for your iOS app.

## 🛠️ Features

- Browse a list of SpaceX rocket launches.
- View detailed information about each launch.
- Anonymous sign-in via Firebase Authentication.
- User profile screen with session management.
- Shared UI across Android, iOS, and Desktop using Compose Multiplatform.

## 🔒 Authentication

- Users sign in anonymously — no registration required.
- Session state is managed via Firebase Authentication.
- Users can sign out from the profile screen.

## ⚙️ Project Setup

### Android

To build and run the development version of the Android app, use the run configuration from the run widget
in your IDE's toolbar or build it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew :androidApp:assembleDebug
  ```
- on Windows
  ```shell
  .\gradlew.bat :androidApp:assembleDebug
  ```

To install and run the app on a connected device or emulator:
- on macOS/Linux
  ```shell
  ./gradlew :androidApp:installDebug
  ```
- on Windows
  ```shell
  .\gradlew.bat :androidApp:installDebug
  ```

### iOS

To build and run the development version of the iOS app, use the run configuration from the run widget
in your IDE's toolbar or open the [/iosApp](./iosApp) directory in Xcode and run it from there.

### Desktop (JVM)

To run the Desktop app:
- on macOS/Linux
  ```shell
  ./gradlew :desktopApp:run
  ```
- on Windows
  ```shell
  .\gradlew.bat :desktopApp:run
  ```

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…

## 📄 License

```
Copyright 2026 kevinah95 (Kevin A. Hernández Rostrán)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
