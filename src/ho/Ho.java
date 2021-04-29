package ho;

import org.jdesktop.swingx.JXTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.List;
import java.util.Vector;

public class Ho extends JFrame {
    JFrame frame;
    JXTable table;
    JScrollPane scroll;
    JButton button;
    JPanel tablePan;
    JPanel btnPan;
    DefaultTableModel model;
    Recv recv;
    public Ho(Object[][] data, Connection con){

        String[] columns = new String[]{
                "Date","Region","Product","Qty","Cost","Amt","Tax","Total"
        };
        model = new DefaultTableModel(data, columns);
        frame = new JFrame("Head Office ");
        frame.setPreferredSize(new Dimension(1000, 400));
        frame.setLayout(new BoxLayout(frame.getContentPane(),BoxLayout.PAGE_AXIS));
        tablePan = new JPanel();
        tablePan.setPreferredSize(new Dimension(900, 300));
        table = new JXTable(model);
        table.packAll();
        table.setPreferredSize(new Dimension(900, 300));
        //table.getColumnModel().getColumn(0).setPreferredWidth(100);
        scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(900, 300));
        tablePan.add(scroll);
        button = new JButton("synchronize data");
        button.setSize(150,30);
        recv=new Recv(model);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    recv.recieve();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } );
        btnPan = new JPanel();

        btnPan.add(button);
        frame.add(tablePan);
        frame.add(btnPan);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        List<Object[]> dbElements = new Vector<Object[]>();
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/ho?characterEncoding=utf8&useSSL=false&useUnicode=true", "root", "");
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("select * from sales");
        while (rs.next()) {
            Object[] element = {rs.getString(2),rs.getString(3) , rs.getString(4) , Integer.toString(rs.getInt(5)) , Float.toString(rs.getFloat(6)) , Float.toString(rs.getFloat(7)) , Float.toString(rs.getFloat(8)) , Float.toString(rs.getFloat(9))};
            dbElements.add(element);
        }
        Object[][] obj = new Object[dbElements.size()][];
        for(int i=0; i<dbElements.size(); i++){
            obj[i] = dbElements.get(i);
        }
        new Ho(obj, con);
    }
}
