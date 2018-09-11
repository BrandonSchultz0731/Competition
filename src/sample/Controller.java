package sample;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javax.xml.soap.Text;

public class Controller {

private static String name;
private static String team;

  ObservableList<String> teamStatus = FXCollections.observableArrayList("Team 1",
      "Team 2","Team 3");

  @FXML
  private TextField nameInput;
  @FXML
  private TextField passInput;
  @FXML
  private TextField usernameCreate;
  @FXML
  private TextField passCreate;
  @FXML
  private ChoiceBox teamChoiceBox;
  @FXML
  private Label welcomeLabel;




  @FXML
  void logInPressed(ActionEvent event) throws IOException {
    //System.out.println("YOOOOO");
    String user = nameInput.getText();
    String pass = passInput.getText();
    this.name = user;


    //Read from file
    try{
      FileReader fr = new FileReader("Accounts.txt");
      BufferedReader br = new BufferedReader(fr);
      String str;
      boolean found = false;
      while((str = br.readLine()) != null && !found){
        if((user.toLowerCase() + " " + pass).equals(str)){
//          System.out.println("Logged In!!");
          found = true;
          this.team = br.readLine();

          Stage stage = Main.getPrimaryStage();

          Parent root = FXMLLoader.load(getClass().getResource("LoggedIn.fxml"));

          stage.setScene(new Scene(root,600,400));
          stage.show();
        }
      }
      if(!found){
        AlertBox.display("No Account","Could Not Find Account. Please"
            + " Try Again or Create Account");
        passInput.clear();
      }
      br.close();

    }catch (IOException er){
      System.out.println("error");
    }






  }
  @FXML
  void logOutPressed(ActionEvent event) throws IOException{
    Stage stage = Main.getPrimaryStage();

    Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

    stage.setScene(new Scene(root,287,372));
    stage.show();

  }
  @FXML
  void createClicked(ActionEvent event) throws IOException{
    try{
      FileWriter fw = new FileWriter("Accounts.txt",true);
      PrintWriter pw = new PrintWriter(fw);

      String user = usernameCreate.getText();
      String pass = passCreate.getText();

      pw.println(user.toLowerCase() + " " + pass.toLowerCase());
      pw.println(teamChoiceBox.getValue());

      //test = " " + (String)(teamChoiceBox.getValue());
      pw.close();



    }catch (IOException er){
      System.out.println("ERROR");
    }
    AlertBox.display("Account created","Account Successfully Created. Click"
        + " \'Ok\' to go sign in");
    Stage stage = Main.getPrimaryStage();

    Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

    stage.setScene(new Scene(root,287,372));
    stage.show();

  }

  @FXML
  void createActClicked(ActionEvent event) throws  IOException{
    Stage stage = Main.getPrimaryStage();

    Parent root = FXMLLoader.load(getClass().getResource("CreateAccount.fxml"));

    stage.setScene(new Scene(root,287,372));
    stage.show();
  }
  @FXML
  private void setTeamChoiceBox(){
    teamChoiceBox.setItems(teamStatus);
  }
  @FXML
  private void welcomeSelected(){
    welcomeLabel.setText("Welcome, " + Controller.name + ", to " + Controller.team);

  }
  @FXML
  private void signOutSelected(){
    System.out.println("The sign out tab is selected");
  }
  @FXML
  private void statsSelected(){
    System.out.println("The Stats tab is selected");
  }
  @FXML
  private void calSelected(){
    System.out.println("The Calendar tab is selected");
  }

  @FXML
  void closeProgram(){
    Platform.exit();
  }

  @FXML
  void minimizeProg(){
    Stage stage = Main.getPrimaryStage();
    stage.setIconified(true);
  }

  @FXML
  void maxProgWindow(){
    Stage stage = Main.getPrimaryStage();
    stage.setMaximized(true);
  }

}
