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
  private static String currentUserType;
  @FXML
  private TextField usernameEntered;
  @FXML
  private TextField passwordEntered;
  @FXML
  private Label userNameLabel;
  @FXML
  private Label userTypeLabel;


  public void logInButtonPressed() {
    String user = usernameEntered.getText();
    String pass = passwordEntered.getText();
    this.name = user;

    //Read from file
    try {
      FileReader fr = new FileReader("Accounts.txt");
      BufferedReader br = new BufferedReader(fr);
      String str;
      boolean found = false;
      while ((str = br.readLine()) != null && !found) {
        if ((user.toLowerCase() + " " + pass).equals(str)) {
//          System.out.println("Logged In!!");
          found = true;

          currentUserType = br.readLine();//gets the current user type from the next line
          Stage stage = Main.getPrimaryStage();

          Parent root = FXMLLoader.load(getClass().getResource("LoggedIn.fxml"));

          stage.setScene(new Scene(root, 600, 400));
          stage.show();
        }
      }
      if (!found) {
        AlertBox.display("No Account", "Could Not Find Account. Please"
            + " Try Again or Create Account");
        passwordEntered.clear();
      }
      br.close();

    } catch (IOException er) {
      System.out.println("error");
    }
  }

  @FXML
  public void createAccountButtonPressed() throws IOException {
    Stage stage = Main.getPrimaryStage();

    Parent root = FXMLLoader.load(getClass().getResource("CreateNewAccount.fxml"));

    stage.setScene(new Scene(root, 600, 450));
    stage.show();
  }
  @FXML
  public void signOutButtonPressed() throws IOException {
    Stage stage = Main.getPrimaryStage();

    Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

    stage.setScene(new Scene(root, 600, 450));
    stage.show();
  }

  @FXML
  public void setNameTab() {
    userNameLabel.setText(name);
    userTypeLabel.setText(currentUserType);
  }

  public void GoToCreateAccount() throws IOException {
    Stage stage = Main.getPrimaryStage();

    Parent root = FXMLLoader.load(getClass().getResource("CreateAccount.fxml"));

    stage.setScene(new Scene(root, 600, 450));
    stage.show();

  }


}
