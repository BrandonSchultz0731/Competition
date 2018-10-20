package sample;


import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Controller {
  private static String name;
  private static String userType;
  private static String currentUserType;

  ObservableList<String> accountTypeList = FXCollections.observableArrayList("Fan","Player","Manager");

  @FXML
  private TextField createUsername;

  @FXML
  private PasswordField createPassword;

  @FXML
  private TextField usernameEntered;
  @FXML
  private TextField passwordEntered;
  @FXML
  private ChoiceBox accountTypeChoice;
  @FXML
  private Label userNameLabel;
  @FXML
  private Label userTypeLabel;







public void logInButtonPressed(){
  String user = usernameEntered.getText();
  String pass = passwordEntered.getText();
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

        currentUserType = br.readLine();//gets the current user type from the next line
        Stage stage = Main.getPrimaryStage();

        Parent root = FXMLLoader.load(getClass().getResource("LoggedIn.fxml"));

        stage.setScene(new Scene(root,600,400));
        stage.show();
      }
    }
    if(!found){
      AlertBox.display("No Account","Could Not Find Account. Please"
          + " Try Again or Create Account");
      passwordEntered.clear();
    }
    br.close();

  }catch (IOException er){
    System.out.println("error");
  }


}

@FXML
  public void createAccountButtonPressed() throws IOException {
  Stage stage = Main.getPrimaryStage();

  Parent root = FXMLLoader.load(getClass().getResource("CreateAccount.fxml"));

  stage.setScene(new Scene(root,600,450));
  stage.show();
}
public void backButtonPressed() throws IOException{
  Stage stage = Main.getPrimaryStage();

  Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

  stage.setScene(new Scene(root,600,450));
  stage.show();
}
public int createButtonClicked() throws IOException{
  try{
    FileWriter fw = new FileWriter("Accounts.txt",true);
    PrintWriter pw = new PrintWriter(fw);

    String user = createUsername.getText();
    String pass = createPassword.getText();
    if(accountTypeChoice.getSelectionModel().isEmpty()){
      AlertBox.display("User Type Error","Please select a user type...");
      return 0;
    }
    userType = accountTypeChoice.getValue().toString();

    User account;

    if(userType.equals("Fan")){
      account = new Fan(name,user,pass);
    }
    else if(userType.equals("Player")){
      account = new Athlete(name,user,pass,new Team("Miami Heat"));
    }
    else if(userType.equals("Manager")){
      account = new Manager(name,user,pass,new Team("Miami Heat"));
    }
    else{
      System.out.println("No user type found");
    }

    pw.println(user.toLowerCase() + " " + pass.toLowerCase());
    pw.println(userType);

    pw.close();



    System.out.println(userType);


  }catch (IOException er){
    System.out.println("ERROR");
  }
  AlertBox.display("Account created","Account Successfully Created. Click"
      + " \'Ok\' to go sign in");
  Stage stage = Main.getPrimaryStage();

  Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

  stage.setScene(new Scene(root,600,450));
  stage.show();
  return 0;

}
@FXML
  public void setAccountTypeChoice(){
  accountTypeChoice.setItems(accountTypeList);
}

@FXML
  public void signOutButtonPressed() throws IOException{
  Stage stage = Main.getPrimaryStage();

  Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

  stage.setScene(new Scene(root,600,450));
  stage.show();
}
@FXML
  public void setNameTab(){
  userNameLabel.setText(name);
  userTypeLabel.setText(currentUserType);
}
public void GoToCreateAccount()throws IOException{
  Stage stage = Main.getPrimaryStage();

  Parent root = FXMLLoader.load(getClass().getResource("CreateAccount.fxml"));

  stage.setScene(new Scene(root,600,450));
  stage.show();

}



}
