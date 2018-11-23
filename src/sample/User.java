package sample;

public abstract class User {

  private String accountType;
  private String firstName;
  private String lastName;
  private String accountName;
  private String password;
  private String team;

  public User(String accountName, String password, String firstName,
      String lastName, String accountType, String team) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.accountName = accountName;
    this.password = password;
    this.team = team;
    this.accountType = accountType;
  }

  public void setTeam(String team){
    this.team = team;
  }

  public String getAccountType() {
    return accountType;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getAccountName() {
    return accountName;
  }

  public String getPassword() {
    return password;
  }

  public String getTeam() {
    return team;
  }
}


