#!/bin/bash

while read p; do
	curl -v -H "accept:text/turtle" "http://localhost:8080/coal/resource?url="$p""
  done < podcasts
