#!/bin/bash
set -e
mvn clean package
docker-compose build rest-api-service
