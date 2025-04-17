#!/bin/bash

until cqlsh -e "describe keyspaces" > /dev/null 2>&1; do
  echo "waiting for cassandra to start..."
  sleep 5
done

echo "cassandra started, initializing schema..."
cqlsh -f ./setup/init.cql

wait
