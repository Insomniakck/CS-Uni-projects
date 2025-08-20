#!/bin/sh

# Set default Bloom filter arguments if none provided
DEFAULT_ARGS="8 1 2"

# Use SERVER_ARGS if defined, otherwise fallback to default
ARGS=${SERVER_ARGS:-$DEFAULT_ARGS}

echo "Launching server with Bloomfilter arguments: $ARGS"

exec ./server $ARGS