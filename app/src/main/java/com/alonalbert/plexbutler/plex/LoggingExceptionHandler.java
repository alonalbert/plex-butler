package com.alonalbert.plexbutler.plex;

import android.util.Log;

/**
 * Default UncaughtExceptionHandler
 */
public class LoggingExceptionHandler implements Thread.UncaughtExceptionHandler {
  private static final String TAG = "PlexButler";

  @Override
  public void uncaughtException(Thread thread, Throwable throwable) {
    Log.e(TAG, "Uncaught Exception", throwable);
  }
}
