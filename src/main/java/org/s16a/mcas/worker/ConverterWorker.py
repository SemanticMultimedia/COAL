from PythonWorkerWrapper import PythonWorker

def func():
	import sys, os
	from pyAudioAnalysis import audioBasicIO

	FS = 16000 # sampling rate of generated WAV files
	NC = 1     # number of channels of generated WAV files

	dirName = sys.argv[1]
	audioBasicIO.convertDirMP3ToWav(dirName, FS, NC, useMp3TagsAsName = False)

if __name__ == "__main__":
	worker = PythonWorker(func)
	worker.run()
