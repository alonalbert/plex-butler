package com.alonalbert.plexbutler.plex.model;

import com.google.gson.annotations.SerializedName;

/**
 * A Library Section
 */
@SuppressWarnings("unused")
public class Section extends PlexObject {
  public enum Type {
    @SerializedName("show")
    SHOW,
    @SerializedName("movie")
    MOVIE,
    @SerializedName("music")
    MUSIC,
    @SerializedName("photo")
    PHOTO,
    @SerializedName("video")
    VIDEO,
  }

  private Type type;

  public Type getType() {
    return type;
  }

  @Override
  public String toString() {
    return "Section: " + getTitle();
  }
}
