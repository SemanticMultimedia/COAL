#!/bin/bash

rm -rf cache/

light_red='\e[1;91m%s\e[0m\n'
light_green='\e[1;92m%s\e[0m\n'

#test if
curl -v --connect-timeout 1 -H "accept:application/x-turtle" "http://172.16.65.75:8080/coal/resource?url=http://static.nico.is/testpodcast.mp3"
if [ "$?" -eq 0 ]; then
  printf "$light_green" "[ USE HPI internal IP ]"; LOCAL="false"
else
  printf "$light_red" "[ USE LOCALHOST ]"; LOCAL="true"
fi

for i in {1 .. 2}
do
    if [ $LOCAL = "true" ]
    then curl -v  -H "accept:application/x-turtle" "http://localhost:8080/coal/resource?url=http://static.nico.is/testpodcast.mp3";
	else curl -v  -H "accept:application/x-turtle" "http://172.16.65.75:8080/coal/resource?url=http://static.nico.is/testpodcast.mp3"
	fi
	sleep 5
done
