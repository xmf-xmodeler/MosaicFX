param(
    [string]$InnoSetupPath
)

# Get current path
$ScriptDirectory = Split-Path -Parent $MyInvocation.MyCommand.Path
# Define name of SetupScript
$SetupScript = "XModelerToSetup.iss"
$ScriptPath = Join-Path -Path $ScriptDirectory -ChildPath $SetupScript

# Check if path to Inno Setup is valid
if (-not (Test-Path $InnoSetupPath)) {
    Write-Host "Fehler: Der Pfad zu Inno Setup '$InnoSetupPath' existiert nicht."
    exit
}

# Check if scriptname is valid
if (-not (Test-Path $ScriptPath )) {
    Write-Host "Fehler: Das Skript '$ScriptFileName' existiert nicht."
    exit
}

Set-Location $InnoSetupPath
# execute Inno Setup
& ".\ISCC.exe" $ScriptPath