import fs from "node:fs";

const version = process.argv[2];

if (!version) {
  console.error("Missing version argument");
  process.exit(1);
}

function updateLine(filePath, regex, replacement) {
  const content = fs.readFileSync(filePath, "utf8");
  const updated = regex.test(content)
    ? content.replace(regex, replacement)
    : `${content.trimEnd()}\n${replacement}\n`;

  fs.writeFileSync(filePath, updated);
}

function incrementCurrentProjectVersion(filePath) {
  const content = fs.readFileSync(filePath, "utf8");
  const regex = /^CURRENT_PROJECT_VERSION=(\d+)$/m;
  const match = content.match(regex);

  const nextValue = match ? Number.parseInt(match[1], 10) + 1 : 1;
  const replacement = `CURRENT_PROJECT_VERSION=${nextValue}`;
  const updated = match
    ? content.replace(regex, replacement)
    : `${content.trimEnd()}\n${replacement}\n`;

  fs.writeFileSync(filePath, updated);
}

updateLine("gradle.properties", /^app\.version=.*/m, `app.version=${version}`);
updateLine(
  "iosApp/Configuration/Config.xcconfig",
  /^MARKETING_VERSION=.*/m,
  `MARKETING_VERSION=${version}`,
);
incrementCurrentProjectVersion("iosApp/Configuration/Config.xcconfig");
