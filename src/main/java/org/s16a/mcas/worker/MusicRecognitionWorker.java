package org.s16a.mcas.worker;

import java.io.FileWriter;
import java.io.IOException;

import com.hp.hpl.jena.rdf.model.Model;
import org.apache.commons.cli.*;
import org.s16a.mcas.Enqueuer;
import org.s16a.mcas.MCAS;
import org.s16a.mcas.Cache;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.File;
import java.io.PrintStream;

import org.s16a.mcas.util.acoustid.AcoustID;
import org.s16a.mcas.util.acoustid.ChromaPrint;
import org.s16a.mcas.util.musicbrainz.MusicBrainz;
import org.s16a.mcas.util.TrackInformation;

public class MusicRecognitionWorker implements Runnable {
    private static final String TASK_QUEUE_NAME = MCAS.music.toString();
    private static final String FPCALC = "/knowmin/chromaprint-fpcalc-1.3.2-linux-x86_64/fpcalc";
    private static final int QUERYRATE = 5;

    public void run () {

        try {
            executeWorker();
        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }

    // todo: naming of "main"-method in java workers
    public static void executeWorker() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(System.getenv().get("RABBIT_HOST"));

        final Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();

        channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        channel.basicQos(1);

        final Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String url = new String(body, "UTF-8");

                System.out.println(" [x] Received '" + url + "'");
                try {
                    Cache cache = new Cache(url);

                    String output = "";

                    // Walk cache directory and process .wav audio files
                    final File[] files = new File(cache.getPath()).listFiles();

                    for (File file : files) {
                        if (file.isFile()) {
                            if (isValidForProcessing(file.getName())) {
                                try {
                                    output += processAudioFile(file.getAbsolutePath()) + "\n";

                                    // Sleep to prevent musicbrainz from overloading
                                    Thread.sleep(QUERYRATE * 1000);
                                } catch (final Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    String modelFileName = cache.getFilePath("data.ttl");
                    PrintStream fileStream = new PrintStream(modelFileName);
                    fileStream.println(output);
                    fileStream.close();

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    System.out.println(" [x] Done");
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            }
        };
        channel.basicConsume(TASK_QUEUE_NAME, false, consumer);
    }


    private static String processAudioFile(String url) throws Exception {
        File file = new File(url);

        final ChromaPrint chromaprint = AcoustID.chromaprint(file, FPCALC);
        final String musicbrainzId = AcoustID.lookup(chromaprint);

        if (musicbrainzId != null) {
            final TrackInformation trackInformation = MusicBrainz.lookup(musicbrainzId);

            if (trackInformation != null) {
                return trackInformation.toString();
            }

            return "No track information found in Musicbrainz database";
        }

        return "No recording id found in AcoustId database";
    }

    private static boolean isValidForProcessing(String filename) {
        if(!filename.toLowerCase().endsWith(".wav"))
            return false;

        // Cut .wav
        filename = filename.substring(0, filename.length() - 4);

        if (filename.equals("data"))
            return false;

        String[] segments = filename.split("_");

        if(segments[1].equals("speech"))
            return false;

        String[] range = segments[0].split("-");
        int start = Integer.parseInt(range[0]);
        int end = Integer.parseInt(range[1]);

        return (end-start) > 60;
    }

    private static void printTurtle(Model model, FileWriter writer, Cache cache) {
        try {
            System.out.println("MusicMetaData try save");
            model.write(writer, "TURTLE");
        } finally {
            try {
                writer.close();

                try {
                    Enqueuer.workerFinished(MCAS.music, cache);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (IOException closeException) {
                // todo: exception handling
                System.out.println("Fehler!!!");
            }
        }
    }
}