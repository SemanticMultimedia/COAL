# COAL
web media content analysis framework

### Requirements
- RabbitMQ
- Java SDK

### How to run

#### start rabbitmq
- [for mac] run '/usr/local/sbin/rabbitmq-server' in terminal
- [for linux] run 'sudo rabbitmq-plugins enable rabbitmq_management' in terminal
- [for linux] run 'sudo service rabbitmq-server restart' in terminal
- go to http://localhost:15672/
- login (Name: guest, Password: guest)

#### start Application
- run 'COAL/src/main/java/org/s16a/mcas/Main.java' in terminal
- start the workers you want to use

#### start Application (Server) from cammand line
- [for Linux] run 'sudo apt-get install maven' in terminal
- [for mac] run 'brew install maven' in terminal
- navigate into COAL-project
- run 'mvn clean install -DskipTests=true' in terminal
- run 'java -jar target/org.s16a.mcas-1.0-SNAPSHOT.jar' in terminal

#### start Workers from command line (after Server is started)
- run 'java -cp target/org.s16a.mcas-1.0-SNAPSHOT.jar org/s16a/mcas/worker/DownloadWorker' in terminal
- run 'java -cp target/org.s16a.mcas-1.0-SNAPSHOT.jar org/s16a/mcas/worker/MediainfoWorker' in terminal
- run 'java -cp target/org.s16a.mcas-1.0-SNAPSHOT.jar org/s16a/mcas/worker/ConverterWorker' in terminal
- run 'java -cp target/org.s16a.mcas-1.0-SNAPSHOT.jar org/s16a/mcas/worker/SegmentationWorker' in terminal

#### restart RabbitMQ
- run 'rabbitmqctl stop_app'
- run 'rabbitmqctl reset'
- run 'rabbitmqctl start_app'

#### run docker-machine
- [for mac] run 'docker-machine start' in terminal to start virtual machine for docker
- [for mac] run 'docker-machine env' in terminal to get environment variables
- [for mac] run 'eval "$(docker-machine env default)" ' to set environment variables

#### build docker-image
- run 'docker build -t boeckhoff/knowmin .' in terminal

#### run docker-image
- run 'docker run -t -i boeckhoff/knowmin bash' in terminal

### How to use
- call the following command in your terminal
```
curl -v -H "accept:application/x-turtle" "http://localhost:8080/coal/resource?url=RESOURCE-URI"
```
