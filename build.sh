#!/bin/bash
set -e

pushd messaging-utilities-3.3
./build.sh
popd

pushd account-service
./build.sh
popd

pushd token-management-service
./build.sh
popd

pushd transaction-service
./build.sh
popd

pushd rest-api
./build.sh
popd

