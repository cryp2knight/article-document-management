/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ams_project;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.swing.JOptionPane;


public class CreateArticleController implements Initializable {

    private User us;
    private int id;
    Database db = new Database();
    
    @FXML VBox vbx;
    @FXML TextField txtHeadline, txtAuthor, txtPath, txtCaption, txtPhotog;
    @FXML RadioButton rbAuthor, rbPhotog;
    @FXML ComboBox cmbSection;
    @FXML HTMLEditor txtBody;
    @FXML Button btnBrowse, btnSave;
    @FXML ImageView imgPic;
    @FXML Label lblHead;
    
    
    public void setUser(User s){
        this.us = s;
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ArrayList<Object> sec = db.getData("select section from Sections");
        cmbSection.getItems().clear();
        for(int i = 0; i < sec.size(); i++){
            String s = ((ArrayList<Object>) sec.get(i)).get(0).toString();
            cmbSection.getItems().add(s);
        }
    }
    
    
    @FXML
    public void addNote(ActionEvent e){
        addNotes pnlNote = new addNotes();
        String[] options = new String[]{"Cancel", "Ok"};
        int option = JOptionPane.showOptionDialog(null, pnlNote, "Add Note",
                                 JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                                 null, options, options[1]);
            if(option == 1){
                String sn = pnlNote.txtNote.getText();
                
                Label userl = new Label();
                userl.setText(this.us.getUsername());
                TextField notel = new TextField();
                notel.getStyleClass().add("txt");
                notel.setText(sn);
                Button btnl = new Button("X");
                btnl.getStyleClass().add("btn");
                
                VBox vb = new VBox();
                HBox hbl = new HBox();
                hbl.getChildren().add(btnl);
                hbl.getChildren().add(userl);
                vb.getChildren().add(hbl);
                vb.getChildren().add(notel);
                
                vbx.getChildren().add(vb);
                btnl.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        vbx.getChildren().remove(vb);
                    }
                });
                 
            }
    }
    
    public void setNotes(){
        for (int i = 0; i < vbx.getChildren().size(); i++){
            VBox vb = (VBox) vbx.getChildren().get(i);
            TextField notel = (TextField) vb.getChildren().get(1);
            String b = notel.getText();
            Date date = new Date();
            long time = date.getTime();
            Timestamp ts = new Timestamp(time);
            db.executeQuery("insert into Notes(article_id, staffer_id, status, timestamp) values(?,?,?,?)", new Object[]{id, this.us.getID(), b, ts});
        }
    }

    @FXML
    public void browseFile(ActionEvent event) {
    	FileChooser chooser = new FileChooser();
    	chooser.setTitle("Select Image");
        FileChooser.ExtensionFilter ext = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png","*.gif");
        chooser.getExtensionFilters().add(ext);
        
    	File file = chooser.showOpenDialog(new Stage());
        try{
            txtPath.setText(file.toString());
            imgPic.setFitHeight(300);
            imgPic.setFitWidth(300);
            Image img = new Image(file.toURI().toString());
            imgPic.setImage(img);
        }catch(Exception e){
            
        }
    }
    
    @FXML
    public void insertData(ActionEvent event){
        Date date = new Date();
        long time = date.getTime();
        Timestamp ts = new Timestamp(time);
        Object[] d = {txtHeadline.getText(), txtBody.getHtmlText(), cmbSection.getSelectionModel().getSelectedItem(), txtAuthor.getText(), ts, this.us.getID()};
        if (!(txtAuthor.getText().isEmpty() || txtHeadline.getText().isEmpty() || cmbSection.getSelectionModel().getSelectedItem().toString().isEmpty() || txtBody.getHtmlText().isEmpty()))
        {
            id = db.executeQuery("INSERT INTO Articles(headline, article_text, section, author, timestamp, staffer_id) values(?,?,?,?,?,?);", d);
            String fname = null;
            if(!(txtHeadline.getText().isEmpty() || txtCaption.getText().isEmpty())){
                try{
                    File source = new File(txtPath.getText());
                    fname = source.getName();
                    int ind = fname.indexOf('.');
                    String extension = fname.substring(ind);
                    File dest = new File(db.getFileDirectory()+time+"_"+id+extension); 
                    fname = time+"_"+id+extension;
                    Object ob[] = {id, txtCaption.getText(), fname, ts, txtPhotog.getText(), this.us.getID()};
                    db.executeQuery("INSERT INTO Photos(article_id, caption, image, timestamp, photographer, staffer_id) values(?,?,?,?,?,?)", ob);
                    Files.copy(source.toPath(), dest.toPath());
                }catch(Exception e){
                    System.out.println(e);
                }
                System.out.println(fname);
            }
            setNotes();
            
            refresh(event);
        }else{
            
        }
        
        
    }

    public void refresh(ActionEvent event){
        JOptionPane.showMessageDialog(null, "Article saved!");
         FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
         Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
         try {
            stage.setScene(new Scene((Pane) loader.load()));
            
         } catch (IOException ex) {
          
          System.out.print(ex);
        }
        DashboardController controller = loader.<DashboardController>getController();
        controller.setUser(this.us);
        controller.readArticle(id);
        stage.show();

    }
    
    boolean rbA = false;
    @FXML
    public void checkAuthor(ActionEvent event){
        if(rbA== false){
            txtAuthor.setText(us.getName());
            rbA = true;
        }else{
            txtAuthor.setText("");
            rbA = false;
        }
        txtAuthor.setDisable(rbA);
    }
    
    boolean rbP = false;
    @FXML
    public void checkPhotog(ActionEvent event){
        if(rbP== false){
            txtPhotog.setText(us.getName());
            rbP = true;
        }else{
            txtPhotog.setText("");
            rbP = false;
        }
        txtPhotog.setDisable(rbP);     
    }

}
