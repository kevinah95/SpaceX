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
package io.github.kevinah95.spacex.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.sql.DriverManager
import java.util.Properties

actual class DriverFactory {
  actual fun createDriver(): SqlDriver {
    val databasePath = databasePath()
    Files.createDirectories(databasePath.parent)
    registerSqliteDriver()

    // In-Memory: JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY, Properties(), AppDatabase.Schema)
    val driver: SqlDriver =
        JdbcSqliteDriver(
            "jdbc:sqlite:${databasePath.toAbsolutePath()}",
            Properties(),
            AppDatabase.Schema,
        )
    return driver
  }

  private fun registerSqliteDriver() {
    val driverClassName = "org.sqlite.JDBC"
    val hasSqliteDriver =
        DriverManager.getDrivers().asSequence().any { driver ->
          driver::class.java.name == driverClassName
        }

    if (!hasSqliteDriver) {
      Class.forName(driverClassName)
    }
  }

  private fun databasePath(): Path {
    val appDirectory =
        when (System.getProperty("os.name")?.lowercase()) {
          null -> Paths.get(System.getProperty("user.home"), ".spacex")
          else -> {
            when {
              "mac" in System.getProperty("os.name").lowercase() ->
                  Paths.get(
                      System.getProperty("user.home"),
                      "Library",
                      "Application Support",
                      "io.github.kevinah95.spacex",
                  )
              "win" in System.getProperty("os.name").lowercase() ->
                  Paths.get(
                      System.getenv("APPDATA") ?: System.getProperty("user.home"),
                      "io.github.kevinah95.spacex",
                  )
              else -> Paths.get(System.getProperty("user.home"), ".local", "share", "spacex")
            }
          }
        }

    return appDirectory.resolve("launch.db")
  }
}
