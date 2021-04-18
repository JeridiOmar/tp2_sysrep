package ho;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.sql.*;
import java.sql.PreparedStatement;
import java.util.StringTokenizer;


public class Recv {
    private final static String QUEUE_NAME = "sync";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            try {
                Class.forName("com.mysql.jdbc.Driver");
                java.sql.Connection con = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3308/ho?characterEncoding=utf8&useSSL=false&useUnicode=true", "root", "");
                StringTokenizer stringTokenizer=new StringTokenizer(message,";");
                PreparedStatement  ps = con
                        .prepareStatement("insert into sales  values ( ?, ?, ?, ? , ?, ?,?,?)");
                ps.setDate(1, Date.valueOf(stringTokenizer.nextToken()));
                ps.setString(2,stringTokenizer.nextToken());
                ps.setString(3,stringTokenizer.nextToken());
                ps.setInt(4, Integer.parseInt(stringTokenizer.nextToken()));
                ps.setFloat(5, Float.parseFloat(stringTokenizer.nextToken()));

                ps.setFloat(6, Float.parseFloat(stringTokenizer.nextToken()));
                ps.setFloat(7, Float.parseFloat(stringTokenizer.nextToken()));
                ps.setFloat(8, Float.parseFloat(stringTokenizer.nextToken()));

                System.out.println(" [x] Received '" + message + "'");
            }catch (Exception e){e.printStackTrace();}
        };
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });

    }
}
