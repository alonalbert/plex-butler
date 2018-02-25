package com.alonalbert.plexbutler.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.alonalbert.plexbutler.R;
import com.alonalbert.plexbutler.settings.PlexButlerPreferences;
import com.alonalbert.plexbutler.settings.PlexButlerPreferences_;

import org.androidannotations.annotations.AfterPreferences;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.PreferenceByKey;
import org.androidannotations.annotations.PreferenceChange;
import org.androidannotations.annotations.PreferenceScreen;
import org.androidannotations.annotations.sharedpreferences.Pref;

/**
 * Settings Activity
 */
@SuppressLint("Registered")
@PreferenceScreen(R.xml.settings)
@EActivity
public class SettingsActivity extends PreferenceActivity {

  @Pref
  PlexButlerPreferences_ prefs;

  @PreferenceByKey(R.string.sickrage_server)
  EditTextPreference sickrageServer;

  @PreferenceByKey(R.string.sickrage_port)
  EditTextPreference sickragePort;

  @PreferenceByKey(R.string.sickrage_api_key)
  EditTextPreference sickrageApiKey;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getPreferenceManager().setSharedPreferencesName(PlexButlerPreferences.class.getSimpleName());
  }

  @AfterPreferences
  void afterPrefs() {
    sickrageServerChanged(sickrageServer.getText());
    sickragePortChanged(sickragePort.getText());
    sickrageApiKeyChanged(sickrageApiKey.getText());
  }

  @PreferenceChange(R.string.sickrage_server)
  void sickrageServerChanged(String newValue) {
    if (TextUtils.isEmpty(newValue)) {
      sickrageServer.setSummary(R.string.sickrage_server_summary);
    } else {
      sickrageServer.setSummary(newValue);
    }
  }

  @PreferenceChange(R.string.sickrage_port)
  void sickragePortChanged(String newValue) {
    sickragePort.setSummary(newValue);
  }

  @PreferenceChange(R.string.sickrage_api_key)
  void sickrageApiKeyChanged(String newValue) {
    if (TextUtils.isEmpty(newValue)) {
      sickrageApiKey.setSummary(R.string.sickrage_api_key_summary);
    } else {
      sickrageApiKey.setSummary(newValue);
    }
  }
}
