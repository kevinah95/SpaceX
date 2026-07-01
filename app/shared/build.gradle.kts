/*
 * Copyright 2025-2026 kevinah95 (Kevin A. Hernández Rostrán)
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
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.kotlinMultiplatform)
  alias(libs.plugins.androidMultiplatformLibrary)
  alias(libs.plugins.kotlinxSerialization)
  alias(libs.plugins.sqlDelight)
  alias(libs.plugins.mokkery)
}

kotlin {
  jvmToolchain(21)

  android {
    namespace = "io.github.kevinah95.spacex.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    compilerOptions { jvmTarget.set(JvmTarget.JVM_21) }

    androidResources { enable = true }
  }

  // https://kotlinlang.org/docs/native-target-support.html
  iosArm64()
  iosSimulatorArm64()

  jvm { compilerOptions { jvmTarget.set(JvmTarget.JVM_21) } }

  sourceSets {
    androidMain.dependencies {
      implementation(libs.ktor.client.okhttp)
      implementation(libs.sqldelight.driver.android)
      implementation(project.dependencies.platform(libs.firebase.bom))
      implementation(libs.gitlive.firebase.analytics)
      implementation(libs.gitlive.firebase.crashlytics)
      implementation(libs.gitlive.firebase.messaging)
    }

    commonMain.dependencies {
      implementation(libs.kotlinx.coroutines.core)
      implementation(libs.kotlinx.datetime)
      // Koin
      implementation(project.dependencies.platform(libs.koin.bom))
      implementation(libs.koin.core)
      implementation(libs.koin.compose.viewmodel)
      // Ktor
      implementation(project.dependencies.platform(libs.ktor.bom))
      implementation(libs.ktor.client.core)
      implementation(libs.ktor.client.content.negotiation)
      implementation(libs.ktor.serialization.kotlinx.json)
      // Firebase GitLive
      implementation(libs.gitlive.firebase.common)
      implementation(libs.gitlive.firebase.auth)
      implementation(libs.gitlive.firebase.firestore)
      implementation(libs.gitlive.firebase.database)
    }
    commonTest.dependencies {
      implementation(libs.kotlin.test)
      implementation(libs.kotlinx.coroutines.test)
      // Koin
      implementation(libs.koin.test)
      implementation(libs.ktor.client.mock)
    }
    iosMain.dependencies {
      implementation(libs.ktor.client.darwin)
      implementation(libs.sqldelight.driver.native)
      implementation(libs.gitlive.firebase.analytics)
      implementation(libs.gitlive.firebase.crashlytics)
      implementation(libs.gitlive.firebase.messaging)
    }
    jvmMain.dependencies {
      implementation(libs.ktor.client.okhttp)
      implementation(libs.sqldelight.driver.sqlite)
    }
  }
}

sqldelight {
  databases { create("AppDatabase") { packageName.set("io.github.kevinah95.spacex.data.local") } }
  linkSqlite = true
}
