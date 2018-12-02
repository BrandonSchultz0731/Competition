package Competition.Controller;


import Competition.Model.AlertBox;
import Competition.Model.Main;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class MainController implements Initializable {

  @FXML
  private TextField usernameEntered;
  @FXML
  private TextField passwordEntered;
  public static String currentUserName, currentUserAccountType, currentUserTeam;
  public static int userID = -1, currentUserID;

  /**
   * Method to check login credentials. Stores textfield entries for Username and Password into
   * String variables. A text file is then read through using a BufferedReader. Each separate entry
   * is compared to the String variables until a match is found. Once a match is found, it updates
   * the scene to the post log-in home scene. If no match is found, a pop-up alert box is triggered
   * passing this message to the user currently accessing the program and clears the password
   * textfield.
   *
   * @throws IOException - handled by try/catch code block should the file not be
   * found/inaccessible.
   */
  public void logInButtonPressed() {
    String user = usernameEntered.getText();
    String pass = passwordEntered.getText();

    if (user.equals("admin") && pass.equals("admin")) {
      Stage stage = Main.getPrimaryStage();
      try {
        Parent root = FXMLLoader.load(getClass().getResource("../View/AdminScene.fxml"));
        stage.setScene(new Scene(root, 800, 600));
      } catch (IOException ex) {
        ex.printStackTrace();
      }
      stage.show();
    } else {
      //Read from file
      try {
        FileReader fr = new FileReader("Accounts.txt");
        BufferedReader br = new BufferedReader(fr);
        String str;
        boolean found = false;
        while ((str = br.readLine()) != null && !found) {
          String[] checkForUserName = str.split("\\s+");
          if (checkForUserName[0].equals("ecun0000") && (user + " " + pass)
              .equals(checkForUserName[1] + " " + checkForUserName[2])) {
            found = true;
            str = br.readLine();
            this.currentUserName = str;
            str = br.readLine();
            String[] splitString = str.split("\\s+");
            this.currentUserAccountType = splitString[0];
            this.currentUserTeam = splitString[1];
            String[] splitForID = checkForUserName[3].split("userID=");
            this.currentUserID = Integer.parseInt(splitForID[1]);
            Stage stage = Main.getPrimaryStage();
            Parent root = FXMLLoader.load(getClass().getResource("../View/LoggedIn.fxml"));
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
          }
        }
        if (!found) {
          AlertBox.display("No Account", "Could Not Find Account. Please"
              + " Try Again or Create Account");
          passwordEntered.clear();
          passwordEntered.requestFocus();
        }
        br.close();

      } catch (IOException er) {
        System.out.println("error");
        er.printStackTrace();
      }
    }
  }

  /**
   * Generates the scene to create a new account.
   *
   * @throws IOException - Exception for FXML file to be found.
   */
  @FXML
  public void createAccountButtonPressed() throws IOException {
    Stage stage = Main.getPrimaryStage();
    Parent root = FXMLLoader.load(getClass().getResource("../View/CreateNewAccount.fxml"));
    stage.setScene(new Scene(root, 800, 600));
    stage.show();
  }

  // Initialize method used to set userID to next corresponding
  // ID for when a new account is created.
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    try {
      FileReader fr = new FileReader("Accounts.txt");
      BufferedReader br = new BufferedReader(fr);
      String string;
      while ((string = br.readLine()) != null) {
        if (string.contains("userID=")) {
          String[] split = string.split(" userID=");
          userID = Integer.parseInt(split[1]);
        }
      }
      br.close();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    userID++;
    System.out.println("Next available user ID for newly created account is: " + userID);
  }
}
