#!/bin/bash
set -e

ENV=$1
VERBOSE=false
[[ "$2" == "-v" || "$2" == "--verbose" ]] && VERBOSE=true

CONFIG_FILE="deploy/config-$ENV.txt"
BUILD_SCRIPT="./deploy/build.sh"
DOCKER_COMPOSE_LOCAL="./docker-compose.yml"
SEPARATOR="------------------------------------------------------------"

NC="\033[0m"        # RESET
BLUE="\033[1;34m"   # INFO
GREEN="\033[1;32m"  # SUCCESS
YELLOW="\033[1;33m" # WARN
RED="\033[1;31m"    # ERROR

USER=""
IP=""
APP_NAME=""
VERSION=""
REMOTE_MACHINE=""
DEPLOY_DIR=""
DOCKER_COMPOSE_FILE=""

REGISTRY="registry.gitlab.com/golden-gd/servers-files"
REGISTRY_USER="golden"
REGISTRY_PASSWORD="Ts7SyrsUzvf774fkeq_E"

log() {
  local level=$1
  local message=$2
  case "$level" in
    info) echo -e "${BLUE}[INFO] $message${NC}" ;;
    success) echo -e "${GREEN}[SUCCESS] $message${NC}" ;;
    warn) echo -e "${YELLOW}[WARN] $message${NC}" ;;
    error) echo -e "${RED}[ERROR] $message${NC}" ;;
    *) echo "$message" ;;
  esac
}

usage() {
  echo -e "${BLUE}usage: ./deploy.sh [env] [-v|--verbose]${NC}"
  echo -e "${BLUE}example: ./deploy.sh test --verbose${NC}"
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
  if [[ ! -f $CONFIG_FILE ]]; then
    log error "config file not found: $CONFIG_FILE"
    exit 1
  fi

  VERSION=$(grep 'version' "$CONFIG_FILE" | cut -d '=' -f2)
  APP_NAME=$(grep 'name' "$CONFIG_FILE" | cut -d '=' -f2)
  USER=$(grep 'user' "$CONFIG_FILE" | cut -d '=' -f2)
  IP=$(grep 'ip' "$CONFIG_FILE" | cut -d '=' -f2)
  REMOTE_MACHINE="$USER@$IP"
  DEPLOY_DIR="/opt/apps/$APP_NAME"
  DOCKER_COMPOSE_FILE="$DEPLOY_DIR/docker-compose.yml"
}

build_project() {
  "$BUILD_SCRIPT" "$ENV" $([[ $VERBOSE == true ]] && echo "--verbose")
}

upload_files() {
  log info "creating remote directory ::: $DEPLOY_DIR"
  run "ssh $REMOTE_MACHINE 'sudo mkdir -p $DEPLOY_DIR && sudo chmod -R 777 $DEPLOY_DIR'"

  log info "uploading ::: $DOCKER_COMPOSE_LOCAL"
  run "scp $DOCKER_COMPOSE_LOCAL $REMOTE_MACHINE:$DOCKER_COMPOSE_FILE"

  log success "files uploaded successfully to ::: $REMOTE_MACHINE"
}

remote_deploy() {
  log info "executing remote deploy ::: $REMOTE_MACHINE"

  run "ssh \"$REMOTE_MACHINE\" bash <<EOF
docker login $REGISTRY -u $REGISTRY_USER -p $REGISTRY_PASSWORD
export APP_NAME=\"$APP_NAME\"
export APP_VERSION=\"$VERSION\"
export APP_ENV=\"$ENV\"
docker compose -f \"$DOCKER_COMPOSE_FILE\" pull
docker compose -f \"$DOCKER_COMPOSE_FILE\" up -d
EOF
  "
  log success "deployment completed successfully :::"
}

show_logs() {
  log info "showing container logs ::: $APP_NAME"
  ssh "$REMOTE_MACHINE" "docker logs -f $APP_NAME"
}

main() {
  if [[ -z "$ENV" ]]; then
    usage
  fi

  log info "starting full deploy process :::"

  load_config
  build_project
  upload_files
  remote_deploy
  show_logs

  log success "deploy completed successfully :::"
}

main