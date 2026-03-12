package io.github.kevinah95.spacex

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSBundle

@Composable
actual fun rememberAppVersionName(): String =
    remember {
      val versionName =
          NSBundle.mainBundle.objectForInfoDictionaryKey("SpaceXReleaseVersion") as? String
              ?: NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString")
                  as? String

      formatAppVersion(versionName)
    }
