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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import io.github.kevinah95.spacex.navigation.LaunchDetail
import io.github.kevinah95.spacex.navigation.LaunchList
import io.github.kevinah95.spacex.presentation.rocketLaunch.RocketLaunchViewModel
import io.github.kevinah95.spacex.screens.LaunchDetailScreen
import io.github.kevinah95.spacex.screens.LaunchListScreen
import io.github.kevinah95.spacex.theme.AppTheme
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(versionName: String, viewModel: RocketLaunchViewModel = koinViewModel()) {
  val navController = rememberNavController()
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentDestination = navBackStackEntry?.destination

  val state by viewModel.uiState.collectAsState()

  AppTheme {
    Scaffold(
        topBar = {
          val isDetail = currentDestination?.hasRoute<LaunchDetail>() == true
          val launch =
              if (isDetail) {
                val flightNumber = navBackStackEntry?.toRoute<LaunchDetail>()?.flightNumber
                state.launches.find { it.flightNumber == flightNumber }
              } else null

          TopAppBar(
              title = {
                if (isDetail) {
                  Text(
                      text = launch?.missionName ?: "Launch Detail",
                      style = MaterialTheme.typography.headlineMedium,
                  )
                } else {
                  Column {
                    Text("SpaceX Launches", style = MaterialTheme.typography.headlineLarge)
                    Text(
                        text = versionName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.padding(start = 2.dp),
                    )
                  }
                }
              },
              navigationIcon = {
                if (isDetail) {
                  IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                    )
                  }
                }
              },
          )
        }
    ) { paddingValues ->
      NavHost(navController = navController, startDestination = LaunchList) {
        composable<LaunchList> {
          LaunchListScreen(
              paddingValues = paddingValues,
              onLaunchClick = { launch ->
                navController.navigate(LaunchDetail(flightNumber = launch.flightNumber))
              },
              viewModel = viewModel,
          )
        }
        composable<LaunchDetail> { backStackEntry ->
          val route = backStackEntry.toRoute<LaunchDetail>()
          LaunchDetailScreen(
              paddingValues = paddingValues,
              flightNumber = route.flightNumber,
              viewModel = viewModel,
          )
        }
      }
    }
  }
}

@Preview
@Composable
fun AppPreview() {
  App(versionName = "v0.0.0")
}
