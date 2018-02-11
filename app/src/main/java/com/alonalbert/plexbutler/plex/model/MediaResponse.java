package com.alonalbert.plexbutler.plex.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MediaResponse {
  @SerializedName("MediaContainer")
  private MediaContainer mediaContainer;

  public List<Media> getItems() {
    return mediaContainer.items;
  }

  @Override
  public String toString() {
    return "Items=" + getItems();
  }

  private class MediaContainer {
    @SerializedName("Metadata")
    private List<Media> items;
  }
}
