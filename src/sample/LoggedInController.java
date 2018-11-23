package sample;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
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
}
