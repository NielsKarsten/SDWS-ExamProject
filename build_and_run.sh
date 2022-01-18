#!/bin/bash
set -e

./build.sh

docker image prune -f
docker-compose up -d rabbitMq
sleep 10
docker-compose up -d account-service token-management-service transaction-service rest-api
