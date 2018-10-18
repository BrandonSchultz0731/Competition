package sample;

import java.util.ArrayList;

public class Fan extends User {

  private ArrayList<Team> teamsFollowed;
  private ArrayList<Athlete> playersFollowed;

  public Fan(String name, String accountName, String password) {
    super(name, accountName, password);
    teamsFollowed = new ArrayList<>();
    playersFollowed = new ArrayList<>();
  }
}
