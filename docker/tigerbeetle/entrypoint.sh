#!/bin/sh
set -e

if [ ! -f /data/0_0.tigerbeetle ]; then
  echo "Creating TigerBeetle files..."
  /tigerbeetle format --cluster=0 --replica=0 --replica-count=1 /data/0_0.tigerbeetle
fi

echo "Starting TigerBeetle..."
exec /tigerbeetle start --addresses=0.0.0.0:3000 /data/0_0.tigerbeetle
