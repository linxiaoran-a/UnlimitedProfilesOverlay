# Pack module zip from repo files (repo-relative, requires JDK jar on PATH or JAVA_HOME)
$ErrorActionPreference='Stop'
$here=Split-Path -Parent $MyInvocation.MyCommand.Path
$out="$here\build\unlimited_profiles_overlay"
if(Test-Path "$here\build"){Remove-Item "$here\build" -Recurse -Force}
New-Item -ItemType Directory -Force -Path "$out\system\product\overlay","$out\system\system_ext\overlay","$out\system\vendor\overlay","$out\system\odm\overlay" | Out-Null
foreach($p in 'product','system_ext','vendor','odm'){
  Copy-Item "$here\UnlimitedProfilesOverlay.apk" "$out\system\$p\overlay\UnlimitedProfilesOverlay.apk" -Force
}
foreach($f in 'module.prop','config.sh','post-fs-data.sh','service.sh','webui_ctl.sh'){
  Copy-Item "$here\$f" "$out\$f" -Force
}
New-Item -ItemType Directory -Force -Path "$out\webroot" | Out-Null
Copy-Item "$here\webroot\index.html" "$out\webroot\index.html" -Force
$jar = if($env:JAVA_HOME){"$env:JAVA_HOME\bin\jar.exe"}else{'jar.exe'}
$ver = (Select-String -Path "$here\module.prop" -Pattern '^version=(.+)$').Matches[0].Groups[1].Value
$zip = "$here\UnlimitedProfilesOverlay-KSU-$ver.zip"
if(Test-Path $zip){Remove-Item $zip -Force}
& $jar cfM $zip -C $out .
Write-Host "packed -> $zip"
