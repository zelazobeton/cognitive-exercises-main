#!/bin/sh

# Abort on any error (including if wait-for-it fails).
set -e

# Wait for the backend to be up, if we know where it is.
if [ -n "$DB_HOST" ]; then
  /usr/src/app/wait-for-it.sh "$DB_HOST:${DB_HOST:-6000}"
fi

# Run the main container command.
exec java -Dspring.profiles.active=$SPRING_PROFILE -Djava.security.egd=file:/dev/./urandom -jar ./cognitive-exercises-web.jar
