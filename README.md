# COAL
web media content analysis framework

### Requirements
- RabbitMQ
- Java SDK

### How to run

#### start rabbitmq 
- [for mac] execute /usr/local/sbin/rabbitmq-server in terminal
- go to http://localhost:15672/
- login (Name: guest, Password: guest)

#### start Application
- run COAL/src/main/java/org/s16a/mcas/Main.java
- start the workers you want to use


### How to use
- if you get "request failed"/"Error 500" you have to create a folder called "cache" manually in your project folder (same folder as "lib", "src", "target")
- call the following comman in your terminal
```
curl -v -H "accept:application/x-turtle" "http://localhost:8080/myapp/resource?url={RESOURCE-URI}"
```
