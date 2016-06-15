package org.s16a.mcas;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.vocabulary.DC;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

public class Enqueuer {

	public static void workerFinished(Property finishedWorker, Cache cache) throws Exception {
        /* Enter the workers and the workers which are waiting for them to complete */
		Map<Property, List<Property>> workerDependencies = new HashMap<Property, List<Property>>();
        workerDependencies.put(MCAS.download, Arrays.asList(MCAS.converter, MCAS.mediainfo));
        workerDependencies.put(MCAS.converter, Arrays.asList(MCAS.segments));
        workerDependencies.put(MCAS.segments, Arrays.asList(MCAS.speech));

        Model model = ModelFactory.createDefaultModel();
        String modelFileName = cache.getFilePath("data.ttl");
        File modelFile = new File(modelFileName);

        if (modelFile.exists()) {
            model.read(modelFileName);
        }

        if(workerDependencies.containsKey(finishedWorker)) {
            for (Property sleepingWorker : workerDependencies.get(finishedWorker)) {
                if (model.getResource(cache.getUrl()).getProperty(sleepingWorker) == null) {
                    System.out.println("enqueue for " + sleepingWorker.toString());

                    String QUEUE_NAME = sleepingWorker.toString();
                    ConnectionFactory factory = new ConnectionFactory();
                    factory.setHost("localhost");
                    Connection connection = factory.newConnection();
                    Channel channel = connection.createChannel();
                    channel.queueDeclare(QUEUE_NAME, true, false, false, null);
                    String message = cache.getUrl();
                    channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());

                    System.out.println(" [x] Sent '" + message + "'");

                    channel.close();
                    connection.close();
                }
            }
		}

	}

}
