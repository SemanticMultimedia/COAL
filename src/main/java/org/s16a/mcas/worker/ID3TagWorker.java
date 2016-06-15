package org.s16a.mcas.worker;

import com.rabbitmq.client.*;
import org.s16a.mcas.Cache;
import org.s16a.mcas.MCAS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.IOException;

public class ID3TagWorker {

    private static final String TASK_QUEUE_NAME = MCAS.id3tags.toString();

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
                    getID3Tags(message);
                } finally {
                    System.out.println(" [x] Done");
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            }
        };
        channel.basicConsume(TASK_QUEUE_NAME, false, consumer);
    }

    private static void getID3Tags(String url) throws IOException {
        Cache cache = new Cache(url);

        Process p = Runtime.getRuntime().exec("/usr/bin/python ./src/main/java/org/s16a/mcas/worker/ID3TagWorker.py " + cache.getPath());
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));

        String line;
        String lastLine = "";
        while ((line = in.readLine()) != null) {
            lastLine = line;
        }

        if (lastLine.equals("0")) {
            System.out.println("[x] Extracted segments successfully");
        } else {
            System.out.println("[E] SOMETHING WENT WRONG");
        }
    }


}
