# COAL
web media content analysis framework

### Requirements
- [Docker](https://docs.docker.com/engine/installation/)

### How to run

#### run docker-machine (only necessary for mac)
- [for mac] run 'docker-machine start' in terminal to start virtual machine for docker
- [for mac] run 'docker-machine env' in terminal to get environment variables
- [for mac] run 'eval "$(docker-machine env default)" ' to set environment variables

#### build docker-image (you need to be in /COAL/docker directory)
- docker build -t boeckhoff/knowmin .

#### run docker-image (you need to be in /COAL/docker directory)
- docker run -v $(pwd):/knowmin/COAL -t -i -p 8080:8080 boeckhoff/knowmin ./bash/startup.sh

- [fast](https://github.com/chanezon/docker-tips/blob/master/java-build-mount-maven-repo.md) docker run -v ~/.m2:/root/.m2 -v $(pwd):/knowmin/COAL -t -i -p 8080:8080 boeckhoff/knowmin ./bash/startup.sh

### How to use
- call the following command in your terminal (not in docker container)
```
curl -v -H "accept:text/turtle" "http://localhost:8080/coal/resource?url=RESOURCE-URI"
```
