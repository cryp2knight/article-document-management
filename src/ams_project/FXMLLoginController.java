/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams_project;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javax.swing.JOptionPane;


/**
 *
 * @author Dell
 */
public class FXMLLoginController implements Initializable {
    
    Database db = new Database();
    Stage stage;
    @FXML WebView webview;
    
    @FXML
    private TextField username;
    
    @FXML
    private PasswordField password1;
    
    @FXML
    private void Login(ActionEvent event) {
        String un = username.getText();
        String pass = password1.getText();
        try{
            ArrayList<Object> c = (ArrayList<Object>)db.getData("Select * from Staffers where username='"+un+"'").get(0);
            String p = c.get(2).toString();
            if (pass.equals(p)){
                User s = new User(c.get(1).toString(), c.get(3).toString(), c.get(4).toString(), c.get(5).toString(), (int)c.get(0));

                FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
                Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
                try {
                    stage.setScene(new Scene(loader.load()));
                } catch (IOException ex) {
                   System.out.print(ex);
                }
                DashboardController controller = loader.<DashboardController>getController();
                controller.setUser(s);
                
                stage.show();
                stage.setMaximized(true);
                
                
            }else{
                JOptionPane.showMessageDialog(null, "Login Failed!");
            }

        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Login Failed!");
            System.out.println("Failed");
        }

    }
    


   
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
         WebEngine webengine = webview.getEngine();
         webengine.load("file:///"+db.getHTMLDirectory()+"welcomepage\\index.html");
    }    
   
    
}

class User{
    private String username,name, role, status;
    private int id;
    
    public User(String un, String n,  String r, String s, int id){
        this.username = un;
        this.name = n;
        this.id = id;
        this.role = r;
        this.status = s;
    }
    
    public String getUsername(){
        return this.username;
    }
    
    public String getRole(){
        return this.role;
    }
    
    public String getStatus(){
        return this.status;
    }
    
    public String getName(){
        return this.name;
    }
    
    public int getID(){
        return this.id;
    }
    
    
    
    

}
