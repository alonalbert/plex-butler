package com.alonalbert.plexbutler;

import android.app.Application;

import com.alonalbert.plexbutler.settings.LoggingExceptionHandler;

/**
 * Application class
 */
public class PlexButlerApplication extends Application {
  @Override
  public void onCreate() {
    super.onCreate();
    Thread.setDefaultUncaughtExceptionHandler(new LoggingExceptionHandler());
  }
}
