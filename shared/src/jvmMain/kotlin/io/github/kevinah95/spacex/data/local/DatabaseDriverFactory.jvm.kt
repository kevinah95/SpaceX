package io.github.kevinah95.spacex.data.local

import app.cash.sqldelight.db.SqlDriver
import org.koin.core.scope.Scope

actual class DriverFactory actual constructor(scope: Scope) {
    actual fun createDriver(): SqlDriver {
        TODO("Not yet implemented")
    }
}