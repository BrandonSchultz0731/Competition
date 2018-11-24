package sample;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
 /* Format of text files created or appended to when account created.
 *
 * ---Accounts.txt---
 * [userName] [passWord]
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

  private static String name;
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
                createTeamLabel.setDisable(true);
                createTeamLabel.setVisible(false);
              }
            }
          });
    }
    choiceClicked = true;
  }

  public void backButtonPressed() throws IOException {
    Stage stage = Main.getPrimaryStage();
    Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
    stage.setScene(new Scene(root, 800, 600));
    stage.show();
  }

  public int createButtonClicked() throws IOException {
    String userType;
    try {

      FileWriter accountFW = new FileWriter("Accounts.txt", true);
      PrintWriter accountPW = new PrintWriter(accountFW);

      String user = createUsername.getText();
      String pass = createPassword.getText();
      String first = createFirstName.getText();
      String last = createLastName.getText();
      String team = createTeam.getText();

      userType = accountTypeChoice.getValue().toString();
      User account;

      switch (userType) {
        case "Fan":
          account = new Fan(user, pass, first, last, userType);
          break;
        case "Athlete":
          account = new Athlete(user, pass, first, last, userType);
          // Adds new athlete to Athletes.txt file
          FileWriter athleteFW = new FileWriter("Athletes.txt", true);
          PrintWriter athletePW = new PrintWriter(athleteFW);
          athletePW.println(account.getFirstName() + " " + account.getLastName());
          athletePW.println(account.getTeam() + " 0 0");
          athletePW.close();
          break;
        case "Manager":
          account = new Manager(user, pass, first, last, userType, team);
          // Add new team to Teams.txt file
          FileWriter teamFW = new FileWriter("Teams.txt", true);
          PrintWriter teamPW = new PrintWriter(teamFW);
          teamPW.println(account.getTeam() + " 0 0");
          teamPW.println(account.getFirstName() + " " + account.getLastName());
          String newTeamFile = account.getTeam() + ".txt";
          File file = new File(newTeamFile);
          file.createNewFile();
          teamPW.close();
          break;
        default:
          account = new Fan("", "", "", "", "");
          System.out.println("No user type found");
      }

      // This prints to Accounts.txt, ecun0000 = Error Checking User Name, used to check this line
      // for matching user names.
      accountPW.println("ecun0000 " + account.getAccountName() + " " + account.getPassword());
      accountPW.println(account.getFirstName() + " " + account.getLastName());
      accountPW.println(account.getAccountType() + " " + account.getTeam());

      accountPW.close();
    } catch (IOException er) {
      System.out.println("ERROR");
    }
    AlertBox.display("Account created", "Account Successfully Created. Click"
        + " \'Ok\' to go sign in");
    Stage stage = Main.getPrimaryStage();

    Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));

    stage.setScene(new Scene(root, 800, 600));
    stage.show();
    return 0;

  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    accountTypeChoice.setItems(accountTypeList);
    accountTypeChoice.setValue("Fan");
  }
}