package sample;

import java.util.Date;

public class Game {
  private Team teamA;
  private Team teamB;
  private Date date;
  private Boolean pastGame;

  public Game(Team teamA, Team teamB, Date date, Boolean pastGame) {
    this.teamA = teamA;
    this.teamB = teamB;
    this.date = date;
    this.pastGame = pastGame;
  }
}
