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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.kevinah95.spacex.presentation.rocketLaunch.RocketLaunchViewModel
import io.github.kevinah95.spacex.theme.app_theme_successful
import io.github.kevinah95.spacex.theme.app_theme_unsuccessful

@Composable
fun LaunchDetailScreen(
    paddingValues: PaddingValues,
    id: String,
    viewModel: RocketLaunchViewModel,
) {
  val state by viewModel.uiState.collectAsState()
  val launch = state.launches.find { it.id == id }

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
