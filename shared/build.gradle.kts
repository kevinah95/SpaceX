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
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.kotlinMultiplatform)
  alias(libs.plugins.androidLibrary)
  alias(libs.plugins.kotlinxSerialization)
  alias(libs.plugins.sqlDelight)
  alias(libs.plugins.spotlessConventions)
}

kotlin {
  androidTarget { compilerOptions { jvmTarget.set(JvmTarget.JVM_21) } }

  iosX64()
  iosArm64()
  iosSimulatorArm64()

  sourceSets {
    androidMain.dependencies {
      implementation(libs.ktor.client.okhttp)
      implementation(libs.sqldelight.driver.android)
    }
    commonMain.dependencies {
      implementation(project.dependencies.platform(libs.koin.bom))
      implementation(project.dependencies.platform(libs.ktor.bom))
      implementation(libs.koin.core)
      implementation(libs.koin.compose.viewmodel)
      implementation(libs.ktor.client.core)
      implementation(libs.ktor.client.content.negotiation)
      implementation(libs.ktor.serialization.kotlinx.json)
      implementation(libs.kotlinx.coroutines.core)
      implementation(libs.kotlinx.datetime)
    }
    commonTest.dependencies { implementation(libs.kotlin.test) }
    iosMain.dependencies {
      implementation(libs.ktor.client.darwin)
      implementation(libs.sqldelight.driver.native)
    }
  }
}

android {
  namespace = "io.github.kevinah95.spacex.shared"
  compileSdk = libs.versions.android.compileSdk.get().toInt()
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
  }
  defaultConfig { minSdk = libs.versions.android.minSdk.get().toInt() }
}

sqldelight {
  databases { create("AppDatabase") { packageName.set("io.github.kevinah95.spacex.data.local") } }
  linkSqlite = true
}
