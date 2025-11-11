import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// Compute an integer versionCode from a semantic versionName like "1.2.3-alpha.12".
fun versionCodeFrom(versionName: String): Int {
    val strippedVersionName = versionName.removePrefix("v")
    val parts = strippedVersionName.split("-", limit = 2)

    val versionNumbers = parts[0].split(".").map { it.toInt() }
    val major = versionNumbers.getOrElse(0) { 0 }
    val minor = versionNumbers.getOrElse(1) { 0 }
    val patch = versionNumbers.getOrElse(2) { 0 }

    if (major > 214 || minor > 99 || patch > 99) {
        throw GradleException("Version component out of range. Max values: major (214), minor (99), patch (99).")
    }

    val stage: Int
    val preReleaseNumber: Int

    if (parts.size > 1) {
        val preParts = parts[1].split(".")
        val label = preParts[0]
        preReleaseNumber = preParts.getOrNull(1)?.toIntOrNull() ?: 0

        stage = when (label) {
            "alpha" -> 1
            "beta" -> 2
            "rc" -> 3
            else -> 4 // other pre-types
        }
    } else {
        stage = 5 // stable
        preReleaseNumber = 0
    }

    if (preReleaseNumber > 99) {
        throw GradleException("Pre-release number cannot exceed 99.")
    }

    // This new scheme allows for major versions up to 214.
    return major * 10_000_000 +
            minor * 100_000 +
            patch * 1_000 +
            stage * 100 +
            preReleaseNumber
}

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
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
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
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
        
        val appVersion = "v1.6.0"
        versionName = appVersion
        versionCode = versionCodeFrom(appVersion)
    }
    signingConfigs {
        create("release") {
            // Production keystore
            storeFile = if (System.getenv("RELEASE_KEYSTORE_FILE") != null) {
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
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}
