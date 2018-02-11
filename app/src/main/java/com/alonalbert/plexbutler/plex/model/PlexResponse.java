package com.alonalbert.plexbutler.plex.model;

import com.google.gson.annotations.SerializedName;

public class PlexResponse {
  @SerializedName("MediaContainer")
  private MediaContainer mediaContainer;

  public MediaContainer getMediaContainer() {
    return mediaContainer;
  }

  @Override
  public String toString() {
    return "PlexResponse{" +
        "mediaContainer=" + mediaContainer +
        '}';
  }
}
