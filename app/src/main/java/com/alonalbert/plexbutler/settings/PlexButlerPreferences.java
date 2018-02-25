package com.alonalbert.plexbutler.settings;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.DefaultInt;
import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Shared preferences for Plex butler
 */
@SuppressWarnings("unused")
@SharedPref(SharedPref.Scope.UNIQUE)
public interface PlexButlerPreferences {
  @DefaultString("")
  String plexAuthToken();

  @DefaultString("")
  String serverName();

  @DefaultBoolean(false)
  boolean filterUnwatched();

  @DefaultString("")
  String sickrageServer();

  @DefaultInt(8081)
  int sickragePort();

  @DefaultBoolean(false)
  boolean sickrageHttps();

  @DefaultString("")
  String sickrageApiKey();
}
