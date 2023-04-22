#!/bin/bash

for i in $(find . -name '*.kt');
do
  if ! grep "Apache License" "$i"
  then
    cat LICENSE-HEADER "$i" > "$i".new && mv "$i".new "$i"
  fi
done