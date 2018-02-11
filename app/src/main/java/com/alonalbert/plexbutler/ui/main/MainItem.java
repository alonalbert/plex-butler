package com.alonalbert.plexbutler.ui.main;

/**
 * All main screen items implement this
 */
public interface MainItem<T> {
  public static final int TYPE_SHOW = 1;
  public static final int TYPE_MOVIE = 2;

  T get();

  int getType();
}
