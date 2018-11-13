package sample;

public class Athlete extends User {

  private Team team;

  public Athlete(String name, String accountName, String password, Team team) {
    super(name, accountName, password);
    this.team = team;
  }
}
