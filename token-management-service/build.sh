#!/bin/bash
set -e
mvn clean package
docker-compose build token-management-service 
docker-compose up -d