package io.github.kevinah95.spacex

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberAppVersionName(): String =
    remember {
      val versionName =
          System.getProperty("spacex.app.version")
              ?: DesktopVersionMarker::class.java.`package`?.implementationVersion

      formatAppVersion(versionName)
    }

private class DesktopVersionMarker
