package com.alonalbert.plexbutler.settings;

import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Shared preferences for Plex butler
 */
@SharedPref(SharedPref.Scope.UNIQUE)
public interface PlexButlerPreferences {
  @DefaultString("")
  String plexAuthToken();
}
