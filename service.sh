#!/system/bin/sh
# Runs at late_boot. Re-apply user-configured limit + labels.
MODDIR="${0%/*}"
. "$MODDIR/config.sh"
: ${LIMIT:=99}
/system/bin/setprop fw.max_users "$LIMIT" 2>/dev/null

# SELinux labels so PackageManager can parse the vfs-presented overlay apks
chcon u:object_r:system_file:s0 \
  "$MODDIR/system/product/overlay/UnlimitedProfilesOverlay.apk" \
  "$MODDIR/system/system_ext/overlay/UnlimitedProfilesOverlay.apk" 2>/dev/null
chcon u:object_r:vendor_overlay_file:s0 \
  "$MODDIR/system/vendor/overlay/UnlimitedProfilesOverlay.apk" \
  "$MODDIR/system/odm/overlay/UnlimitedProfilesOverlay.apk" 2>/dev/null

# Runtime fabrications carry the live LIMIT (takes precedence over static overlay)
cmd overlay fabricate --target android --name maxusers99 \
  android:integer/config_multiuserMaximumUsers 0x10 "$LIMIT" >/dev/null 2>&1
cmd overlay enable --user 0 com.android.shell:maxusers99 >/dev/null 2>&1
cmd overlay fabricate --target android --name maxrunning99 \
  android:integer/config_multiuserMaxRunningUsers 0x10 "$LIMIT" >/dev/null 2>&1
cmd overlay enable --user 0 com.android.shell:maxrunning99 >/dev/null 2>&1
