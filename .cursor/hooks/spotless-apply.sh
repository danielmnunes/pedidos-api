#!/usr/bin/env bash
# Roda o Spotless quando o agente ou o Tab editam um arquivo Java.

input=$(cat)
file_path=$(printf '%s' "$input" | python3 -c 'import json,sys
raw = sys.stdin.read()
try:
    data = json.loads(raw) if raw.strip() else {}
except json.JSONDecodeError:
    data = {}
if not isinstance(data, dict):
    data = {}
tool_input = data.get("tool_input") or data.get("toolInput") or {}
if isinstance(tool_input, str):
    try:
        tool_input = json.loads(tool_input)
    except json.JSONDecodeError:
        tool_input = {}
if not isinstance(tool_input, dict):
    tool_input = {}
print(data.get("file_path") or data.get("filePath") or tool_input.get("file_path") or tool_input.get("filePath") or tool_input.get("path") or "")')

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
