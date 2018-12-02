package Competition.Controller;

import Competition.Model.Main;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class AdminSceneController implements Initializable {

  //---FXML Elements---
  @FXML
  private DatePicker datePicker;
  @FXML
  private Label createSuccessLabel, editSuccessLabel, rowSelectionWarning;
  @FXML
  private AnchorPane createGameAnchorPane, editScoreAnchorPane;
  @FXML
  private TextField team1TextField, team2TextField, scoreField1, scoreField2;
  @FXML
  private ListView<String> createdGamesList;
  @FXML
  private Button submitButton;

  //---Variables---
  // Integer holds the index of the selected row to use as reference for
  // inserting and removing row from ListView.
  private int indexOfSelected = -1;
  // String array used for splitting score string at "-" to get score values for each team.
  private String[] scores;
  private Alert alert = new Alert(AlertType.ERROR);

  // Instructions to run when scene loaded
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // Call to updateListView method to populate ListView
    updateListView();
    //createdGamesList.setCellFactory(TextFieldListCell.forListView());
    // Add an event listener for when a row of the ListView is selected.
    createdGamesList.getSelectionModel().selectedItemProperty().addListener(
        new ChangeListener<String>() {
          @Override
          public void changed(ObservableValue<? extends String> observable, String oldValue,
              String newValue) {
            hideCreateGameMenu();
            // Checks if the selected row matches format of [number]-[number]
              if (newValue.matches("\\d+-\\d+")) {
                rowSelectionWarning.setVisible(false);
                // Split string at '-' and each score to respective text fields.
                scores = newValue.split("-");
                scoreField1.setText(scores[0]);
                scoreField2.setText(scores[1]);
                indexOfSelected = createdGamesList.getSelectionModel().getSelectedIndex();
                submitButton.setDisable(false);
              } else {
                // If row does not match format, display warning label and disable submit button.
                rowSelectionWarning.setVisible(true);
                submitButton.setDisable(true);
                resetFields();
              }
          }
        });
    // Add an event listener for when a date is selected in the DatePicker
    // to hide the edit menu and show create menu.
    datePicker.valueProperty().addListener(new ChangeListener<LocalDate>() {
      @Override
      public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue,
          LocalDate newValue) {
        hideEditScoreMenu();
      }
    });
  }

  // Hides createGame pane and displays editScore pane.
  private void hideCreateGameMenu() {
    createGameAnchorPane.setVisible(false);
    createGameAnchorPane.setDisable(true);
    createSuccessLabel.setVisible(false);
    editScoreAnchorPane.setVisible(true);
    editScoreAnchorPane.setDisable(false);
    resetFields();
  }

  // Hides editScore pane and displays createGame pane.
  private void hideEditScoreMenu() {
    createGameAnchorPane.setVisible(true);
    createGameAnchorPane.setDisable(false);
    editScoreAnchorPane.setVisible(false);
    editScoreAnchorPane.setDisable(true);
    editSuccessLabel.setVisible(false);
    resetFields();
  }

  // Resets all the TextFields in the scene.
  @FXML
  public void resetFields() {
    team1TextField.setText("");
    team2TextField.setText("");
    scoreField1.setText("");
    scoreField2.setText("");
  }

  /*
   * Creates a game and appends to the Games.txt file in the format:
   * YYYY-MM-DD
   * [Team 1] vs. [Team2]
   * 0-0 // 0 for both because a new game does not have scores yet
   * Then updates the ListView and resets the TextFields
   * */
  @FXML
  public void createGameButtonPressed() {
    if (team1TextField.getText().contains(" ") || team1TextField.getText().isEmpty()
        || team2TextField.getText().contains(" ") || team2TextField.getText().isEmpty()) {
      alert.setTitle("Input Error");
      alert.setHeaderText("Text Fields Cannot Contain Spaces or be empty");
      alert.setContentText("Please enter a valid team name into the input fields.");
      alert.showAndWait();
      return;
    }
    try {
      FileWriter gameFW = new FileWriter("Games.txt", true);
      PrintWriter gamePW = new PrintWriter(gameFW);
      String date = datePicker.getValue().toString();
      String[] dateSplit = date.split("-");
      dateSplit[1] = String.valueOf(Integer.parseInt(dateSplit[1]));
      dateSplit[2] = String.valueOf(Integer.parseInt(dateSplit[2]));
      date = dateSplit[0] + "-" + dateSplit[1] + "-" + dateSplit[2];
      String game = team1TextField.getText() + " vs. " + team2TextField.getText();
      String score = "0-0";
      gamePW.println(date);
      gamePW.println(game);
      gamePW.println(score);
      gamePW.close();
      createdGamesList.getItems().addAll(date, game, score);
      resetFields();
      createSuccessLabel.setVisible(true);
    } catch (IOException ex) {
      ex.printStackTrace();
      System.out.println("Unable to load file \"Games.txt\"");
    }
  }

  /*
   * TextField value are added to the ListView at the same index and the old value is removed.
   * Then overwrite the Games.txt and print each item into the ListView, updating it to the new list.
   * */
  @FXML
  public void submitButtonPressed() {
    if (scoreField1.getText().contains(" ") || scoreField1.getText().isEmpty()
        || scoreField2.getText().contains(" ") || scoreField2.getText().isEmpty()) {
      alert.setTitle("Input Error");
      alert.setHeaderText("Text Fields Cannot Contain Spaces or be empty");
      alert.setContentText("Please enter a numeric value into the input fields.");
      alert.showAndWait();
      return;
    }
    createdGamesList.getItems().add(indexOfSelected,
        scoreField1.getText() + "-" + scoreField2.getText());
    createdGamesList.getItems().remove((indexOfSelected + 1), (indexOfSelected + 2));
    submitButton.setDisable(true);
    try {
      FileWriter fw = new FileWriter("Games.txt");
      PrintWriter pw = new PrintWriter(fw);
      for (int i = 0; i < createdGamesList.getItems().size(); i++) {
        pw.println(createdGamesList.getItems().get(i));
        System.out.println(createdGamesList.getItems().get(i));
      }
      pw.close();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  // Method for populating ListView with data in Games.txt.
  @FXML
  private void updateListView() {
    createdGamesList.getItems().clear();
    try {
      FileReader fr = new FileReader("Games.txt");
      BufferedReader br = new BufferedReader(fr);
      String date;
      while ((date = br.readLine()) != null) {
        String teams = br.readLine();
        String score = br.readLine();
        createdGamesList.getItems().addAll(date, teams, score);
      }
      br.close();
    } catch (IOException ex) {
      ex.printStackTrace();
      System.out.println("Unable to load file \"Games.txt\"");
    }
  }

  // Method to call when the Back button is pressed, changes scene to the main scene.
  public void backButtonPressed() throws IOException {
    Stage stage = Main.getPrimaryStage();
    Parent root = FXMLLoader.load(getClass().getResource("../View/main.fxml"));
    stage.setScene(new Scene(root, 800, 600));
    stage.show();
  }
}
