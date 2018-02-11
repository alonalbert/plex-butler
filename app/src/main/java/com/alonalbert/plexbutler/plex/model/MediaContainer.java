package com.alonalbert.plexbutler.plex.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * A Media Container
 */
public class MediaContainer {
  @SerializedName("Directory")
  private List<Section> sections;

  public List<Section> getSections() {
    return sections;
  }

  @Override
  public String toString() {
    return "MediaContainer{" +
        "sections=" + sections +
        '}';
  }
}
