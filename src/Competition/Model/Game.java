package Competition.Model;

import java.util.Date;

public class Game {

  //First of two teams involved in a game.
  private Team teamA;

  //Second of two teams involved in a game.
  private Team teamB;

  //The day/month/year a game takes place.
  private Date date;

  //Boolean designation for creating a log of a game already played.
  private Boolean pastGame;

  /**
   * Constructor for object type Game. Stores information for which two teams were involved, the date it takes place,
   * and whether the game has already been played. Displayed on the Calendar.
   *
   * @param teamA - first team involved in game, faces TeamB.
   * @param teamB - second team involved in game, faces TeamA.
   * @param date - Designation for what day the game will take place.
   * @param pastGame - Designation if the game has already been played or is upcoming.
   */
  public Game(Team teamA, Team teamB, Date date, Boolean pastGame) {
    this.teamA = teamA;
    this.teamB = teamB;
    this.date = date;
    this.pastGame = pastGame;
  }
}
