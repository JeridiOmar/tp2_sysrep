package bo;

import java.io.IOException;
import java.sql.*;

public class Bo {
    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
        Sender sender= new Sender();
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3308/bo?characterEncoding=utf8&useSSL=false&useUnicode=true", "root", "");
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("select * from sales");
        while (rs.next()) {
            sender.send(rs.getString(2) +";" + rs.getString(3) +";" + rs.getString(4) +";" + rs.getInt(5) + ";" +rs.getFloat(6) +";" + rs.getFloat(7) +";" + rs.getFloat(8) +";" + rs.getFloat(9));
            System.out.println(rs.getString(2) +";" + rs.getString(3) +";" + rs.getString(4) +";" + rs.getInt(5) + ";" +rs.getFloat(6) +";" + rs.getFloat(7) +";" + rs.getFloat(8) +";" + rs.getFloat(9));
        }
    }
}
