package bo;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Sender {
    private final static String QUEUE_NAME = "synch";
    public void send(String msg, String bdId) throws IOException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.56.101");
        factory.setUsername("admin");
        factory.setPassword("admin");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(QUEUE_NAME+bdId, false, false, false, null);

            channel.basicPublish("", QUEUE_NAME+bdId, null, msg.getBytes());
            System.out.println(" [x] Sent '" + msg + "'");

        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
