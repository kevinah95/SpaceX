/*
 * Copyright 2026 kevinah95 (Kevin A. Hernández Rostrán)
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

plugins {
  // this is necessary to avoid the plugins to be loaded multiple times
  // in each subproject's classloader
  alias(libs.plugins.androidApplication) apply false
  alias(libs.plugins.androidMultiplatformLibrary) apply false
  alias(libs.plugins.composeMultiplatform) apply false
  alias(libs.plugins.composeCompiler) apply false
  alias(libs.plugins.kotlinMultiplatform) apply false
  alias(libs.plugins.mokkery) apply false
  alias(libs.plugins.kotlinJvm) apply false
  alias(libs.plugins.googleServices) apply false
  alias(libs.plugins.firebaseCrashlytics) apply false
  alias(libs.plugins.spotless)
}

spotless {
  ratchetFrom("origin/prod")

  kotlin {
    target("**/src/**/*.kt")
    targetExclude("**/build/**", "**/generated/**")
    ktfmt()
    licenseHeaderFile(rootProject.file("spotless/spotless.license.kt"), "(^(?![\\/ ]\\*).*$)")
  }

  kotlinGradle {
    target("*.gradle.kts", "**/*.gradle.kts")
    targetExclude("**/build/**")
    ktfmt()
    licenseHeaderFile(rootProject.file("spotless/spotless.license.kt"), "(^(?![\\/ ]\\*).*$)")
  }

  format("misc") {
    target("../.prettierrc.yml", "../.releaserc.yml", "**/*.yaml", "**/*.yml", "**/*.json")
    targetExclude("**/build/**", "**/.gradle/**")

    trimTrailingWhitespace()
    leadingTabsToSpaces(2)
    endWithNewline()
    prettier(mapOf("prettier" to "3.8.1"))
        .configFile(rootProject.file("../.prettierrc.yml"))
        .npmInstallCache("${rootProject.rootDir}/.gradle/spotless-npm-cache")
  }
}

subprojects {
  tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions { freeCompilerArgs.add("-Xexpect-actual-classes") }
  }
}
