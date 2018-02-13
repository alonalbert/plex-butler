#!/bin/sh
#

curl -H 'X-Plex-Token: YzfpN2WCTMq6PJnF3rcx' -H 'Accept: application/json' "$@" | python -mjson.tool
