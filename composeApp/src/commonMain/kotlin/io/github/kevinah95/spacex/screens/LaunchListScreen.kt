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
package io.github.kevinah95.spacex.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.kevinah95.spacex.domain.entity.RocketLaunch
import io.github.kevinah95.spacex.presentation.rocketLaunch.RocketLaunchViewModel
import io.github.kevinah95.spacex.theme.app_theme_successful
import io.github.kevinah95.spacex.theme.app_theme_unsuccessful
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaunchListScreen(
    paddingValues: PaddingValues,
    onLaunchClick: (RocketLaunch) -> Unit,
    viewModel: RocketLaunchViewModel,
) {
  val state by viewModel.uiState.collectAsState()
  val coroutineScope = rememberCoroutineScope()
  var isRefreshing by remember { mutableStateOf(false) }
  val pullToRefreshState = rememberPullToRefreshState()

  PullToRefreshBox(
      modifier = Modifier.fillMaxSize().padding(paddingValues),
      state = pullToRefreshState,
      isRefreshing = isRefreshing,
      onRefresh = {
        isRefreshing = true
        coroutineScope.launch {
          viewModel.loadLaunches()
          isRefreshing = false
        }
      },
  ) {
    if (state.isLoading && !isRefreshing) {
      Column(
          verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.fillMaxSize(),
      ) {
        Text("Loading...", style = MaterialTheme.typography.bodyLarge)
      }
    } else {
      LazyColumn {
        items(state.launches) { launch: RocketLaunch ->
          Column(
              modifier = Modifier.fillMaxWidth().clickable { onLaunchClick(launch) }.padding(16.dp)
          ) {
            Text(
                text = "${launch.missionName} — ${launch.launchYear}",
                style = MaterialTheme.typography.headlineSmall,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = if (launch.launchSuccess == true) "Successful" else "Unsuccessful",
                color =
                    if (launch.launchSuccess == true) app_theme_successful
                    else app_theme_unsuccessful,
            )
            Spacer(Modifier.height(8.dp))
            val details = launch.details
            if (!details.isNullOrBlank()) {
              Text(
                  text = details,
                  style = MaterialTheme.typography.bodyMedium,
                  maxLines = 2,
              )
            }
          }
          HorizontalDivider()
        }
      }
    }
  }
}
