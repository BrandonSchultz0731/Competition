package Competition.Controller;

import Competition.Model.Athlete;
import Competition.Model.Fan;
import Competition.Model.Main;
import Competition.Model.Manager;
import Competition.Model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.SQLOutput;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
/* Format of text files created or appended to when account created.
 *
 * ---Accounts.txt---
 * ecun0000 [userName] [passWord] userID=[#]
 * [firstName] [lastName]
 * [accountType] [teamName]
 *
 * ---Athletes.txt---
 * [firstName] [lastName]
 * [teamName] [wins] [losses]
 *
 * ---Teams.txt---
 * [teamName] [wins] [losses]
 * [managerFirstName] [managerLastName]
 *
 * ---[teamName].txt---
 * [athleteLastName] [athleteFirstName]
 */

// Controller class for creating a new account.
public class CreateNewAccountController implements Initializable {

  //List pushed into Choicebox accountTypeChoice.
  private ObservableList<String> accountTypeList = FXCollections
      .observableArrayList("Fan", "Athlete", "Manager");

  @FXML
  private ChoiceBox accountTypeChoice;
  @FXML
  private TextField createUsername;
  @FXML
  private PasswordField createPassword;
  @FXML
  private TextField createFirstName;
  @FXML
  private TextField createLastName;
  @FXML
  private TextField createTeam;
  @FXML
  private Label createTeamLabel;


  boolean choiceClicked = false;

  /**
   * Sets which textfields/labels appear on account creation scene. Options based on account type
   * chosen from Choicebox. Sets boolean choiceClicked to true.
   */
  @FXML
  public void setAccountTypeChoice() {

    if (!choiceClicked) {
      accountTypeChoice.getSelectionModel().selectedIndexProperty()
          .addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue,
                Number newValue) {
              if (newValue.equals(2)) {
                //Manager selected
                createTeam.setDisable(false);
                createTeam.setVisible(true);
                createTeamLabel.setDisable(false);
                createTeamLabel.setVisible(true);
              } else {
                createTeam.setDisable(true);
                createTeam.setVisible(false);
                createTeam.setText("");
                createTeamLabel.setDisable(true);
                createTeamLabel.setVisible(false);
              }
            }
          });
    }
    choiceClicked = true;
  }

  /**
   * Sets scene back to main log-in scene.
   *
   * @throws IOException - FXML file accessible/inaccessible.
   */
  public void backButtonPressed() throws IOException {
    Stage stage = Main.getPrimaryStage();
    Parent root = FXMLLoader.load(getClass().getResource("../View/main.fxml"));
    stage.setScene(new Scene(root, 800, 600));
    stage.show();
  }

  /**
   * Creates an account for the specified type from the ChoiceBox, stores the information from
   * textfields into String type variables, and writes these variables into a text file for storage.
   * When an account is successfully created, a pop-up alert box displays a message saying it was
   * successful and to press "OK" to return to the log-in scene and enter the newly created account
   * credentials to log-in to the program.
   *
   * @return 0 - program runs without error.
   * @throws IOException - Output file error check. Checks to see that the file being written to
   * exists and can be written.
   */
  public void createButtonClicked() throws IOException {
    String userType = accountTypeChoice.getValue().toString();
    Alert alert;
    // Error checking: No spaces allowed in any text fields.
    if (createUsername.getText().contains(" ") || createPassword.getText().contains(" ")
        || createFirstName.getText().contains(" ") || createLastName.getText().contains(" ")
        || createTeam.getText().contains(" ")) {
      System.out.println("Space found one or more of the text fields.");
      alert = new Alert(AlertType.ERROR);
      alert.setTitle("Input Error");
      alert.setHeaderText("Text Fields Cannot Contain Spaces");
      alert.setContentText("One or more spaces found in input fields.");
      alert.showAndWait();
      return;
    }
    // Error Checking: Password must be at least 6 characters long.
    if (createPassword.getText().length() < 6) {
      System.out.println("Password too short, must contain at least 6 characters.");
      alert = new Alert(AlertType.ERROR);
      alert.setTitle("Invalid Password Length");
      alert.setHeaderText("Passwoord Too Short");
      alert.setContentText("Please enter a password that is at least 6 characters long.");
      alert.showAndWait();
      createPassword.setText("");
      return;
    }
    // Error Checking: Checking if username and/or team name already exists.
    try (FileReader fr = new FileReader("Accounts.txt");
        BufferedReader br = new BufferedReader(fr)){
      String line;
      while (true){
        line = br.readLine();
        if (line == null){
          break;
        }
        if(line.contains(" " + createUsername.getText() + " ")){
          alert = new Alert(AlertType.ERROR);
          alert.setTitle("Username Error");
          alert.setHeaderText("Username Already Exists.");
          alert.setContentText("Entered username already exists, please enter a new unique username.");
          alert.showAndWait();
          createUsername.requestFocus();
          br.close();
          return;
        } else if (userType.equals("Manager") && line.contains(" " + createTeam.getText())){
          alert = new Alert(AlertType.ERROR);
          alert.setTitle("Team Name Error");
          alert.setHeaderText("Team Name Already Exists Or Too Similar.");
          alert.setContentText("Entered username already exists or is too similar to an existing team.\n\nPlease enter a new unique username.");
          alert.showAndWait();
          createTeam.requestFocus();
          br.close();
          return;
        }
      }
    }catch (IOException ex){
      ex.printStackTrace();
    }

    try {
      FileWriter accountFW = new FileWriter("Accounts.txt", true);
      PrintWriter accountPW = new PrintWriter(accountFW);

      //Stores text from textfields into String variables used in implementing objects of type User.
      // (Fan, Athlete, Manager)
      String user = createUsername.getText();
      String pass = createPassword.getText();
      String first = createFirstName.getText();
      String last = createLastName.getText();
      String team = createTeam.getText();
      User account;

      //Switch case dictated by chosen account type. Creates objects of User.
      switch (userType) {
        case "Fan":
          account = new Fan(user, pass, first, last, userType);
          break;
        case "Athlete":
          account = new Athlete(user, pass, first, last, userType);

          // Adds new athlete to Athletes.txt file
          FileWriter athleteFW = new FileWriter("Athletes.txt", true);
          PrintWriter athletePW = new PrintWriter(athleteFW);
          athletePW.println(account.getFirstName() + " " + account.getLastName()
              + " userID=" + MainController.userID);
          athletePW.println(account.getTeam() + " 0 0");
          athletePW.close();
          break;
        case "Manager":
          account = new Manager(user, pass, first, last, userType, team);

          // Add new team to Teams.txt file
          FileWriter teamFW = new FileWriter("Teams.txt", true);
          PrintWriter teamPW = new PrintWriter(teamFW);
          teamPW.println(account.getTeam() + " 0 0");
          teamPW.println(account.getFirstName() + " " + account.getLastName()
              + " userID=" + MainController.userID);
          String newTeamFile = "../" + account.getTeam() + ".txt";
          File file = new File(newTeamFile);
          boolean x = file.createNewFile();
          if(x){
            System.out.println("File creation was successful.");
          }
          teamPW.close();
          break;
        default:
          account = new Fan("", "", "", "", "");
          System.out.println("No user type found");
      }

      // This prints to Accounts.txt, ecun0000 = Error Checking User Name, used to check this line
      // for matching user names.
      accountPW.println("ecun0000 " + account.getAccountName() + " "
          + account.getPassword() + " userID=" + MainController.userID);
      accountPW.println(account.getFirstName() + " " + account.getLastName());
      accountPW.println(account.getAccountType() + " " + account.getTeam());

      accountPW.close();
    } catch (IOException er) {
      System.out.println("ERROR");
    }
    alert = new Alert(AlertType.INFORMATION);
    alert.setTitle("Account Created");
    alert.setHeaderText("You're now signed up!");
    alert.showAndWait();
    Stage stage = Main.getPrimaryStage();

    Parent root = FXMLLoader.load(getClass().getResource("../View/main.fxml"));

    stage.setScene(new Scene(root, 800, 600));
    stage.show();
    return;

  }

  /**
   * Used in Java's FXML loader.
   */
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    accountTypeChoice.setItems(accountTypeList);
    accountTypeChoice.setValue("Fan");
  }
}