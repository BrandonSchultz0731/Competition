package sample;

import java.util.ArrayList;

public class Team {
  private String name;
  private int wins = 0;
  private int loses = 0;
  private ArrayList<Athlete> roster;
  private Manager manager;

  public Team(String name) {
    this.name = name;
  }

  public void setManager(Manager manager){
    this.manager = manager;
  }

  public void addAthlete(Athlete athlete){
    roster.add(athlete);
  }


}
