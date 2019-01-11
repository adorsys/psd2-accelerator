#!/usr/bin/env bash
set -e

SCRIPT_PATH="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
CALL_SITE=`pwd`
REPO_ROOT=`git rev-parse --show-toplevel`

function major_minor_only {
  local v=$1
  echo "${v%.*}"
}

function setAppVersion {
  (cd service && mvn versions:set -DnewVersion=$1)
  (cd ui && npm version $1)
}



if [ $# -ne 2 ]; then
  echo 'Usage: release.sh <release-version> <next-snapshot-version>' 1>&2
  echo 'For example: release.sh 0.1.0 0.2.0' 1>&2
  exit 2
fi

RELEASE_VERSION=$1
NEXT_VERSION=$2
CURRENT_BRANCH=`git rev-parse --abbrev-ref HEAD`

# source own helper functions
if [ -f $SCRIPT_PATH/bash_functions.sh ]; then
  source $SCRIPT_PATH/bash_functions.sh
else
  echo "ERROR Could not load '$SCRIPT_PATH/bash_functions.sh'" 1>&2
  exit 1
fi


if ! [[ $REPO_ROOT -ef $CALL_SITE ]]; then
  echo "This script must be run from your repo root (which is '$REPO_ROOT')."  1>&2
  exit 1
fi

if ! checkSemver $RELEASE_VERSION ; then
  echo "ERROR RELEASE_VERSION='$RELEASE_VERSION' must follow the semver format." 1>&2
  exit 1
fi

if ! checkSemver $NEXT_VERSION ; then
  echo "ERROR NEXT_VERSION='$NEXT_VERSION' must follow the semver format." 1>&2
  exit 1
fi

# semver.sh returns NEXT_VERSION if OK, else nothing
if [[ ! $($SCRIPT_PATH/sh-semver/semver.sh -r ">$RELEASE_VERSION" $NEXT_VERSION) ]]; then
  echo "ERROR NEXT_VERSION='$NEXT_VERSION' must be greater than RELEASE_VERSION='$RELEASE_VERSION'" 1>&2
  exit 1
fi

if [ ! "$CURRENT_BRANCH" = "master" ]; then
  echo "ERROR You must check out 'master' before calling the release script." 1>&2
  exit 1
fi

if ! git diff-index --quiet HEAD --
then
  echo "ERROR Can't release with local modifications. See 'git status' for more details." 1>&2
  exit 1
fi

setAppVersion $RELEASE_VERSION

if ! git diff-files --quiet --ignore-submodules --; then
  echo "DEBUG Create version bump commit ($RELEASE_VERSION)" 1>&2
  git commit -am "Bump version to $RELEASE_VERSION"
  echo "DEBUG Create release tag $(releaseTag $RELEASE_VERSION)" 1>&2
  git tag -a -m "Release version $RELEASE_VERSION" $(releaseTag $RELEASE_VERSION)
else
  echo "ERROR Expected modified package.json/pom.xml but there are no local changes." 1>&2
  exit 1
fi

NEXT_SNAPSHOT_VERSION=`snapshotVersion "$NEXT_VERSION"`
setAppVersion "$NEXT_SNAPSHOT_VERSION"

if ! git diff-files --quiet --ignore-submodules --; then
  echo "DEBUG Create version bump commit ($NEXT_SNAPSHOT_VERSION)" 1>&2
  git commit -am "Bump version to $NEXT_SNAPSHOT_VERSION"
else
  echo "ERROR Expected modified package.json/pom.xml but there are no local changes." 1>&2
  exit 1
fi

echo ''
echo ''
echo 'Release created locally on this machine. To publish use'
echo '  $ git push --atomic --follow-tags'
echo ''
echo "Don't forget to merge this release branch to develop and"
echo "develop to master after the release is done. Good luck."
