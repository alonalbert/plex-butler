package com.alonalbert.plexbutler.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alonalbert.plexbutler.R;
import com.alonalbert.plexbutler.plex.PlexClientImpl;
import com.alonalbert.plexbutler.plex.model.Media;
import com.alonalbert.plexbutler.plex.model.PlexObject;
import com.alonalbert.plexbutler.plex.model.Section;
import com.alonalbert.plexbutler.plex.model.Section.Type;
import com.alonalbert.plexbutler.plex.model.Server;
import com.alonalbert.plexbutler.settings.PlexButlerPreferences_;
import com.alonalbert.plexbutler.ui.MainActivity_.SectionItemView_;
import com.google.common.collect.ImmutableMap;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;
import org.androidannotations.annotations.res.DimensionRes;
import org.androidannotations.annotations.res.DrawableRes;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressLint("Registered")
@OptionsMenu(R.menu.main)
@EActivity(R.layout.main_activity)
public class MainActivity extends AppCompatActivity {
  @SuppressWarnings("unused")
  private static final String TAG = "PlexButler";

  private static final int LOGIN_REQUEST_CODE = 0;
  private static final int SELECT_SERVER_REQUEST_CODE = 1;

  @Pref
  protected PlexButlerPreferences_ prefs;

  @Bean
  protected PlexClientImpl plexClient;

  @Bean
  protected SectionListAdapter sectionAdapter;

  @ViewById(R.id.toolbar)
  protected Toolbar toolbar;

  @ViewById(R.id.serverName)
  protected TextView serverName;

  @ViewById(R.id.drawer_layout)
  protected DrawerLayout drawerLayout;

  @ViewById(R.id.section_list)
  protected ListView sectionList;

  @FragmentById(R.id.main_fragment)
  MainFragment mainFragment;

  @DrawableRes(R.drawable.watched_watched_24)
  protected Drawable iconWatched;

  @DrawableRes(R.drawable.watched_unwatched_24)
  protected Drawable iconUnwatched;

  @DimensionRes(R.dimen.list_item_image_width)
  protected float imageWidth;

  @DimensionRes(R.dimen.list_item_image_height)
  protected float imageHeight;

  @ColorRes(R.color.unwatched)
  protected int unwatchedColor;

  @ColorRes(R.color.watched)
  protected int watchedColor;

  @OptionsMenuItem(R.id.menu_unwatched)
  protected MenuItem menuUnwatched;

  @NonConfigurationInstance
  protected Server server;

  @NonConfigurationInstance
  protected List<Media> items;

  @NonConfigurationInstance
  protected Section currentSection;

  @NonConfigurationInstance
  protected PlexObject currentPlexObject;

  @NonConfigurationInstance
  protected int currentPosition;

  @AfterViews
  protected void afterViews() {
    setSupportActionBar(toolbar);

    final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawerLayout.addDrawerListener(toggle);
    toggle.syncState();

    sectionList.setAdapter(sectionAdapter);

    final String authToken = prefs.plexAuthToken().get();

    if (server == null) {
      if (!authToken.isEmpty()) {
        selectServer();
      } else {
        startActivityForResult(new Intent(this, LoginActivity_.class), LOGIN_REQUEST_CODE);
      }
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    final boolean filterUnwatched = prefs.filterUnwatched().get();
    menuUnwatched.setChecked(filterUnwatched);
    menuUnwatched.setIcon(filterUnwatched ? iconUnwatched : iconWatched);
    return super.onCreateOptionsMenu(menu);
  }

  @Background
  @OptionsItem(R.id.menu_scan)
  public void scanLibrary() {
    plexClient.scanLibrary(server, currentSection);
  }

  @OptionsItem(R.id.menu_refresh)
  protected void refreshItems() {
    loadItems(currentPlexObject, -1);
  }

  @UiThread
  protected void loadItems(PlexObject plexObject, int scrollTo) {
    setTitle(plexObject.getTitle());
    mainFragment.swipeRefresh.setRefreshing(true);
    doLoadItems(plexObject, scrollTo);
  }

  @Background
  protected void doLoadItems(PlexObject plexObject, int scrollTo) {
    items = plexObject.load(plexClient, server, false);
    handleLoadItemsResults(plexObject, scrollTo);
  }

  @UiThread
  void handleLoadItemsResults(PlexObject parent, int scrollTo) {
    mainFragment.handleLoadItemsResults(parent, items, scrollTo);
  }

  @OptionsItem(R.id.menu_unwatched)
  protected void menuToggleWatched() {
    final boolean newState = !menuUnwatched.isChecked();
    menuUnwatched.setChecked(newState);
    prefs.edit().filterUnwatched().put(newState).apply();
    mainFragment.adapter.notifyDataSetChanged(newState);
    menuUnwatched.setIcon(newState ? iconUnwatched : iconWatched);
  }


  @Background
  protected void selectServer() {
    if (server == null) {
      final Map<String, Server> servers = plexClient.getServersAsMap();

      if (servers.size() == 1) {
        server = servers.values().iterator().next();
        prefs.edit().serverName().put(server.getName()).apply();
      } else {
        final String serverName = prefs.serverName().get();
        if (!serverName.isEmpty()) {
          server = servers.get(serverName);
        }
      }
    }
    if (server != null) {
      setServerName(server.getName());
      loadSections();
    } else {
      startActivityForResult(new Intent(this, ServerPickerActivity_.class), SELECT_SERVER_REQUEST_CODE);
    }
  }

  @UiThread
  protected void setServerName(String name) {
    serverName.setText(name);
  }

  @Background
  protected void loadSections() {
    final List<Section> sections = plexClient.getSections(server);
    sectionAdapter.setSections(sections);
    final Section section = sections.get(0);
    loadSection(section);
  }

  private void loadSection(Section section) {
    currentPlexObject = currentSection = section;
    loadItems(section, -1);
  }

  @OnActivityResult(LOGIN_REQUEST_CODE)
  void onLoginCompleted() {
    selectServer();
  }

  @OnActivityResult(SELECT_SERVER_REQUEST_CODE)
  void onServerSelected(Intent data) {
    server = new Server(
        data.getStringExtra(ServerPickerActivity.EXTRA_NAME),
        data.getStringExtra(ServerPickerActivity.EXTRA_ADDRESS),
        data.getIntExtra(ServerPickerActivity.EXTRA_PORT, 0));
    loadSections();
  }

  @Override
  public void onBackPressed() {
    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
      drawerLayout.closeDrawer(GravityCompat.START);
    } else {
      if (currentPlexObject == currentSection) {
        super.onBackPressed();
      } else {
        currentPlexObject = currentSection;
        loadItems(currentPlexObject, currentPosition);
      }
    }
  }

  @ItemClick(R.id.section_list)
  protected void sectionSelected(Section section) {
    loadSection(section);
    drawerLayout.closeDrawer(GravityCompat.START);
  }

  @UiThread
  public void setTitle(PlexObject plexObject) {
    super.setTitle(plexObject.getTitle());
  }

  @Background
  public void setMediaWatched(Media media, boolean watched) {
    plexClient.setWatched(server, media, watched);
  }

  public String getPhotoUrl(String thumb) {
    return plexClient.getPhotoUrl(server, thumb, (int) imageWidth, (int) imageHeight);
  }

  public void loadShow(Media show) {
    currentPlexObject = show;
    currentPosition = mainFragment.getCurrentPosition();
    loadItems(show, -1);
  }

  public void loadMovie(Media media) {
    currentPlexObject = media;
    currentPosition = mainFragment.getCurrentPosition();
    mainFragment.setItems(media, new ArrayList<Media>());
  }

  @EBean
  public static class SectionListAdapter extends BaseAdapter {

    private List<Section> sections;

    @RootContext
    Context context;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

      final SectionItemView itemView;
      if (convertView == null) {
        itemView = SectionItemView_.build(context);
      } else {
        itemView = (SectionItemView) convertView;
      }

      itemView.bind(getItem(position));

      return itemView;
    }

    @Override
    public int getCount() {
      return sections != null ? sections.size() : 0;
    }

    @Override
    public Section getItem(int position) {
      return sections.get(position);
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @UiThread
    void setSections(List<Section> sections) {
      this.sections = sections;
      notifyDataSetChanged();
    }
  }

  @EViewGroup(R.layout.section_item)
  public static class SectionItemView extends LinearLayout {
    static final ImmutableMap<Type, Integer> SECTION_ICONS = new ImmutableMap.Builder<Type, Integer>()
        .put(Type.SHOW, R.drawable.library_type_show)
        .put(Type.MOVIE, R.drawable.library_type_movie)
        .put(Type.PHOTO, R.drawable.library_type_photo)
        .put(Type.MUSIC, R.drawable.library_type_music)
        .put(Type.VIDEO, R.drawable.library_type_video)
        .build();

    @ViewById(R.id.section_title)
    protected TextView section_title;

    @ViewById(R.id.section_image)
    protected ImageView section_image;

    public SectionItemView(Context context) {
      super(context);
    }

    public void bind(Section section) {
      section_title.setText(section.getTitle());
      section_image.setImageResource(SECTION_ICONS.get(section.getType()));
    }
  }
}
