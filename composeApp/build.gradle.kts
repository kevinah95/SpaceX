import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// Compute an integer versionCode from a semantic versionName like "1.2.3[-suffix]".
// Scheme: MAJOR * 10000 + MINOR * 100 + PATCH (e.g., 1.2.3 -> 10203)
fun versionCodeFrom(versionName: String): Int {
    val match = Regex("""(\n?)(\d+)\.(\d+)\.(\n?)(\d+)""").find(versionName)
        ?: Regex("""(\d+)\.(\d+)(?:\.(\d+))?""").find(versionName)
        ?: return 1
    val groups = match.groupValues
    val major = groups.getOrNull(2)?.toIntOrNull() ?: groups.getOrNull(1)?.toIntOrNull() ?: 0
    val minor = groups.getOrNull(3)?.toIntOrNull() ?: 0
    val patch = groups.getOrNull(5)?.toIntOrNull() ?: groups.getOrNull(4)?.toIntOrNull() ?: 0
    return major * 10000 + minor * 100 + patch
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
            jvmTarget.set(JvmTarget.JVM_11)
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

android {
    namespace = "io.github.kevinah95.spacex"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    signingConfigs {
        getByName("debug") {
            // Use environment variables in CI, fallback to local debug keystore
            storeFile = if (System.getenv("KEYSTORE_FILE") != null) {
                file(System.getenv("KEYSTORE_FILE"))
            } else {
                file("${System.getProperty("user.home")}/.android/debug.keystore")
            }
            storePassword = System.getenv("KEYSTORE_PASSWORD") ?: "android"
            keyAlias = System.getenv("KEY_ALIAS") ?: "androiddebugkey"
            keyPassword = System.getenv("KEY_PASSWORD") ?: "android"
        }

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

    // Define environments (flavors)
    flavorDimensions += "environment"
    productFlavors {
        create("alpha") {
            dimension = "environment"
            applicationIdSuffix = ".alpha"
            versionNameSuffix = "-alpha"
            resValue("string", "app_name", "SpaceX alpha")
            // Alpha uses debug keystore for signing
            signingConfig = signingConfigs.getByName("debug")
        }
        
        create("prod") {
            dimension = "environment"
            resValue("string", "app_name", "SpaceX")
            // Production has no suffixes
            // Prod uses release keystore for signing
            signingConfig = signingConfigs.getByName("release")
        }
    }

    defaultConfig {
        applicationId = "io.github.kevinah95.spacex"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        // Keep versionName managed by release-please; versionCode is derived from it.
        val relVersionName = "1.2.0-alpha.7" // x-release-please-version
        versionName = relVersionName
        versionCode = versionCodeFrom(relVersionName)
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
        }
        
        getByName("release") {
            isMinifyEnabled = false
            // Let flavors decide signing; do not override here
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

