import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
  alias(libs.plugins.kotlinJvm)
  alias(libs.plugins.composeMultiplatform)
  alias(libs.plugins.composeCompiler)
  alias(libs.plugins.spotlessConventions)
}

kotlin {
  dependencies {
    implementation(projects.composeApp)
    implementation(projects.shared)

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)
  }
}

compose.desktop {
  application {
    mainClass = "io.github.kevinah95.spacex.MainKt"

    nativeDistributions {
      targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
      packageName = "io.github.kevinah95.spacex"
      packageVersion = "1.0.0"
    }
  }
}
