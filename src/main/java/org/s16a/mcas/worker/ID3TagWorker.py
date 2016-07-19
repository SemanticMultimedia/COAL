from PythonWorkerWrapper import PythonWorker
from mutagen.mp3 import MP3
from mutagen.easyid3 import EasyID3
import sys, os

def func():
	pathToFile = sys.argv[1]
	audio = MP3(pathToFile, ID3=EasyID3)
	#print audio.info.length, audio.info.bitrate


	#print EasyID3.valid_keys.keys()

	print audio["TIT2"]
	# print audio["TCON"]
	# print audio["TDRC"]
	# #print audio["APIC"]
	# print audio["TCOP"]
	# print audio["TDRC"]
	# print audio["TPE1"]
	# print audio["TALB"]

	#print audio.pprint()

	#f = open("id3", "w")
	#f.write(str(audio["TIT2"]))
	#f.write(str(audio["TCOP"]))

if __name__ == "__main__":
	worker = PythonWorker(func)
	worker.run()
