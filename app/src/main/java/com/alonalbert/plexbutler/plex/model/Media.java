package com.alonalbert.plexbutler.plex.model;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused", "MismatchedQueryAndUpdateOfCollection"})
public class Media extends PlexObject {
  public enum Type {
    @SerializedName("show")
    SHOW,
    @SerializedName("season")
    SEASON,
    @SerializedName("episode")
    EPISODE,
    @SerializedName("movie")
    MOVIE,
  }

  private Type type;
  private String parentTitle;
  private String grandparentTitle;
  private String summary;
  private int index;
  private int parentIndex;
  private String banner;
  private String thumb;
  private int leafCount;
  private int viewedLeafCount;
  private int year;

  @SerializedName("Genre")
  private List<Genre> genres;

  public Type getType() {
    return type;
  }

  public String getSummary() {
    return summary;
  }

  public String getBanner() {
    return banner;
  }

  public String getThumb() {
    return thumb;
  }

  public int getLeafCount() {
    return leafCount;
  }

  public int getViewedLeafCount() {
    return viewedLeafCount;
  }

  public int getYear() {
    return year;
  }

  public List<String> getGenres() {
    final ArrayList<String> values = new ArrayList<>();
    if (genres != null) {
      for (Genre genre : genres) {
        values.add(genre.tag);
      }
    }
    return values;
  }

  @Override
  public String toString() {
    return "Media: " + getTitle();
  }

  private static class Genre {
    private String tag;
  }
}
