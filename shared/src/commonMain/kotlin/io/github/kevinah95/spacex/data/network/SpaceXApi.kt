package io.github.kevinah95.spacex.data.network

import io.github.kevinah95.spacex.domain.entity.RocketLaunch
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.json.Json

class SpaceXApi(private val ioDispatcher: CoroutineDispatcher) {
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                useAlternativeNames = false
            })
        }
    }

    val latestLaunches: Flow<List<RocketLaunch>> = flow {
        while(true) {
            val latestLaunches = httpClient.get("https://api.spacexdata.com/v5/launches").body<List<RocketLaunch>>()
            emit(latestLaunches) // Emits the result of the request to the flow
        }
    }
        .flowOn(ioDispatcher)
}