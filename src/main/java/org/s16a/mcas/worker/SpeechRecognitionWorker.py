from PythonWorkerWrapper import PythonWorker
from Segmentation import *
import speech_recognition as sr
import sys
import os
import ast

PATH_TO_DIR = sys.argv[1]
r = sr.Recognizer()

def fileNameForSegment(segment):
    return PATH_TO_DIR + "/" + str(segment[0]) + "-" + str(segment[1]) + "_speech.wav"


def textFileNameForSegment(segment):
    return PATH_TO_DIR + "/" + str(segment[0]) + "-" + str(segment[1]) + "_text"


def recognizeForSegment(segment):
    parts = sliceSegmentIntoParts(fileNameForSegment(segment))
    for fileName in parts:
        with sr.WavFile(fileName) as source:
            audio = r.record(source)

            try:
                txt = r.recognize(audio)
                print(txt)
            except LookupError:
                pass

            try:
                with open(textFileNameForSegment(segment), "a") as textFile: 
                    textFile.write(txt + "\n")
                textFile.close()
            except sr.UnknownValueError:
                pass
            except sr.RequestError as e:
                pass

        os.remove(fileName)


def func():
    segments = ast.literal_eval(open(PATH_TO_DIR + "/segments", "r").readline())
    speechSegments = segments[0]

    for s in speechSegments:
        recognizeForSegment(s)


worker = PythonWorker(func)
worker.run()
