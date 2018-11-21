package sample;

import java.util.ArrayList;

public class Team {
  private String name;
  private int wins = 0;
  private int losses = 0;
  private ArrayList<Athlete> roster;
  private Manager manager;

  public Team(String name, int wins, int losses, String managerFirstName, String managerLastName) {
    this.name = name;
    this.wins = wins;
    this.losses = losses;
    //this.manager =
  }

  public void setManager(Manager manager){
    this.manager = manager;
  }

  public void addAthlete(Athlete athlete){
    roster.add(athlete);
  }


}
