package io.github.kevinah95.spacex.data.local

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import org.koin.core.scope.Scope

actual class DriverFactory actual constructor(scope: Scope) {
    val context = scope.get<Context>()
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(AppDatabase.Schema, context, "launch.db")
    }
}