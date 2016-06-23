FROM ubuntu:latest
MAINTAINER boeckhoff

# HOW TO USE
# TO BUILD:        docker build -t boeckhoff/knowmin .
# TO RUN:          docker run -t -i boeckhoff/knowmin bash
# TO MOUNT & RUN:  docker run -v /HOSTPATH/:/IMAGEPATH -t -i boeckhoff/knowmin

# Update and install dependencies
RUN apt-get update
RUN apt-get upgrade -y

# Install dependencies
RUN apt-get install -y \
  git \
  wget

# Install Python dependencies
##RUN apt-get install -y \
##  python-pip \
##  python-numpy \
##  python-matplotlib \
##  python-scipy \
##  libgsl0-dev \
##  python-eyed3

##RUN wget http://sourceforge.net/projects/mlpy/files/mlpy%203.5.0/mlpy-3.5.0.tar.gz
##RUN tar xvf mlpy-3.5.0.tar.gz
##RUN cd mlpy-3.5.0 && python setup.py install

# Install Java dependencies
RUN apt-get install -y \
  openjdk-8-jdk \
  maven

# Pip dependencies
##RUN pip install --upgrade pip
##RUN pip install scikit-learn==0.16.1
##RUN pip install scikits.talkbox
##RUN pip install simplejson

# Pythonpath
##RUN echo "export PYTHONPATH=$PYTHONPATH:"/knowmin"" >> /root/.bashrc

# Create folder
RUN mkdir -p /knowmin/COAL
WORKDIR /knowmin/COAL

# Clone
##RUN git clone https://github.com/boeckhoff/pyAudioAnalysis.git
##RUN export PYTHONPATH=$PYTHONPATH:$(pwd)
#RUN source ~/.bashrc

# Install COAL

##RUN git clone https://github.com/NicoKnoll/COAL
ADD . /knowmin/COAL

# Run RabbitMQ, COAL Server and Workers
#RUN rabbitmq-server -detached

ENV RABBIT_HOST $RABBIT_PORT_4369_TCP_ADDR

RUN mvn clean install -DskipTests=true

EXPOSE 8080

CMD mvn exec:java
#RUN java -jar target/org.s16a.mcas-1.0-SNAPSHOT.jar & java -cp target/org.s16a.mcas-1.0-SNAPSHOT.jar org/s16a/mcas/worker/DownloadWorker & java -cp target/org.s16a.mcas-1.0-SNAPSHOT.jar org/s16a/mcas/worker/MediainfoWorker & java -cp target/org.s16a.mcas-1.0-SNAPSHOT.jar org/s16a/mcas/worker/ConverterWorker & java -cp target/org.s16a.mcas-1.0-SNAPSHOT.jar org/s16a/mcas/worker/SegmentationWorker
