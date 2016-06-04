# COAL
web media content analysis framework

### Requirements
- RabbitMQ
- Java SDK

### How to run

#### start rabbitmq 
- [for mac] execute "/usr/local/sbin/rabbitmq-server" in terminal
- [for linux] execute "sudo rabbitmq-plugins enable rabbitmq_management" in terminal
- [for linux] execute "sudo service rabbitmq-server restart" in terminal
- go to http://localhost:15672/
- login (Name: guest, Password: guest)

#### start Application
- run COAL/src/main/java/org/s16a/mcas/Main.java
- start the workers you want to use


### How to use
- call the following command in your terminal
```
curl -v -H "accept:application/x-turtle" "http://localhost:8080/coal/resource?url=RESOURCE-URI"
```
