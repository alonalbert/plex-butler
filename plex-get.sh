#!/bin/sh
#

curl -H "X-Plex-Token: $1" -H 'Accept: application/json' "$2" | python -mjson.tool
