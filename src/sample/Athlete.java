package sample;

public class Athlete extends User {

  private Team team;
  private int points;

  public Athlete(String name, String accountName, String password, Team team) {
    super(name, accountName, password);
    points = 0;
    this.team = team;
  }
}
