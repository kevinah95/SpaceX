// swift-tools-version: 5.9
import PackageDescription
let package = Package(
  name: "_internal_linkage_SwiftPMImport",
  platforms: [
    .iOS("15.0")
  ],
  products: [
    .library(
      name: "_internal_linkage_SwiftPMImport",
      type: .none,
      targets: ["_internal_linkage_SwiftPMImport"]
    )
  ],
  dependencies: [
    .package(
      url: "https://github.com/firebase/firebase-ios-sdk.git",
      from: "12.10.0",
    ),
    .package(path: "subpackages/_shared")
  ],
  targets: [
    .target(
      name: "_internal_linkage_SwiftPMImport",
      dependencies: [
        .product(
          name: "FirebaseCore",
          package: "firebase-ios-sdk",
        ),
        .product(
          name: "FirebaseAnalytics",
          package: "firebase-ios-sdk",
        ),
        .product(
          name: "FirebaseCrashlytics",
          package: "firebase-ios-sdk",
        ),
        .product(name: "_shared", package: "_shared")
      ]
    )
  ]
)
