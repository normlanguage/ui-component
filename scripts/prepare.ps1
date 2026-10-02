param(
    [string]$UiRoot = (Join-Path (Split-Path $PSScriptRoot -Parent) '../ui'),
    [string]$UiFxRoot = (Join-Path (Split-Path $PSScriptRoot -Parent) '../ui-fx'),
    [string]$ThemeRoot = (Join-Path (Split-Path $PSScriptRoot -Parent) '../theme'),
    [string]$JdkRoot = (Join-Path (Split-Path $PSScriptRoot -Parent) '../jdk-base')
)
$ErrorActionPreference = 'Stop'
$root = Split-Path $PSScriptRoot -Parent
$ui = (Resolve-Path -LiteralPath $UiRoot).Path
$uiFx = (Resolve-Path -LiteralPath $UiFxRoot).Path
$theme = (Resolve-Path -LiteralPath $ThemeRoot).Path
$jdk = (Resolve-Path -LiteralPath $JdkRoot).Path
$norm = Join-Path $PSScriptRoot 'norm.ps1'
$packages = Join-Path $root '.norm-home/.norm/cache/packages'
$maven = Join-Path $root '.norm-home/.norm/cache/maven'
New-Item -ItemType Directory -Force $packages, $maven | Out-Null

function Invoke-Norm {
    param([string[]]$Arguments)
    & $norm @Arguments
    if ($LASTEXITCODE -ne 0) { throw "Norm command failed: $($Arguments -join ' ')" }
}

$themeRepository = Join-Path $theme 'build/repository'
if (!(Test-Path -LiteralPath (Join-Path $themeRepository 'dev/normlanguage/theme-colors/1/theme-colors-1.jar'))) {
    throw 'Theme color artifact is missing; build the theme repository first'
}
Copy-Item -Path (Join-Path $themeRepository '*') -Destination $maven -Recurse -Force

Invoke-Norm -Arguments @('package', (Join-Path $jdk 'jdk/base'), '--output', $packages)
Invoke-Norm -Arguments @('package', (Join-Path $theme 'theme'), '--output', $packages)
Invoke-Norm -Arguments @('package', (Join-Path $uiFx 'ui/fx'), '--output', $packages)
Invoke-Norm -Arguments @('package', (Join-Path $ui 'ui'), '--output', $packages)

& (Join-Path $root 'gradlew.bat') -p $root publish normDependencies --console=plain
if ($LASTEXITCODE -ne 0) { throw 'Java component artifact build failed' }
Copy-Item -Path (Join-Path $root 'build/repository/*') -Destination $maven -Recurse -Force

$bindingModule = Join-Path $root 'ui/component/fx/module.norm'
$source = Get-Content -LiteralPath $bindingModule -Raw
$withoutPin = [regex]::Replace($source,
    '(artifact: "ui-component", version: "2"), resolution: sha256\("[a-f0-9]+"\)', '$1')
if ($withoutPin -eq $source -and $source -notmatch 'artifact: "ui-component", version: "2"') {
    throw 'Component Java artifact declaration is missing'
}
if ($withoutPin -ne $source) { Set-Content -LiteralPath $bindingModule -Value $withoutPin -NoNewline }
Invoke-Norm -Arguments @('resolve', (Join-Path $root 'ui/component/fx'))
Invoke-Norm -Arguments @('package', (Join-Path $root 'ui/component/fx'), '--output', $packages)
Invoke-Norm -Arguments @('package', (Join-Path $root 'ui/component'), '--output', $packages)
Invoke-Norm -Arguments @('check', (Join-Path $root 'samples/gallery'))
