package sample;

import java.util.Date;

/**
 * Single account type user "Admin", supercedes other users in scope of release usage. Controls management of creating
 * games and editing of statistics for both teams and players.
 */
public class Admin extends User {

  //Constructor for account type Admin
  public Admin() {
    super("","","","","","");
  }

  /**
   * Admin user can create games by selecting two teams to compete, a date for the game to take place, and whether the
   * game is displayed on the program as already having been played.
   *
   * @param teamA - first team involved in game, faces TeamB.
   * @param teamB - second team involved in game, faces TeamA.
   * @param date - Designation for what day the game will take place.
   * @param pastGame - Designation if the game has already been played or is upcoming.
   */
  public void createGame(Team teamA, Team teamB, Date date, Boolean pastGame){

  }

  //Method to allow user Admin to edit games that are already created. (WIP)
  public void editGameStats(){

  }
}
