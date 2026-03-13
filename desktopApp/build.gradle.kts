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
import org.gradle.api.tasks.JavaExec
import org.gradle.jvm.tasks.Jar
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
  alias(libs.plugins.kotlinJvm)
  alias(libs.plugins.composeMultiplatform)
  alias(libs.plugins.composeCompiler)
  alias(libs.plugins.spotlessConventions)
}

val appVersion = providers.gradleProperty("app.version").get()
val appMarketingVersion = providers.gradleProperty("app.marketingVersion").get()
val desktopProguardConfig = layout.projectDirectory.file("proguard-rules.pro")
val appDisplayName = "SpaceX Launches"
val appBundleId = "io.github.kevinah95.spacex"
val appDescription = "Kotlin Multiplatform desktop app for browsing SpaceX launches."
val appVendor = "Kevin A. Hernandez Rostran"
val macSigningEnabled =
    providers
        .gradleProperty("compose.desktop.mac.sign")
        .orElse(providers.environmentVariable("COMPOSE_DESKTOP_MAC_SIGN"))
        .map(String::toBoolean)
        .orElse(false)
        .get()
val macSigningIdentity =
    providers
        .gradleProperty("compose.desktop.mac.signing.identity")
        .orElse(providers.environmentVariable("COMPOSE_DESKTOP_MAC_SIGNING_IDENTITY"))
        .orNull
val macSigningKeychain =
    providers
        .gradleProperty("compose.desktop.mac.signing.keychain")
        .orElse(providers.environmentVariable("COMPOSE_DESKTOP_MAC_SIGNING_KEYCHAIN"))
        .orNull
val notarizationAppleId =
    providers
        .gradleProperty("compose.desktop.mac.notarization.appleID")
        .orElse(providers.environmentVariable("NOTARIZATION_APPLE_ID"))
        .orNull
val notarizationPassword =
    providers
        .gradleProperty("compose.desktop.mac.notarization.password")
        .orElse(providers.environmentVariable("NOTARIZATION_PASSWORD"))
        .orNull
val notarizationTeamId =
    providers
        .gradleProperty("compose.desktop.mac.notarization.teamID")
        .orElse(providers.environmentVariable("NOTARIZATION_TEAM_ID"))
        .orNull

tasks.withType<Jar>().configureEach { manifest.attributes["Implementation-Version"] = appVersion }

tasks.withType<JavaExec>().configureEach { systemProperty("spacex.app.version", appVersion) }

kotlin {
  dependencies {
    implementation(projects.composeApp)
    implementation(projects.shared)

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutines.swing)
  }
}

compose.desktop {
  application {
    mainClass = "io.github.kevinah95.spacex.MainKt"
    jvmArgs += listOf("-Dspacex.app.version=$appVersion")

    buildTypes.release.proguard {
      optimize.set(false)
      configurationFiles.from(desktopProguardConfig)
    }

    nativeDistributions {
      modules("java.sql")
      targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
      packageName = appDisplayName
      packageVersion = appMarketingVersion
      description = appDescription
      vendor = appVendor
      copyright = "Copyright 2026 $appVendor"
      licenseFile.set(rootProject.file("LICENSE"))

      macOS {
        packageName = appDisplayName
        dockName = appDisplayName
        bundleID = appBundleId
        packageBuildVersion = appVersion
        appCategory = "public.app-category.utilities"

        signing {
          sign.set(macSigningEnabled)

          if (!macSigningIdentity.isNullOrBlank()) {
            identity.set(macSigningIdentity)
          }

          if (!macSigningKeychain.isNullOrBlank()) {
            keychain.set(macSigningKeychain)
          }
        }

        if (
            !notarizationAppleId.isNullOrBlank() &&
                !notarizationPassword.isNullOrBlank() &&
                !notarizationTeamId.isNullOrBlank()
        ) {
          notarization {
            appleID.set(notarizationAppleId)
            password.set(notarizationPassword)
            teamID.set(notarizationTeamId)
          }
        }
      }

      windows {
        menuGroup = appDisplayName
        dirChooser = true
        perUserInstall = true
      }

      linux {
        packageName = "spacex-launches"
        menuGroup = appDisplayName
      }
    }
  }
}
