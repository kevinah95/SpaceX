# Changelog

## [1.1.1](https://github.com/kevinah95/SpaceX/compare/v1.1.0...v1.1.1) (2025-10-28)


### 🐛 Bug Fixes

* **iosApp:** Rename bundle identifier and update architecture ([4738024](https://github.com/kevinah95/SpaceX/commit/4738024ddfe647b51d2e4ffc694fff88ed84ff0a))

## [1.1.0](https://github.com/kevinah95/SpaceX/compare/v1.0.2...v1.1.0) (2025-10-28)


### ✨ Features

* Display version name in the top bar ([6deb366](https://github.com/kevinah95/SpaceX/commit/6deb3669a7bba22fcc796461fc3fb5d1bf12a9ce))

## [1.0.2](https://github.com/kevinah95/SpaceX/compare/v1.0.1...v1.0.2) (2025-10-27)


### 🏗️ Build System

* **composeApp:** Compute versionCode from versionName ([70b8935](https://github.com/kevinah95/SpaceX/commit/70b8935dfd7dbedd8889a46768e5ca08cf0811a3))
* **composeApp:** Update version name to 1.0.1 ([dee2dc4](https://github.com/kevinah95/SpaceX/commit/dee2dc47e30c414386b1ffefae0ea841abb93ab2))

## [1.0.1](https://github.com/kevinah95/SpaceX/compare/v1.0.0...v1.0.1) (2025-10-27)


### 📚 Documentation

* Update README and add release signing documentation ([0db7652](https://github.com/kevinah95/SpaceX/commit/0db7652a80152f5559614acb6490e470803144a0))


### 🏗️ Build System

* **composeApp:** Update version name to 1.0.0 ([06921c6](https://github.com/kevinah95/SpaceX/commit/06921c6b544623afbe96d4471a980ffaf54c4517))

## 1.0.0 (2025-10-27)


### ✨ Features

* Add local and remote data sources for RocketLaunch and update repository structure ([3ac484b](https://github.com/kevinah95/SpaceX/commit/3ac484bef560571c21e32fc28bf13fe76d1d77c0))
* Add RocketLaunchViewModel for managing rocket launch data ([338c140](https://github.com/kevinah95/SpaceX/commit/338c140091a5240462e34a2eb36cdb98ceb75d52))
* Implement local and remote data sources for RocketLaunch ([8f22cf1](https://github.com/kevinah95/SpaceX/commit/8f22cf17d5e9b8ddd67ba0202f0eb0a4311de23c))
* Implement local and remote data sources for RocketLaunch and update dependency injection ([0b69874](https://github.com/kevinah95/SpaceX/commit/0b69874761557fffff521c505ca06daf961158d8))
* Initialize SpaceX project with iOS and server components ([5d6f930](https://github.com/kevinah95/SpaceX/commit/5d6f9308de9b914a041e2a4209ff4e4bbbe21b24))
* Integrate Koin for dependency injection and add SQLDelight for local database management ([dfbff05](https://github.com/kevinah95/SpaceX/commit/dfbff050718f543288b4286923db0a7aeaac8cc8))
* Refactor data layer and integrate SQLDelight for local database management ([c9d66f9](https://github.com/kevinah95/SpaceX/commit/c9d66f91b050c61618122d8d9e55acf9541eaa5f))
* Refactor RocketLaunchViewModel to use StateFlow and update UI state management ([3e4b653](https://github.com/kevinah95/SpaceX/commit/3e4b65354d8d432e804a79962a55170b1a3c7985))
* Refactor SpaceXApi and SpaceXSDK to use Flow for fetching latest launches and update RocketLaunchViewModel ([f00a6c4](https://github.com/kevinah95/SpaceX/commit/f00a6c428482237182e715f6fcb88bebf86bea4a))
* **release:** Add release-please configuration files ([4bfba31](https://github.com/kevinah95/SpaceX/commit/4bfba31be53d334bacdc82cf3b759c6cea19347e))
* Update getLaunches method to return Flow and improve UI state management in RocketLaunchViewModel ([0bdcde2](https://github.com/kevinah95/SpaceX/commit/0bdcde2f2601e7650ef4adee4467b97d0b478991))
* Update iOS configuration and integrate SQLDelight for local database management ([4c3aad7](https://github.com/kevinah95/SpaceX/commit/4c3aad7fdc4423e1da2dd0a868c3dc583ad96778))


### 🐛 Bug Fixes

* Remove infinite loop in latestLaunches flow to fetch and emit latest launches once ([bcb7ba7](https://github.com/kevinah95/SpaceX/commit/bcb7ba73f34b9a87214897ea12dbbdc1351f2a3a))
* Update androidx-lifecycle and composeMultiplatform versions ([e2b132f](https://github.com/kevinah95/SpaceX/commit/e2b132f5631b62800ab9f15bb6bf00863488d720))


### 🏗️ Build System

* **android:** Add debug signing configuration ([267b164](https://github.com/kevinah95/SpaceX/commit/267b1642d927711f793a9b9fd3fefef860ce0edc))
