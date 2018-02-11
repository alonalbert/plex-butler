package com.alonalbert.plexbutler;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.alonalbert.plexbutler.plex.PlexClient;
import com.alonalbert.plexbutler.plex.model.PlexServer;
import com.alonalbert.plexbutler.settings.PlexButlerPreferences_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.androidannotations.rest.spring.annotations.RestService;

@SuppressLint("Registered")
@OptionsMenu(R.menu.main)
@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {

  private static final int LOGIN_REQUEST_CODE = 0;
  private static final int SELECT_SERVER_REQUEST_CODE = 1;

  @Pref
  protected PlexButlerPreferences_ prefs;

  @RestService
  protected PlexClient plexClient;

  @ViewById(R.id.toolbar)
  protected Toolbar toolbar;

  @ViewById(R.id.drawer_layout)
  protected DrawerLayout drawerLayout;

  @ViewById(R.id.navigation_view)
  protected NavigationView navigationView;

  @ViewById(R.id.text_view)
  protected TextView textView;

  @AfterViews
  protected void initialize() {
    setSupportActionBar(toolbar);

    final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
      this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawerLayout.addDrawerListener(toggle);
    toggle.syncState();

    navigationView.setNavigationItemSelectedListener(this);

    final String authToken = prefs.plexAuthToken().get();
    if (!authToken.isEmpty()) {
      selectServer();
    } else {
      startActivityForResult(new Intent(this, LoginActivity_.class), LOGIN_REQUEST_CODE);
    }
  }

  @Background
  protected void selectServer() {
    // First check if we have a server configured yet
    final String serverAddress = prefs.serverAddress().get();
    if (serverAddress.isEmpty()) {
      final PlexServer[] servers = plexClient.getServers();
      if (servers.length == 1) {
        final PlexServer server = servers[0];
        prefs.edit()
            .serverName().put(server.getName())
            .serverAddress().put(server.getAddress())
            .serverPort().put(server.getPort())
            .apply();
        loadSections(server);
      } else {
        startActivityForResult(new Intent(this, ServerPickerActivity_.class), SELECT_SERVER_REQUEST_CODE);
      }
    }  else {
      loadSections();
    }
  }

  private void loadSections() {
    loadSections(new PlexServer(prefs.serverName().get(), prefs.serverAddress().get(), prefs.serverPort().get()));
  }

  @UiThread
  protected void loadSections(PlexServer server) {
    Toast.makeText(this, "Selected server " + server.getName(), Toast.LENGTH_SHORT).show();
  }

  @OnActivityResult(LOGIN_REQUEST_CODE)
  void onLoginCompleted(int resultCode) {
    selectServer();
  }

  @OnActivityResult(LOGIN_REQUEST_CODE)
  void onServerSelected(int resultCode) {
    loadSections();
  }

  @Override
  public void onBackPressed() {
    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
      drawerLayout.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }

  @OptionsItem(R.id.settings)
  public void settingsSelected() {
    Toast.makeText(this, "Settings pressed", Toast.LENGTH_SHORT).show();
  }

  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    drawerLayout.closeDrawer(GravityCompat.START);
    return true;
  }
}
