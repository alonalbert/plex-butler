package com.alonalbert.plexbutler.plex.model;

/**
 * A Plex Section
 */
public class Section {
  public enum Type {
    SHOW,
    MOVIE,
    MUSIC,
    PHOTO,
    VIDEO,
  }

  private Type type;
  private String name;

  public Section(Type type, String name) {
    this.type = type;
    this.name = name;
  }

  public Type getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return "Section{" +
        "type=" + type +
        ", name='" + name + '\'' +
        '}';
  }
}
