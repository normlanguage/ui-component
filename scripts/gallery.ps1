param(
    [switch]$Verify,
    [string]$ThemeRoot = (Join-Path (Split-Path $PSScriptRoot -Parent) '../theme'),
    [string]$JdkRoot = (Join-Path (Split-Path $PSScriptRoot -Parent) '../jdk-base')
)
$ErrorActionPreference = 'Stop'
$root = Split-Path $PSScriptRoot -Parent
& (Join-Path $PSScriptRoot 'prepare.ps1') -ThemeRoot $ThemeRoot -JdkRoot $JdkRoot
& (Join-Path $PSScriptRoot 'norm.ps1') package (Join-Path $root 'ui/component') --output (Join-Path $root '.norm-home/.norm/cache/packages')
if ($LASTEXITCODE -ne 0) { throw 'Component packaging failed' }
$sample = Join-Path $root 'samples/gallery'
$module = Join-Path $sample 'module.norm'
$body = [regex]::Replace((Get-Content -Raw $module), '(artifact: "ui-component", version: "1"), resolution: sha256\("[a-f0-9]+"\)', '$1')
Set-Content -LiteralPath $module -Value $body -NoNewline
& (Join-Path $PSScriptRoot 'norm.ps1') resolve $sample
if ($LASTEXITCODE -ne 0) { throw 'Gallery resolution failed' }
Push-Location $root
try {
    if ($Verify) {
        & (Join-Path $PSScriptRoot 'norm.ps1') test $sample --filter gallery.exportThemeFixtures --format json
        if ($LASTEXITCODE -ne 0) { throw 'Theme fixture export failed' }
        & (Join-Path $PSScriptRoot 'norm.ps1') test $sample --filter gallery.liveThemePreservesInputAndLocalScope --format json
        if ($LASTEXITCODE -ne 0) { throw 'Live theme integration failed' }
        & (Join-Path $PSScriptRoot 'norm.ps1') test $sample --filter gallery.platformValuesCrossComponentBoundary --format json
        if ($LASTEXITCODE -ne 0) { throw 'JDK value integration failed' }
        & (Join-Path $PSScriptRoot 'norm.ps1') test $sample --filter gallery.uploadAcceptsNormTaskAndPath --format json
        if ($LASTEXITCODE -ne 0) { throw 'Norm asynchronous upload integration failed' }
        & (Join-Path $PSScriptRoot 'norm.ps1') test $sample --filter gallery.normListsCreateCollectionControls --format json
        if ($LASTEXITCODE -ne 0) { throw 'Norm collection integration failed' }
        & (Join-Path $root 'gradlew.bat') -p $root themeRenderingTest -PtestSource=ThemeRenderingTest --console=plain
    } else {
        & (Join-Path $PSScriptRoot 'norm.ps1') run $sample
    }
    $result = $LASTEXITCODE
} finally { Pop-Location }
exit $result
