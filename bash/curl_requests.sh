#!/bin/bash

rm -rf cache

light_red='\e[1;91m%s\e[0m\n'
light_green='\e[1;92m%s\e[0m\n'

#test if
curl -v --connect-timeout 1 -H "accept:text/turtle" "http://172.16.65.75:8080/coal/resource?url=http://static.nico.is/testpodcast.mp3"
if [ "$?" -eq 0 ]; then
  printf "$light_green" "[ USE HPI internal IP ]"; LOCAL="false"
else
  printf "$light_red" "[ USE LOCALHOST ]"; LOCAL="true"
fi

LOCAL="true"

for i in {1 .. 3}
do
    if [ $LOCAL = "true" ]
    then curl -v -H "accept:text/turtle" "http://localhost:8080/coal/resource?url=http://acdk2.de/knowexample3.mp3";
	else curl -v -H "accept:text/turtle" "http://172.16.65.75:8080/coal/resource?url=http://acdk2.de/knowexample3.mp3"
	fi
	sleep 5
done

#curl -v  -H "accept:text/turtle" "http://172.16.65.75:8080/coal/resource?url=http://static.nico.is/testpodcast.mp3"
