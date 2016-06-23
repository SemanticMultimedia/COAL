#!/bin/bash

rabbitmq-server -detached
mvn exec:java
#java -jar target/org.s16a.mcas-1.0-SNAPSHOT.jar
