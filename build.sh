#!/bin/bash
set -e

pushd messaging-utilities-3.3
bash build.sh
popd

pushd account-service
bash build.sh
popd

pushd token-management-service
bash build.sh
popd

pushd transaction-service
bash build.sh
popd

pushd rest-api
bash build.sh
popd

