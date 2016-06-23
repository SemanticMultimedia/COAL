#!/bin/bash

rabbitmq-server -detached
mvn exec:java
