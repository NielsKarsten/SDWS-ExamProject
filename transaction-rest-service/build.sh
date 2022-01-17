#!/bin/bash
set -e
mvn clean package
docker-compose build transaction-rest-service
