package sample;

import java.util.ArrayList;

public class Fan extends User {

  private Team teamFollowed;

  public Fan(String name, String accountName, String password) {
    super(name, accountName, password);
    teamFollowed = null; // teamless
  }
}
