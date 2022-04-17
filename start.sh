#!/bin/bash

if [[ $(uname -s) == CYGWIN* ]];then
  set -a
    . .env
  set +a
  ./mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local
else
  # shellcheck disable=SC2046
  # shellcheck disable=SC2002
  export $(cat .env | xargs)
  ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
fi
