package sample;

import java.util.Date;

public class Admin extends User {

  public Admin(String accountName, String password) {
    super("admin", accountName, password);
  }

  public void createGame(Team teamA, Team teamB, Date date, Boolean pastGame){

  }

  public void editGameStats(){

  }
}
