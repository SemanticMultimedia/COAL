from pyAudioAnalysis import audioSegmentation as aS
from PythonWorkerWrapper import PythonWorker
from Segmentation import *

PATH_TO_DIR = sys.argv[1]
PATH_TO_WAV = PATH_TO_DIR + "data.wav"
PATH_TO_SVM = "../pyAudioAnalysis/data/svmSM"
PATH_TO_SEGMENTS_FILE = PATH_TO_DIR + "data.segments"

def getSegments(i):
    count = 0
    start = 0
    current = ""
    speechIntervals = []
    musicIntervals = []
    for j in i:
        count += 1
        if j == 0.0:
            if current != "speech":
                musicIntervals.append((start,count))
                current = "speech"
                start = count
        elif j == 1.0:
            if current != "music":
                speechIntervals.append((start,count))
                current = "music"
                start = count

    if current == "speech":
        speechIntervals.append((start, count))
    elif current == "music":
        musicIntervals.append((start, count))

    return [speechIntervals, musicIntervals]


def cutSegments(segments):
    "Slices the wav file into segments"

    speechSegments = segments[0]
    musicSegments = segments[1]

    for i in speechSegments:
        start_s = i[0]
        end_s = i[1]
        fileOutName = str(start_s) + "-" + str(end_s) + "_speech.wav"

        sliceWav(wave.open(PATH_TO_WAV, "r"), PATH_TO_DIR + fileOutName, start_s*1000, end_s*1000)


    for i in musicSegments:
        start_s = i[0]
        end_s = i[1]
        fileOutName = str(start_s) + "-" + str(end_s) + "_music.wav"

        try:
            sliceWav(wave.open(PATH_TO_WAV, "r"), PATH_TO_DIR + fileOutName, start_s*1000, end_s*1000)

        except Exception, e:
            if "position not in range" in str(e):
                pass
            else:
                raise Exception(str(e))

def func():
    res = [flagsInd, classesAll, acc] = aS.mtFileClassification(PATH_TO_WAV, PATH_TO_SVM, "svm", False, PATH_TO_SEGMENTS_FILE)
    print res
    segments = getSegments(res[0])
    print segments
    f = open(PATH_TO_DIR + "segments", "w")
    f.write(str(segments))
    cutSegments(segments)

worker = PythonWorker(func)
worker.run()
