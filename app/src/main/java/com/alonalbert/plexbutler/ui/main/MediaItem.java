package com.alonalbert.plexbutler.ui.main;

import com.alonalbert.plexbutler.plex.model.Media;

/**
 * A MainItem for a Media
 */
public class MediaItem implements MainItem<Media> {
  private Media media;

  public MediaItem(Media media) {
    this.media = media;
  }

  @Override
  public Media get() {
    return media;
  }

  @Override
  public int getType() {
    switch (media.getType()) {
      case SHOW:
        return MainItem.TYPE_SHOW;
      case EPISODE:
        return MainItem.TYPE_EPISODE;
      case MOVIE:
        return TYPE_MOVIE;

      default:
        throw new IllegalStateException();
    }
  }
}
