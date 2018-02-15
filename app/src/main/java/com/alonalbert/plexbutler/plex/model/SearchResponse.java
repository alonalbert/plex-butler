package com.alonalbert.plexbutler.plex.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResponse {
  @SerializedName("MediaContainer")
  private MediaContainer mediaContainer;

  public List<Match> getItems() {
    return mediaContainer.items;
  }

  @Override
  public String toString() {
    return "Items=" + getItems();
  }

  private class MediaContainer {
    @SerializedName("SearchResult")
    private List<Match> items;
  }
}
