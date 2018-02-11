package com.alonalbert.plexbutler.plex.model;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class Show {
  private String key;
  private String title;
  private String summary;
  private String banner;
  private String thumb;
  private int leafCount;
  private int viewedLeafCount;
  private int year;

  @SerializedName("Genre")
  private List<Genre> genres;

  public String getKey() {
    return key;
  }

  public String getTitle() {
    return title;
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
    final ArrayList<String> values = new ArrayList<>(genres.size());
    for (Genre genre : genres) {
      values.add(genre.tag);
    }
    return values;
  }

  @Override
  public String toString() {
    return "Show: " + title;
  }

  private static class Genre {
    private String tag;
  }
}
