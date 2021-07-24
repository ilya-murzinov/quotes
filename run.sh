#!/bin/sh

./gradlew build
java -jar ext/partner-service-1.0-all.jar &
PARTNER_PID=$!
java -jar build/libs/quotes-shadowJar.jar

function cleanup {
  echo "Cleaning up"
  kill $PARTNER_PID
}

trap cleanup EXIT