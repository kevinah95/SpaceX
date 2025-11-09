const branch = process.env.GITHUB_REF_NAME;
const flavor = branch === 'prod' ? 'prod' : 'alpha';
const apkPath = `./composeApp/build/outputs/apk/${flavor}/release/composeApp-${flavor}-release.apk`;

module.exports = {
  branches: ["prod", {name: "alpha", prerelease: true}],
  plugins: [
    "@semantic-release/commit-analyzer",
    "@semantic-release/release-notes-generator",
    [
      "@semantic-release/changelog",
      {
        "changelogFile": "CHANGELOG.md"
      }
    ],
    [
      "@semantic-release/git",
      {
        "assets": ["CHANGELOG.md"],
        "message": "chore(release): ${nextRelease.version}\n\n${nextRelease.notes}"
      }
    ],
    [
      "@semantic-release/github",
      {
        "assets": [
          { "path": apkPath, "label": "Android APK" }
        ]
      }
    ]
  ]
};
