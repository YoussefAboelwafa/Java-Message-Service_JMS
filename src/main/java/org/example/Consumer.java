package org.example;

import javax.jms.*;

import org.apache.activemq.ActiveMQConnectionFactory;

import java.util.ArrayList;
import java.util.List;

import static org.example.Metrics.calculateLatency;
import static org.example.Metrics.calculateResponseTime;

public class Consumer {
    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String QUEUE_NAME = "TEST-QUEUE";
    private static final int MESSAGE_COUNT = 10000;

    public static void main(String[] args) {
        Connection connection = null;
        List<Long> responseTimes = new ArrayList<>();
        List<Long> latencies = new ArrayList<>();

        try {
            // 1. Create ConnectionFactory
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);

            // 2. Create Connection
            connection = connectionFactory.createConnection();
            connection.start();

            // 3. Create Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // 4. Create Destination (Queue)
            Destination destination = session.createQueue(QUEUE_NAME);

            // 5. Create MessageConsumer
            MessageConsumer consumer = session.createConsumer(destination);

            // 6. Consume messages and measure performance
            System.out.println("Waiting for messages...");
            for (int i = 0; i < MESSAGE_COUNT; i++) {
                long startTime = System.currentTimeMillis();
                Message message = consumer.receive();
                long responseTime = System.currentTimeMillis() - startTime;
                responseTimes.add(responseTime);

                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;

                    // Calculate latency if timestamp was set by producer
                    if (message.getJMSTimestamp() > 0) {
                        long latency = System.currentTimeMillis() - message.getJMSTimestamp();
                        latencies.add(latency);
                    }

                    System.out.println("Received " + (i + 1) + " messages");
                }
            }

            // 7. Calculate and print statistics
            System.out.println("\nConsumer Statistics:");
            calculateResponseTime(responseTimes);
            if (!latencies.isEmpty()) {
                System.out.println("\nLatency Statistics:");
                calculateLatency(latencies);
            } else {
                System.out.println("\nNo latency data available.");
            }

            // 8. Clean up
            consumer.close();
            session.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}