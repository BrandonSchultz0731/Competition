package sample;

public class Manager extends User{

  private Team team;

  public Manager(String name, String accountName, String password, Team team) {
    super(name, accountName, password);
    this.team = team;

  }
}
