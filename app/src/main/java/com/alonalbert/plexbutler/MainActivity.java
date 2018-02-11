package com.alonalbert.plexbutler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.alonalbert.plexbutler.plex.model.Server;
import com.alonalbert.plexbutler.settings.PlexButlerPreferences_;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.androidannotations.rest.spring.annotations.RestService;

import java.util.List;

@SuppressLint("Registered")
@OptionsMenu(R.menu.main)
@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {
  private static final int LOGIN_REQUEST_CODE = 0;
  private static final int SELECT_SERVER_REQUEST_CODE = 1;

  @Pref
  protected PlexButlerPreferences_ prefs;

  @Bean
  protected SectionListAdapter adapter;

  @RestService
  protected PlexClient plexClient;

  @ViewById(R.id.toolbar)
  protected Toolbar toolbar;

  @ViewById(R.id.drawer_layout)
  protected DrawerLayout drawerLayout;

  @ViewById(R.id.section_list)
  protected ListView sectionList;

  @ViewById(R.id.text_view)
  protected TextView textView;

  @AfterViews
  protected void initialize() {
    setSupportActionBar(toolbar);

    final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
      this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawerLayout.addDrawerListener(toggle);
    toggle.syncState();

    sectionList.setAdapter(adapter);

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
      final Server[] servers = plexClient.getServers();
      if (servers.length == 1) {
        final Server server = servers[0];
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
    loadSections(new Server(prefs.serverName().get(), prefs.serverAddress().get(), prefs.serverPort().get()));
  }

  @Background
  protected void loadSections(Server server) {
    adapter.setSections(Lists.newArrayList(
        new Section(Type.MOVIE, "Movies"),
        new Section(Type.SHOW, "TV Shows")));
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

  @ItemClick(R.id.section_list)
  protected void sectionSelected(Section section) {
    Toast.makeText(this, "Section selected: " + section.getName(), Toast.LENGTH_SHORT).show();
    drawerLayout.closeDrawer(GravityCompat.START);
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

    void setSections(List<Section> sections) {
      this.sections = sections;
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

    @ViewById(R.id.section_name)
    protected TextView section_name;

    @ViewById(R.id.section_image)
    protected ImageView section_image;

    public SectionItemView(Context context) {
      super(context);
    }

    public void bind(Section section) {
      section_name.setText(section.getName());
      section_image.setImageResource(SECTION_ICONS.get(section.getType()));
    }
  }
}
