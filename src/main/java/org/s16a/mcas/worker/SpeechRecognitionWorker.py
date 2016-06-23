import speech_recognition as sr
import sys
import os

def getDigit(s):
    return int(s[:s.index("-")])

r = sr.Recognizer()

segments = []

print(sys.argv[1])

for i in os.listdir(sys.argv[1]):
    if i.endswith("speech.wav"): 
        segments.append(i)
        continue

segments.sort(key=lambda x: getDigit(x))

for i in segments:

    print(sys.argv[1] + i)

    with sr.AudioFile(sys.argv[1] + i) as source:
        audio = r.record(source) 
        #try:
        txt = r.recognize_google(audio)
        print(txt)
        try:
            with open(sys.argv[1] + "/text", "a") as myfile: 
                myfile.write(r.recognize_google(audio) + "\n")
            myfile.close()
        except sr.UnknownValueError:
            pass
        except sr.RequestError as e:
            pass

print("0")
