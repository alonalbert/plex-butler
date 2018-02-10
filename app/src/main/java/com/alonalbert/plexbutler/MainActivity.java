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
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.androidannotations.rest.spring.annotations.RestService;

@SuppressLint("Registered")
@OptionsMenu(R.menu.main)
@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {

  private static final int LOGIN_REQUEST_CODE = 0;

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
      loadPlexServers();
    } else {
      startActivityForResult(new Intent(this, LoginActivity_.class), LOGIN_REQUEST_CODE);
    }
  }

  @OnActivityResult(LOGIN_REQUEST_CODE)
  void onLoginCompleted(int resultCode, Intent data) {
    loadPlexServers();
  }

  @Background
  protected void loadPlexServers() {
    final PlexServer[] servers = plexClient.getServers();
    System.out.println(servers);
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
