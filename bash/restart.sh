#!/bin/bash

# build and start coal server
mvn clean install -DskipTests=true
mvn exec:java
