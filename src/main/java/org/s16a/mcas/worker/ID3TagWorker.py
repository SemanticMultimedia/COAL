from mutagen.mp3 import MP3
import sys
audio = MP3(sys.argv[1])
#print audio.info.length, audio.info.bitrate
print audio["TIT2"]
print audio["TCON"]
print audio["TDRC"]
#print audio["APIC"]
print audio["TCOP"]
print audio["TDRC"]
print audio["TPE1"]
print audio["TALB"]