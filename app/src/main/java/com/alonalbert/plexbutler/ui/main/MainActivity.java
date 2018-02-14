package com.alonalbert.plexbutler.ui.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
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
import com.alonalbert.plexbutler.ui.login.LoginActivity_;
import com.alonalbert.plexbutler.ui.main.MainActivity_.SectionItemView_;
import com.alonalbert.plexbutler.ui.recyclerview.AbstractRecyclerViewAdapter;
import com.alonalbert.plexbutler.ui.recyclerview.ViewWrapper;
import com.alonalbert.plexbutler.ui.serverpicker.ServerPickerActivity;
import com.alonalbert.plexbutler.ui.serverpicker.ServerPickerActivity_;
import com.google.common.collect.ImmutableMap;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EViewGroup;
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
import org.androidannotations.annotations.res.DrawableRes;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

@SuppressLint("Registered")
@OptionsMenu(R.menu.main)
@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {
  @SuppressWarnings("unused")
  private static final String TAG = "PlexButler";

  private static final int LOGIN_REQUEST_CODE = 0;
  private static final int SELECT_SERVER_REQUEST_CODE = 1;

  @Pref
  protected PlexButlerPreferences_ prefs;

  @Bean
  protected SectionListAdapter sectionAdapter;

  @Bean
  protected MainAdapter mainAdapter;

  @Bean
  protected PlexClientImpl plexClient;

  @ViewById(R.id.swipe_refresh)
  protected SwipeRefreshLayout swipeRefresh;

  @ViewById(R.id.toolbar)
  protected Toolbar toolbar;

  @ViewById(R.id.serverName)
  protected TextView serverName;

  @ViewById(R.id.drawer_layout)
  protected DrawerLayout drawerLayout;

  @ViewById(R.id.section_list)
  protected ListView sectionList;

  @ViewById(R.id.recycler_view)
  protected RecyclerView recyclerView;

  @OptionsMenuItem(R.id.menu_unwatched)
  protected MenuItem menuUnwatched;

  @NonConfigurationInstance
  protected Server server;

  @NonConfigurationInstance
  protected Stack<PlexObject> displayStack = new Stack<>();

  @ColorRes(R.color.unwatched)
  protected int unwatchedColor;

  @ColorRes(R.color.watched)
  protected int watchedColor;

  @DrawableRes(R.drawable.watched_watched_24)
  protected Drawable iconWatched;

  @DrawableRes(R.drawable.watched_unwatched_24)
  protected Drawable iconUnwatched;

  private Boolean isUnwatchedFilterSet;

  @AfterViews
  protected void afterViews() {
    setSupportActionBar(toolbar);

    final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawerLayout.addDrawerListener(toggle);
    toggle.syncState();

    swipeRefresh.setOnRefreshListener(new OnRefreshListener() {
      @Override
      public void onRefresh() {
        loadItems();
      }
    });

    recyclerView.setAdapter(mainAdapter);
    sectionList.setAdapter(sectionAdapter);

    final String authToken = prefs.plexAuthToken().get();
    if (!authToken.isEmpty()) {
      selectServer();
    } else {
      startActivityForResult(new Intent(this, LoginActivity_.class), LOGIN_REQUEST_CODE);
    }

    isUnwatchedFilterSet = prefs.filterUnwatched().get();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    menuUnwatched.setChecked(isUnwatchedFilterSet);
    menuUnwatched.setIcon(isUnwatchedFilterSet ? iconUnwatched : iconWatched);
    return super.onCreateOptionsMenu(menu);
  }

  @Background
  @OptionsItem(R.id.menu_refresh)
  protected void loadItems() {
    final PlexObject plexObject = displayStack.peek();
    setTitle(plexObject);
    setRefreshing(true);
    final List<Media> mediaList = plexObject.load(
        plexClient,
        server,
        false);
    mainAdapter.setItems(getMainItems(mediaList));
    setRefreshing(false);
  }

  @OptionsItem(R.id.menu_unwatched)
  protected void menuToggleWatched() {
    final boolean newState = !menuUnwatched.isChecked();
    menuUnwatched.setChecked(newState);
    prefs.edit().filterUnwatched().put(newState).apply();
    isUnwatchedFilterSet = newState;
    mainAdapter.notifyDataSetChanged();
    menuUnwatched.setIcon(isUnwatchedFilterSet ? iconUnwatched : iconWatched);
  }

  @Background
  @OptionsItem(R.id.menu_scan)
  public void scanLibrary() {
    final PlexObject plexObject = displayStack.firstElement();
    if (plexObject instanceof Section) {
      plexClient.scanLibrary(server, ((Section) plexObject));
    }
  }

  @UiThread
  protected void setRefreshing(boolean refreshing) {
    swipeRefresh.setRefreshing(refreshing);
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
    displayStack.push(section);
    loadItems();
  }

  @OnActivityResult(LOGIN_REQUEST_CODE)
  void onLoginCompleted(int resultCode) {
    selectServer();
  }

  @OnActivityResult(SELECT_SERVER_REQUEST_CODE)
  void onServerSelected(int resultCode, Intent data) {
    server = new Server(
        data.getStringExtra(ServerPickerActivity.EXTRA_NAME),
        data.getStringExtra(ServerPickerActivity.EXTRA_NAME),
        data.getIntExtra(ServerPickerActivity.EXTRA_PORT, 0));
    loadSections();
  }

  @Override
  public void onBackPressed() {
    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
      drawerLayout.closeDrawer(GravityCompat.START);
    } else {
      displayStack.pop();
      if (displayStack.empty()) {
        super.onBackPressed();
      } else {
        loadItems();
      }
    }
  }

  @ItemClick(R.id.section_list)
  protected void sectionSelected(Section section) {
    displayStack.clear();
    displayStack.push(section);
    loadItems();
    drawerLayout.closeDrawer(GravityCompat.START);
  }

  @Background
  protected void loadPlexObject(PlexObject plexObject) {
    displayStack.push(plexObject);
    loadItems();
  }

  @UiThread
  public void setTitle(PlexObject plexObject) {
    super.setTitle(plexObject.getTitle());
  }

  @NonNull
  private static List<MainItem> getMainItems(List<Media> mediaList) {
    final List<MainItem> items = new ArrayList<>();
    for (Media media : mediaList) {
      items.add(new MediaItem(media));
    }
    return items;
  }

  @Background
  public void setMediaWatched(Media media, boolean watched) {
    plexClient.setWatched(server, media, watched);
  }

  public String getPhotoUrl(Server server, String thumb, int imageWidth, int imageHeight) {
    return plexClient.getPhotoUrl(server, thumb, imageWidth, imageHeight);
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

  @EBean
  public static class MainAdapter extends AbstractRecyclerViewAdapter<MainItem, MainItemView> {
    @RootContext
    MainActivity mainActivity;

    private List<MainItem> unwatchedItems = new ArrayList<>();

    @Override
    protected MainItemView onCreateItemView(ViewGroup parent, int viewType) {
        return MediaItemView_.build(mainActivity);
    }

    @Override
    public void onBindViewHolder(ViewWrapper<MainItemView> holder, int position) {
      MainItemView view = holder.getView();
      final MainItem item = getItem(position);

      view.bind(item, mainActivity.server);
    }

    @UiThread
    void setItems(List<MainItem> items) {
      this.items = items;
      unwatchedItems.clear();
      for (MainItem item : items) {
        if (!item.isWatched()) {
          unwatchedItems.add(item);
        }
      }
      notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
      return getItem(position).getType();
    }

    @Override
    public int getItemCount() {
      if (mainActivity.isUnwatchedFilterSet) {
        return unwatchedItems.size();
      } else {
        return items.size();
      }
    }

    private MainItem getItem(int position) {
      if (mainActivity.isUnwatchedFilterSet) {
        return unwatchedItems.get(position);
      } else {
        return items.get(position);
      }
    }
  }
}
