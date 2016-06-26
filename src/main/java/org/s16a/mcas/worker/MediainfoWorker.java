package org.s16a.mcas.worker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.s16a.mcas.Cache;
import org.s16a.mcas.Enqueuer;
import org.s16a.mcas.MCAS;
import org.s16a.mcas.util.MediaInfo;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class MediainfoWorker implements Runnable{

	private static final String TASK_QUEUE_NAME = MCAS.mediainfo.toString();

	public void run () {

		try {
			executeWorker();
		} catch (Exception e) {
			System.out.println(e.toString());
		}

	}
	public static void executeWorker() throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
//		factory.setHost(System.getenv().get("RABBIT_HOST"));
        factory.setHost("localhost");

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

                    Model model = ModelFactory.createDefaultModel();
                    model.read(cache.getFilePath("data.ttl"));

                    String mimetype = simplifyMime(model.getResource(url).getProperty(DC.format).getString());

                    if(mimetype.equals("audio")) {
                        System.out.println(" [x] Mime type: audio ");
                        extractMediainfoAudio(url);
                    } else if(mimetype == "image") {
                        System.out.println(" [x] Mime type: Image ");
                        extractMediainfoImage(url);
                    } else {
                        System.out.println(" [x] Unsupported mime type: " + mimetype);
                    }

				} finally {
					System.out.println(" [x] Done");
					channel.basicAck(envelope.getDeliveryTag(), false);
				}
			}
		};
		channel.basicConsume(TASK_QUEUE_NAME, false, consumer);
	}

    private static String simplifyMime(String mimetype) {
        return mimetype.split("/")[0];
    }

	private static void extractMediainfoImage(String url) throws IOException {
		Cache cache = new Cache(url);

		// open model
		Model model = ModelFactory.createDefaultModel();
		String modelFileName = cache.getFilePath("data.ttl");
		File f = new File(modelFileName);

		if (f.exists()) {
			model.read(modelFileName);
		}

		String dataFileName = cache.getResourceFilePath();

		MediaInfo info = new MediaInfo();
		info.open(new File(dataFileName));		

		String format = info.get(MediaInfo.StreamKind.Image, 0, "Format", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
		String width = info.get(MediaInfo.StreamKind.Image, 0, "Width", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
		String height = info.get(MediaInfo.StreamKind.Image, 0, "Height", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
		String bits = info.get(MediaInfo.StreamKind.Image, 0, "Bit depth", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
		String compressionMode = info.get(MediaInfo.StreamKind.Image, 0, "Compression mode", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
		
		Resource r = model.createResource();
		r.addLiteral(DC.format, format);
		r.addLiteral(model.createProperty("http://ogp.me/ns#image:height"), height);
		r.addLiteral(model.createProperty("http://ogp.me/ns#image:width"), width);
		model.getResource(url).addProperty(MCAS.mediainfo, r);
		System.out.println(model.getResource(url).addProperty(MCAS.mediainfo, r));
		FileWriter out = new FileWriter(modelFileName);

		printTurtle(model, out, cache);
	}

	private static void extractMediainfoAudio(String url) throws IOException {
		Cache cache = new Cache(url);

		// open model
		Model model = ModelFactory.createDefaultModel();
		String modelFileName = cache.getFilePath("data.ttl");
		File f = new File(modelFileName);

		if (f.exists()) {
			model.read(modelFileName);
		}

		String dataFileName = cache.getFilePath("data.mp3");

		MediaInfo info = new MediaInfo();
		info.open(new File(dataFileName));

		String fileExtension = info.get(MediaInfo.StreamKind.General, 0, "FileExtension", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
		String fileSize = info.get(MediaInfo.StreamKind.General, 0, "FileSize", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);

		String format = info.get(MediaInfo.StreamKind.Audio, 0, "Format", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
		String formatVersion = info.get(MediaInfo.StreamKind.Audio, 0, "Format_Version", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
		String formatProfile = info.get(MediaInfo.StreamKind.Audio, 0, "Format_Profile", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
		String codecId = info.get(MediaInfo.StreamKind.Audio, 0, "CodecID", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);

		String duration = info.get(MediaInfo.StreamKind.Audio, 0, "Duration", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
		String bitRateMode = info.get(MediaInfo.StreamKind.Audio, 0, "BitRate_Mode", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
		String bitRate = info.get(MediaInfo.StreamKind.Audio, 0, "BitRate", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);

		String channels = info.get(MediaInfo.StreamKind.Audio, 0, "Channels", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
		String channelPositions = info.get(MediaInfo.StreamKind.Audio, 0, "ChannelPositions", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);

		String samplingRate = info.get(MediaInfo.StreamKind.Audio, 0, "SamplingRate", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);
		String streamSize = info.get(MediaInfo.StreamKind.Audio, 0, "StreamSize", MediaInfo.InfoKind.Text, MediaInfo.InfoKind.Name);


		Resource r = model.createResource();

		r.addLiteral(model.createProperty("fileSize"), fileSize);
		r.addLiteral(model.createProperty("fileExtension"), fileExtension);

		r.addLiteral(model.createProperty("format"), format);
		r.addLiteral(model.createProperty("formatVersion"), formatVersion);
		r.addLiteral(model.createProperty("formatProfile"), formatProfile);
		r.addLiteral(model.createProperty("codecId"), codecId);

		r.addLiteral(model.createProperty("duration"), duration);

		r.addLiteral(model.createProperty("bitrate"), bitRate);
		r.addLiteral(model.createProperty("bitRateMode"), bitRateMode);

		r.addLiteral(model.createProperty("channels"), channels);
		r.addLiteral(model.createProperty("channelPositions"), channelPositions);

		r.addLiteral(model.createProperty("samplingRate"), samplingRate);
		r.addLiteral(model.createProperty("streamSize"), streamSize);


		model.getResource(url).addProperty(MCAS.mediainfo, r);
		System.out.println(model.getResource(url).addProperty(MCAS.mediainfo, r));
		FileWriter out = new FileWriter(modelFileName);

		printTurtle(model, out, cache);
	}

	private static void printTurtle(Model model, FileWriter writer, Cache cache) {
		try {
			System.out.println("Mediainfo try save");
			model.write(writer, "TURTLE");
		} finally {
			try {
				writer.close();

				try {
					Enqueuer.workerFinished(MCAS.mediainfo, cache);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (IOException closeException) {
				// ignore
				System.out.println("Fehler!!!");
			}
		}
	}

}