package com.alonalbert.plexbutler;

import android.app.Application;
import android.util.Log;

/**
 * Application class
 */
public class PlexButlerApplication extends Application {
  private final Thread.UncaughtExceptionHandler systemHandler = Thread.getDefaultUncaughtExceptionHandler();

  @Override
  public void onCreate() {
    super.onCreate();
    Thread.setDefaultUncaughtExceptionHandler(new LoggingExceptionHandler());
  }

  /**
   * Default UncaughtExceptionHandler
   */
  public class LoggingExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "PlexButler";

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
      Log.e(TAG, "Uncaught Exception", throwable);
      systemHandler.uncaughtException(thread, throwable);
    }
  }
}
