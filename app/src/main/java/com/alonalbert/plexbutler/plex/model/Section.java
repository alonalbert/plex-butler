package com.alonalbert.plexbutler.plex.model;

import com.alonalbert.plexbutler.plex.PlexClientImpl;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * A Library Section
 */
@SuppressWarnings("unused")
public class Section extends PlexObject {
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
  private Type type;

  public Type getType() {
    return type;
  }

  @Override
  public List<Media> load(PlexClientImpl plexClient, Server server, boolean unwatched) {
    return plexClient.getSection(server, getKey(), unwatched);
  }

  @Override
  public String toString() {
    return "Section: " + getTitle();
  }
}
