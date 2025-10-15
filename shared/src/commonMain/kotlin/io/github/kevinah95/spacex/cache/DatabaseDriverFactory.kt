package io.github.kevinah95.spacex.cache

import app.cash.sqldelight.db.SqlDriver
import org.koin.core.scope.Scope

expect class DriverFactory(scope: Scope) {
    fun createDriver(): SqlDriver
}