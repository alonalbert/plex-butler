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
  private String key;

  public String getTitle() {
    return title;
  }

  public Type getType() {
    return type;
  }

  public String getKey() {
    return key;
  }


  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("Section{");
    sb.append("title='").append(title).append('\'');
    sb.append(", type=").append(type);
    sb.append(", key='").append(key).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
