package org.example;

import javax.jms.*;

import org.apache.activemq.ActiveMQConnectionFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.example.Metrics.calculateResponseTime;

public class Producer {
    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String QUEUE_NAME = "TEST-QUEUE";
    private static final int MESSAGE_COUNT = 10000;
    private static final String MESSAGE_FILE_PATH = "src/main/resources/message.txt";

    public static void main(String[] args) {
        Connection connection = null;
        List<Long> responseTimes = new ArrayList<>();

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

            // 5. Create MessageProducer
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);

            // 6. Generate message content
            String messageContent = generateMessageContent();

            // 7. Produce messages and measure response times
            for (int i = 0; i < MESSAGE_COUNT; i++) {
                TextMessage message = session.createTextMessage(messageContent);

                long startTime = System.currentTimeMillis();
                producer.send(message);
                long responseTime = System.currentTimeMillis() - startTime;

                responseTimes.add(responseTime);

                System.out.println("Sent " + (i + 1) + " messages");
            }

            // 8. Calculate and print statistics
            System.out.println("\nProducer Statistics:");
            calculateResponseTime(responseTimes);

            // 9. Clean up
            producer.close();
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

    private static String generateMessageContent() {
        try {
            return new String(Files.readAllBytes(Paths.get(MESSAGE_FILE_PATH)));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}