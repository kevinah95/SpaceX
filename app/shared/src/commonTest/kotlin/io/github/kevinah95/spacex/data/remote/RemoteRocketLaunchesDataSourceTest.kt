/*
 * Copyright 2025-2026 kevinah95 (Kevin A. Hernández Rostrán)
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
package io.github.kevinah95.spacex.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json

class RemoteRocketLaunchesDataSourceTest {

  private val json = Json { ignoreUnknownKeys = true }

  @Test
  fun `latestLaunches should return list of rocket launches on success`() = runTest {
    // Arrange
    val mockResponse =
        """
        {
          "count": 2,
          "next": null,
          "previous": null,
          "results": [
            {
              "id": "e3df2ecd-c239-472f-95e4-2b89b4f75800",
              "url": "https://ll.thespacedevs.com/2.3.0/launches/e3df2ecd/",
              "name": "FalconSat",
              "response_mode": "list",
              "slug": "falconsat",
              "launch_designator": "2006-001",
              "status": { "id": 4, "name": "Launch Failure", "abbrev": "Failure", "description": "Failure" },
              "last_updated": "2024-01-01T00:00:00Z",
              "net": "2006-03-24T22:30:00.000Z",
              "net_precision": null,
              "window_end": "2006-03-24T22:30:00.000Z",
              "window_start": "2006-03-24T22:30:00.000Z",
              "image": {
                "id": 1,
                "name": "FalconSat image",
                "image_url": "https://example.com/large.png",
                "thumbnail_url": "https://example.com/small.png",
                "credit": null,
                "license": { "id": 1, "name": "Unknown", "priority": 9, "link": null },
                "single_use": true,
                "variants": []
              },
              "infographic": null
            },
            {
              "id": "f8c9f344-a6df-4f30-873a-90fe3a7840b3",
              "url": "https://ll.thespacedevs.com/2.3.0/launches/f8c9f344/",
              "name": "DemoSat",
              "response_mode": "list",
              "slug": "demosat",
              "launch_designator": "2007-001",
              "status": { "id": 3, "name": "Launch Successful", "abbrev": "Success", "description": "Success" },
              "last_updated": "2024-01-01T00:00:00Z",
              "net": "2007-03-21T01:10:00.000Z",
              "net_precision": null,
              "window_end": "2007-03-21T01:10:00.000Z",
              "window_start": "2007-03-21T01:10:00.000Z",
              "image": null,
              "infographic": null
            }
          ]
        }
        """
            .trimIndent()

    val mockEngine = MockEngine { request ->
      assertEquals(
          "https://ll.thespacedevs.com/2.3.0/launches/?format=json",
          request.url.toString(),
      )
      respond(
          content = mockResponse,
          status = HttpStatusCode.OK,
          headers = headersOf(HttpHeaders.ContentType, "application/json"),
      )
    }

    val httpClient = HttpClient(mockEngine) { install(ContentNegotiation) { json(json) } }

    val dataSource =
        RemoteRocketLaunchesDataSource(
            httpClient = httpClient,
            ioDispatcher = Dispatchers.Unconfined,
        )

    // Act
    val result = dataSource.latestLaunches().first()

    // Assert
    assertEquals(2, result.size)

    assertEquals("e3df2ecd-c239-472f-95e4-2b89b4f75800", result[0].id)
    assertEquals("FalconSat", result[0].missionName)
    assertEquals("2006-03-24T22:30:00.000Z", result[0].launchDateUTC)
    assertEquals(null, result[0].details)
    assertEquals(false, result[0].launchSuccess)
    assertEquals("https://example.com/small.png", result[0].links.patch?.small)
    assertEquals("https://example.com/large.png", result[0].links.patch?.large)
    assertEquals(null, result[0].links.article)

    assertEquals("f8c9f344-a6df-4f30-873a-90fe3a7840b3", result[1].id)
    assertEquals("DemoSat", result[1].missionName)
    assertEquals("2007-03-21T01:10:00.000Z", result[1].launchDateUTC)
    assertEquals(true, result[1].launchSuccess)
    assertEquals(null, result[1].links.patch)
    assertEquals(null, result[1].links.article)

    httpClient.close()
  }

  @Test
  fun `latestLaunches should return empty list when API returns empty results`() = runTest {
    // Arrange
    val mockResponse = """{"count": 0, "next": null, "previous": null, "results": []}"""

    val mockEngine = MockEngine { request ->
      respond(
          content = mockResponse,
          status = HttpStatusCode.OK,
          headers = headersOf(HttpHeaders.ContentType, "application/json"),
      )
    }

    val httpClient = HttpClient(mockEngine) { install(ContentNegotiation) { json(json) } }

    val dataSource =
        RemoteRocketLaunchesDataSource(
            httpClient = httpClient,
            ioDispatcher = Dispatchers.Unconfined,
        )

    // Act
    val result = dataSource.latestLaunches().first()

    // Assert
    assertEquals(0, result.size)

    httpClient.close()
  }

  @Test
  fun `latestLaunches should throw exception when API returns error`() = runTest {
    // Arrange
    val mockEngine = MockEngine { request ->
      respond(
          content = "Internal Server Error",
          status = HttpStatusCode.InternalServerError,
          headers = headersOf(HttpHeaders.ContentType, "text/plain"),
      )
    }

    val httpClient = HttpClient(mockEngine) { install(ContentNegotiation) { json(json) } }

    val dataSource =
        RemoteRocketLaunchesDataSource(
            httpClient = httpClient,
            ioDispatcher = Dispatchers.Unconfined,
        )

    // Act & Assert
    assertFailsWith<Exception> { dataSource.latestLaunches().first() }

    httpClient.close()
  }

  @Test
  fun `latestLaunches should handle nullable fields correctly`() = runTest {
    // Arrange
    val mockResponse =
        """
        {
          "count": 1,
          "next": null,
          "previous": null,
          "results": [
            {
              "id": "3",
              "name": "Trailblazer",
              "net": "2008-08-03T03:34:00.000Z",
              "status": { "id": 4, "name": "Launch Unknown", "abbrev": "Unknown", "description": "Unknown" },
              "details": null,
              "success": null,
              "image": null,
              "links": {
                "patch": null,
                "article": null
              }
            }
          ]
        }
        """
            .trimIndent()

    val mockEngine = MockEngine { request ->
      respond(
          content = mockResponse,
          status = HttpStatusCode.OK,
          headers = headersOf(HttpHeaders.ContentType, "application/json"),
      )
    }

    val httpClient = HttpClient(mockEngine) { install(ContentNegotiation) { json(json) } }

    val dataSource =
        RemoteRocketLaunchesDataSource(
            httpClient = httpClient,
            ioDispatcher = Dispatchers.Unconfined,
        )

    // Act
    val result = dataSource.latestLaunches().first()

    // Assert
    assertEquals(1, result.size)
    assertEquals("3", result[0].id)
    assertEquals("Trailblazer", result[0].missionName)
    assertEquals("2008-08-03T03:34:00.000Z", result[0].launchDateUTC)
    assertEquals(null, result[0].details)
    assertEquals(null, result[0].launchSuccess)
    assertEquals(null, result[0].links.patch)
    assertEquals(null, result[0].links.article)

    httpClient.close()
  }
}
