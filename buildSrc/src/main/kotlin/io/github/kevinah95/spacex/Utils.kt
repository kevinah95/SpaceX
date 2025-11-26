package io.github.kevinah95.spacex

import org.gradle.api.GradleException

object Utils {
    // Compute an integer versionCode from a semantic versionName like "1.2.3-alpha.12".
    fun versionCodeFrom(versionName: String): Int {
        val parts = versionName.split("-", limit = 2)

        val versionNumbers = parts[0].split(".").map { it.toInt() }
        val major = versionNumbers.getOrElse(0) { 0 }
        val minor = versionNumbers.getOrElse(1) { 0 }
        val patch = versionNumbers.getOrElse(2) { 0 }

        if (major > 214 || minor > 99 || patch > 99) {
            throw GradleException("Version component out of range. Max values: major (214), minor (99), patch (99).")
        }

        val stage: Int
        val preReleaseNumber: Int

        if (parts.size > 1) {
            val preParts = parts[1].split(".")
            val label = preParts[0]
            preReleaseNumber = preParts.getOrNull(1)?.toIntOrNull() ?: 0

            stage = when (label) {
                "alpha" -> 1
                "beta" -> 2
                "rc" -> 3
                else -> 4 // other pre-types
            }
        } else {
            stage = 5 // stable
            preReleaseNumber = 0
        }

        if (preReleaseNumber > 99) {
            throw GradleException("Pre-release number cannot exceed 99.")
        }

        // This new scheme allows for major versions up to 214.
        return major * 10_000_000 +
                minor * 100_000 +
                patch * 1_000 +
                stage * 100 +
                preReleaseNumber
    }
}