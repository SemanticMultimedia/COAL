# Sonic Annotator

To install and use Sonic Annotator, follow these steps:
- [download](https://code.soundsoftware.ac.uk/projects/sonic-annotator/files) and install the Sonic Annotator binary for your operating system
- [download](http://vamp-plugins.org/download.html) a few Vamp Plugins, e.g.
    + BBC Vamp Plugins
    + Chordino and NNLS Chroma
    + ...
- copy the plugin's library file and any supplied category or RDF files into your system or personal [Vamp plugin location](http://www.vamp-plugins.org/download.html#install)
- execute the commands:
```shell
# basic help
$ sonic-annotator -h

# list available transforms:
$ sonic-annotator -l
# list supported writer types
$ sonic-annotator --list-writers  
# list supported input audio formats 
$ sonic-annotator --list-formats  


# run a simple feature extractor
$ sonic-annotator -d vamp:bbc-vamp-plugins:bbc-rhythm:tempo -w csv path/to/audio/file.mp3
```
In the last command you need to substitute vamp:bbc-vamp-plugins:bbc-rhythm:tempo
 with a key you obtained when using the list (-l) switch, and path/to/audio/file.mp3 with an existing .wav or .mp3 file on your machine.

Click [here](http://www.vamp-plugins.org/sonic-annotator/) for furter information about Sonic Annotator

# SPARQL
To install SPARQL-Wrapper you may use:

```shell
$ sudo easy_install SPARQLWrapper
$ python sparql.py
```