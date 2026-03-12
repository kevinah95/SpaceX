package io.github.kevinah95.spacex

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberAppVersionName(): String {
  val context = LocalContext.current

  return remember(context) { formatAppVersion(context.packageVersionName()) }
}

private fun Context.packageVersionName(): String? =
    runCatching {
          val packageInfo =
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(
                    packageName,
                    PackageManager.PackageInfoFlags.of(0),
                )
              } else {
                @Suppress("DEPRECATION")
                packageManager.getPackageInfo(packageName, 0)
              }

          packageInfo.versionName
        }
        .getOrNull()
