/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams_project;

/**
 *
 * @author Dell
 */

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JOptionPane;

public class Database {
    private String filedir = "C:\\Users\\Dell\\Documents\\Images\\";
    private String HTMLdir = "C:\\Users\\Dell\\Desktop\\external\\";
    private String connString = "jdbc:ucanaccess://C:\\Users\\Dell\\Documents\\OOPdb.accdb";
    private String query;
    
    public String getFileDirectory(){
        return this.filedir;
    }
    public String getHTMLDirectory(){
        return this.HTMLdir;
    }
    
    public Database(){
        try{
            File file = new File("directory.txt"); 
            Scanner sc = new Scanner(file); 
            filedir = sc.nextLine();
            connString += sc.nextLine();
            while (sc.hasNextLine()) 
              System.out.println(sc.nextLine());
        }catch(IOException e){
            System.out.println(e);
        }
    }
    
    public ArrayList<Object> getData(String qry){
        ArrayList<Object> data = new ArrayList<>();
         try
        {
            Connection conn=DriverManager.getConnection(connString);
            Statement stment = conn.createStatement();
            ResultSet rs = stment.executeQuery(qry);
            ResultSetMetaData rsmd = rs.getMetaData();

            int columnsNumber = rsmd.getColumnCount();
  
            while (rs.next()){
               ArrayList<Object> row_data = new ArrayList<>();
               for (int i = 1; i <= columnsNumber; i++){
                   row_data.add(rs.getObject(i));
               }
               data.add(row_data);
            }
        }
        catch(SQLException err)
        {
            System.out.println(err);
        }
       this.query = qry;
       return data;
    }
    
    public int executeQuery(String q, Object[] object){
        int key = 0;
        PreparedStatement pstmt = null;
        try
        {
            Connection conn=DriverManager.getConnection(connString);
            pstmt = conn.prepareStatement(q);
            int p = pstmt.getParameterMetaData().getParameterCount();
            System.out.println(p);
            
            for(int j = 0; j<p; j++){
                pstmt.setObject(j+1, object[j]);
            }
            pstmt.execute();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if(rs.next())
             {
                key = rs.getInt(1);
            }
            
            
            pstmt.close();
        }
        catch(SQLException err)
        {
            JOptionPane.showMessageDialog(null, "Error");
            System.out.println(err);
        }
        return key;
    }
    
    
    public void setQuery(String q){
        this.query = q;
    }
    
    public void setParam(PreparedStatement ps, int parameterIndex, Object object) throws SQLException {
        if (object instanceof Timestamp) {
            ps.setTimestamp(parameterIndex, (Timestamp) object);
        } else if (object instanceof Date) {
            ps.setDate(parameterIndex, (Date) object);
        } else if (object instanceof String) {
            ps.setString(parameterIndex, (String) object);
        } else if (object instanceof Integer) {
            ps.setInt(parameterIndex, ((Integer) object).intValue());
        } else if (object instanceof Long) {
            ps.setLong(parameterIndex, ((Long) object).longValue());
        } else if (object instanceof Boolean) {
            ps.setBoolean(parameterIndex, ((Boolean) object).booleanValue());
        } else {
            ps.setObject(parameterIndex, object);
        }
    }

    
}
