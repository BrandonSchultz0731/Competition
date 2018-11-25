package sample;

public abstract class User {

  private String accountType;
  private String firstName;
  private String lastName;
  private String accountName;
  private String password;
  private String team;

  /**
   * General case abstract class for user accounts. Extended by types Fan, Athlete, Manager, and Admin.
   *
   * @param accountName - String used as log-in credential Username.
   * @param password - String used as log-in credential Password.
   * @param firstName - First name of user creating account.
   * @param lastName - Last name of user creating account.
   * @param accountType - Created during account creation, string type (Fan, Athlete, Manager).
   * @param team - String designation for account types. Athletes/managers are part of a team, fans can follow a team.
   */
  public User(String accountName, String password, String firstName,
      String lastName, String accountType, String team) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.accountName = accountName;
    this.password = password;
    this.team = team;
    this.accountType = accountType;
  }

  //Sets team of a user to specified string. String must be a team.
  public void setTeam(String team){
    this.team = team;
  }

  //return account type (fan, athlete, manager).
  public String getAccountType() {
    return accountType;
  }

  //return account user's first name.
  public String getFirstName() {
    return firstName;
  }

  //return account user's last name.
  public String getLastName() {
    return lastName;
  }

  //return account name.
  public String getAccountName() {
    return accountName;
  }

  //return account password.
  public String getPassword() {
    return password;
  }

  //return team that account is part of. (Fans follow team instead of being on one.)
  public String getTeam() {
    return team;
  }
}


