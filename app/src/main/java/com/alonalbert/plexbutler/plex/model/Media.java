package com.alonalbert.plexbutler.plex.model;

import com.alonalbert.plexbutler.plex.PlexClientImpl;
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

  private String ratingKey;
  private Type type;
  private String parentTitle;
  private String grandparentTitle;
  private String summary;
  private String rating;
  private int index;
  private int parentIndex;
  private String banner;
  private String thumb;
  private int viewCount;
  private int leafCount;
  private int viewedLeafCount;
  private int year;
  private String originallyAvailableAt;

  @SerializedName("Genre")
  private List<Genre> genres;

  public String getRatingKey() {
    return ratingKey;
  }

  public Type getType() {
    return type;
  }

  public String getParentTitle() {
    return parentTitle;
  }

  public String getGrandparentTitle() {
    return grandparentTitle;
  }

  public String getSummary() {
    return summary;
  }

  public String getRating() {
    return rating;
  }

  public int getIndex() {
    return index;
  }

  public int getParentIndex() {
    return parentIndex;
  }

  public String getBanner() {
    return banner;
  }

  public String getThumb() {
    return thumb;
  }

  public int getViewCount() {
    return viewCount;
  }

  public void setViewCount(int viewCount) {
    this.viewCount = viewCount;
  }

  public int getLeafCount() {
    return leafCount;
  }

  public int getViewedLeafCount() {
    return viewedLeafCount;
  }

  public void setViewedLeafCount(int viewedLeafCount) {
    this.viewedLeafCount = viewedLeafCount;
  }

  public int getYear() {
    return year;
  }

  public String getOriginallyAvailableAt() {
    return originallyAvailableAt;
  }

  public List<String> getGenres() {
    final ArrayList<String> values = new ArrayList<>();
    //noinspection ConstantConditions
    if (genres != null) {
      for (Genre genre : genres) {
        values.add(genre.tag);
      }
    }
    return values;
  }

  @Override
  public List<Media> load(PlexClientImpl plexClient, Server server, boolean unwatched) {
    if (type == Type.SHOW) {
      return plexClient.getShow(server, getKey(), unwatched);
    }
    throw new UnsupportedOperationException();
  }

  public boolean isWatched() {
    return type == Type.SHOW ? viewedLeafCount == leafCount : viewCount > 0;
  }
  @Override
  public String toString() {
    return "Media: " + getTitle();
  }

  private static class Genre {
    private String tag;
  }
}
