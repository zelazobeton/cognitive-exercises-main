#!/bin/sh

# Run the main container command.
exec java -Dspring.profiles.active=dev-mysql -Djava.security.egd=file:/dev/./urandom -jar ./cognitive-exercises-web.jar

