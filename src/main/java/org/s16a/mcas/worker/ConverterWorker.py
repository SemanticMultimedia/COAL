import sys, os
import pyAudioAnalysis

FS = 16000 # sampling rate of generated WAV files
NC = 1     # number of channels of generated WAV files

dirName = sys.argv[1]
pyAudioAnalysis.audioBasicIO.convertDirMP3ToWav(dirName, FS, NC, useMp3TagsAsName = False)

#return 0 if no errors occured
print('0')
