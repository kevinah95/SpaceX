/*
 * Copyright 2025-2026 kevinah95 (Kevin A. Hernández Rostrán)
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
package io.github.kevinah95.spacex.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.kevinah95.spacex.presentation.rocketLaunch.RocketLaunchViewModel
import io.github.kevinah95.spacex.theme.app_theme_successful
import io.github.kevinah95.spacex.theme.app_theme_unsuccessful

@Composable
fun LaunchDetailScreen(
    paddingValues: PaddingValues,
    flightNumber: Int,
    viewModel: RocketLaunchViewModel,
) {
  val state by viewModel.uiState.collectAsState()
  val launch = state.launches.find { it.flightNumber == flightNumber }

  if (launch == null) {
    Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
      Text("Launch not found.", style = MaterialTheme.typography.bodyLarge)
    }
  } else {
    Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
      Text(
          text = "${launch.missionName} — ${launch.launchYear}",
          style = MaterialTheme.typography.headlineSmall,
      )
      Spacer(Modifier.height(8.dp))
      Text(
          text = "Flight #${launch.flightNumber}",
          style = MaterialTheme.typography.labelLarge,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      Spacer(Modifier.height(8.dp))
      LaunchDetailPatchImage(
          imageUrl = launch.links.patch?.large ?: launch.links.patch?.small,
          contentDescription = "Mission patch for ${launch.missionName}",
      )
      Text(
          text = "Date (UTC): ${launch.launchDateUTC}",
          style = MaterialTheme.typography.bodyMedium,
      )
      Spacer(Modifier.height(8.dp))
      Text(
          text = if (launch.launchSuccess == true) "Successful" else "Unsuccessful",
          style = MaterialTheme.typography.titleMedium,
          color =
              if (launch.launchSuccess == true) app_theme_successful else app_theme_unsuccessful,
      )
      val details = launch.details
      if (!details.isNullOrBlank()) {
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Details",
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = details,
            style = MaterialTheme.typography.bodyMedium,
        )
      }
      val article = launch.links.article
      if (!article.isNullOrBlank()) {
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Article",
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = article,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
        )
      }
    }
  }
}

@Composable
private fun LaunchDetailPatchImage(imageUrl: String?, contentDescription: String) {
  val usableUrl = imageUrl?.takeIf { it.isNotBlank() } ?: return
  var showImage by remember(usableUrl) { mutableStateOf(true) }

  if (!showImage) return

  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
    AsyncImage(
        model = usableUrl,
        contentDescription = contentDescription,
        contentScale = ContentScale.Fit,
        onError = { showImage = false },
        modifier = Modifier.size(180.dp),
    )
  }
  Spacer(Modifier.height(16.dp))
}
