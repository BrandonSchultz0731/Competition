package sample;

public class Athlete extends User {

  /**
   * Account type Athlete, user can join/leave teams to play on when games are scheduled. By default, athlete users are
   * not on a team until they are invited or request to join one. Default team value of "NoTeam" therefore set.
   *
   * @param userName - String used as log-in ID.
   * @param password - String used as log-in Password.
   * @param firstName - First name of user, displayed on own and team profiles.
   * @param lastName - Last name of user, displayed on own and team profiles.
   * @param accountType - String used to designate type of user account (fan, athlete, manager).
   */
  public Athlete(String userName, String password,
      String firstName, String lastName, String accountType) {
    super(userName, password, firstName, lastName, accountType, "NoTeam");
  }
}
