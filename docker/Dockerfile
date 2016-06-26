FROM ubuntu:latest
MAINTAINER boeckhoff

# HOW TO USE
# TO BUILD:        docker build -t boeckhoff/knowmin .
# TO RUN:          docker run -t -i boeckhoff/knowmin ./bash/startup.sh
# TO MOUNT & RUN:  docker run -v /HOSTPATH/:/IMAGEPATH -t -i boeckhoff/knowmin

# Update and install dependencies
RUN apt-get update
RUN apt-get upgrade -y

# Install dependencies
RUN apt-get install -y git
RUN apt-get install -y wget

# Install Python dependencies
RUN apt-get install -y \
  python-pip \
  python-numpy \
  python-matplotlib \
  python-scipy \
  libgsl0-dev \
  python-eyed3

RUN wget http://sourceforge.net/projects/mlpy/files/mlpy%203.5.0/mlpy-3.5.0.tar.gz 
RUN tar xvf mlpy-3.5.0.tar.gz
RUN cd mlpy-3.5.0 && python setup.py install

# Install Java dependencies
RUN apt-get install -y \
  openjdk-8-jdk \
  maven

# Pip dependencies
RUN pip install --upgrade pip
RUN pip install scikit-learn==0.16.1
RUN pip install scikits.talkbox
RUN pip install simplejson
RUN pip install SpeechRecognition


WORKDIR /knowmin

# RabbitMQ
RUN apt-get install -y rabbitmq-server

# Audio Converter
RUN apt-get install -y libav-tools

# Install COAL
#RUN git clone https://github.com/NicoKnoll/COAL #Please keep in mind that cache will be used unless you change this line
#WORKDIR /knowmin/COAL
#RUN mkdir cache
#ADD . /knowmin/COAL

# Install pyAudioAnalysis
WORKDIR /knowmin
RUN git clone https://github.com/boeckhoff/pyAudioAnalysis.git
RUN export PYTHONPATH=$PYTHONPATH:$(pwd)

# Pythonpath
RUN echo "export PYTHONPATH=$PYTHONPATH:"/knowmin/COAL/lib"" >> /root/.bashrc

ENV RABBIT_HOST $RABBIT_PORT_4369_TCP_ADDR

RUN mkdir COAL
WORKDIR /knowmin/COAL

EXPOSE 8080
