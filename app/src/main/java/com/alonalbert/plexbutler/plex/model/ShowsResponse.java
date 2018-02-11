package com.alonalbert.plexbutler.plex.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ShowsResponse {
  @SerializedName("MediaContainer")
  private MediaContainer mediaContainer;

  public List<Show> getShows() {
    return mediaContainer.shows;
  }

  @Override
  public String toString() {
    return "Shows=" + getShows();
  }

  private class MediaContainer {
    @SerializedName("Metadata")
    private List<Show> shows;
  }
}
