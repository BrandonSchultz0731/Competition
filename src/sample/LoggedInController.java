package sample;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
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
  private TableColumn<athRecord, String> athleteTableName, athleteTableTeam;
  @FXML
  private TableColumn teamTableName, teamTableWins,
      teamTableStandings, rosterTableRoster;
  @FXML
  private TableView teamTeamTable, teamRosterTable;
  @FXML
  private TableView<athRecord> athleteTable;
  @FXML
  private AnchorPane hiddenFromFans;

  @FXML
  public void signOutButtonPressed() throws IOException {
    Stage stage = Main.getPrimaryStage();

    Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));

    stage.setScene(new Scene(root, 800, 600));
    stage.show();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {

    // Populate Profile Tab
    String thisUser = MainController.currentUserName;
    profileNameLabel.setText(thisUser);
    String thisAccount = MainController.currentUserAccountType;
    profileAccountTypeLabel.setText(thisAccount);
    String thisUserTeam = MainController.currentUserTeam;
    profileTeamLabel.setText(thisUserTeam);
    if (thisAccount.equals("Athlete")) {
      try {
        FileReader fr = new FileReader("Athletes.txt");
        BufferedReader br = new BufferedReader(fr);
        String str;
        while (true) {
          str = br.readLine();
          if (str == null) {
            break;
          } else if (thisUser.equals(str)) {
            str = br.readLine();
            String[] splitString = str.split("\\s+");
            if (splitString[0].equals(thisUserTeam)) {
              profileWinsLabel.setText(splitString[1]);
              profileLossesLabel.setText(splitString[2]);
            }
            break;
          }
        }
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    } else if (thisAccount.equals("Manager")) {

    } else if (thisAccount.equals("Fan")) {
        hiddenFromFans.setVisible(false);
        hiddenFromFans.setDisable(true);
    }

    //

    // Setup Columns of Athlete Table in Athlete Tab
    athleteTableName
        .setCellValueFactory(new PropertyValueFactory<athRecord, String>("name"));
    athleteTableTeam
        .setCellValueFactory(new PropertyValueFactory<athRecord, String>("team"));
    athleteTable.getSelectionModel().

        setSelectionMode(SelectionMode.MULTIPLE);
    // Populate Athlete Table
    athleteTable.setItems(

        getRecord());
  }

  public ObservableList<athRecord> getRecord() {

    String temp1;
    String temp2;
    ObservableList<athRecord> record = FXCollections.observableArrayList();
    try {
      FileReader fr = new FileReader("Athletes.txt");
      BufferedReader br = new BufferedReader(fr);
      while (true) {
        temp1 = br.readLine();
        if (temp1 == null) {
          break;
        }
        String[] tempSplit1 = temp1.split("\\s+");
        System.out.println(tempSplit1[0] + "\\" + tempSplit1[1]);
        temp2 = br.readLine();
        String[] tempSplit2 = temp2.split("\\s+");
        System.out.println(tempSplit2[0]);
        athRecord newRecord = new athRecord(tempSplit1[0], tempSplit1[1],
            tempSplit2[0]);
        record.add(newRecord);
      }
    } catch (IOException ex) {
      System.out.println("Blah");
    }
    record.add(new athRecord("Luis", "Mendez", "teamName"));
    record.add(new athRecord("Kamp", "Duong", "teamName"));
    record.add(new athRecord("Kamp", "Duong", "teamName"));
    return record;
  }

  public class athRecord {

    public SimpleStringProperty name;
    public SimpleStringProperty team;

    public athRecord(String firstName, String lastName, String team) {
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
}
