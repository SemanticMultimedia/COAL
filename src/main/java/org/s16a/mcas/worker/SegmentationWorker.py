import sys, os
from pyAudioAnalysis import audioSegmentation as aS

pathToDir = sys.argv[1]

pathToWav = pathToDir + "data.wav"
pathToSvm = "./lib/pyAudioAnalysis/data/svmSM"
pathToDataSegments = pathToDir + "data.segments"

def getSegments(i):
    count = 0
    start = 0
    current = ""
    speech_intervals = []
    music_intervals = []
    for j in i:
        count += 1
        if j == 0.0:
            if current != "speech":
                music_intervals.append((asMin(start),asMin(count)))
                current = "speech"
                start = count
        elif j == 1.0:
            if current != "music":
                speech_intervals.append((asMin(start),asMin(count)))
                current = "music"
                start = count

    if current == "speech":
        speech_intervals.append((asMin(start), asMin(count)))
    elif current == "music":
        music_intervals.append((asMin(start), asMin(count)))

    return [speech_intervals, music_intervals]


def asMin(s):
    return str(s/60) + ":" + str(s%60)


if __name__ == "__main__":
    res = [flagsInd, classesAll, acc] = aS.mtFileClassification(pathToWav, pathToSvm, "svm", False, pathToDataSegments) 
    print res
    segments = getSegments(res[0])
    print segments
    f = open(pathToDir + "segments", "w")
    f.write(str(segments))
    print('0')
