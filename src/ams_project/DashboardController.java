
package ams_project;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javax.swing.JOptionPane;


public class DashboardController implements Initializable {
    
    Database db = new Database();
    private User user;
    private WebEngine webengine;
    private String aid = null;
    @FXML private Button btnEdit, btnDelete;
    @FXML private Accordion sections;
    @FXML private WebView webview1;
    @FXML private TabPane tabpane;
    @FXML private HBox toolbar;
    
    public void setUser(User u){
        btnEdit.setVisible(false);
        btnDelete.setVisible(false);
        this.user = u;
         if(this.user.getRole().equalsIgnoreCase("Editor")){
            btnEdit.setDisable(false);
            btnDelete.setDisable(false);
        }
    }
    
    public void refresh(ActionEvent event){
         FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
         Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
         try {
            stage.setScene(new Scene((Pane) loader.load()));
            
         } catch (IOException ex) {
          
          System.out.print(ex);
        }
        DashboardController controller = loader.<DashboardController>getController();
        controller.setUser(this.user);
        controller.initialize(null, null);
        
        stage.show();
    }
    
    @FXML
    public void showCreateArticlePane(ActionEvent event){
        Parent root = null;
        Tab t = new Tab();
        t.setText("Create Article");  
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateArticle.fxml"));
        CreateArticleController controller = null;
        try{
            root = loader.load();
            controller = loader.<CreateArticleController>getController();
            controller.setUser(user);
        }catch(IOException ex){
           System.out.print(ex);
        }
        t.setContent(root);
        t.setClosable(true);
        tabpane.getTabs().add(t);
        tabpane.selectionModelProperty().get().selectLast();
    }
    
    @FXML
    public void deleteArticle(ActionEvent event){
       int option = JOptionPane.showOptionDialog(null, "Delete this article?", "Delete", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
       if (option == JOptionPane.YES_OPTION){
           db.executeQuery("delete from Articles where article_id=?",new Object[]{aid} );
           JOptionPane.showMessageDialog(null, "Article Deleted!");
           refresh(event);
       }
    }
    
    @FXML
    public void editArticlePane(ActionEvent event){
        Parent root = null;
        Tab t = new Tab();
        t.setText("Edit Article");  
        FXMLLoader loader = new FXMLLoader(getClass().getResource("EditArticle.fxml"));
        try{
            root = loader.load();
            EditArticleController controller = loader.<EditArticleController>getController();
             controller.setUser(user);
            controller.EditArticle(aid); 
        }catch(IOException ex){
           System.out.print(ex);
        }
        t.setContent(root);
        t.setClosable(true);
        tabpane.getTabs().add(t);
        tabpane.selectionModelProperty().get().selectLast();
    }
    
    @FXML
    public void logout(ActionEvent event){
       FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLLogin.fxml"));
         Stage stage = (Stage)webview1.getScene().getWindow();
         try {
            stage.setScene(new Scene((Pane) loader.load()));
            
         } catch (IOException ex) {
          
          System.out.print(ex);
        }
        stage.show();
    }
    
    
    @FXML
    public void homeRefresh(ActionEvent event){
        webengine.loadContent(showArticles());
        btnEdit.setVisible(false);
        btnDelete.setVisible(false);
        tabpane.getTabs().get(0).setText("Your Articles");
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ArrayList<Object> sec = db.getData("select section from Sections");
        sections.getPanes().clear();
         webengine = webview1.getEngine();
         webengine.loadContent(showArticles());
         
        for(int i = 0; i < sec.size(); i++){
            String s = ((ArrayList<Object>) sec.get(i)).get(0).toString();
            System.out.println(s);
            TitledPane pane = new TitledPane();
            ListView anc = new ListView();
            ArrayList<Object> articles = db.getData("select * from Articles where Section='"+s+"' order by timestamp desc");
            for(int j =  0; j < articles.size(); j++){
                String headline = ((ArrayList<Object>) articles.get(j)).get(1).toString();
                anc.getItems().add(headline);
            }
            anc.setOnMouseClicked(new EventHandler<MouseEvent>(){
                @Override
                public void handle(MouseEvent event) {
                    tabpane.selectionModelProperty().get().selectFirst();
                    btnEdit.setVisible(true);
                    btnDelete.setVisible(true);
                    int index = anc.getSelectionModel().getSelectedIndex(); 
                    String headline = ((ArrayList<Object>) articles.get(index)).get(1).toString();
                    String body = ((ArrayList<Object>) articles.get(index)).get(2).toString();
                    String id = ((ArrayList<Object>) articles.get(index)).get(0).toString();
                    String author = ((ArrayList<Object>) articles.get(index)).get(5).toString();
                    String staff_id = ((ArrayList<Object>) articles.get(index)).get(4).toString();
                    String dateposted = ((ArrayList<Object>) articles.get(index)).get(7).toString();
                    String whoposted = ((ArrayList<Object>)db.getData("select staffer_name from Staffers where staffer_id="+staff_id).get(0)).get(0).toString();
                    
                    aid = id;
                    try{
                    ArrayList<Object> images = db.getData("select * from Photos where article_id='"+id+"'");
                    System.out.println(images);
                    String img = ((ArrayList<Object>) images.get(0)).get(4).toString();
                    String caption = ((ArrayList<Object>) images.get(0)).get(2).toString();
                    String photog = ((ArrayList<Object>) images.get(0)).get(6).toString();
                    webengine.loadContent(showArticle(headline, body, img, caption, author, whoposted, photog, dateposted));
                    tabpane.getTabs().get(0).setText(headline);
                    
                    }catch (Exception e){
                        webengine.loadContent(showArticle(headline, body, "", "", author, whoposted, "", dateposted));
                        tabpane.getTabs().get(0).setText(headline);
                    }
                }
                
            });
            pane.setContent(anc);
            pane.setText(s);
            sections.getPanes().add(pane);
        }
        tabpane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        tabpane.getTabs().get(0).setClosable(false);
    }
    
    public void readArticle(int id){
        btnEdit.setVisible(true);
        btnDelete.setVisible(true);
        ArrayList<Object> articles = db.getData("select * from Articles where article_id='"+id+"'");
        String headline = ((ArrayList<Object>) articles.get(0)).get(1).toString();
        String body = ((ArrayList<Object>) articles.get(0)).get(2).toString();
        String author = ((ArrayList<Object>) articles.get(0)).get(5).toString();
        String staff_id = ((ArrayList<Object>) articles.get(0)).get(4).toString();
        String dateposted = ((ArrayList<Object>) articles.get(0)).get(7).toString();
        String whoposted = ((ArrayList<Object>)db.getData("select staffer_name from Staffers where staffer_id="+staff_id).get(0)).get(0).toString();
        aid = String.valueOf(id);
        try{
            ArrayList<Object> images = db.getData("select * from Photos where article_id='"+id+"'");
            System.out.println(images);
            String img = ((ArrayList<Object>) images.get(0)).get(4).toString();
            String caption = ((ArrayList<Object>) images.get(0)).get(2).toString();
            String photog = ((ArrayList<Object>) images.get(0)).get(6).toString();
            webengine.loadContent(showArticle(headline, body, img, caption, author, whoposted, photog, dateposted));
            tabpane.getTabs().get(0).setText(headline);
                    
        }catch (Exception e){
            webengine.loadContent(showArticle(headline, body, "", "", author, whoposted, "", dateposted));
            tabpane.getTabs().get(0).setText(headline);
        }
    }
    
    public void refresh(ActionEvent event, String f){
        try{
            Parent root = FXMLLoader.load(getClass().getResource(f));
            Scene scene = new Scene(root);
            Stage window = (Stage)((Node) event.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        }catch(Exception e){
            
        }
    }
    
    public String showArticle(String h, String body, String img, String caption, String author, String whoposted, String photog, String dateposted){
        File ff = new File(db.getFileDirectory()+img);
        File htmls = new File(db.getHTMLDirectory());
        body = body.replaceAll("contenteditable=\"true\"", "");
        String imahe = null;
        if (img.isEmpty()){
           imahe = "";
        }else{
            imahe = "          <img class=\"card-img-top\" src=\""+ff.toURI()+"\" alt=\"Card image cap\">\n" +
"          <div class=\"card-footer text-muted\">\n" +
"            "+caption+" | \n" +
"            <a href=\"#\">"+photog+"</a>\n" +
"          </div>\n";
        }
        ArrayList<Object> getNotes = db.getData("select * from Notes where article_id="+aid+" order by note_id desc");
        String notess = "        <h3 class=\"my-4\">Notes</h3>\n";
        if(getNotes.size() == 0){
            notess+="No notes found.<br>"; 
        }
        for (int i = 0; i < getNotes.size(); i++){
         String a = ((ArrayList<Object>) getNotes.get(i)).get(2).toString(); //staffer id
         String whonoted = ((ArrayList<Object>)db.getData("select staffer_name from Staffers where staffer_id="+a).get(0)).get(0).toString();
         String b = ((ArrayList<Object>) getNotes.get(i)).get(3).toString(); //note text
         String c = ((ArrayList<Object>) getNotes.get(i)).get(4).toString(); //timestamp
         notess += "        <!-- Side Widget -->\n" +
"        <div class=\"card my-4\">\n" +
"          <h5 class=\"card-header\">"+whonoted+"</h5>\n" +
"          <div class=\"card-body\">\n" +
"            "+b+"" +
"          </div>\n" +
"          <div class=\"card-footer text-muted\">\n" +
"            Posted on "+c+"" +
"          </div>\n" +
"        </div>\n" ;
        }
        String bud = "<html>\n" +
"<head>\n" +
"  <link href=\""+htmls.toURI()+"vendor/bootstrap/css/bootstrap.min.css\" rel=\"stylesheet\">\n" +
"  <link href=\""+htmls.toURI()+"css/blog-home.css\" rel=\"stylesheet\">\n" +
"</head>\n" +
"\n" +
"<body>\n" +
"\n" +
"  <nav class=\"navbar navbar-expand-lg navbar-dark bg-dark fixed-top\">\n" +
"    <div class=\"container\">\n" +
"      <a class=\"navbar-brand\" href=\"#\">Article Management System</a>\n" +
"      \n" +
"    </div>\n" +
"  </nav>\n" +
"  <div class=\"container\">\n" +
"\n" +
"    <div class=\"row\">\n" +
"\n" +
"      <!-- Blog Entries Column -->\n" +
"      <div class=\"col-md-8\">\n" +
"        <h1 class=\"my-4\">"+h+"</h1>\n" +
"        <!-- Blog Post -->\n" +
"        <div class=\"card mb-4\">\n" + imahe +
"          <div class=\"card-body\">\n" +
"              <h6 class=\"card-title\">by <a href=\"#\">"+author+"</a></h6>\n" +
"            <p class=\"card-text\">"+body+"</p>\n" +
"          </div>\n" +
"          <div class=\"card-footer text-muted\">\n" +
"            Posted on "+dateposted+" by\n" +
"            <a href=\"#\">"+whoposted+"</a>\n" +
"          </div>\n" +
"        </div>\n" +
"\n" +
"      </div>\n" +
"      <div class=\"col-md-4\">\n" +
"\n" +
notess+
"\n" +
"      </div>\n" +
"\n" +
"    </div>\n" +
"\n" +
"  </div>\n" +
"  <footer class=\"py-5 bg-dark\">\n" +
"    <div class=\"container\">\n" +
"      <p class=\"m-0 text-center text-white\">END OF DOCUMENT</p>\n" +
"    </div>\n" +
"\n" +
"  </footer>\n" +
"\n" +
"  <script src=\""+htmls.toURI()+"vendor/jquery/jquery.min.js\"></script>\n" +
"  <script src=\""+htmls.toURI()+"vendor/bootstrap/js/bootstrap.bundle.min.js\"></script>\n" +
"\n" +
"</body>\n" +
"\n" +
"</html>";
        
        return bud;
    }
    
    
    public String showArticles(){
        File htmls = new File(db.getHTMLDirectory());
        ArrayList<Object> articles = db.getData("select * from Articles order by article_id desc");
        System.out.println(articles);
        String txt = "<html>\n" +
"<head>\n" +
"  <link href=\""+htmls.toURI()+"vendor/bootstrap/css/bootstrap.min.css\" rel=\"stylesheet\">\n" +
"  <link href=\""+htmls.toURI()+"css/blog-home.css\" rel=\"stylesheet\">\n" +
"</head>\n" +
"\n" +
"<body>\n" +
"\n" +
"  <nav class=\"navbar navbar-expand-lg navbar-dark bg-dark fixed-top\">\n" +
"    <div class=\"container\">\n" +
"      <a class=\"navbar-brand\" href=\"#\">Article Management System</a>\n" +
"      \n" +
"    </div>\n" +
"  </nav>\n" +
"  <div class=\"container\">\n" +
"\n" +
"    <div class=\"row\">\n" +
"       \n" +
"      <div class=\"col-md-12\">\n" +
"          <h1 class=\"my-4\">Your Articles</h1>\n" +
"      </div>";
        //////////////////
        for (int i = 0; i < articles.size(); i++){
            String id = ((ArrayList<Object>) articles.get(i)).get(0).toString();
            System.out.print(id);
            String dateposted = ((ArrayList<Object>) articles.get(i)).get(7).toString();
            String staff_id = ((ArrayList<Object>) articles.get(i)).get(4).toString();
            String whoposted = ((ArrayList<Object>)db.getData("select staffer_name from Staffers where staffer_id="+staff_id).get(0)).get(0).toString();
            String categ = ((ArrayList<Object>) articles.get(i)).get(3).toString();
            String head = ((ArrayList<Object>) articles.get(i)).get(1).toString();
            ArrayList<Object> images = db.getData("select * from Photos where article_id='"+id+"'");
            String img = "";
            try{
            img = ((ArrayList<Object>) images.get(0)).get(4).toString();
            }catch(Exception e){
                System.out.print("errr");
                img = "";
            }
            String imahe1 = "";
            if(!img.isEmpty()){
                File ff = new File(db.getFileDirectory()+img);
                imahe1 = "<img class=\"card-img-top\" src=\""+ff.toURI()+"\" alt=\"Card image cap\">";
            }
            String acl = "  <div class=\"col-md-4\">\n" +
"        <!-- Blog Post -->\n" +
"        <div class=\"card mb-4\">\n" +imahe1+
"          <div class=\"card-body\">\n" +
"              <h6 class=\"card-title\"><b>"+categ+"</b></h6>\n" +
"            <p class=\"card-text\">"+head+"</p>\n" +
"          </div>\n" +
"          <div class=\"card-footer text-muted\">\n" +
"            Posted on "+dateposted+" by\n" +
"            <a href=\"#\">"+whoposted+"</a>\n" +
"          </div>\n" +
"        </div>\n" +
"      </div>";
            txt = txt+ acl;
            
            
        }
        //////////////////
        
        txt = txt+ "  </div>\n" +
"\n" +
"  </div>\n" +
"  <script src=\""+htmls.toURI()+"vendor/jquery/jquery.min.js\"></script>\n" +
"  <script src=\""+htmls.toURI()+"vendor/bootstrap/js/bootstrap.bundle.min.js\"></script>\n" +
"\n" +
"</body>\n" +
"\n" +
"</html>";
        return txt;
    }
    
    
    
    @FXML
    private void printJob(ActionEvent e){
        System.out.print("testingg");
       
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(webview1.getScene().getWindow())){
            webengine.print(job);
            job.endJob();
        }  
    }
}





