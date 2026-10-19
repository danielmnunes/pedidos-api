#!/usr/bin/env bash
# Roda o Spotless quando o agente ou o Tab editam um arquivo Java.

input=$(cat)
file_path=$(printf '%s' "$input" | python3 -c 'import json,sys
raw = sys.stdin.read()
try:
    data = json.loads(raw) if raw.strip() else {}
except json.JSONDecodeError:
    data = {}
print(data.get("file_path") or "")')

case "$file_path" in
*.java) ;;
*)
	printf '%s\n' '{}'
	exit 0
	;;
esac

root=$(cd "$(dirname "$0")/../.." && pwd)
case "$file_path" in
"$root"/*) ;;
*)
	printf '%s\n' '{}'
	exit 0
	;;
esac

cd "$root"
./gradlew spotlessApply
status=$?
printf '%s\n' '{}'
exit "$status"
