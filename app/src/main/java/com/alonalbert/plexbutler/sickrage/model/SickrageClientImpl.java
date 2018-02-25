package com.alonalbert.plexbutler.sickrage.model;

import com.alonalbert.plexbutler.settings.PlexButlerPreferences_;
import com.alonalbert.plexbutler.sickrage.SickrageClient;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.androidannotations.rest.spring.annotations.RestService;

/**
 * Small wrapper for Sickrage client
 */
@EBean
public class SickrageClientImpl {
  @RestService
  SickrageClient sickrageClient;

  @Pref
  PlexButlerPreferences_ prefs;

  public AddShowResponse addShow(int id) {
    return sickrageClient.addShow(
        prefs.sickrageHttps().get() ? "https" : "http",
        prefs.sickrageServer().get(),
        prefs.sickragePort().get(),
        prefs.sickrageApiKey().get(),
        id);
  }
}
