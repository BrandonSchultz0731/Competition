package Competition.Model;

public class Manager extends User{
  /**
   * Account type Athlete, user can join/leave teams to play on when games are scheduled. By default, athlete users are
   * not on a team until they are invited or request to join one. Default team value of "NoTeam" therefore set.
   *
   * @param userName - String used as log-in ID.
   * @param password - String used as log-in Password.
   * @param firstName - First name of user, displayed on own and team profiles.
   * @param lastName - Last name of user, displayed on own and team profiles.
   * @param accountType - String used to designate type of user account (fan, athlete, manager).
   * @param team - String used to designate the team a user account type Manager is on.
   */
  public Manager(String userName, String password,
      String firstName, String lastName, String accountType, String team) {
    super(userName, password, firstName, lastName, accountType, team);
  }
}
