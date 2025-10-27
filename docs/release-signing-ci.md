# Android CI signing (alpha) and Firebase App Distribution

This doc explains how release APKs are signed in CI with an "alpha" signature (the Android debug keystore) and published to GitHub Releases and Firebase App Distribution.

Use this for internal alpha testing only. Do not ship to Play Store with the debug keystore.

## How it works

- Locally: Gradle signs with your default debug keystore at `~/.android/debug.keystore`.
- In CI: A base64-encoded keystore is stored as a secret, decoded at runtime, and used to sign the release build.
- The release build is uploaded to the GitHub Release and distributed via Firebase App Distribution.

Key files:
- Gradle config: `composeApp/build.gradle.kts`
- GitHub Actions workflow: `.github/workflows/release-please.yml`

## Gradle configuration

`composeApp/build.gradle.kts` is configured to use environment variables in CI and fall back to the local debug keystore for developers:

```kotlin
android {
    signingConfigs {
        getByName("debug") {
            // CI: use env-provided keystore; Local: use default debug keystore
            storeFile = if (System.getenv("KEYSTORE_FILE") != null) {
                file(System.getenv("KEYSTORE_FILE"))
            } else {
                file("${System.getProperty("user.home")}/.android/debug.keystore")
            }
            storePassword = System.getenv("KEYSTORE_PASSWORD") ?: "android"
            keyAlias = System.getenv("KEY_ALIAS") ?: "androiddebugkey"
            keyPassword = System.getenv("KEY_PASSWORD") ?: "android"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            // Alpha: sign release builds with debug signing
            signingConfig = signingConfigs.getByName("debug")
        }
    }
}
```

Resulting artifact path (signed):
```
composeApp/build/outputs/apk/release/composeApp-release.apk
```

## GitHub Actions workflow

Workflow file: `.github/workflows/release-please.yml`

- Creates a release using release-please
- Writes `google-services.json` from secrets
- Decodes the base64 keystore and sets env vars for Gradle
- Builds the Android release
- Uploads the signed APK to the GitHub release
- Distributes to Firebase App Distribution

Key steps (excerpt):

```yaml
      - name: Create google-services.json
        if: ${{ steps.release.outputs.release_created }}
        run: echo '${{ secrets.GOOGLE_SERVICES_JSON }}' > ./composeApp/google-services.json

      - name: Decode and setup keystore
        if: ${{ steps.release.outputs.release_created }}
        run: |
          echo "${{ secrets.KEYSTORE_BASE64 }}" | base64 -d > ${{ github.workspace }}/debug.keystore

      - name: Set up JDK
        if: ${{ steps.release.outputs.release_created }}
        uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '17'

      - name: Build Android App
        if: ${{ steps.release.outputs.release_created }}
        env:
          KEYSTORE_FILE: ${{ github.workspace }}/debug.keystore
          KEYSTORE_PASSWORD: android
          KEY_ALIAS: androiddebugkey
          KEY_PASSWORD: android
        run: ./gradlew :composeApp:assembleRelease

      - name: Upload Release Artifact
        if: ${{ steps.release.outputs.release_created }}
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        run: gh release upload ${{ steps.release.outputs.tag_name }} ./composeApp/build/outputs/apk/release/composeApp-release.apk

      - name: Firebase App Distribution
        if: ${{ steps.release.outputs.release_created }}
        uses: wzieba/Firebase-Distribution-Github-Action@v1
        with:
          appId: ${{ secrets.FIREBASE_APP_ID }}
          serviceCredentialsFileContent: ${{ secrets.FIREBASE_CREDENTIAL_FILE_CONTENT }}
          testers: "kevinah95@gmail.com"
          file: ./composeApp/build/outputs/apk/release/composeApp-release.apk
          releaseNotes: ${{ steps.release.outputs.body }}
```

## Required GitHub Secrets

Add these in Settings → Secrets and variables → Actions:

- `RELEASE_PLEASE_PAT` – Personal Access Token for release-please
- `GOOGLE_SERVICES_JSON` – The full contents of your `google-services.json`
- `FIREBASE_APP_ID` – Firebase Android App ID
- `FIREBASE_CREDENTIAL_FILE_CONTENT` – Firebase service account JSON
- `KEYSTORE_BASE64` – Base64-encoded debug keystore

`GITHUB_TOKEN` is provided automatically by GitHub Actions.

## Create or reuse the debug keystore locally

- Default location: `~/.android/debug.keystore`
- Defaults: password `android`, alias `androiddebugkey`

Check it exists:

```zsh
ls -la ~/.android/debug.keystore
```

Create if missing:

```zsh
keytool -genkey -v -keystore ~/.android/debug.keystore \
  -storepass android \
  -alias androiddebugkey \
  -keypass android \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -dname "CN=Android Debug,O=Android,C=US"
```

Get SHA-1 (for Firebase Auth/Google Sign-In):

```zsh
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android | grep SHA1
```

Add this SHA-1 to Firebase Console → Project Settings → Your App → SHA certificate fingerprints.

## Encode keystore for CI

On macOS:

```zsh
base64 -i ~/.android/debug.keystore | pbcopy
```

Paste the clipboard value into the `KEYSTORE_BASE64` secret.

Alternatively, to see output in terminal:

```zsh
base64 -i ~/.android/debug.keystore
```

## Verify locally

Build a signed release APK locally:

```zsh
./gradlew :composeApp:assembleRelease
```

The signed file should be at:

```
composeApp/build/outputs/apk/release/composeApp-release.apk
```

Optionally verify signature:

```zsh
jarsigner -verify -verbose -certs composeApp/build/outputs/apk/release/composeApp-release.apk
```

## Notes and limitations

- This setup signs "release" with the debug keystore for alpha/internal testing only.
- For production, create a separate release keystore and signing config and update the workflow to use it.
- If you use Firebase features requiring SHA-1/256, remember to add the debug keystore fingerprints to Firebase.
