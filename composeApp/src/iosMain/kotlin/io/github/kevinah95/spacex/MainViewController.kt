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
package io.github.kevinah95.spacex

import androidx.compose.ui.window.ComposeUIViewController
import io.github.kevinah95.spacex.di.initKoin
import platform.Foundation.NSBundle

private fun iosVersionName(): String {
	val bundleVersion =
			NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String
	return "v${bundleVersion ?: "0.0.0"}"
}

fun MainViewController() =
		ComposeUIViewController(configure = { initKoin() }) { App(versionName = iosVersionName()) }
