package sample;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
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
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
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

  // Format used to display only 2 decimal places for Doubles.
  DecimalFormat decimalFormat = new DecimalFormat("#.00");

  @FXML
  private Label profileNameLabel, profileAccountTypeLabel, profileTeamLabel, profileWinsLabel,
      profileLossesLabel, teamTeamName, teamTeamManager, teamWins, teamLosses, athleteAthleteName,
      athleteTeamName, athleteWins, athleteLosses;
  @FXML
  private TableColumn athleteTableName, athleteTableTeam, teamTableName, teamTableWins,
      teamTableStandings, teamRosterTableRoster, profileRosterTableRoster;
  @FXML
  private TableView<RosterRecord> teamRosterTable, profileRosterTable;
  @FXML
  private TableView<TeamRecord> teamTeamTable;
  @FXML
  private TableView<athleteRecord> athleteTable;
  @FXML
  private AnchorPane hiddenFromFans;
  @FXML
  private Pane paneAthletePieChart, paneTeamPieChart;
  @FXML
  private Button teamButton, athleteButton, profileButton;
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

  //Logs out current user and returns them to main log-in scene.
  @FXML
  public void signOutButtonPressed() throws IOException {
    Stage stage = Main.getPrimaryStage();
    Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
    stage.setScene(new Scene(root, 800, 600));
    stage.show();
  }

  // Initialize Method used to populate all tables and labels depending on login credentials and general data.
  @Override
  public void initialize(URL location, ResourceBundle resources) {

    populateProfileTab();
    initializeCalendar();
    populateTeamTab();
    populateAthleteTab();
  }

  /**
   * Updates information on the profile tab based on the user who is currently logged in. Scene labels are modified
   * based on which account type is being viewed. For Athletes, the wins and losses, as well as the team they are on,
   * are read from file as strings using a BufferedReader.
   *
   * @exception IOException - handled by try/catch for reading from the account type files.
   */
  private void populateProfileTab() {
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
      if (!thisUserTeam.equals("NoTeam")) {
        profileButton.setText("Leave Team");
      } else {
        profileButton.setDisable(true);
        profileButton.setVisible(false);
        profileRosterTable.setVisible(false);
      }
      athleteButton.setDisable(true);
      athleteButton.setVisible(false);
    } else if (thisAccount.equals("Manager")) {
      try {
        FileReader fr = new FileReader("Teams.txt");
        BufferedReader br = new BufferedReader(fr);
        String str;
        String strPrevious = br.readLine();
        while (true) {
          str = br.readLine();
          if (str == null) {
            br.close();
            break;
          } else if (thisUser.equals(str)) {
            String[] splitString = strPrevious.split("\\s+");
            profileWinsLabel.setText(splitString[1]);
            profileLossesLabel.setText(splitString[2]);
            br.close();
            break;
          } else {
            strPrevious = str;
          }
        }
      } catch (IOException ex) {
        ex.printStackTrace();
      }
      teamButton.setVisible(false);
      teamButton.setDisable(true);
    } else if (thisAccount.equals("Fan")) {
      // Hide Wins and Losses labels in profile tab if Fan account signed in.
      hiddenFromFans.setVisible(false);
      hiddenFromFans.setDisable(true);
      // Change Text of Button in Team Tab and hide Button in Athlete Tab.
      teamButton.setText("Follow Team");
      athleteButton.setDisable(true);
      athleteButton.setVisible(false);
      if (!thisUserTeam.equals("NoTeam")) {
        profileButton.setText("Unfollow Team");
      } else {
        profileButton.setDisable(true);
        profileButton.setVisible(false);
        profileRosterTable.setVisible(false);
      }
    }

    // Fill Roster Table if belongs to a team.
    profileRosterTableRoster.setCellValueFactory(new PropertyValueFactory<>("name"));
    profileRosterTable.setItems(getTeamRoster(thisUserTeam));
  }

  /**
   * Void method used to populate the list of teams on the Teams tab. Information is taken in through a BufferedReader
   * and stored intro String variables. When a team is selected, these variables are displayed to the right of the list
   * detailing the team's statistics including a pie chart for win/loss. A roster is set up with a TableView allowing
   * the current user to see the members of the selected team.
   *
   * @exception IOException - try/catch handling for exception on reading .txt file "Teams".
   */
  private void populateTeamTab() {
    // Setup Columns of Team Table and Roster Table
    teamTableName.setCellValueFactory(new PropertyValueFactory<>("name"));
    teamTableWins.setCellValueFactory(new PropertyValueFactory<>("wins"));
    teamTableStandings.setCellValueFactory(new PropertyValueFactory<>("standings"));
    // Calls a method to read the Teams.txt file and store data into a list.
    // Then populate cells in Team Table with data in list.
    teamTeamTable.setItems(getTeamRecord());

    teamTeamTable.getFocusModel().focusedCellProperty().addListener(
        new ChangeListener<TablePosition>() {
          @Override
          public void changed(ObservableValue<? extends TablePosition> observable,
              TablePosition oldValue, TablePosition newValue) {
            if (teamTeamTable.getSelectionModel().getSelectedCells() != null) {
              // Get selected row's team name
              TeamRecord selectedTeam = teamTeamTable.getSelectionModel().getSelectedItem();
              String teamName = selectedTeam.getName();
              // Search Team.txt for matching name and set respective labels in team tab to values
              try {
                FileReader fr = new FileReader("Teams.txt");
                BufferedReader br = new BufferedReader(fr);
                String checkString;
                while (true) {
                  if ((checkString = br.readLine()) != null) {
                    String[] splitTeamWinLoss = checkString.split("\\s+");
                    if (teamName.equals(splitTeamWinLoss[0])) {
                      // Set respective labels to values of Team
                      String manager = br.readLine();
                      teamTeamName.setText(teamName);
                      teamTeamManager.setText(manager);
                      teamWins.setText(splitTeamWinLoss[1]);
                      teamLosses.setText(splitTeamWinLoss[2]);

                      // Set pie chart data to mirror wins losses ratio
                      paneTeamPieChart.getChildren().clear();
                      ObservableList<PieChart.Data> list = FXCollections.observableArrayList();
                      list.add(new PieChart.Data(
                          "Wins", Double.parseDouble(splitTeamWinLoss[1])));
                      list.add(new PieChart.Data(
                          "Losses", Double.parseDouble(splitTeamWinLoss[2])));
                      PieChart pieChart = new PieChart(list);
                      pieChart.setTitle("Win/Loss Ratio");
                      pieChart.setLegendVisible(false);
                      paneTeamPieChart.getChildren().add(pieChart);
                      pieChart.setMaxSize(250, 250);
                      pieChart.setMinSize(250,250);

                      // Set roster table to roster of selected team
                      // Setup roster table name column <RosterRecord>
                      teamRosterTableRoster.setCellValueFactory(new PropertyValueFactory<>("name"));
                      teamRosterTable.setItems(getTeamRoster(teamName));
                      break;
                    }
                  } else {
                    System.out.println("Error. No Team Info Found!");
                    break;
                  }
                }
              } catch (IOException ex) {
                ex.printStackTrace();
              }
            }
          }
        }
    );
  }

  /**
   * Void method to display all current athletes stored in the program on the Athletes tab. Information is read from a
   * BufferedReader and stored into String variables. When an athlete is selected from a TableView, their data (name,
   * wins, losses, and team) is displayed on the screen. A pie chart is used to graphically display their win/loss
   * record.
   *
   * @exception IOException - try/catch handling for exception on reading .txt file "Athletes".
   */
  private void populateAthleteTab() {
    // Setup Columns of Athlete Table in Athlete Tab
    athleteTableName
        .setCellValueFactory(new PropertyValueFactory<>("name"));
    athleteTableTeam
        .setCellValueFactory(new PropertyValueFactory<>("team"));

    // Calls a method to read the Athletes.txt file and store into an ObservableList.
    // Then populate cells in Athlete Table with data in List.
    athleteTable.setItems(getAthleteRecord());

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
              String[] splitStrName = strName.split(",\\s+");
              String selectedName = splitStrName[1] + " "
                  + splitStrName[0];
              String selectedTeam = selectedAthlete.getTeam();
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
                      String[] splitTeamWinLoss = readTeamWinLoss.split("\\s+");

                      if (splitTeamWinLoss[0].equals(selectedTeam)) {
                        athleteAthleteName.setText(selectedName);
                        athleteTeamName.setText(splitTeamWinLoss[0]);
                        athleteWins.setText(splitTeamWinLoss[1]);
                        athleteLosses.setText(splitTeamWinLoss[2]);

                        // Before loading new pie data, clear the pane holding the pie chart.
                        paneAthletePieChart.getChildren().clear();
                        ObservableList<PieChart.Data> list = FXCollections.observableArrayList();
                        list.add(new PieChart.Data(
                            "Wins", Double.parseDouble(splitTeamWinLoss[1])));
                        list.add(new PieChart.Data(
                            "Losses", Double.parseDouble(splitTeamWinLoss[2])));
                        PieChart pieChart = new PieChart(list);
                        pieChart.setLegendVisible(false);
                        pieChart.setTitle("Win/Loss Ratio");
                        paneAthletePieChart.getChildren().add(pieChart);
                        pieChart.setMaxSize(250, 250);
                        pieChart.setMinSize(250,250);
                      }
                      break;
                    }
                  } else {
                    System.out.println("Error. No Athlete Info Found!");
                    break;
                  }
                }
                br.close();
              } catch (IOException ex) {
                ex.printStackTrace();
                System.out.println("Unable to load file.");
              }
            }
          }
        });
  }

  /**
   * Method accesses the "Teams" .txt file to read the manager name and score ratio of a specific team. This data
   * is passed to the populateTeamTab method to be displayed on screen.
   *
   * @return - variable type ObservableList with the win/loss statistics of a team and team manager name.
   * @exception IOException - try/catch handling for exception on reading .txt file "Teams".
   */
  private ObservableList getTeamRecord() {

    String string1, string2;
    ObservableList<TeamRecord> records = FXCollections.observableArrayList();
    double ratio;
    try {
      FileReader teamFR = new FileReader("Teams.txt");
      BufferedReader teamBR = new BufferedReader(teamFR);
      while (true) {
        string1 = teamBR.readLine();
        if (string1 == null) {
          break;
        }
        String[] teamWinLoss = string1.split("\\s+");
        string2 = teamBR.readLine();
        String[] managerFirstLast = string2.split("\\s+");
        ratio = Double.parseDouble(teamWinLoss[1]) / Double.parseDouble(teamWinLoss[2]);
        ratio = Double.parseDouble(decimalFormat.format(ratio));
        TeamRecord newRecord = new TeamRecord(teamWinLoss[0], teamWinLoss[1], ratio);
        records.add(newRecord);
      }
      teamBR.close();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    return records;
  }

  /**
   * Method accesses the "Athletes" .txt file to read the athlete name and their respective win/loss ratio. This data
   * is passed to the populateAthleteTab method to be displayed on screen.
   *
   * @return - variable type ObservableList with the win/loss statistics of an athlete and their name.
   * @exception IOException - try/catch handling for exception on reading .txt file "Athletes".
   */
  private ObservableList getAthleteRecord() {
    String temp1;
    String temp2;
    ObservableList<athleteRecord> records = FXCollections.observableArrayList();
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
        records.add(newRecord);
      }
      br.close();
    } catch (IOException ex) {
      System.out.println("Error reading Athletes.txt.");
      ex.printStackTrace();
    }
    return records;
  }

  /**
   * Method used to store the roster of athletes of a team to be displayed when said team is selected on the Teams tab.
   * Data is parsed into method populateTeamTab.
   *
   * @param teamName - Name of team to parse file for.
   * @return - Observable list of type RosterRecord containing the names of each athlete assigned to that team.
   */
  private ObservableList getTeamRoster(String teamName) {
    String athlete;
    ObservableList<RosterRecord> records = FXCollections.observableArrayList();
    try {
      FileReader teamFR = new FileReader(teamName + ".txt");
      BufferedReader teamBR = new BufferedReader(teamFR);
      while (true) {
        athlete = teamBR.readLine();
        if (athlete == null) {
          break;
        }
        RosterRecord newRecord = new RosterRecord(athlete);
        records.add(newRecord);
      }
      teamBR.close();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    return records;
  }

  public class TeamRecord {

    private SimpleStringProperty name, standings;
    private int wins;

    //TeamRecord constructor
    // When a new TeamRecord is created, it takes in a name, string number for wins, and a double for their win/loss
    // ratio.
    public TeamRecord(String teamName, String wins, Double standings) {
      this.name = new SimpleStringProperty(teamName);
      this.wins = new Integer(wins);
      this.standings = new SimpleStringProperty(standings.toString());
    }

    //returns String name of TeamRecord object
    public String getName() {
      return name.get();
    }

    //sets String name of TeamRecord object with passed in String parameter.
    public void setName(String name) {
      this.name.set(name);
    }

    //returns int variable wins of TeamRecord object.
    public int getWins() {
      return wins;
    }

    //sets int variable wins of TeamRecord object with passed in int parameter.
    public void setWins(int wins) {
      this.wins = wins;
    }

    //returns double variable standings of TeamRecord object.
    public String getStandings() {
      return standings.get();
    }

    //sets String variable standings of TeamRecord object with passed in String parameter.
    public void setStandings(String standings) {
      this.standings.set(standings);
    }
  }

  // Local class used to hold data of selected row in Athlete Table View.
  public class athleteRecord {

    private SimpleStringProperty name;
    private SimpleStringProperty team;

    //athleteRecord constructor
    // Takes in String parameters firstName, lastName, and team to produce a fresh record of a new athlete.
    public athleteRecord(String firstName, String lastName, String team) {
      this.name = new SimpleStringProperty(lastName + ", " + firstName);
      this.team = new SimpleStringProperty(team);
    }

    //returns String variable name of athleteRecord object.
    public String getName() {
      return name.get();
    }

    //sets String variable name of athleteRecord object with passed in String type parameter.
    public void setName(String nameX) {
      this.name.set(nameX);
    }

    //returns String variable team of athleteRecord object.
    public String getTeam() {
      return team.get();
    }

    //sets String variable team of athleteRecord object with passed in String type parameter.
    public void setTeam(String teamX) {
      this.team.set(teamX);
    }
  }

  public class RosterRecord {

    private SimpleStringProperty name;

    //RosterRecord constructor
    // A new RosterRecord object is created with a passed in String parameter.
    public RosterRecord(String name) {
      this.name = new SimpleStringProperty(name);
    }

    //returns String variable name of RosterRecord object.
    public String getName() {
      return name.get();
    }

    //sets String variable name of RosterRecord object with passed in String type parameter.
    public void setName(String name) {
      this.name.set(name);
    }
  }

  //ALL CALENDAR STUFF FROM HERE ON OUT

  //Creates calendar upon initialization by FXML Loader. Sets current day by local computer time.
  private void initializeCalendar(){
    today = LocalDate.now();
    date = LocalDate.now();
    setYearMonthAndDay();
    gamesToHashMap();
    populateGridPane();
  }

  /**
   * Method to advance month in calendar display. Utilized in button on calendar tab of program.
   *
   * @param mouseEvent - unused.
   */
  public void goToNextMonth(MouseEvent mouseEvent) {
    date = date.plusMonths(1);
    setYearMonthAndDay();
    populateGridPane();
  }

  /**
   * Method to move to previous month in calendar display. Utilized in button on calendar tab of program.
   *
   * @param mouseEvent - unused.
   */
  public void goToPrevMonth(MouseEvent mouseEvent) {
    date = date.minusMonths(1);
    setYearMonthAndDay();
    populateGridPane();
  }

  /**
   * Generates labels for calendar display in tandem with which month is currently "selected" on the calendar tab.
   * Labels for month and year are set above the calendar display.
   */
  private void setYearMonthAndDay() {
    currentYear = date.getYear();
    currentMonth = date.getMonth();
    currentDay = date.getDayOfMonth();
    daysInAMonth = date.lengthOfMonth();

    yearLabel.setText("" + currentYear);
    monthLabel.setText(currentMonth.toString());
  }

  /**
   * Void method clears all children of the calendar display upon calendar initialization. It iterates through the days
   * and months of a year while adding labels of games based on date to the appropriate day. These labels are stored in
   * a hashmap in the gamesToHashMap method. Method finishes executing once the entire "Games" .txt file has been read
   * and stored into the calendar display.
   */
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
          if (dayAsNumber == currentDay && currentMonth == today.getMonth()) {
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

  /**
   * Enters every game read in the "Games" .txt file and stores them into a hashmap. This hashmap is then used to
   * populate the calendar display in the method populateGridPane.
   *
   * @exception IOException - file handling for reading games info.
   */
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
