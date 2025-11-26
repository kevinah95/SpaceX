// Ref: https://docs.gradle.org/current/userguide/sharing_build_logic_between_subprojects.html
plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}