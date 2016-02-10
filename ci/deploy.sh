#!/usr/bin/env bash

set -e

pushd elpaaso-sandbox-service
  ./mvnw -Dmaven.test.skip=true -e -s settings.xml deploy
popd
