package com.alonalbert.plexbutler.ui.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alonalbert.plexbutler.GlideApp;
import com.alonalbert.plexbutler.GlideRequest;
import com.alonalbert.plexbutler.R;
import com.alonalbert.plexbutler.plex.PlexClientImpl;
import com.alonalbert.plexbutler.plex.model.Media;
import com.alonalbert.plexbutler.plex.model.PlexObject;
import com.alonalbert.plexbutler.plex.model.Section;
import com.alonalbert.plexbutler.plex.model.Section.Type;
import com.alonalbert.plexbutler.plex.model.Server;
import com.alonalbert.plexbutler.settings.PlexButlerPreferences_;
import com.alonalbert.plexbutler.ui.login.LoginActivity_;
import com.alonalbert.plexbutler.ui.main.MainActivity_.MediaItemView_;
import com.alonalbert.plexbutler.ui.main.MainActivity_.SectionItemView_;
import com.alonalbert.plexbutler.ui.recyclerview.AbstractRecyclerViewAdapter;
import com.alonalbert.plexbutler.ui.recyclerview.ViewWrapper;
import com.alonalbert.plexbutler.ui.serverpicker.ServerPickerActivity;
import com.alonalbert.plexbutler.ui.serverpicker.ServerPickerActivity_;
import com.google.common.collect.ImmutableMap;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
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
import org.androidannotations.annotations.res.DimensionRes;
import org.androidannotations.annotations.res.DrawableRes;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import static com.alonalbert.plexbutler.plex.model.Media.Type.EPISODE;
import static com.alonalbert.plexbutler.plex.model.Media.Type.MOVIE;
import static com.alonalbert.plexbutler.plex.model.Media.Type.SHOW;

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

  @DimensionRes(R.dimen.list_item_image_width)
  protected float imageWidth;

  @DimensionRes(R.dimen.list_item_image_height)
  protected float imageHeight;


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
    mainAdapter.setItems(plexObject.load(plexClient, server, false));
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
  void onLoginCompleted() {
    selectServer();
  }

  @OnActivityResult(SELECT_SERVER_REQUEST_CODE)
  void onServerSelected(Intent data) {
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

  @Background
  public void setMediaWatched(Media media, boolean watched) {
    plexClient.setWatched(server, media, watched);
  }

  public String getPhotoUrl(String thumb) {
    return plexClient.getPhotoUrl(server, thumb, (int) imageWidth, (int) imageHeight);
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
  public static class MainAdapter extends AbstractRecyclerViewAdapter<Media, MediaItemView> {
    @RootContext
    MainActivity mainActivity;

    private List<Media> unwatchedItems = new ArrayList<>();

    @Override
    protected MediaItemView onCreateItemView(ViewGroup parent, int viewType) {
        return MediaItemView_.build(mainActivity);
    }

    @Override
    public void onBindViewHolder(ViewWrapper<MediaItemView> holder, int position) {
      MediaItemView view = holder.getView();
      final Media item = getItem(position);

      view.bind(item);
    }

    @UiThread
    void setItems(List<Media> items) {
      this.items = items;
      unwatchedItems.clear();
      for (Media item : items) {
        if (!item.isWatched()) {
          unwatchedItems.add(item);
        }
      }
      notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
      if (mainActivity.isUnwatchedFilterSet) {
        return unwatchedItems.size();
      } else {
        return items.size();
      }
    }

    private Media getItem(int position) {
      if (mainActivity.isUnwatchedFilterSet) {
        return unwatchedItems.get(position);
      } else {
        return items.get(position);
      }
    }
  }

  /**
   * A MainItemView for a Shows
   */
  @EViewGroup(R.layout.media_item)
  public static class MediaItemView extends RelativeLayout {

    @ViewById(R.id.image)
    protected ImageView image;

    @ViewById(R.id.text1)
    protected TextView text1;

    @ViewById(R.id.text2)
    protected TextView text2;

    @ViewById(R.id.text3)
    protected TextView text3;

    @ViewById(R.id.toggle_watched)
    protected ImageView toggleWatched;

    @ViewById(R.id.unwatched_count)
    protected TextView unwatchedCount;

    private Media media;
    private MainActivity mainActivity;

    public MediaItemView(Context context) {
      super(context);
      mainActivity = ((MainActivity) getContext());
    }

    public void bind(Media media) {
      this.media = media;

      final int placeholder = this.media.getType() == MOVIE ? R.drawable.library_type_movie : R.drawable.library_type_show;

      if (this.media.getThumb() != null) {
        final String photoUrl = mainActivity.getPhotoUrl(this.media.getThumb());
        final GlideRequest<Drawable> request = GlideApp
            .with(getContext())
            .load(photoUrl)
            .placeholder(placeholder);
        if (this.media.getType() == EPISODE) {
          request.centerCrop();
        } else {
          request.fitCenter();
        }
        request.into(image);
      } else {
        image.setImageResource(placeholder);
      }

      final Resources res = getResources();
      if (this.media.getType() == EPISODE) {
        text1.setText(res.getString(R.string.episode_title, this.media.getParentIndex(), this.media.getIndex()));
        text2.setText(this.media.getTitle());
        text3.setText(res.getString(R.string.aired_on, this.media.getOriginallyAvailableAt()));
      } else {
        text1.setText(this.media.getTitle());
        if (this.media.getRating() != null) {
          text2.setText(res.getString(R.string.year_and_rating, this.media.getYear(), this.media.getRating()));
        } else {
          text2.setText(String.valueOf(this.media.getYear()));
        }
        text3.setText(TextUtils.join(", ", this.media.getGenres()));
      }
      updatedWatchedToggle();
    }

    @SuppressLint("DefaultLocale")
    protected void updatedWatchedToggle() {
      if (media.getType() == SHOW) {
        final int leafCount = media.getLeafCount();
        final int numUnwatched = leafCount - media.getViewedLeafCount();
        final int color = numUnwatched > 0 ? mainActivity.unwatchedColor : mainActivity.watchedColor;
        toggleWatched.setColorFilter(color);

        final String text;
        if (numUnwatched == 0) {
          text = String.format("<font color=#%1$06x>%2$d</font>", mainActivity.watchedColor & 0xFFFFFF, leafCount);
        } else {
          if (numUnwatched == leafCount) {
            text = String.format("<font color=#%1$06x>%2$d</font>", mainActivity.unwatchedColor & 0xFFFFFF, leafCount);
          } else {
            text = String.format("<font color=#%1$06x>%2$d</font><font color=#%3$06x>/%4$d</font>",
                mainActivity.unwatchedColor & 0xFFFFFF, numUnwatched,
                mainActivity.watchedColor & 0xFFFFFF, leafCount);
          }
        }
        unwatchedCount.setText(Html.fromHtml(text));
        unwatchedCount.setVisibility(VISIBLE);
      } else {
        unwatchedCount.setVisibility(GONE);
        toggleWatched.setColorFilter(media.getViewCount() == 0 ? mainActivity.unwatchedColor : mainActivity.watchedColor);
      }
    }

    @Click(R.id.layout)
    protected void onItemClick() {
      if (media.getType() == SHOW) {
        mainActivity.loadPlexObject(media);
      }
    }

    @Click(R.id.toggle_watched)
    protected void onImageClick() {
      final boolean watched;
      if (media.getType() == SHOW) {
        final int leafCount = media.getLeafCount();
        final int numUnwatched = leafCount - media.getViewedLeafCount();
        if (numUnwatched == 0) {
          media.setViewedLeafCount(0);
          watched = false;
        } else {
          media.setViewedLeafCount(leafCount);
          watched = true;
        }

      } else {
        if (media.getViewCount() == 0) {
          media.setViewCount(1);
          watched = true;
        } else {
          media.setViewCount(0);
          watched = false;
        }
      }
      mainActivity.setMediaWatched(media, watched);
      updatedWatchedToggle();
    }
  }
}
