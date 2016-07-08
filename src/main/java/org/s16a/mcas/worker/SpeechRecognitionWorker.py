from PythonWorkerWrapper import PythonWorker
import speech_recognition as sr
import sys
import os

def getDigit(s):
    return int(s[:s.index("-")])

def func():
    r = sr.Recognizer()

    segments = []

    print(sys.argv[1])

    for i in os.listdir(sys.argv[1]):
        if i.endswith("speech.wav"): 
            segments.append(i)
            continue

    segments.sort(key=lambda x: getDigit(x))

    for i in segments:

        with sr.WavFile(sys.argv[1] + i) as source:
            audio = r.record(source) #AIzaSyBOti4mM-6x9WDnZIjIeyEU21OpBXqWBgw

            try:
                txt = r.recognize(audio)
                print(txt)
            except LookupError:
                pass

            try:
                with open(sys.argv[1] + "/text", "a") as myfile: 
                    myfile.write(txt + "\n")
                myfile.close()
            except sr.UnknownValueError:
                pass
            except sr.RequestError as e:
                pass

if __name__ == "__main__":
    worker = PythonWorker(func)
    worker.run()
