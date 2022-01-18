#!/bin/bash
set -e
mvn clean install
docker-compose build rabbitMq
docker-compose up -d

