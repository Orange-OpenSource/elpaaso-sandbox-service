#!/usr/bin/env bash

set -e

pushd elpaaso-sandbox-service
  ./mvnw package -e -s settings.xml
popd