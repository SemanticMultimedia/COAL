#!/usr/bin/env python3

import speech_recognition as sr
import sys
# obtain path to "english.wav" in the same folder as this script
import os

def getDigit(s):
    return int(s[:s.index("-")])

# use the audio file as the audio source
r = sr.Recognizer()

segments = []

for i in os.listdir(sys.argv[1]):
    if i.endswith("speech.wav"): 
        segments.append(i)
        continue

segments.sort(key=lambda x: getDigit(x))

for i in segments:

    with sr.AudioFile(sys.argv[1] + "/" + i) as source:
        audio = r.record(source) 
        #try:
        txt = r.recognize_google(audio)
        print(txt)
        try:
            with open(sys.argv[1] + "/text", "a") as myfile: 
                myfile.write(r.recognize_google(audio) + "\n")
        except sr.UnknownValueError:
            pass
        except sr.RequestError as e:
            pass
        finally:
            myfile.close()

print("0")
