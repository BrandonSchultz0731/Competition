package sample;

import java.util.ArrayList;

public class Team {

  //String variable name, used for name of the team.
  private String name;

  //Int variable initialized to 0 for a new team. Incremented after a game if won.
  private int wins = 0;

  //Int variable initialized to 0 for a new team. Incremented after a game if lost.
  private int losses = 0;

  //ArrayList of type Athlete used to store the players on a team.
  private ArrayList<Athlete> roster;

  //Account type Manager used to designate the team's manager position to a specific account.
  private Manager manager;

  /**
   * Constructor for object type Game. Takes parameters of type String, int, and int to create a Game object to hold
   * data of past/upcoming games.
   *
   * @param name - Team name.
   * @param wins - Total wins of a team, initialized to 0.
   * @param losses - Total losses of a team, initialized to 0.
   * @param managerFirstName - Unused currently.
   * @param managerLastName - Unused currently.
   */
  public Team(String name, int wins, int losses, String managerFirstName, String managerLastName) {
    this.name = name;
    this.wins = wins;
    this.losses = losses;
    //this.manager =
  }

  /**
   * Method used to declare the manager of a team
   *
   * @param manager - Account type Manager passed in on team creation.
   */
  public void setManager(Manager manager){
    this.manager = manager;
  }

  /**
   * Method used to add an athlete to the ArrayList "Roster" type Athlete.
   *
   * @param athlete - Account type Athlete passed in.
   */
  public void addAthlete(Athlete athlete){
    roster.add(athlete);
  }


}
