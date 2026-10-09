#!/system/bin/sh
# UnlimitedProfilesOverlay WebUI backend
MODDIR="${0%/*}"
. "$MODDIR/config.sh"
: ${LIMIT:=99}

json_escape(){ sed 's/\\/\\\\/g; s/"/\\"/g' | tr -d '\r\n'; }

do_status(){
  FW=$(getprop fw.max_users)
  DS=$(dumpsys user 2>/dev/null | grep -m1 'Max users' | sed 's/^ *//' | json_escape)
  L1=$(cmd overlay lookup --user 0 android android:integer/config_multiuserMaximumUsers 2>/dev/null)
  L2=$(cmd overlay lookup --user 0 android android:integer/config_multiuserMaxRunningUsers 2>/dev/null)
  ULC=$(pm list users 2>/dev/null | grep -c UserInfo)
  ULIST=$(pm list users 2>/dev/null | grep -o 'UserInfo{[0-9]*:[^:]*' | sed 's/UserInfo{//' | json_escape | tr '\n' ',' | sed 's/,$//')
  OVS=$(cmd overlay list --user 0 android 2>/dev/null | grep -E 'unlimitedprofiles|shell:max' | json_escape | tr '\n' ';' | sed 's/;$//')
  echo "{\"limit\":$LIMIT,\"fw\":\"$FW\",\"ds\":\"$DS\",\"l1\":\"$L1\",\"l2\":\"$L2\",\"user_count\":$ULC,\"users\":\"$ULIST\",\"ovs\":\"$OVS\"}"
}

do_set(){ # N
  case "$1" in ''|*[!0-9]*) echo ERR; exit 1 ;; esac
  if [ "$1" -lt 1 ] || [ "$1" -gt 999 ]; then echo ERR; exit 1; fi
  sed -i "s|^LIMIT=.*|LIMIT=$1|" "$MODDIR/config.sh" || { echo ERR; exit 1; }
  LIMIT=$1
  do_apply
}

do_apply(){
  setprop fw.max_users "$LIMIT"
  cmd overlay fabricate --target android --name maxusers99 \
    android:integer/config_multiuserMaximumUsers 0x10 "$LIMIT" >/dev/null 2>&1
  cmd overlay enable --user 0 com.android.shell:maxusers99 >/dev/null 2>&1
  cmd overlay fabricate --target android --name maxrunning99 \
    android:integer/config_multiuserMaxRunningUsers 0x10 "$LIMIT" >/dev/null 2>&1
  cmd overlay enable --user 0 com.android.shell:maxrunning99 >/dev/null 2>&1
  echo OK
}

case "$1" in
  status) do_status ;;
  set) do_set "$2" ;;
  apply) do_apply ;;
  *) echo "usage: webui_ctl.sh status|set <N>|apply" ;;
esac
