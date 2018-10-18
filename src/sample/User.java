package sample;

public abstract class User {

  private String name;
  private String accountName;
  private String password;

  public User(String name, String accountName, String password) {
    this.name = name;
    this.accountName = accountName;
    this.password = password;
  }
}


