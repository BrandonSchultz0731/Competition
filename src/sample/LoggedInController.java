package sample;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class LoggedInController implements Initializable {

  @FXML
  private Label profileNameLabel, profileAccountTypeLabel, profileTeamLabel, profileWinsLabel,
      profileLossesLabel, teamTeamName, teamTeamManager, teamWins, teamLosses, athleteAthleteName,
      athleteTeamName, athleteWins, athleteLosses;
  @FXML
  private TableColumn athleteTableName, athleteTableTeam;
  @FXML
  private TableColumn teamTableName, teamTableWins,
      teamTableStandings, rosterTableRoster;
  @FXML
  private TableView teamTeamTable, teamRosterTable;
  @FXML
  private TableView<athleteRecord> athleteTable;
  @FXML
  private AnchorPane hiddenFromFans;


  @FXML
  public GridPane daysOfTheWeek;
  @FXML
  public Label yearLabel;

  private LocalDate today;
  private LocalDate date;
  private int daysInAMonth;
  private Month currentMonth;
  private int currentYear;
  private int currentDay;


  private Map<String, String> gameDataMap = new HashMap<>();

  private Tooltip gameInfoToolTip;

  @FXML
  private GridPane calendarView;

  @FXML
  private Label monthLabel;

  @FXML
  public void signOutButtonPressed() throws IOException {
    Stage stage = Main.getPrimaryStage();
    Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
    stage.setScene(new Scene(root, 800, 600));
    stage.show();
  }

  // Initialize Method used to populate all tables and labels depending on login credentials.
  @Override
  public void initialize(URL location, ResourceBundle resources) {

    // Populate Profile Tab
    String thisUser = MainController.currentUserName;
    profileNameLabel.setText(thisUser);
    String thisAccount = MainController.currentUserAccountType;
    profileAccountTypeLabel.setText(thisAccount);
    String thisUserTeam = MainController.currentUserTeam;
    profileTeamLabel.setText(thisUserTeam);
    // Account type dependent labels
    if (thisAccount.equals("Athlete")) {
      try {
        FileReader fr = new FileReader("Athletes.txt");
        BufferedReader br = new BufferedReader(fr);
        String str;
        while (true) {
          str = br.readLine();
          if (str == null) {
            br.close();
            break;
          } else if (thisUser.equals(str)) {
            str = br.readLine();
            String[] splitString = str.split("\\s+");
            if (splitString[0].equals(thisUserTeam)) {
              profileWinsLabel.setText(splitString[1]);
              profileLossesLabel.setText(splitString[2]);
            }
            br.close();
            break;
          }
        }
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    } else if (thisAccount.equals("Manager")) {

    } else if (thisAccount.equals("Fan")) {
      // Hide Wins and Losses labels in profile tab if Fan account signed in.
      hiddenFromFans.setVisible(false);
      hiddenFromFans.setDisable(true);
    }

    //

    // Setup Columns of Athlete Table in Athlete Tab
    athleteTableName
        .setCellValueFactory(new PropertyValueFactory<>("name"));
    athleteTableTeam
        .setCellValueFactory(new PropertyValueFactory<>("team"));
    //athleteTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    // Populate Athlete Table
    athleteTable.setItems(getRecord());

    // Sets an event listener on Athlete Table
    athleteTable.getFocusModel().focusedCellProperty().addListener(
        new ChangeListener<TablePosition>() {
          @Override
          public void changed(ObservableValue<? extends TablePosition> observable,
              TablePosition oldValue, TablePosition newValue) {
            if (athleteTable.getSelectionModel().getSelectedCells() != null) {
              // Get selected row's name and team values
              athleteRecord selectedAthlete = athleteTable.getSelectionModel().getSelectedItem();
              String strName = selectedAthlete.getName();
              //System.out.println(strName);
              String[] splitStrName = strName.split(",\\s+");
              String selectedName = splitStrName[1] + " "
                  + splitStrName[0];
              //System.out.println(selectedName);
              String selectedTeam = selectedAthlete.getTeam();
              System.out.println(selectedTeam);

              // Search Athletes.txt for matching name and team and set
              // respective labels in athlete tab with name, team, wins, and losses.
              try {
                FileReader fr = new FileReader("Athletes.txt");
                BufferedReader br = new BufferedReader(fr);
                String checkString;
                while (true) {
                  if ((checkString = br.readLine()) != null) {
                    if ((splitStrName[1] + " " + splitStrName[0]).equals(checkString)) {
                      String readTeamWinLoss = br.readLine();
                      String[] splitThat = readTeamWinLoss.split("\\s+");
                      if (splitThat[0].equals(selectedTeam)) {
                        athleteAthleteName.setText(selectedName);
                        athleteTeamName.setText(splitThat[0]);
                        athleteWins.setText(splitThat[1]);
                        athleteLosses.setText(splitThat[2]);
                      }
                      break;
                    }
                  } else {
                    System.out.println("Error.");
                    break;
                  }
                }
              } catch (IOException ex) {
                ex.printStackTrace();
                System.out.println("Unable to load file.");
              }
            }
          }
        });

    initializeCalendar();
  }

  public ObservableList getRecord() {

    String temp1;
    String temp2;
    ObservableList<athleteRecord> record = FXCollections.observableArrayList();
    try {
      FileReader fr = new FileReader("Athletes.txt");
      BufferedReader br = new BufferedReader(fr);
      while (true) {
        temp1 = br.readLine();
        if (temp1 == null) {
          break;
        }
        String[] tempSplit1 = temp1.split("\\s+");
        temp2 = br.readLine();
        String[] tempSplit2 = temp2.split("\\s+");
        athleteRecord newRecord = new athleteRecord(tempSplit1[0], tempSplit1[1],
            tempSplit2[0]);
        record.add(newRecord);
      }
    } catch (IOException ex) {
      System.out.println("Error reading Athletes.txt.");
      ex.printStackTrace();
    }
    return record;
  }

  public class athleteRecord {

    private SimpleStringProperty name;
    private SimpleStringProperty team;

    public athleteRecord(String firstName, String lastName, String team) {
      this.name = new SimpleStringProperty(lastName + ", " + firstName);
      this.team = new SimpleStringProperty(team);
    }

    public String getName() {
      return name.get();
    }

    public void setName(String nameX) {
      this.name.set(nameX);
    }

    public String getTeam() {
      return team.get();
    }

    public void setTeam(String teamX) {
      this.team.set(teamX);
    }
  }

  /*public void onEdit(){
    if(athleteTable.getSelectionModel().getSelectedCells() != null){
      athleteRecord selectedAthlete = athleteTable.getSelectionModel().getSelectedItem();
      nameTextField.setText(selectedAthlete.getName());
      addressTextField.setText(selectedAthlete.getAddress());
    }
  }*/

  //ALL CALENDAR STUFF FROM HERE ON OUT
  private void initializeCalendar(){
    today = LocalDate.now();
    date = LocalDate.now();
    setYearMonthAndDay();
    gamesToHashMap();
    populateGridPane();
  }

  public void goToNextMonth(MouseEvent mouseEvent) {
    date = date.plusMonths(1);
    setYearMonthAndDay();
    populateGridPane();
  }

  public void goToPrevMonth(MouseEvent mouseEvent) {
    date = date.minusMonths(1);
    setYearMonthAndDay();
    populateGridPane();
  }

  private void setYearMonthAndDay() {
    currentYear = date.getYear();
    currentMonth = date.getMonth();
    currentDay = date.getDayOfMonth();
    daysInAMonth = date.lengthOfMonth();

    yearLabel.setText("" + currentYear);
    monthLabel.setText(currentMonth.toString());
  }

  private void populateGridPane() {
    boolean executed = false;
    int col;
    calendarView.getChildren().clear();
    int dayAsNumber = 1;

    for (int row = 0; row < 6; row++) {
      if (!executed) {
        col = date.with(firstDayOfMonth()).getDayOfWeek().getValue() % 7;
        executed = true;
      } else {
        col = 0;
      }
      for (; col < 7; col++) {

        if (dayAsNumber <= daysInAMonth) {

          Pane cell = new Pane();
          cell.setPrefSize(calendarView.getWidth(), calendarView.getPrefHeight());
          cell.setStyle("-fx-border-color: black; "
              + "-fx-border-radius: .2");

          //FOR KAMP
          if (dayAsNumber == currentDay && currentMonth == today.getMonth() && currentYear == today.getYear()) {
            cell.setStyle("-fx-border-color: red; "
                + "-fx-border-radius: .2");
          }

          Label day = new Label("" + dayAsNumber);
          calendarView.add(day, col, row);
          GridPane.setValignment(day, VPos.TOP);

          if (gameDataMap
              .containsKey(currentYear + "-" + currentMonth.getValue() + "-" + dayAsNumber)) {
            StringTokenizer st = new StringTokenizer(
                gameDataMap.get(currentYear + "-" + currentMonth.getValue() + "-" + dayAsNumber),
                "|");

            Label gameLabel = new Label(st.nextToken());
            calendarView.add(gameLabel, col, row);
            GridPane.setValignment(gameLabel, VPos.CENTER);
            GridPane.setHalignment(gameLabel, HPos.CENTER);

            Label scoreLabel = new Label(st.nextToken());
            calendarView.add(scoreLabel, col, row);
            GridPane.setValignment(scoreLabel, VPos.BOTTOM);
            GridPane.setHalignment(scoreLabel, HPos.CENTER);

            cell.setOnMouseEntered(e -> {
                  gameInfoToolTip = new Tooltip();
                  gameInfoToolTip.setText(gameLabel.getText() +
                      '\n' + scoreLabel.getText());
                  gameInfoToolTip.setTextAlignment(TextAlignment.CENTER);
                  Tooltip.install(cell, gameInfoToolTip);
                }
            );

          }
          calendarView.add(cell, col, row);
          dayAsNumber++;
        }
      }
    }
  }

  private void gamesToHashMap() {
    try {
      FileReader fReader = new FileReader("Games.txt");
      BufferedReader bReader = new BufferedReader(fReader);

      String key;
      String value;

      while ((key = bReader.readLine()) != null) {
        value = bReader.readLine();
        value = value + "|" + bReader.readLine();
        gameDataMap.put(key, value);
      }
      bReader.close();
      fReader.close();
    } catch (IOException ex) {
      System.out.println("Error no games");
      ex.printStackTrace();
    }
  }
}