package Competition.Model;

public class Fan extends User {

  /**
   * Account type Fan, user can follow a specific team which will be displayed on their profile. By default, Fan users
   * do not have a team they're following yet, String set to "NoTeam".
   *
   * @param userName - String used as log-in ID.
   * @param password - String used as log-in Password.
   * @param firstName - First name of user, displayed on own and team profiles.
   * @param lastName - Last name of user, displayed on own and team profiles.
   * @param accountType - String used to designate type of user account (fan, athlete, manager).
   */
  public Fan(String userName, String password,
      String firstName, String lastName, String accountType) {
    super(userName, password, firstName, lastName, accountType, "NoTeam");
  }
}
