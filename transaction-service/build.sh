#!/bin/bash
set -e
mvn clean package
docker-compose build transaction-service
docker-compose up -d