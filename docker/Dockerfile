FROM ubuntu:latest
MAINTAINER boeckhoff

# HOW TO USE
# TO BUILD:        docker build -t boeckhoff/knowmin .
# TO RUN:          docker run -t -i boeckhoff/knowmin bash
# TO MOUNT & RUN:  docker run -v /HOSTPATH/:/IMAGEPATH -t -i boeckhoff/knowmin

# Update

RUN apt-get update

# Create folder

RUN mkdir knowmin
WORKDIR "/knowmin"

# Install and clone

RUN apt-get install git-all -y
RUN apt-get install wget -y
RUN apt-get install python-pip -y
RUN git clone https://github.com/boeckhoff/pyAudioAnalysis.git
RUN export PYTHONPATH=$PYTHONPATH:$(pwd)
#RUN source ~/.bashrc

# Python Dependencies

RUN apt-get install python-numpy -y
RUN apt-get install python-matplotlib -y
RUN apt-get install python-scipy -y
RUN apt-get install libgsl0-dev -y
RUN apt-get install python-eyed3 -y

RUN wget http://sourceforge.net/projects/mlpy/files/mlpy%203.5.0/mlpy-3.5.0.tar.gz
RUN tar xvf mlpy-3.5.0.tar.gz
RUN cd mlpy-3.5.0 && python setup.py install

# Pip dependencies

RUN pip install scikit-learn==0.16.1
RUN pip install scikits.talkbox
RUN pip install simplejson

# Pythonpath
RUN echo "export PYTHONPATH=$PYTHONPATH:"/knowmin"" >> /root/.bashrc

# Install RabbitMQ
RUN apt-get install rabbitmq-server -y

# Install and clone COAl
RUN apt-get install maven -y
RUN cd ~/
RUN git clone https://github.com/NicoKnoll/COAL

# Run RabbitMQ, COAL Server and Workers
RUN cd COAL & rabbitmq-server
RUN mvn clean install -DskipTests=true
RUN java -jar target/org.s16a.mcas-1.0-SNAPSHOT.jar & java -cp target/org.s16a.mcas-1.0-SNAPSHOT.jar org/s16a/mcas/worker/DownloadWorker & java -cp target/org.s16a.mcas-1.0-SNAPSHOT.jar org/s16a/mcas/worker/MediainfoWorker & java -cp target/org.s16a.mcas-1.0-SNAPSHOT.jar org/s16a/mcas/worker/ConverterWorker

