package com.alonalbert.plexbutler.ui.main;

/**
 * All main screen items implement this
 */
public interface MainItem {
  public static final int TYPE_SHOW = 1;
  public static final int TYPE_EPISODE = 2;
  public static final int TYPE_MOVIE = 3;

  Object get();

  int getType();

  boolean isWatched();
}
