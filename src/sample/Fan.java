package sample;

public class Fan extends User {

  public Fan(String userName, String password,
      String firstName, String lastName, String accountType) {
    super(userName, password, firstName, lastName, accountType, "NoTeam");
  }
}
