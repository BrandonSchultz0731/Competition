package sample;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class MainController {

  @FXML
  private TextField usernameEntered;
  @FXML
  private TextField passwordEntered;
  public static String currentUserName, currentUserAccountType, currentUserTeam;
  public static int currentUserWins, currentUserLosses;

  // Method to check login credentials.
  public void logInButtonPressed() {
    String user = usernameEntered.getText();
    String pass = passwordEntered.getText();

    //Read from file
    try {
      FileReader fr = new FileReader("Accounts.txt");
      BufferedReader br = new BufferedReader(fr);
      String str;
      boolean found = false;
      while ((str = br.readLine()) != null && !found) {
        if ((user + " " + pass).equals(str)) {
          found = true;
          str = br.readLine();
          this.currentUserName = str;
          str = br.readLine();
          String[] splitString = str.split("\\s+");
          this.currentUserAccountType = splitString[0];
          this.currentUserTeam = splitString[1];
          Stage stage = Main.getPrimaryStage();
          Parent root = FXMLLoader.load(getClass().getResource("LoggedIn.fxml"));
          stage.setScene(new Scene(root, 800, 600));
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
      er.printStackTrace();
    }
  }

  // Method for switching to create account scene.
  @FXML
  public void createAccountButtonPressed() throws IOException {
    Stage stage = Main.getPrimaryStage();
    Parent root = FXMLLoader.load(getClass().getResource("CreateNewAccount.fxml"));
    stage.setScene(new Scene(root, 800, 600));
    stage.show();
  }
}
