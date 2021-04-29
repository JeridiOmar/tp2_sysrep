package ho;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.jdesktop.swingx.JXTable;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.sql.PreparedStatement;
import java.util.StringTokenizer;


public class Recv {
    private final  String QUEUE_NAME = "synch";
    public DefaultTableModel model;

    public Recv(DefaultTableModel model) {
        this.model = model;
    }

    public  void recieve() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.56.101");
        factory.setUsername("admin");
        factory.setPassword("admin");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        Gson g=new Gson();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            try {
                Class.forName("com.mysql.jdbc.Driver");
                java.sql.Connection con = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/ho?characterEncoding=utf8&useSSL=false&useUnicode=true", "root", "");
                Sale sale =g.fromJson(message, Sale.class);
                PreparedStatement  ps = con
                        .prepareStatement("insert into sales (`date`,`region`,`product`,`qty`,`cost`,`amt`,`tax`,`total`) values ( ?, ?, ?, ? , ?, ?,?,?)");
                System.out.println(sale.date);
                System.out.println(sale.qty);
                ps.setDate(1, Date.valueOf(sale.date));
                ps.setString(2,sale.region);
                ps.setString(3,sale.product);
                ps.setInt(4, sale.qty);
                ps.setFloat(5, sale.cost);
                ps.setFloat(6, sale.amt);
                ps.setFloat(7, sale.tax);
                ps.setFloat(8, sale.total);
                System.out.println(" [x] Received '" + message + "'");
                Object[] element=
                        {Date.valueOf(sale.date),sale.region,sale.product,sale.qty,sale.cost,sale.amt,sale.tax,sale.total};
                ps.executeUpdate();
                model.addRow(element);
            }catch (Exception e){e.printStackTrace();}
        };
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });

    }
}
