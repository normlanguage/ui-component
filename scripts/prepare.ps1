param(
    [string]$ThemeRoot = (Join-Path (Split-Path $PSScriptRoot -Parent) '../theme'),
    [string]$JdkRoot = (Join-Path (Split-Path $PSScriptRoot -Parent) '../jdk-base')
)
$ErrorActionPreference = 'Stop'
$root = Split-Path $PSScriptRoot -Parent
& (Join-Path $root 'gradlew.bat') -p $root publish normDependencies --console=plain
if ($LASTEXITCODE -ne 0) { throw 'Java component build failed' }
$mavenCache = Join-Path $root '.norm-home/.norm/cache/maven'
New-Item -ItemType Directory -Force $mavenCache | Out-Null
Copy-Item -Path (Join-Path $root 'build/repository/*') -Destination $mavenCache -Recurse -Force
Copy-Item -Path (Join-Path $ThemeRoot 'build/repository/*') -Destination $mavenCache -Recurse -Force
& (Join-Path $PSScriptRoot 'norm.ps1') package (Join-Path $ThemeRoot 'theme') --output (Join-Path $root '.norm-home/.norm/cache/packages')
if ($LASTEXITCODE -ne 0) { throw 'Theme module packaging failed; build the theme repository first' }
& (Join-Path $PSScriptRoot 'norm.ps1') package (Join-Path $JdkRoot 'jdk/base') --output (Join-Path $root '.norm-home/.norm/cache/packages')
if ($LASTEXITCODE -ne 0) { throw 'JDK base module packaging failed' }
$module = Join-Path $root 'ui/component/module.norm'
$body = [regex]::Replace((Get-Content -Raw $module), '(artifact: "ui-component", version: "1"), resolution: sha256\("[a-f0-9]+"\)', '$1')
Set-Content -LiteralPath $module -Value $body -NoNewline
& (Join-Path $PSScriptRoot 'norm.ps1') resolve (Join-Path $root 'ui/component')
if ($LASTEXITCODE -ne 0) { throw 'Component module resolution failed' }
