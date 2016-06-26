import sys, os
import wave
from pyAudioAnalysis import audioSegmentation as aS

pathToDir = sys.argv[1]

pathToWav = pathToDir + "data.wav"
pathToSvm = "../pyAudioAnalysis/data/svmSM"
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
    #return str(s/60) + ":" + str(s%60)
    return s

def slice(infile, outfilename, start_ms, end_ms):
    width = infile.getsampwidth()
    rate = infile.getframerate()
    fpms = rate / 1000 # frames per ms
    length = (end_ms - start_ms) * fpms
    start_index = start_ms * fpms

    out = wave.open(outfilename, "w")
    out.setparams((infile.getnchannels(), width, rate, length, infile.getcomptype(), infile.getcompname()))
    
    infile.rewind()
    anchor = infile.tell()
    infile.setpos(anchor + start_index)
    out.writeframes(infile.readframes(length))


def cut_segments(segments):
    speech_segments = segments[0]
    music_segments = segments[1]

    for i in speech_segments:
        fname = str(i[0]) + "-" + str(i[1]) + "_speech.wav"
        try:
            b_sec = i[0]*1000
            e_sec = b_sec + 5000
            while e_sec <= i[1]*1000:
                fname = str(b_sec/1000) + "-" + str(e_sec/1000) + "_speech.wav"
                slice(wave.open(pathToWav, "r"), pathToDir + fname, b_sec, e_sec) 
                b_sec = e_sec
                e_sec += 5000
            
            #Add segments < 5 sec at the end
            #e_sec = i[1]*1000
            #fname = str(b_sec/1000) + "-" + str(e_sec/1000) + "_speech.wav"
            #slice(wave.open(pathToWav, "r"), pathToDir + fname, b_sec, e_sec) 

        except Exception, e:
            if "position not in range" in str(e):
                pass
            else:
                print(str(e))

    for i in music_segments:
        fname = str(i[0]) + "-" + str(i[1]) + "_music.wav"
        try:
            slice(wave.open(pathToWav, "r"), pathToDir + fname, int(i[0]*1000), int(i[1]*1000))
        except Exception, e:
            if "position not in range" in str(e):
                pass
            else:
                print(str(e))


if __name__ == "__main__":
    res = [flagsInd, classesAll, acc] = aS.mtFileClassification(pathToWav, pathToSvm, "svm", False, pathToDataSegments) 
    print res
    segments = getSegments(res[0])
    print segments
    f = open(pathToDir + "segments", "w")
    f.write(str(segments))
    cut_segments(segments)
    #slice(wave.open("onetwothree.wav", "r"), "out.wav", 500, 3000)
    print("0")
