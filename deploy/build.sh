#!/bin/bash
set -e

sufix=$1
VERBOSE=false
[[ "$2" == "-v" || "$2" == "--verbose" ]] && VERBOSE=true

configFile="deploy/config-$sufix.txt"
REGISTRY="registry.gitlab.com/golden-gd/servers-files"
SEPARATOR="------------------------------------------------------------"

NC="\033[0m"        # RESET
BLUE="\033[1;34m"   # INFO
GREEN="\033[1;32m"  # SUCCESS
YELLOW="\033[1;33m" # WARN
RED="\033[1;31m"    # ERROR

log() {
  local level=$1 message=$2
  case "$level" in
    info) echo -e "${BLUE}[INFO] $message${NC}" ;;
    success) echo -e "${GREEN}[SUCCESS] $message${NC}" ;;
    warn) echo -e "${YELLOW}[WARN] $message${NC}" ;;
    error) echo -e "${RED}[ERROR] $message${NC}" ;;
    *) echo "$message" ;;
  esac
}

usage() {
  echo -e "${BLUE}usage: ./build.sh [env] [-v] example: ./build.sh test -v${NC}"
  exit 1
}

run() {
  if $VERBOSE; then
    eval "$1"
  else
    eval "$1" > /dev/null 2>&1
  fi
}

load_config() {
  [[ ! -f "$configFile" ]] && log error "config file not found: $configFile" && exit 1
  version=$(grep 'version' "$configFile" | cut -d '=' -f2)
  name=$(grep 'name' "$configFile" | cut -d '=' -f2)
  log info "environment: $sufix app: $name | version: $version"
}

compile() {
  log info "compiling project :::"
  run "./gradlew clean bootJar"
  log success "project compiled :::"
}

build_image() {
  log info "building docker image :::"
  run "docker build -t $REGISTRY/$name:$version ./"
  log success "docker image built ::: $REGISTRY/$name:$version"
}

push_image() {
  log info "pushing image to GITLAB :::"
  run "docker push $REGISTRY/$name:$version"
  log success "image pushed to ::: $REGISTRY/$name:$version"
}

main() {
  [[ -z "$sufix" ]] && usage

  log info "starting build process :::"

  load_config
  compile
  build_image
  push_image

  log success "build process completed successfully :::"
}

main