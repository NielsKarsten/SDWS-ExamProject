#!/bin/bash
set -e

bash build.sh

docker image prune -f
docker-compose up -d rabbitMq
sleep 10
docker-compose up -d account-service token-management-service transaction-service rest-api

sleep 20
pushd end-to-end-tests
bash build.sh
popd