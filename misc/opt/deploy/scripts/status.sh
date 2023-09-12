#!/bin/bash
BIN_DIR=$(dirname $0)
source $BIN_DIR/env.sh

if [ $? -ne 0 ]; then
	exit 1
fi

LINE=$(netstat -ntlp | awk '{print $7,$4}' | grep -E ':'$SERVER_PORT'$')

if [ -n "$LINE" ]; then
	PID=${LINE%/*}
	echo "  > '$APP_NAME' has started -> (PID: $PID, SERVER_PORT: $SERVER_PORT)"
	exit 0
fi

echo "  > '$APP_NAME' has stopped -> (SERVER_PORT: $SERVER_PORT)"
exit 1
