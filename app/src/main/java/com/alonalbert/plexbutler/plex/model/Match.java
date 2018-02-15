package com.alonalbert.plexbutler.plex.model;

/**
 * A Result From a Match Search
 */
public class Match {
  private String guid;
  private String name;
  private int score;
  private String thumb;
  private int year;

  public Match(String guid, String name, int score, String thumb, int year) {
    this.guid = guid;
    this.name = name;
    this.score = score;
    this.thumb = thumb;
    this.year = year;
  }

  public String getGuid() {
    return guid;
  }

  public String getName() {
    return name;
  }

  public int getScore() {
    return score;
  }

  public String getThumb() {
    return thumb;
  }

  public int getYear() {
    return year;
  }
}
