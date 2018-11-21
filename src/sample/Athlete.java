package sample;

public class Athlete extends User {

  public Athlete(String userName, String password,
      String firstName, String lastName, String accountType) {
    super(userName, password, firstName, lastName, accountType, "NoTeam");
  }
}
