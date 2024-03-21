/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;



public class connectdb {
    String url = "jdbc:sqlite:mail.db";
    ClientWin win;
    public connectdb(ClientWin win) {
        Connection conn = null;
        this.win = win;
        try
        {
            conn = DriverManager.getConnection(url);
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
        finally
        {
            try
            {
                if (conn != null)
                {
                    conn.close();
                }
            } 
            catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
    
    private Connection connect()
    {
        Connection conn = null;
        try 
        {
            conn = DriverManager.getConnection(url);
        } 
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
        return conn;
    }
    
    public void createNewTable() {
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS maildata (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	sender text NOT NULL,\n"
                + "	receiver text NOT NULL,\n"
                + "	senddate text NOT NULL,\n"
                + "	subject text NOT NULL,\n"
                + "	message text NOT NULL\n"
                + ");";
        
        try (Connection conn = DriverManager.getConnection(url);
                Statement stmt = conn.createStatement())
        {
            stmt.execute(sql);
        } 
        catch (SQLException e) 
        {
            System.out.println(e.getMessage());
        }
    }
    
    public void insert(String sender, String receiver, String senddate, String subject, String message)
    {
        String sql = "INSERT INTO maildata(sender,receiver,senddate,subject,message) VALUES(?,?,?,?,?)";

        try (Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, sender);
            pstmt.setString(2, receiver);
            pstmt.setString(3, senddate);
            pstmt.setString(4, subject);
            pstmt.setString(5, message);
            pstmt.executeUpdate();
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }
    
    public void delete(int id) 
    {
        String sql = "DELETE FROM maildata WHERE id = ?";

        try (Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } 
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void selectAll()
    {
        String sql = "SELECT id, sender, subject FROM maildata";
        win.databaseMsg.setText("");
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            
            while (rs.next()) {
                win.databaseMsg.append("Id : " + rs.getInt("id") + "  " + "Sender : " + rs.getString("sender") + "  " + "Subject : " + rs.getString("subject") + "\n");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void selectOne(int id, boolean get, boolean re)
    {
        String sql = "SELECT sender, receiver, senddate, subject, message FROM maildata WHERE id = ?";
        win.databaseMsg.setText("");
        try (Connection conn = this.connect();
             PreparedStatement pstmt  = conn.prepareStatement(sql)){
            pstmt.setInt(1,id); 
            ResultSet rs =  pstmt.executeQuery();
            rs.next();
            if(!get)
            {
                win.databaseMsg.append("Mail id : " + id + "\n");
                win.databaseMsg.append("Mail From : " + rs.getString("sender") + "\n");
                win.databaseMsg.append("Mail To : " + rs.getString("receiver") + "\n");
                win.databaseMsg.append("Mail Date : " + rs.getString("senddate") + "\n");
                win.databaseMsg.append("Subject : " + rs.getString("subject") + "\n");
                win.databaseMsg.append("Message :\n" );
                win.databaseMsg.append(rs.getString("message"));
            }
            else
            {
                if(!re)
                {
                    win.login.setEnabled(true);
                    String tmp = rs.getString("sender") ;
                    win.acc.setText(tmp);
                    win.pass.setText("");
                    win.pass.setEnabled(true);
                    win.checkAuth.setEnabled(false);
                    
                    win.yourEmailAddress.setText(tmp);
                    tmp = rs.getString("subject");
                    win.subjectValue.setText(tmp);
                    tmp = rs.getString("message");
                    win.emailMsg.setText(tmp);
                }
                else
                {
                    win.login.setEnabled(true);
                    String tmp = rs.getString("receiver") ;
                    win.acc.setText(tmp);
                    win.pass.setText("");
                    win.pass.setEnabled(true);
                    win.checkAuth.setEnabled(false);
                    win.yourEmailAddress.setText(tmp);
                    tmp = rs.getString("subject");
                    win.subjectValue.setText("RE : " + tmp);
                    tmp = rs.getString("sender");
                    win.receiverAddr.setText(tmp);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            win.databaseMsg.append("The mail id is not found.");
        }
    }
}
