#!/bin/bash
set -e
mvn clean package
docker-compose build account-service
docker-compose up -d