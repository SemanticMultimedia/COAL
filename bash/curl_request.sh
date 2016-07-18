#!/bin/bash

rm -r cache/*

#curl -v -H "accept:text/turtle" "http://localhost:8080/coal/resource?url=http://media-stream-pmd.rbb-online.de/content/70058479-d361-4764-b79d-b53b3039ebc4_415d370e-0bd1-4fe5-b079-2f8a05cf2486.mp3"
#curl -v -H "accept:text/turtle" "http://localhost:8080/myapp/resource?url={http://hpi.de/fileadmin/_processed_/csm_merkel_girlsday_hpi_larissa_hoffaeller_1020x420_e8da764440.jpg}"
#curl -v -H "accept:text/turtle" "http://localhost:8080/coal/resource?url=http://www.manhattanbeachmusic.com/audio/allegro-brillante-ms.mp3"
#curl -v -H "accept:text/turtle" "http://localhost:8080/coal/resource?url=http://www.podtrac.com/pts/redirect.mp3/audio.wnyc.org/rl_extras/rl_extras0616buriedbodiescase.mp3"
#curl -v -H "accept:text/turtle" "http://localhost:8080/coal/resource?url=http://13453.mc.tritondigital.com/NPR_500005/media-session/fd7f6fba-068a-4247-8bbf-51ba00e72246/anon.npr-mp3/npr/newscasts/2016/06/14/newscast060632.mp3"
#curl -v -H "accept:text/turtle" "http://localhost:8080/coal/resource?url=http://open.live.bbc.co.uk/mediaselector/5/redir/version/2.0/mediaset/audio-nondrm-download/proto/http/vpid/p03x4n0q.mp3"
#curl -v -H "accept:text/turtle" "http://192.168.99.100:8080/coal/resource?url=http://static.nico.is/testpodcast.mp3"

curl -v -H "accept:text/turtle" "http://192.168.99.100:8080/coal/resource?url=http://acdk2.de/knowexample3.mp3"
