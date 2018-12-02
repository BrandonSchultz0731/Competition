package Competition.Controller;

import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;

import Competition.Model.Main;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
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

  private String selectedAthleteName;
  private String selectedAthleteTeam;
  private int selectedAthleteUserID;
  @FXML
  private Label profileNameLabel, profileAccountTypeLabel, profileTeamLabel, profileWinsLabel,
      profileLossesLabel, teamTeamName, teamTeamManager, teamWins, teamLosses, athleteAthleteName,
      athleteTeamName, athleteWins, athleteLosses;
  @FXML
  private TableColumn athleteTableName, athleteTableTeam, athleteTableUserID,
      teamTableName, teamTableWins, teamTableStandings, teamRosterTableRoster,
      profileRosterTableRoster;
  @FXML
  private TableView<RosterRecord> teamRosterTable, profileRosterTable;
  @FXML
  private TableView<TeamRecord> teamTeamTable;
  @FXML
  private TableView<AthleteRecord> athleteTable;
  @FXML
  private AnchorPane hiddenFromFans, hideLogoScreen;
  @FXML
  private Pane paneAthletePieChart, paneTeamPieChart;
  @FXML
  private Button teamButton, athleteButton, profileButton, acceptInviteButton, declineInviteButton;
  @FXML
  public GridPane daysOfTheWeek;
  @FXML
  public Label yearLabel;
  @FXML
  private ListView<String> newsTextArea;
  @FXML
  private GridPane calendarView;
  @FXML
  private Label monthLabel;

  private LocalDate today;
  private LocalDate date;
  private int daysInAMonth;
  private Month currentMonth;
  private int currentYear;
  private int currentDay;
  private Map<String, String> gameDataMap = new HashMap<>();
  private Tooltip gameInfoToolTip;
  private String messageSelected, objectOfMessage;

  //Logs out current user and returns them to main log-in scene.
  @FXML
  public void signOutButtonPressed() throws IOException {
    Stage stage = Main.getPrimaryStage();
    Parent root = FXMLLoader.load(getClass().getResource("../View/main.fxml"));
    stage.setScene(new Scene(root, 800, 600));
    stage.show();
  }

  // Initialize Method used to populate all tables and labels depending on login credentials and general data.
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    populateHomeTab();
    populateProfileTab();
    initializeCalendar();
    populateTeamTab();
    populateAthleteTab();
  }

  private void populateHomeTab() {
    newsTextArea.getSelectionModel().selectedItemProperty().addListener(
        new ChangeListener<String>() {
          @Override
          public void changed(ObservableValue<? extends String> observable, String oldValue,
              String newValue) {
            declineInviteButton.setDisable(false);
            acceptInviteButton.setDisable(false);
            messageSelected = newValue;
            if (!messageSelected.isEmpty() && messageSelected != null) {
              String[] teamExtract = messageSelected.split("\"");
              objectOfMessage = teamExtract[1];
            }
          }
        });
    if (MainController.currentUserAccountType.equals("Fan")) {
      hideLogoScreen.setVisible(false);
      hideLogoScreen.setDisable(true);
    }
    if (MainController.currentUserAccountType.equals("Manager")) {
      acceptInviteButton.setVisible(false);
      declineInviteButton.setText("Dismiss Message");
    }
    try {
      FileReader fr = new FileReader("Messages.txt");
      BufferedReader br = new BufferedReader(fr);
      String string;
      while (true) {
        string = br.readLine();
        if (string == null) {
          break;
        } else if (string.contains("userID=")) {
          String[] splitIDTeam = string.split("\\s+");
          String[] compareID = splitIDTeam[0].split("userID=");
          if (String.valueOf(MainController.currentUserID).equals(compareID[1])) {
            string = br.readLine();
            newsTextArea.getItems().add(string);
          }
        }
      }
      br.close();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  /**
   * Updates information on the profile tab based on the user who is currently logged in. Scene
   * labels are modified based on which account type is being viewed. For Athletes, the wins and
   * losses, as well as the team they are on, are read from file as strings using a BufferedReader.
   *
   * @throws IOException - handled by try/catch for reading from the account type files.
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
      // If this Athlete belongs to a team:
      if (!thisUserTeam.equals("NoTeam")) {
        profileButton.setText("Leave Team");
        teamButton.setDisable(true);
        teamButton.setVisible(false);
      } else {
        profileButton.setDisable(true);
        profileButton.setVisible(false);
        profileRosterTable.setVisible(false);
      }
      athleteButton.setDisable(true);
      athleteButton.setVisible(false);
    } else if (thisAccount.equals("Manager")) {
      profileButton.setDisable(true);
      profileButton.setVisible(false);
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
   * Void method used to populate the list of teams on the Teams tab. Information is taken in
   * through a BufferedReader and stored intro String variables. When a team is selected, these
   * variables are displayed to the right of the list detailing the team's statistics including a
   * pie chart for win/loss. A roster is set up with a TableView allowing the current user to see
   * the members of the selected team.
   *
   * @throws IOException - try/catch handling for exception on reading .txt file "Teams".
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
                      String[] splitNameID = manager.split("\\s+");
                      teamTeamName.setText(teamName);
                      teamTeamManager.setText(splitNameID[0] + " " + splitNameID[1]);
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
                      pieChart.setMinSize(250, 250);

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
                br.close();
              } catch (IOException ex) {
                ex.printStackTrace();
              }
            }
          }
        }
    );
  }

  /**
   * Void method to display all current athletes stored in the program on the Athletes tab.
   * Information is read from a BufferedReader and stored into String variables. When an athlete is
   * selected from a TableView, their data (name, wins, losses, and team) is displayed on the
   * screen. A pie chart is used to graphically display their win/loss record.
   *
   * @throws IOException - try/catch handling for exception on reading .txt file "Athletes".
   */
  private void populateAthleteTab() {
    // Setup Columns of Athlete Table in Athlete Tab
    athleteTableName
        .setCellValueFactory(new PropertyValueFactory<>("name"));
    athleteTableTeam
        .setCellValueFactory(new PropertyValueFactory<>("team"));
    athleteTableUserID
        .setCellValueFactory(new PropertyValueFactory<>("userID"));

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
              AthleteRecord selectedAthlete = athleteTable.getSelectionModel().getSelectedItem();
              String strName = selectedAthlete.getName();
              String[] splitStrName = strName.split(",\\s+");
              selectedAthleteName = splitStrName[1] + " "
                  + splitStrName[0];
              selectedAthleteTeam = selectedAthlete.getTeam();
              selectedAthleteUserID = selectedAthlete.getuserID();
              //System.out.println("userID=" + selectedAthleteUserID);
              // Search Athletes.txt for matching name and team and set
              // respective labels in athlete tab with name, team, wins, and losses.
              try {
                FileReader fr = new FileReader("Athletes.txt");
                BufferedReader br = new BufferedReader(fr);
                String checkString;
                while (true) {
                  if ((checkString = br.readLine()) != null) {
                    String[] splitCheck = checkString.split("\\suserID=");
                    if ((splitStrName[1] + " " + splitStrName[0]).equals(splitCheck[0])) {
                      String readTeamWinLoss = br.readLine();
                      String[] splitTeamWinLoss = readTeamWinLoss.split("\\s+");
                      if (splitTeamWinLoss[0].equals(selectedAthleteTeam)) {
                        athleteAthleteName.setText(selectedAthleteName);
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
                        pieChart.setMinSize(250, 250);
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
   * Method accesses the "Teams" .txt file to read the manager name and score ratio of a specific
   * team. This data is passed to the populateTeamTab method to be displayed on screen.
   *
   * @return - variable type ObservableList with the win/loss statistics of a team and team manager
   * name.
   * @throws IOException - try/catch handling for exception on reading .txt file "Teams".
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
        if (!teamWinLoss[2].equals("0")) {
          ratio = Double.parseDouble(teamWinLoss[1]) / Double.parseDouble(teamWinLoss[2]);
          ratio = Double.parseDouble(decimalFormat.format(ratio));
        } else {
          ratio = Double.parseDouble(teamWinLoss[1]);
        }
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
   * Method accesses the "Athletes" .txt file to read the athlete name and their respective win/loss
   * ratio. This data is passed to the populateAthleteTab method to be displayed on screen.
   *
   * @return - variable type ObservableList with the win/loss statistics of an athlete and their
   * name.
   * @throws IOException - try/catch handling for exception on reading .txt file "Athletes".
   */
  private ObservableList getAthleteRecord() {
    String temp1;
    String temp2;
    ObservableList<AthleteRecord> records = FXCollections.observableArrayList();
    try {
      FileReader fr = new FileReader("Athletes.txt");
      BufferedReader br = new BufferedReader(fr);
      while (true) {
        temp1 = br.readLine();
        if (temp1 == null) {
          break;
        }
        String[] splitFirstLastID = temp1.split("\\s+");
        String[] splitForID = splitFirstLastID[2].split("userID=");
        temp2 = br.readLine();
        //System.out.println(Integer.parseInt(splitForID[1]));
        String[] splitTeamWinLoss = temp2.split("\\s+");
        AthleteRecord newRecord = new AthleteRecord(splitFirstLastID[0], splitFirstLastID[1],
            splitTeamWinLoss[0], Integer.parseInt(splitForID[1]));
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
   * Method used to store the roster of athletes of a team to be displayed when said team is
   * selected on the Teams tab. Data is parsed into method populateTeamTab.
   *
   * @param teamName - Name of team to parse file for.
   * @return - Observable list of type RosterRecord containing the names of each athlete assigned to
   * that team.
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
        String[] splitString = athlete.split(" userID=");
        RosterRecord newRecord = new RosterRecord(splitString[0]);
        records.add(newRecord);
      }
      teamBR.close();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    return records;
  }

  public static class TeamRecord {

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
  public static class AthleteRecord {

    private SimpleStringProperty name;
    private SimpleStringProperty team;
    private int userID;

    //AthleteRecord constructor
    // Takes in String parameters firstName, lastName, and team to produce a fresh record of a new athlete.
    public AthleteRecord(String firstName, String lastName, String team, int userID) {
      this.name = new SimpleStringProperty(lastName + ", " + firstName);
      this.team = new SimpleStringProperty(team);
      this.userID = userID;
    }

    //returns String variable name of AthleteRecord object.
    public String getName() {
      return name.get();
    }

    //sets String variable name of AthleteRecord object with passed in String type parameter.
    public void setName(String name) {
      this.name.set(name);
    }

    //returns String variable team of AthleteRecord object.
    public String getTeam() {
      return team.get();
    }

    //sets String variable team of AthleteRecord object with passed in String type parameter.
    public void setTeam(String team) {
      this.team.set(team);
    }

    public int getuserID() {
      return userID;
    }

    public void setUserID(int userID) {
      this.userID = userID;
    }
  }

  public static class RosterRecord {

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

  public void declineInviteButtonClicked() {
    if (MainController.currentUserAccountType.equals("Athlete")) {
      String[] manager = messageSelected.split("\\s+");
      respondToManager("declined", (manager[0] + " " + manager[1]), objectOfMessage);
      removeMessage();
    } else if (MainController.currentUserAccountType.equals("Manager")) {
      removeMessage();
    }
  }

  // Method called when user clicks Accept button.
  public void acceptInviteButtonClicked() {
    String teamFile = objectOfMessage + ".txt";
    //System.out.println(teamFile);
    switch (MainController.currentUserAccountType) {
      case "Athlete":
        // If Athlete was already part of a team, remove user from that team.
        if (!MainController.currentUserTeam.equals("NoTeam")) {
          removeAthleteFromCurrentTeam();
        }
        configureTeamInAccounts(MainController.currentUserTeam, objectOfMessage);
        configureTeamInAthletes(MainController.currentUserTeam, objectOfMessage);
        addToTeam(teamFile);
        String[] manager = messageSelected.split("\\s+");
        respondToManager("accepted", (manager[0] + " " + manager[1]), objectOfMessage);
        removeMessage();
        MainController.currentUserTeam = objectOfMessage;
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Accepted Team Invite");
        alert.setHeaderText("Team changed!");
        alert.setContentText("Please sign out and re-sign in to see changes.");
        alert.showAndWait();
        break;
      case "Manager":

        break;
      default:
        System.out.println("This shouldn't print.");
    }
  }

  private void removeMessage() {
    try {
      FileReader fr = new FileReader("Messages.txt");
      FileWriter fw = new FileWriter("temp.txt");
      BufferedReader oldFile = new BufferedReader(fr);
      PrintWriter tempFile = new PrintWriter(fw);
      String line1 = "";
      String line2 = "";
      while (true) {
        line1 = oldFile.readLine();
        if (line1 == null) {
          break;
        }
        line2 = oldFile.readLine();
        if (line2.equals(newsTextArea.getSelectionModel().getSelectedItem())
            && line1.equals("userID=" + MainController.currentUserID + " "
            + MainController.currentUserTeam)) {
          continue;
        }
        tempFile.println(line1);
        tempFile.println(line2);
      }
      tempFile.close();
      oldFile.close();

      File file = new File("temp.txt");
      OutputStream os = new FileOutputStream("Messages.txt");
      Files.copy(Paths.get(file.getPath()), os);
      os.close();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    if (newsTextArea.getItems().size() > 1) {
      int index = newsTextArea.getSelectionModel().getSelectedIndex();
      newsTextArea.getItems().remove((index), (index + 1));
    } else {
      newsTextArea.getItems().clear();
    }
  }

  private void respondToManager(String acceptDecline, String manager, String team) {
    String managerID = "userID=";
    try {
      FileWriter fw = new FileWriter("Messages.txt", true);
      PrintWriter pw = new PrintWriter(fw);
      FileReader fr = new FileReader("Accounts.txt");
      BufferedReader br = new BufferedReader(fr);
      while (true) {
        String usernameAndId = br.readLine();
        if (usernameAndId == null) {
          break;
        }
        String name = br.readLine();
        String accountAndTeam = br.readLine();
        if (name.equals(manager) && ("Manager " + team).equals(accountAndTeam)) {
          String[] splitUsernameAndId = usernameAndId.split("\\s+");
          managerID = splitUsernameAndId[3];
        }
      }
      pw.println(managerID + " " + team);
      pw.println(MainController.currentUserName + " " + acceptDecline
          + " your invitation to join team " + team);
      br.close();
      pw.close();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  private void addToTeam(String teamFilePath) {
    try {
      FileWriter fw = new FileWriter(teamFilePath, true);
      PrintWriter pw = new PrintWriter(fw);
      String[] nameSplit = MainController.currentUserName.split("\\s+");
      pw.println(nameSplit[1] + ", " + nameSplit[0] + " userID=" + MainController.currentUserID);
      pw.close();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  private void configureTeamInAccounts(String oldTeam, String newTeam) {
    try {
      FileReader fr = new FileReader("Accounts.txt");
      FileWriter fw = new FileWriter("temp.txt");
      BufferedReader oldFile = new BufferedReader(fr);
      PrintWriter tempFile = new PrintWriter(fw);
      String line;
      String compareWith = MainController.currentUserName;
      while (true) {
        line = oldFile.readLine();
        if (line == null) {
          break;
        }
        if (line.contains("userID=" + MainController.currentUserID)) {
          tempFile.println(line);
          line = oldFile.readLine();
          if (line.equals(compareWith)) {
            tempFile.println(line);
            line = oldFile.readLine();
            line = line.replace(oldTeam, newTeam);
          }
        }
        tempFile.println(line);
      }
      tempFile.close();
      oldFile.close();

      File file = new File("temp.txt");
      OutputStream os = new FileOutputStream("Accounts.txt");
      Files.copy(Paths.get(file.getPath()), os);
      os.close();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  private void configureTeamInAthletes(String oldTeam, String newTeam) {
    try {
      FileReader fr = new FileReader("Athletes.txt");
      FileWriter fw = new FileWriter("temp.txt");
      BufferedReader oldFile = new BufferedReader(fr);
      PrintWriter tempFile = new PrintWriter(fw);
      String line;
      String compareWith = MainController.currentUserName + " userID="
          + MainController.currentUserID;
      while (true) {
        line = oldFile.readLine();
        if (line == null) {
          break;
        }
        if (line.equals(compareWith)) {
          tempFile.println(line);
          line = oldFile.readLine();
          line = line.replace(oldTeam, newTeam);
        }
        tempFile.println(line);
      }
      tempFile.close();
      oldFile.close();

      File file = new File("temp.txt");
      OutputStream os = new FileOutputStream("Athletes.txt");
      Files.copy(Paths.get(file.getPath()), os);
      os.close();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  private void removeAthleteFromCurrentTeam() {
    String oldTeamFilePath = MainController.currentUserTeam + ".txt";
    try {
      FileReader fr = new FileReader(oldTeamFilePath);
      FileWriter fw = new FileWriter("temp.txt");
      BufferedReader oldFile = new BufferedReader(fr);
      PrintWriter tempFile = new PrintWriter(fw);
      String line;
      while (true) {
        line = oldFile.readLine();
        if (line == null) {
          break;
        } else if (line.contains("userID=" + MainController.currentUserID)) {
          continue;
        } else {
          tempFile.println(line);
        }
      }
      tempFile.close();
      oldFile.close();

      File file = new File("temp.txt");
      OutputStream os = new FileOutputStream(oldTeamFilePath);
      Files.copy(Paths.get(file.getPath()), os);
      os.close();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public void athleteButtonPressed() {
    FileWriter fw;
    PrintWriter pw = null;
    try {
      fw = new FileWriter("Messages.txt", true);
      pw = new PrintWriter(fw);
      pw.println("userID=" + selectedAthleteUserID + " " + MainController.currentUserTeam);
      pw.println(MainController.currentUserName + " would like you to join \""
          + MainController.currentUserTeam + "\". Please agree or decline.");
    } catch (IOException ex) {
      ex.printStackTrace();
    } finally {
      if (pw != null) {
        pw.close();
      }
    }
  }

  public void teamButtonPressed() {
    // Fan to follow team
    if (MainController.currentUserAccountType.equals("Fan")) {
      String newTeamName = teamTeamTable.getSelectionModel().getSelectedItem().getName();
      configureTeamInAccounts(MainController.currentUserTeam, newTeamName);
      MainController.currentUserTeam = "NoTeam";
      Alert alert = new Alert(AlertType.INFORMATION);
      alert.setTitle("Team Followed");
      alert.setHeaderText("You're now following " + newTeamName + ".");
      alert.setContentText("Please sign out and re-sign in to see changes.");
      alert.showAndWait();
    }
    // Athlete to request to join
    else if (MainController.currentUserAccountType.equals("Athlete")) {
      FileWriter fw;
      FileReader fr;
      PrintWriter pw = null;
      BufferedReader br = null;
      TeamRecord selectedTeam = teamTeamTable.getSelectionModel().getSelectedItem();
      String teamName = selectedTeam.getName();
      String teamManagerID = "";
      String[] splitLine = null;
      try {
        fw = new FileWriter("Messages.txt", true);
        fr = new FileReader("Teams.txt");
        pw = new PrintWriter(fw);
        br = new BufferedReader(fr);
        String line;
        while (true) {
          line = br.readLine();
          if (line == null) {
            break;
          } else {
            splitLine = line.split("\\s+");
            if (splitLine[0].equals(teamName)){
              line = br.readLine();
              splitLine = line.split("\\s+");
              teamManagerID = splitLine[2];
              //System.out.println(teamManagerID);
              break;
            }
          }
        }
        br.close();
        pw.println(teamManagerID + " " + teamName);
        pw.println("Athlete \""
            + MainController.currentUserName + "\" shows interest in your team. Invite them if you like them to join.");
        pw.close();
        //pw.println("userID=" + selectedAthleteUserID + " " + MainController.currentUserTeam);
        //pw.println(MainController.currentUserName + " would like you to join \" + MainController.currentUserTeam + "\". Please agree or decline.");
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }

  public void profileButtonPressed() {
    // Fan to un-follow team
    if (MainController.currentUserAccountType.equals("Fan")) {
      configureTeamInAccounts(MainController.currentUserTeam, "NoTeam");
      MainController.currentUserTeam = "NoTeam";
      Alert alert = new Alert(AlertType.INFORMATION);
      alert.setTitle("Team Unfollowed");
      alert.setHeaderText("You're now not following any teams.");
      alert.setContentText("Please sign out and re-sign in to see changes.");
      alert.showAndWait();
    }
    // Athlete to leave their current team
    else if (MainController.currentUserAccountType.equals("Athlete")) {
      if (!MainController.currentUserTeam.equals("NoTeam")) {
        removeAthleteFromCurrentTeam();
      }
      configureTeamInAccounts(MainController.currentUserTeam, "NoTeam");
      configureTeamInAthletes(MainController.currentUserTeam, "NoTeam");
      MainController.currentUserTeam = "NoTeam";
      Alert alert = new Alert(AlertType.INFORMATION);
      alert.setTitle("Leave Team");
      alert.setHeaderText("You're now a free lancer!");
      alert.setContentText("Please sign out and re-sign in to see changes.");
      alert.showAndWait();
    }
  }

  //ALL CALENDAR STUFF FROM HERE ON OUT

  //Creates calendar upon initialization by FXML Loader. Sets current day by local computer time.
  private void initializeCalendar() {
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
   * Method to move to previous month in calendar display. Utilized in button on calendar tab of
   * program.
   *
   * @param mouseEvent - unused.
   */
  public void goToPrevMonth(MouseEvent mouseEvent) {
    date = date.minusMonths(1);
    setYearMonthAndDay();
    populateGridPane();
  }

  /**
   * Generates labels for calendar display in tandem with which month is currently "selected" on the
   * calendar tab. Labels for month and year are set above the calendar display.
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
   * Void method clears all children of the calendar display upon calendar initialization. It
   * iterates through the days and months of a year while adding labels of games based on date to
   * the appropriate day. These labels are stored in a hashmap in the gamesToHashMap method. Method
   * finishes executing once the entire "Games" .txt file has been read and stored into the calendar
   * display.
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
          if (dayAsNumber == currentDay && currentMonth == today.getMonth()
              && currentYear == today.getYear()) {
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
   * Enters every game read in the "Games" .txt file and stores them into a hashmap. This hashmap is
   * then used to populate the calendar display in the method populateGridPane.
   *
   * @throws IOException - file handling for reading games info.
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
