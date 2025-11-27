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
import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

// Apply the Spotless plugin to the project using this convention
apply(plugin = "com.diffplug.spotless")

// Configure the Spotless extension
configure<SpotlessExtension> {
  // Format all Kotlin files in the project
  //    kotlin {
  //        target("src/**/*.kt")
  //        targetExclude("**/build/**", "**/generated/**")
  //        ktlint()
  //        licenseHeaderFile(rootProject.file("spotless/spotless.license.kt"), "(^(?![\\/
  // ]\\*).*$)")
  //    }
  // Format all Gradle Kotlin DSL files in the project
  kotlinGradle {
    target("*.gradle.kts", "**/*.gradle.kts")
    ktfmt()
    licenseHeaderFile(rootProject.file("spotless/spotless.license.kt"), "(^(?![\\/ ]\\*).*$)")
  }
}
