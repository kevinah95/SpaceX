#!/usr/bin/env node

import { readFileSync, writeFileSync } from "node:fs";
import { resolve } from "node:path";

const [nextVersion] = process.argv.slice(2);

if (!nextVersion) {
  console.error("Usage: node scripts/sync-version.mjs <version>");
  process.exit(1);
}

const rootDir = resolve(import.meta.dirname, "..");
const versionInfo = parseVersion(nextVersion);

updateKeyValueFile(resolve(rootDir, "gradle.properties"), {
  "app.version": nextVersion,
});

updateKeyValueFile(resolve(rootDir, "iosApp/Configuration/Config.xcconfig"), {
  CURRENT_PROJECT_VERSION: String(versionInfo.versionCode),
  MARKETING_VERSION: versionInfo.marketingVersion,
  APP_RELEASE_VERSION: nextVersion,
});

console.log(
    `Synchronized release version ${nextVersion} (marketing ${versionInfo.marketingVersion}, build ${versionInfo.versionCode}).`,
);

function parseVersion(version) {
  const match = version.match(
      /^(?<major>\d+)\.(?<minor>\d+)\.(?<patch>\d+)(?:-(?<label>[0-9A-Za-z-]+)(?:\.(?<pre>\d+))?)?$/,
  );

  if (!match?.groups) {
    throw new Error(`Unsupported version format: ${version}`);
  }

  const major = Number.parseInt(match.groups.major, 10);
  const minor = Number.parseInt(match.groups.minor, 10);
  const patch = Number.parseInt(match.groups.patch, 10);
  const label = match.groups.label;
  const preReleaseNumber = match.groups.pre ? Number.parseInt(match.groups.pre, 10) : 0;

  if (major > 214 || minor > 99 || patch > 99) {
    throw new Error("Version component out of range. Max values: major (214), minor (99), patch (99).");
  }

  if (preReleaseNumber > 99) {
    throw new Error("Pre-release number cannot exceed 99.");
  }

  const stage =
      label == null
          ? 5
          : {
              alpha: 1,
              beta: 2,
              rc: 3,
            }[label] ?? 4;

  return {
    marketingVersion: `${major}.${minor}.${patch}`,
    versionCode:
        major * 10_000_000 +
        minor * 100_000 +
        patch * 1_000 +
        stage * 100 +
        preReleaseNumber,
  };
}

function updateKeyValueFile(filePath, updates) {
  let content = readFileSync(filePath, "utf8");

  for (const [key, value] of Object.entries(updates)) {
    const pattern = new RegExp(`^${escapeRegExp(key)}=.*$`, "m");

    if (pattern.test(content)) {
      content = content.replace(pattern, `${key}=${value}`);
      continue;
    }

    content = `${key}=${value}\n${content}`;
  }

  writeFileSync(filePath, content);
}

function escapeRegExp(value) {
  return value.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
}
