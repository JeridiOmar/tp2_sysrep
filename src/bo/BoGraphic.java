package bo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.List;

import org.jdesktop.swingx.JXTable;

public class BoGraphic extends JFrame{
    JFrame frame;
    JXTable table;
    JScrollPane scroll;
    JButton button;
    JButton refreshButton;
    JPanel tablePan;
    JPanel btnPan;
    DefaultTableModel model;
    TextField[] inputs;
    JPanel[] panels;
    JLabel[] labels;
    BoGraphic(Object[][] data, Connection con, String id, Map<Integer, Boolean> sent){
        inputs = new TextField[8];
        panels = new JPanel[8];
        labels = new JLabel[8];
        String[] columns = new String[]{
                "Date","Region","Product","Qty","Cost","Amt","Tax","Total"
        };
        for(int i=0;i<8;i++){
            panels[i] = new JPanel();
            labels[i] = new JLabel(columns[i]);
            labels[i].setPreferredSize(new Dimension(50, 20));;
            inputs[i] = new TextField(20);
            panels[i].add(labels[i]);
            panels[i].add(inputs[i]);
        }
        model = new DefaultTableModel(data, columns);
        frame = new JFrame("Branch Office "+id);
        frame.setLayout(new FlowLayout());
        tablePan = new JPanel();
        table = new JXTable(model);
        table.packAll();
        scroll = new JScrollPane(table);
        tablePan.add(scroll);
        refreshButton = new JButton("refresh");
        button = new JButton("add");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    PreparedStatement  ps = con
                            .prepareStatement("insert into sales (`date`,`region`,`product`,`qty`,`cost`,`amt`,`tax`,`total`) values ( ?, ?, ?, ? , ?, ?,?,?)", Statement.RETURN_GENERATED_KEYS);
                    ps.setDate(1, Date.valueOf(inputs[0].getText()));
                    ps.setString(2,inputs[1].getText());
                    ps.setString(3,inputs[2].getText());
                    ps.setInt(4, Integer.parseInt(inputs[3].getText()));
                    ps.setFloat(5, Float.parseFloat(inputs[4].getText()));
                    ps.setFloat(6, Float.parseFloat(inputs[5].getText()));
                    ps.setFloat(7, Float.parseFloat(inputs[6].getText()));
                    ps.setFloat(8, Float.parseFloat(inputs[7].getText()));
//                    ps.addBatch();
//                    ps.executeBatch();
                    int insertedId = -1;
                    int rowsAffected = ps.executeUpdate();
                    if (rowsAffected > 0){
                        try(ResultSet generatedKeys = ps.getGeneratedKeys()){
                            if (generatedKeys.next()) {
                                insertedId = generatedKeys.getInt(1);
                                sent.put(insertedId, false);
                            }
                        }
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                model.addRow(new Object[]{inputs[0].getText(), inputs[1].getText(), inputs[2].getText(), inputs[3].getText(), inputs[4].getText(), inputs[5].getText(), inputs[6].getText(), inputs[7].getText()});
                for (int i = 0; i<8 ; i++) inputs[i].setText("");
            }
        } );
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Sender sender = new Sender();
                int i=0;
                PreparedStatement ps = null;
                try {
                    ps = con.prepareStatement("update sales set `sent` = true where `id` = ?");
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                Vector<Vector> data2 = model.getDataVector();
                for(Map.Entry<Integer, Boolean> mapElement : sent.entrySet()){

                    if(!mapElement.getValue()) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("{" + "\"date\" : \"").append(Date.valueOf((String) data2.get(i).get(0))).append("\",").append("\"region\" : \"").append(data2.get(i).get(1)).append("\",").append("\"product\" : \"").append(data2.get(i).get(2)).append("\",").append("\"qty\" : ").append(Float.parseFloat((String) data2.get(i).get(3))).append(",").append("\"cost\" : ").append(Float.parseFloat((String) data2.get(i).get(4))).append(",").append("\"amt\" : ").append(Float.parseFloat((String) data2.get(i).get(5))).append(",").append("\"tax\" : ").append(Float.parseFloat((String) data2.get(i).get(6))).append(",").append("\"total\" : ").append(Float.parseFloat((String) data2.get(i).get(7))).append("}");
                        try {
                            ps.setInt(1, mapElement.getKey());
                            ps.addBatch();
                            mapElement.setValue(true);
                            sender.send(sb.toString());
                            ps.executeBatch();
                        } catch (SQLException | IOException throwables) {
                            throwables.printStackTrace();
                        }
                    }
                    i++;
                }
            }
        });
        btnPan = new JPanel();
        btnPan.setLayout(new GridLayout(10,1,10,15));
        for(int i=0;i<8;i++) btnPan.add(panels[i]);
        btnPan.add(button);
        btnPan.add(refreshButton);
        frame.add(tablePan);
        frame.add(btnPan);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException
    {
        //Sender sender= new Sender();
        List<Object[]> dbElements = new Vector<Object[]>();
        Map<Integer, Boolean> sent = new LinkedHashMap<Integer, Boolean>();
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/bo"+args[0]+"?characterEncoding=utf8&useSSL=false&useUnicode=true", "root", "");
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("select * from sales");
        while (rs.next()) {
            Object[] element = {rs.getString(2),rs.getString(3) , rs.getString(4) , Integer.toString(rs.getInt(5)) , Float.toString(rs.getFloat(6)) , Float.toString(rs.getFloat(7)) , Float.toString(rs.getFloat(8)) , Float.toString(rs.getFloat(9))};
            sent.put(rs.getInt(1),rs.getBoolean("sent"));
            dbElements.add(element);
            //sender.send(rs.getString(2) +";" + rs.getString(3) +";" + rs.getString(4) +";" + rs.getInt(5) + ";" +rs.getFloat(6) +";" + rs.getFloat(7) +";" + rs.getFloat(8) +";" + rs.getFloat(9), args[0]);
            System.out.println(rs.getString(2) +";" + rs.getString(3) +";" + rs.getString(4) +";" + rs.getInt(5) + ";" +rs.getFloat(6) +";" + rs.getFloat(7) +";" + rs.getFloat(8) +";" + rs.getFloat(9));
        }
        Object[][] obj = new Object[dbElements.size()][];
        for(int i=0; i<dbElements.size(); i++){
            obj[i] = dbElements.get(i);
        }
        new BoGraphic(obj, con, args[0], sent);
    }
}