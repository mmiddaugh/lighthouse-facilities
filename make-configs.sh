#! /usr/bin/env bash

REPO=$(cd $(dirname $0) && pwd)
PROFILE=dev
MARKER=$(date +%s)

makeConfig() {
  local project="$1"
  local profile="$2"
  local target="$REPO/$project/config/application-${profile}.properties"
  [ -f "$target" ] && mv -v $target $target.$MARKER
  grep -E '(.*= *unset)' "$REPO/$project/src/main/resources/application.properties" \
    > "$target"
}

configValue() {
  local project="$1"
  local profile="$2"
  local key="$3"
  local value="$4"
  local target="$REPO/$project/config/application-${profile}.properties"
  local escapedValue=$(echo $value | sed -e 's/\\/\\\\/g; s/\//\\\//g; s/&/\\\&/g')
  sed -i "s/^$key=.*/$key=$escapedValue/" $target
}

checkForUnsetValues() {
  local project="$1"
  local profile="$2"
  local target="$REPO/$project/config/application-${profile}.properties"
  echo "checking $target"
  grep -E '(.*= *unset)' "$target"
  [ $? == 0 ] && echo "Failed to populate all unset values" && exit 1
  diff -q $target $target.$MARKER
  [ $? == 0 ] && rm -v $target.$MARKER
}

makeConfig facilities-collector $PROFILE
configValue facilities-collector $PROFILE access-to-care.url 'http://localhost:8666'
configValue facilities-collector $PROFILE access-to-pwt.url 'http://localhost:8666'
configValue facilities-collector $PROFILE arc-gis.url 'http://localhost:8666'
configValue facilities-collector $PROFILE spring.datasource.password '<YourStrong!Passw0rd>'
configValue facilities-collector $PROFILE spring.datasource.url 'jdbc:sqlserver://localhost:1533;database=fc;sendStringParametersAsUnicode=false'
configValue facilities-collector $PROFILE spring.datasource.username 'SA'
configValue facilities-collector $PROFILE state-cemeteries.url 'http://localhost:8666'

makeConfig facilities $PROFILE
configValue facilities $PROFILE bing.key 'unused'
configValue facilities $PROFILE bing.url 'http://localhost:8666'
configValue facilities $PROFILE facilities-collector.url 'http://localhost:8080'
configValue facilities $PROFILE facilities.url 'http://localhost:8085'
configValue facilities $PROFILE facilities.base-path '/'
configValue facilities $PROFILE spring.datasource.password '<YourStrong!Passw0rd>'
configValue facilities $PROFILE spring.datasource.url 'jdbc:sqlserver://localhost:1533;database=facility;sendStringParametersAsUnicode=false'
configValue facilities $PROFILE spring.datasource.username 'SA'

checkForUnsetValues facilities-collector $PROFILE
