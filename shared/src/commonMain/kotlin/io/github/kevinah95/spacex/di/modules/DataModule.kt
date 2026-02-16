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
package io.github.kevinah95.spacex.di.modules

import app.cash.sqldelight.db.SqlDriver
import io.github.kevinah95.spacex.data.local.AppDatabase
import io.github.kevinah95.spacex.data.local.DatabaseDriverFactory
import io.github.kevinah95.spacex.data.local.ILocalRocketLaunchesDataSource
import io.github.kevinah95.spacex.data.local.LocalRocketLaunchesDataSource
import io.github.kevinah95.spacex.data.remote.IRemoteRocketLaunchesDataSource
import io.github.kevinah95.spacex.data.remote.RemoteRocketLaunchesDataSource
import io.github.kevinah95.spacex.data.repository.IRocketLaunchesRepository
import io.github.kevinah95.spacex.data.repository.RocketLaunchesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module

val dataModule = module {
  single<SqlDriver> { get<DatabaseDriverFactory>().createDriver() }
  single { AppDatabase(get()) }
  single { get<AppDatabase>().appDatabaseQueries }
  single<ILocalRocketLaunchesDataSource> { LocalRocketLaunchesDataSource(get()) }
  single<IRemoteRocketLaunchesDataSource> { RemoteRocketLaunchesDataSource(get(), Dispatchers.IO) }
  single<IRocketLaunchesRepository> { RocketLaunchesRepository(get(), get(), Dispatchers.Default) }
}
