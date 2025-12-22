param(
  [string]$Owner = "pradeepbr2003",
  [string]$RepoName = "accentureHoliday",
  [ValidateSet("private","public","internal")]
  [string]$Visibility = "public",
  [switch]$Force
)

$ErrorActionPreference = 'Stop'

Write-Host "==> Onboarding repo to GitHub: $Owner/$RepoName ($Visibility)" -ForegroundColor Cyan

# Ensure running from project root (where pom.xml exists)
if (-not (Test-Path -Path "pom.xml")) {
  throw "Run this script from the project root where pom.xml is located."
}

# Check for Git
if (-not (Get-Command git -ErrorAction SilentlyContinue)) {
  throw "Git is not installed or not on PATH. Install Git and retry."
}

# Initialize git if needed
if (-not (Test-Path .git)) {
  Write-Host "Initializing git repository..."
  git init | Out-Null
}

# Set default branch to main
try { git symbolic-ref --short HEAD | Out-Null } catch { }
$head = (git symbolic-ref --quiet --short HEAD 2>$null)
if (-not $head) {
  git checkout -b main | Out-Null
} elseif ($head -ne 'main') {
  git branch -M main | Out-Null
}

# Add files and commit (skip if no changes unless -Force)
$changes = git status --porcelain
if ($changes -or $Force) {
  git add -A
  if (-not (git rev-parse --quiet --verify HEAD)) {
    git commit -m "chore: initial import" | Out-Null
  } else {
    git commit -m "chore: onboard to GitHub" | Out-Null
  }
} else {
  Write-Host "No changes to commit."
}

# Determine if GitHub CLI is available
$ghAvailable = $false
if (Get-Command gh -ErrorAction SilentlyContinue) { $ghAvailable = $true }

$remoteUrl = "https://github.com/$Owner/$RepoName.git"

if ($ghAvailable) {
  Write-Host "GitHub CLI detected. Authenticating (if needed)..."
  # This will prompt the user if not logged in
  gh auth status 1>$null 2>$null
  if ($LASTEXITCODE -ne 0) {
    gh auth login
  }

  # Create the repo if it does not exist
  $exists = $false
  gh repo view "$Owner/$RepoName" 1>$null 2>$null
  if ($LASTEXITCODE -eq 0) { $exists = $true }

  if (-not $exists) {
    Write-Host "Creating GitHub repository $Owner/$RepoName..."
    $visFlag = "--private"; if ($Visibility -eq 'public') { $visFlag = "--public" } elseif ($Visibility -eq 'internal') { $visFlag = "--internal" }
    gh repo create "$Owner/$RepoName" $visFlag --source . --remote origin --disable-wiki --disable-issues --push
  } else {
    Write-Host "Repository already exists. Setting remote 'origin' and pushing..."
    git remote remove origin 2>$null
    git remote add origin $remoteUrl
    git push -u origin main
  }
}
else {
  Write-Host "GitHub CLI not found. Using manual remote setup..."
  Write-Host "Ensure the repository $Owner/$RepoName exists on GitHub (create it via the web UI)."
  git remote remove origin 2>$null
  git remote add origin $remoteUrl
  git push -u origin main
}

Write-Host "==> Done. Verify: https://github.com/$Owner/$RepoName" -ForegroundColor Green

