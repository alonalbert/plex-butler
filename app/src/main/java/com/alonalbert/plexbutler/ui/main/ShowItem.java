package com.alonalbert.plexbutler.ui.main;

import com.alonalbert.plexbutler.plex.model.Show;

/**
 * A MainItem for a Show
 */
public class ShowItem implements MainItem<Show> {
  private Show show;

  public ShowItem(Show show) {
    this.show = show;
  }

  @Override
  public Show get() {
    return show;
  }

  @Override
  public int getType() {
    return MainItem.TYPE_SHOW;
  }
}
