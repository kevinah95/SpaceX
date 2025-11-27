plugins {
  id("com.gradle.develocity")
}

// Ref: https://docs.gradle.com/develocity/gradle/current/gradle-plugin/
develocity {
  buildScan {
    publishing.onlyIf { !System.getenv("CI").isNullOrEmpty() }
    termsOfUseUrl.set("https://gradle.com/help/legal-terms-of-use")
    termsOfUseAgree.set("yes")
  }
}
