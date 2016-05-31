package ch.ahoegger.photobox.rest;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

@Singleton
public class DBConnection {

  @PostConstruct
  public void init() {
    System.out.println("INIT DB CONNECTION");
  }
}
