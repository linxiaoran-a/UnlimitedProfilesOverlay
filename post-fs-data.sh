#!/system/bin/sh
# Runs BEFORE zygote. Raise total user limit early.
MODDIR="${0%/*}"
. "$MODDIR/config.sh"
: ${LIMIT:=99}
/system/bin/setprop fw.max_users "$LIMIT" 2>/dev/null
