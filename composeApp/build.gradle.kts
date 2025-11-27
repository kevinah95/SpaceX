/*
 * Copyright 2025 kevinah95 (Kevin A. Hernández Rostrán)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import io.github.kevinah95.spacex.Utils.versionCodeFrom
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.kotlinMultiplatform)
  alias(libs.plugins.androidApplication)
  alias(libs.plugins.composeMultiplatform)
  alias(libs.plugins.composeCompiler)
}

kotlin {
  androidTarget { compilerOptions { jvmTarget.set(JvmTarget.JVM_21) } }

  listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
    iosTarget.binaries.framework {
      baseName = "ComposeApp"
      isStatic = false
      freeCompilerArgs += "-Xbinary=bundleId=io.github.kevinah95.composeapp"
      linkerOpts.add("-lsqlite3")
    }
  }

  sourceSets {
    androidMain.dependencies {
      implementation(compose.preview)
      implementation(libs.androidx.activity.compose)
      implementation(libs.kotlinx.coroutines.android)
    }
    commonMain.dependencies {
      implementation(compose.runtime)
      implementation(compose.foundation)
      implementation(compose.material3)
      implementation(compose.ui)
      implementation(compose.components.resources)
      implementation(compose.components.uiToolingPreview)
      implementation(libs.androidx.lifecycle.viewmodelCompose)
      implementation(libs.androidx.lifecycle.runtimeCompose)
      implementation(projects.shared)
      implementation(project.dependencies.platform(libs.koin.bom))
      implementation(libs.koin.compose)
      implementation(libs.koin.compose.viewmodel)
      implementation(libs.koin.compose.viewmodel.navigation)
    }
    commonTest.dependencies { implementation(libs.kotlin.test) }
  }
}

// To get more info: https://developer.android.com/build/build-variants
android {
  namespace = "io.github.kevinah95.spacex"
  compileSdk = libs.versions.android.compileSdk.get().toInt()

  defaultConfig {
    applicationId = "io.github.kevinah95.spacex"
    minSdk = libs.versions.android.minSdk.get().toInt()
    targetSdk = libs.versions.android.targetSdk.get().toInt()

    val appVersion = "1.7.0"
    versionName = appVersion
    versionCode = versionCodeFrom(appVersion)
  }
  signingConfigs {
    create("release") {
      // Production keystore
      storeFile =
          if (System.getenv("RELEASE_KEYSTORE_FILE") != null) {
            file(System.getenv("RELEASE_KEYSTORE_FILE"))
          } else {
            // Fallback temporal al debug keystore
            file("${System.getProperty("user.home")}/.android/debug.keystore")
          }
      storePassword = System.getenv("RELEASE_KEYSTORE_PASSWORD") ?: "android"
      keyAlias = System.getenv("RELEASE_KEY_ALIAS") ?: "androiddebugkey"
      keyPassword = System.getenv("RELEASE_KEY_PASSWORD") ?: "android"
    }
  }
  buildTypes {
    getByName("release") {
      isMinifyEnabled = true
      signingConfig = signingConfigs.getByName("release")
    }
    getByName("debug") {
      applicationIdSuffix = ".debug"
      isDebuggable = true
    }
  }
  // Define environments (flavors)
  flavorDimensions += "environment"
  productFlavors {
    create("alpha") {
      dimension = "environment"

      // applicationId will be "io.github.kevinah95.spacex.alpha"
      // comes from defaultConfig.applicationId + applicationIdSuffix
      applicationIdSuffix = ".alpha"

      resValue("string", "app_name", "SpaceX alpha")
      // Alpha uses release keystore for signing
      signingConfig = signingConfigs.getByName("release")
    }
    create("prod") {
      dimension = "environment"
      resValue("string", "app_name", "SpaceX")
      // Production has no suffixes
      // Prod uses release keystore for signing
      signingConfig = signingConfigs.getByName("release")
    }
  }
  packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
  }
}

dependencies { debugImplementation(compose.uiTooling) }
