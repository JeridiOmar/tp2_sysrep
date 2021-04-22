package bo;

import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.Vector;

public class Bo {
    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
        Sender sender= new Sender();
        List<String[]> dbElements = new Vector<String[]>();
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/bo?characterEncoding=utf8&useSSL=false&useUnicode=true", "root", "");
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("select * from sales");
        int i = 0;
        while (rs.next()) {
            String[] element = {rs.getString(2),rs.getString(3) , rs.getString(4) , Integer.toString(rs.getInt(5)) , Float.toString(rs.getFloat(6)) , Float.toString(rs.getFloat(7)) , Float.toString(rs.getFloat(8)) , Float.toString(rs.getFloat(9))};
            dbElements.add(element);
            sender.send(rs.getString(2) +";" + rs.getString(3) +";" + rs.getString(4) +";" + rs.getInt(5) + ";" +rs.getFloat(6) +";" + rs.getFloat(7) +";" + rs.getFloat(8) +";" + rs.getFloat(9));
            System.out.println(rs.getString(2) +";" + rs.getString(3) +";" + rs.getString(4) +";" + rs.getInt(5) + ";" +rs.getFloat(6) +";" + rs.getFloat(7) +";" + rs.getFloat(8) +";" + rs.getFloat(9));
        }
        //System.out.println(dbElements);
    }
}
