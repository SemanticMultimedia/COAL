package org.s16a.mcas.worker;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.rabbitmq.client.*;
import org.s16a.mcas.Hasher;
import org.s16a.mcas.MCAS;
import org.s16a.mcas.util.MediaInfo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.client.Invocation.Builder;

public class OnlineRequestWorker {
	private static final String TASK_QUEUE_NAME = MCAS.or.toString();

	public static void main(String[] argv) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		final Connection connection = factory.newConnection();
		final Channel channel = connection.createChannel();

		channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
		System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

		channel.basicQos(1);

		final Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
				String message = new String(body, "UTF-8");

				System.out.println(" [x] Received '" + message + "'");
				try {
                    makeAPIRequest(message);
				} finally {
					System.out.println(" [x] Done");
					channel.basicAck(envelope.getDeliveryTag(), false);
				}
			}
		};
		channel.basicConsume(TASK_QUEUE_NAME, false, consumer);
	}



	/* TODO: Make the Code better ;) */

	private static void makeAPIRequest(String url) throws IOException {

		// open model
		Model model = ModelFactory.createDefaultModel();
		String modelFileName = Hasher.getCacheFilename(url);
		File f = new File(modelFileName);

		if (f.exists()) {
			model.read(modelFileName);
		}

		String dataFileName = Hasher.getCacheFilename(url) + ".data";

        Client client = ClientBuilder.newClient();

        WebTarget resource = client.target("http://jsonplaceholder.typicode.com/posts/1");

        Builder request = resource.request();
        request.accept(MediaType.APPLICATION_JSON);

        Response response = request.get();

        if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
            System.out.println("Success! " + response.getStatus());
            System.out.println(response.getEntity());
            System.out.println(response.readEntity(String.class));
        } else {
            System.out.println("ERROR! " + response.getStatus());
            System.out.println(response.getEntity());
        }


		// Do something with the data in response.getEntity()
		// See other workers for example


		FileWriter out = new FileWriter(modelFileName);
		try {
			model.write(out, "TURTLE");
		} finally {
			try {
				out.close();
			} catch (IOException closeException) {
				// ignore
			}
		}
	}

}