package com.alonalbert.plexbutler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alonalbert.plexbutler.MainActivity_.SectionItemView_;
import com.alonalbert.plexbutler.plex.PlexClient;
import com.alonalbert.plexbutler.plex.model.Section;
import com.alonalbert.plexbutler.plex.model.Section.Type;
import com.alonalbert.plexbutler.plex.model.SectionsResponse;
import com.alonalbert.plexbutler.plex.model.Server;
import com.alonalbert.plexbutler.plex.model.Show;
import com.alonalbert.plexbutler.plex.model.ShowsResponse;
import com.alonalbert.plexbutler.settings.PlexButlerPreferences_;
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
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.androidannotations.rest.spring.annotations.RestService;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("Registered")
@OptionsMenu(R.menu.main)
@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {
  private static final String TAG = "PlexButler";

  private static final int LOGIN_REQUEST_CODE = 0;
  private static final int SELECT_SERVER_REQUEST_CODE = 1;

  @Pref
  protected PlexButlerPreferences_ prefs;

  @Bean
  protected SectionListAdapter sectionAdapter;

  @Bean
  protected MainAdapter mainAdapter;

  @RestService
  protected PlexClient plexClient;

  @ViewById(R.id.toolbar)
  protected Toolbar toolbar;

  @ViewById(R.id.drawer_layout)
  protected DrawerLayout drawerLayout;

  @ViewById(R.id.section_list)
  protected ListView sectionList;

  @ViewById(R.id.recycler_view)
  protected RecyclerView recyclerView;

  @NonConfigurationInstance
  protected Server server;

  @AfterViews
  protected void initialize() {
    setSupportActionBar(toolbar);

    final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
      this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawerLayout.addDrawerListener(toggle);
    toggle.syncState();

    recyclerView.setAdapter(mainAdapter);
    sectionList.setAdapter(sectionAdapter);

    final String authToken = prefs.plexAuthToken().get();
    if (!authToken.isEmpty()) {
      selectServer();
    } else {
      startActivityForResult(new Intent(this, LoginActivity_.class), LOGIN_REQUEST_CODE);
    }
  }

  @Background
  protected void selectServer() {
    if (server == null) {
      final Server[] servers = plexClient.getServers();

      if (servers.length == 1) {
        server = servers[0];
        prefs.edit().serverName().put(server.getName()).apply();
      } else {
        final String serverName = prefs.serverName().get();
        if (!serverName.isEmpty()) {
          for (Server server : servers) {
            if (server.getName().equals(serverName)) {
              this.server = server;
              break;
            }
          }
        } else {
          startActivityForResult(new Intent(this, ServerPickerActivity_.class), SELECT_SERVER_REQUEST_CODE);
          return;
        }
      }
    }
    loadSections();
  }

  @Background
  protected void loadSections() {
    final SectionsResponse response = plexClient.getSections(
        server.getAddress(),
        server.getPort());
    sectionAdapter.setSections(response.getSections());
  }

  @OnActivityResult(LOGIN_REQUEST_CODE)
  void onLoginCompleted(int resultCode) {
    selectServer();
  }

  @OnActivityResult(LOGIN_REQUEST_CODE)
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
      super.onBackPressed();
    }
  }

  @OptionsItem(R.id.settings)
  public void settingsSelected() {
    Toast.makeText(this, "Settings pressed", Toast.LENGTH_SHORT).show();
  }

  @ItemClick(R.id.section_list)
  protected void sectionSelected(Section section) {
    loadSection(section);
    drawerLayout.closeDrawer(GravityCompat.START);
  }

  @Background
  protected void loadSection(Section section) {
    final ShowsResponse response = plexClient.getShowsAll(server.getAddress(), server.getPort(), section.getKey());
    Log.e(TAG, "Section " + section.getTitle() + ": " + response);
    final List<MainItem> items = new ArrayList<>();
    for (Show show : response.getShows()) {
      items.add(new ShowItem(show));
    }
    mainAdapter.setItems(items);

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
  public static class MainAdapter extends RecyclerView.Adapter<ViewWrapper<MainItemView>> {

    private List<MainItem> items = new ArrayList<>();

    @RootContext
    Context context;

    @Override
    public ViewWrapper<MainItemView> onCreateViewHolder(ViewGroup parent, int viewType) {
      return new ViewWrapper<>(onCreateItemView(parent, viewType));
    }

    private MainItemView onCreateItemView(ViewGroup parent, int viewType) {
      switch (viewType) {
        case MainItem.TYPE_SHOW:
          return ShowItemView_.build(context);
      }
      throw new UnsupportedOperationException("Unknown type: " + viewType);
    }

    @Override
    public void onBindViewHolder(ViewWrapper<MainItemView> holder, int position) {
      MainItemView view = holder.getView();
      final MainItem item = items.get(position);

      view.bind(item);
    }

    @UiThread
    protected void setItems(List<MainItem> items) {
      this.items = items;
      notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
      return items.get(position).getType();
    }

    @Override
    public int getItemCount() {
      return items.size();
    }
  }


//  @EBean
//  public static class MainAdapter extends RecyclerView.Adapter<MainViewHolder> {
//    private static ImmutableMap<MainListItem.Type, Integer> VIEW_TYPES = new ImmutableMap.Builder<MainListItem.Type, Integer>()
//        .put(MainListItem.Type.SHOW, R.layout.show_list_item)
//        .build();
//    private List<MainListItem> items;
//
//
//    @Override
//    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//      return ;
//    }
//
//    @Override
//    public void onBindViewHolder(MainViewHolder holder, int position) {
//
//    }
//
//    @Override
//    public int getItemCount() {
//      return items != null ? items.size() : 0;
//    }
//
//    @UiThread
//    void setItems(List<MainListItem> items) {
//      this.items = items;
//      notifyDataSetChanged();
//    }
//
//    static class MainViewHolder extends RecyclerView.ViewHolder {
//
//      public MainViewHolder(View itemView) {
//        super(itemView);
//      }
//    }
//  }

}
