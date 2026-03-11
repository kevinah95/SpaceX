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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import io.github.kevinah95.spacex.navigation.AuthStart
import io.github.kevinah95.spacex.navigation.LaunchDetail
import io.github.kevinah95.spacex.navigation.LaunchList
import io.github.kevinah95.spacex.navigation.Profile
import io.github.kevinah95.spacex.presentation.auth.AuthViewModel
import io.github.kevinah95.spacex.presentation.rocketLaunch.RocketLaunchViewModel
import io.github.kevinah95.spacex.screens.AuthStartScreen
import io.github.kevinah95.spacex.screens.LaunchDetailScreen
import io.github.kevinah95.spacex.screens.LaunchListScreen
import io.github.kevinah95.spacex.screens.ProfileScreen
import io.github.kevinah95.spacex.theme.AppTheme
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App(
    authViewModel: AuthViewModel = koinViewModel(),
    viewModel: RocketLaunchViewModel = koinViewModel(),
) {
  val navController = rememberNavController()
  val versionName = "v3.1.2-alpha.1"
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentDestination = navBackStackEntry?.destination

  val authState by authViewModel.uiState.collectAsState()
  val state by viewModel.uiState.collectAsState()
  val isOnAuth = currentDestination?.hasRoute<AuthStart>() == true
  val isOnLaunchList = currentDestination?.hasRoute<LaunchList>() == true
  val isOnProfile = currentDestination?.hasRoute<Profile>() == true
  val isOnDetail = currentDestination?.hasRoute<LaunchDetail>() == true
  val shouldShowBottomBar = isOnLaunchList || isOnProfile

  LaunchedEffect(authState.isAuthenticated, currentDestination) {
    if (currentDestination == null) return@LaunchedEffect

    if (authState.isAuthenticated && isOnAuth) {
      navController.navigate(LaunchList) { popUpTo(AuthStart) { inclusive = true } }
    } else if (!authState.isAuthenticated && !isOnAuth) {
      navController.navigate(AuthStart) { popUpTo(AuthStart) { inclusive = true } }
    }
  }

  AppTheme {
    Scaffold(
        topBar = {
          val launch =
              if (isOnDetail) {
                val flightNumber = navBackStackEntry?.toRoute<LaunchDetail>()?.flightNumber
                state.launches.find { it.flightNumber == flightNumber }
              } else null

          if (!isOnAuth) {
            TopAppBar(
                title = {
                  if (isOnDetail) {
                    Text(
                        text = launch?.missionName ?: "Launch Detail",
                        style = MaterialTheme.typography.headlineMedium,
                    )
                  } else if (isOnProfile) {
                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.headlineMedium,
                    )
                  } else {
                    Text("SpaceX Launches", style = MaterialTheme.typography.headlineLarge)
                  }
                },
                navigationIcon = {
                  if (isOnDetail) {
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
        },
        bottomBar = {
          if (shouldShowBottomBar) {
            val navigationItemColors =
                NavigationBarItemDefaults.colors(
                    MaterialTheme.colorScheme.onSurface,
                    MaterialTheme.colorScheme.onSurface,
                    MaterialTheme.colorScheme.surfaceVariant,
                    MaterialTheme.colorScheme.onSurfaceVariant,
                    MaterialTheme.colorScheme.onSurfaceVariant,
                )

            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp,
            ) {
              NavigationBarItem(
                  selected = isOnLaunchList,
                  onClick = {
                    navController.navigate(LaunchList) {
                      popUpTo(LaunchList) { saveState = true }
                      launchSingleTop = true
                      restoreState = true
                    }
                  },
                  icon = {
                    Icon(
                        imageVector = Icons.Filled.Home,
                        contentDescription = "Home",
                    )
                  },
                  colors = navigationItemColors,
                  label = { Text("Home") },
              )
              NavigationBarItem(
                  selected = isOnProfile,
                  onClick = {
                    navController.navigate(Profile) {
                      popUpTo(LaunchList) { saveState = true }
                      launchSingleTop = true
                      restoreState = true
                    }
                  },
                  icon = {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Profile",
                    )
                  },
                  colors = navigationItemColors,
                  label = { Text("Profile") },
              )
            }
          }
        },
    ) { paddingValues ->
      NavHost(navController = navController, startDestination = AuthStart) {
        composable<AuthStart> {
          AuthStartScreen(
              paddingValues = paddingValues,
              state = authState,
              versionName = versionName,
              onAnonymousSignIn = { authViewModel.signInAnonymously() },
          )
        }
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
        composable<Profile> {
          ProfileScreen(
              paddingValues = paddingValues,
              state = authState,
              versionName = versionName,
              onSignOut = { authViewModel.signOut() },
          )
        }
      }
    }
  }
}
