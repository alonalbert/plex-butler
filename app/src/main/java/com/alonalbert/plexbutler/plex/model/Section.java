package com.alonalbert.plexbutler.plex.model;

import com.google.gson.annotations.SerializedName;

/**
 * A Library Section
 */
public class Section {
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

  private String title;
  private Type type;

  public String getTitle() {
    return title;
  }

  public Type getType() {
    return type;
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("sections{");
    sb.append("title='").append(title).append('\'');
    sb.append(", type='").append(type).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
