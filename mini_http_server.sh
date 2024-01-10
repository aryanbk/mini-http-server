#!/bin/sh

set -e
(
  cd "$(dirname "$0")"
  mvn -B package -Ddir=/tmp/codecrafters-build-http-server-java
)
exec java -jar /tmp/codecrafters-build-http-server-java/java_http.jar "$@"
