#!/bin/bash
set -e
mvn clean install
docker-compose build account-service
docker-compose up -d