#!/bin/bash

function git2dockerTag {
  case $1 in
    v*)
      stripVersionPrefix $1
      ;;
    *)
      $1
      ;;
  esac
}

# remove leading 'v'
function stripVersionPrefix {
  echo ${1#*v};
}

# Returns snapshot version as text
function snapshotVersion {
  echo "$1-SNAPSHOT"
}

# append 'v' to version
function releaseTag {
  echo "v$1"
}
