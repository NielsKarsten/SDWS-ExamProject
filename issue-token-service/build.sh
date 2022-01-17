#!/bin/bash
set -e
mvn clean package
docker-compose build issue-token-service
