package io.github.kevinah95.spacex

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import io.github.kevinah95.spacex.theme.AppTheme
import io.github.kevinah95.spacex.theme.app_theme_successful
import io.github.kevinah95.spacex.theme.app_theme_unsuccessful
import io.github.kevinah95.spacex.ui.rocketLaunch.RocketLaunchViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@OptIn(
    ExperimentalMaterial3Api::class
)
@Composable
@Preview
fun App() {
    val viewModel = koinViewModel<RocketLaunchViewModel>()
    val state by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }
    val pullToRefreshState = rememberPullToRefreshState()
    val versionName = "v1.6.0"

    AppTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                "SpaceX Launches",
                                style = MaterialTheme.typography.headlineLarge
                            )
                            Row(
                                modifier = Modifier
                                    .padding(start = 2.dp)
                            ) {
                                Text(
                                    text = "${versionName}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                        alpha = 0.5f
                                    )
                                )
                            }
                        }
                    }
                )
            }
        ) { padding ->
            PullToRefreshBox(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                state = pullToRefreshState,
                isRefreshing = isRefreshing,
                onRefresh = {
                    isRefreshing = true
                    coroutineScope.launch {
                        viewModel.loadLaunches()
                        isRefreshing = false
                    }
                }
            ) {
                if (state.isLoading && !isRefreshing) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text("Loading...", style = MaterialTheme.typography.bodyLarge)
                    }
                } else {
                    LazyColumn {
                        items(state.launches) { launch: RocketLaunch ->
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "${launch.missionName} - ${launch.launchYear}",
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = if (launch.launchSuccess == true) "Successful" else "Unsuccessful",
                                    color = if (launch.launchSuccess == true) app_theme_successful else app_theme_unsuccessful
                                )
                                Spacer(Modifier.height(8.dp))
                                val details = launch.details
                                if (details != null && details.isNotBlank()) {
                                    Text(details)
                                }
                            }
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}