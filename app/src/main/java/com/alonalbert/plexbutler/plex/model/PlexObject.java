package com.alonalbert.plexbutler.plex.model;

import com.alonalbert.plexbutler.plex.PlexClientImpl;

import java.util.List;

/**
 * Base class for Plex Objects
 */
@SuppressWarnings("unused")
public abstract class PlexObject {
  private String key;
  private String title;

  public String getKey() {
    return key;
  }

  public String getTitle() {
    return title;
  }

  public abstract List<Media> load(PlexClientImpl plexClient, Server server, boolean unwatched);
}
