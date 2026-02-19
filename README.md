This is a Kotlin Multiplatform project targeting Android and iOS.

* [/androidApp](./androidApp) is the Android application entry point. This module uses Android Gradle Plugin 9 
  and depends on both `composeApp` and `shared` modules. Add Android-specific configurations, resources, and 
  the main Activity here.

* [/composeApp](./composeApp/src) is a Kotlin Multiplatform library that contains the shared UI code built with 
  Compose Multiplatform. This code runs on both Android and iOS. It contains several subfolders:
  - [commonMain](./composeApp/src/commonMain/kotlin) is for Compose UI code that's common for all targets.
  - [iosMain](./composeApp/src/iosMain/kotlin) is for iOS-specific UI code. For example, if you want to use 
    Apple's CoreCrypto or other iOS-specific APIs in your UI layer, this is the right place.
  - [androidMain](./composeApp/src/androidMain/kotlin) is for Android-specific UI code.

* [/shared](./shared/src) is for business logic and data that will be shared between all targets. This includes 
  networking, database, and domain logic. The most important subfolder is [commonMain](./shared/src/commonMain/kotlin). 
  If preferred, you can add platform-specific code to [androidMain](./shared/src/androidMain/kotlin) and 
  [iosMain](./shared/src/iosMain/kotlin) folders.

* [/iosApp](./iosApp/iosApp) contains the iOS application entry point. Even though you're sharing your UI with 
  Compose Multiplatform, you need this SwiftUI wrapper as the entry point for your iOS app.

### Build and Run Android Application

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

### Build and Run iOS Application

To build and run the development version of the iOS app, use the run configuration from the run widget
in your IDE's toolbar or open the [/iosApp](./iosApp) directory in Xcode and run it from there.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…
