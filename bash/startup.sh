#!/bin/bash
source /root/.bashrc

#Start RabbitMQ
rabbitmq-server -detached

#Build coal project
mvn clean install -DskipTests=true

#Add rabbit user (RabbitMQ must be started successfully)
rabbitmqctl add_user coal coal
rabbitmqctl set_user_tags coal administrator
rabbitmqctl set_permissions -p / coal ".*" ".*" ".*"

#Start COAL server
mvn exec:java

#Return to bash
bash
