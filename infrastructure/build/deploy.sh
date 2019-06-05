#!/usr/bin/env bash
set -e

SCRIPT_PATH="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# source own helper functions
if [ -f $SCRIPT_PATH/bash_functions.sh ]; then
  source $SCRIPT_PATH/bash_functions.sh
else
  echo "ERROR Could not load '$SCRIPT_PATH/bash_functions.sh'" 1>&2
  exit 1
fi

if [ $# -ne 1 ]; then
  echo 'Usage: deploy.sh <target-tag>' 1>&2
  echo 'For example: deploy.sh latest' 1>&2
  exit 2
fi

# push latest to openshift
if [ "$1" == "latest" ]; then
  # $OPENSHIFT_NAMESPACE_STATIC_SANDBOX_DEV
  echo $OPENSHIFT_DEPLOY_PASSWORD_STATIC_SANDBOX_DEV | docker login --password-stdin -u openshift $OPENSHIFT_REGISTRY

  docker tag adorsys/$PSD2_SANDBOX_IMAGE:latest $OPENSHIFT_REGISTRY/$OPENSHIFT_NAMESPACE_STATIC_SANDBOX_DEV/$PSD2_SANDBOX_IMAGE:latest
  docker push $OPENSHIFT_REGISTRY/$OPENSHIFT_NAMESPACE_STATIC_SANDBOX_DEV/$PSD2_SANDBOX_IMAGE:latest
  docker tag adorsys/$SSL_PROXY_IMAGE:latest $OPENSHIFT_REGISTRY/$OPENSHIFT_NAMESPACE_STATIC_SANDBOX_DEV/$SSL_PROXY_IMAGE:latest
  docker push $OPENSHIFT_REGISTRY/$OPENSHIFT_NAMESPACE_STATIC_SANDBOX_DEV/$SSL_PROXY_IMAGE:latest

  docker logout $OPENSHIFT_REGISTRY

  # $OPENSHIFT_NAMESPACE_DYNAMIC_SANDBOX_DEV
  echo $OPENSHIFT_DEPLOY_PASSWORD_DYNAMIC_SANDBOX_DEV | docker login --password-stdin -u openshift $OPENSHIFT_REGISTRY

  docker tag adorsys/$SSL_PROXY_IMAGE:latest $OPENSHIFT_REGISTRY/$OPENSHIFT_NAMESPACE_DYNAMIC_SANDBOX_DEV/$SSL_PROXY_IMAGE:latest
  docker push $OPENSHIFT_REGISTRY/$OPENSHIFT_NAMESPACE_DYNAMIC_SANDBOX_DEV/$SSL_PROXY_IMAGE:latest

  docker logout $OPENSHIFT_REGISTRY

  # $OPENSHIFT_NAMESPACE_DYNAMIC_SANDBOX_INTEG
  echo $OPENSHIFT_DEPLOY_PASSWORD_DYNAMIC_SANDBOX_INTEG | docker login --password-stdin -u openshift $OPENSHIFT_REGISTRY

  docker tag adorsys/$SSL_PROXY_IMAGE:latest $OPENSHIFT_REGISTRY/$OPENSHIFT_NAMESPACE_DYNAMIC_SANDBOX_INTEG/$SSL_PROXY_IMAGE:latest
  docker push $OPENSHIFT_REGISTRY/$OPENSHIFT_NAMESPACE_DYNAMIC_SANDBOX_INTEG/$SSL_PROXY_IMAGE:latest

  docker logout $OPENSHIFT_REGISTRY

  #$OPENSHIFT_NAMESPACE_DYNAMIC_SANDBOX_DEMO
  echo $OPENSHIFT_DEPLOY_PASSWORD_DYNAMIC_SANDBOX_DEMO | docker login --password-stdin -u openshift $OPENSHIFT_REGISTRY

  docker tag adorsys/$SSL_PROXY_IMAGE:latest $OPENSHIFT_REGISTRY/$OPENSHIFT_NAMESPACE_DYNAMIC_SANDBOX_DEMO/$SSL_PROXY_IMAGE:latest
  docker push $OPENSHIFT_REGISTRY/$OPENSHIFT_NAMESPACE_DYNAMIC_SANDBOX_DEMO/$SSL_PROXY_IMAGE:latest

  docker logout $OPENSHIFT_REGISTRY

# push tags to dockerhub
elif checkSemver $(git2dockerTag $1); then
  echo $DOCKERHUB_DEPLOY_PASSWORD | docker login --password-stdin -u $DOCKERHUB_DEPLOY_USER

  docker tag adorsys/$PSD2_SANDBOX_IMAGE:latest adorsys/$PSD2_SANDBOX_IMAGE:$(git2dockerTag $1)
  docker push adorsys/$PSD2_SANDBOX_IMAGE:$(git2dockerTag $1)
  docker tag adorsys/$SSL_PROXY_IMAGE:latest adorsys/$SSL_PROXY_IMAGE:$(git2dockerTag $1)
  docker push adorsys/$SSL_PROXY_IMAGE:$(git2dockerTag $1)

  docker logout
# but nothing else
else
  echo "ERROR We only deploy 'latest' or release tags ('v1.2.3') but got '$1'" 1>&2
  exit 1
fi
