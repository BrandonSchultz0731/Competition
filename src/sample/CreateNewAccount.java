package sample;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class CreateNewAccount {

  private static String name;
  private static String userType;
  ObservableList<String> accountTypeList = FXCollections
      .observableArrayList("Fan", "Player", "Manager");
  @FXML
  private ChoiceBox accountTypeChoice;
  @FXML
  private TextField createUsername;
  @FXML
  private PasswordField createPassword;
  @FXML
  private AnchorPane pane0;
  @FXML
  private TextField createTeam;
  @FXML
  private Label createTeamLabel;
  boolean choiceClicked = false;

  @FXML
  public void setAccountTypeChoice() {
    accountTypeChoice.setItems(accountTypeList);
    if (!choiceClicked) {
      accountTypeChoice.getSelectionModel().selectedIndexProperty()
          .addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue,
                Number newValue) {
              if (accountTypeChoice.getValue().toString().equals("Manager")) {
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
              pane0.setDisable(false);
              pane0.setVisible(true);
            }
          });
    }
    choiceClicked = true;
  }

  public void backButtonPressed() throws IOException {
    Stage stage = Main.getPrimaryStage();

    Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

    stage.setScene(new Scene(root, 600, 450));
    stage.show();
  }

  public int createButtonClicked() throws IOException {
    try {
      FileWriter fw = new FileWriter("Accounts.txt", true);
      PrintWriter pw = new PrintWriter(fw);

      String user = createUsername.getText();
      String pass = createPassword.getText();
      if (accountTypeChoice.getSelectionModel().isEmpty()) {
        AlertBox.display("User Type Error", "Please select a user type...");
        return 0;
      }
      userType = accountTypeChoice.getValue().toString();

      User account;

      if (userType.equals("Fan")) {
        account = new Fan(name, user, pass);
      } else if (userType.equals("Player")) {
        account = new Athlete(name, user, pass, new Team("Miami Heat"));
      } else if (userType.equals("Manager")) {
        account = new Manager(name, user, pass, new Team("Miami Heat"));
      } else {
        System.out.println("No user type found");
      }

      pw.println(user.toLowerCase() + " " + pass.toLowerCase());
      pw.println(userType);

      pw.close();

      System.out.println(userType);


    } catch (IOException er) {
      System.out.println("ERROR");
    }
    AlertBox.display("Account created", "Account Successfully Created. Click"
        + " \'Ok\' to go sign in");
    Stage stage = Main.getPrimaryStage();

    Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

    stage.setScene(new Scene(root, 600, 450));
    stage.show();
    return 0;

  }
}