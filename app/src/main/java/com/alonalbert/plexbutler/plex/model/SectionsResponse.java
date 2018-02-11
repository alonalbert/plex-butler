package com.alonalbert.plexbutler.plex.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SectionsResponse {
  @SerializedName("MediaContainer")
  private MediaContainer mediaContainer;

  public List<Section> getSections() {
    return mediaContainer.sections;
  }

  public MediaContainer getMediaContainer() {
    return mediaContainer;
  }

  @Override
  public String toString() {
    return "SectionsResponse{" +
        "sections=" + getSections() +
        '}';
  }

  private class MediaContainer {
    @SerializedName("Directory")
    private List<Section> sections;
  }
}
