package org.s16a.mcas.worker;

import com.hp.hpl.jena.rdf.model.Property;
import com.rabbitmq.client.*;
import org.s16a.mcas.Cache;
import org.s16a.mcas.Enqueuer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

abstract class PythonWorker {

    public static void executePythonWorker (final Property taskQueueName, final String pathToPythonWorker, final String successMessage) throws Exception, IOException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(System.getenv().get("RABBIT_HOST"));
        //factory.setHost("localhost");

        final Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();

        channel.queueDeclare(taskQueueName.toString(), true, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        channel.basicQos(1);

        final Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");

                System.out.println(" [x] Received '" + message + "'");

                try {
                    Cache cache = new Cache(message);

                    Process p = Runtime.getRuntime().exec(pathToPythonWorker + cache.getPath());
                    BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));

                    String line;
                    String lastLine = "";
                    while ((line = in.readLine()) != null) {
                        lastLine = line;
                    }

                    if (lastLine.equals("0")) {
                        System.out.println("[x] " + successMessage);
                        Enqueuer.workerFinished(taskQueueName, cache);
                    } else {
                        System.out.println("[E] SOMETHING WENT WRONG");

                    }
                } catch (Exception e) {
                        System.out.println(e.toString());
                }
                finally {
                    System.out.println(" [x] DONE");
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }

            }
        };

        channel.basicConsume(taskQueueName.toString(), false, consumer);
    }

}
