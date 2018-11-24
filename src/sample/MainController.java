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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MainController {

  @FXML
  private TextField usernameEntered;
  @FXML
  private TextField passwordEntered;
  public static String currentUserName, currentUserAccountType, currentUserTeam;

  @FXML
  private AnchorPane mainAnchorPane;
  @FXML
  private AnchorPane topAnchorPane;
  @FXML
  private AnchorPane bottomAnchorPane;
  @FXML
  private AnchorPane midAnchorpane;
  @FXML
  private Label accountDetailLabel;

  /**
   * Method to check login credentials. Stores textfield entries for Username and Password into String variables. A
   * text file is then read through using a BufferedReader. Each separate entry is compared to the String variables
   * until a match is found. Once a match is found, it updates the scene to the post log-in home scene. If no match is
   * found, a pop-up alert box is triggered passing this message to the user currently accessing the program and clears
   * the password textfield.
   *
   * @exception IOException - handled by try/catch code block should the file not be found/inaccessible.
   */
  public void logInButtonPressed() {
    String user = usernameEntered.getText();
    String pass = passwordEntered.getText();

    //Read from file
    try {
      FileReader fr = new FileReader("Accounts.txt");
      BufferedReader br = new BufferedReader(fr);
      String str;

      //Boolean used in file reading loop.
      boolean found = false;

      //While loops which compares contents of file to String variables above try/catch.
      while ((str = br.readLine()) != null && !found) {
        String[] checkForUserName = str.split("\\s+");
        if (checkForUserName[0].equals("ecun0000") && (user + " " + pass).equals(checkForUserName[1] + " "
                + checkForUserName[2])) {
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

      //No match found, pop-up alert box.
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


  /**
   * Generates the scene to create a new account.
   *
   * @throws IOException - Exception for FXML file to be found.
   */
  @FXML
  public void createAccountButtonPressed() throws IOException {
    Stage stage = Main.getPrimaryStage();
    Parent root = FXMLLoader.load(getClass().getResource("CreateNewAccount.fxml"));
    stage.setScene(new Scene(root, 800, 600));
    stage.show();
  }
}
