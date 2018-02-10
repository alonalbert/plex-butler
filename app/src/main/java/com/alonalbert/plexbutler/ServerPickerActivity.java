package com.alonalbert.plexbutler;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alonalbert.plexbutler.ServerPickerActivity_.PlexServerItemView_;
import com.alonalbert.plexbutler.plex.PlexClient;
import com.alonalbert.plexbutler.plex.model.PlexServer;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.rest.spring.annotations.RestService;

import static android.widget.Toast.LENGTH_SHORT;

@EActivity(R.layout.activity_server_picker)
public class ServerPickerActivity extends AppCompatActivity {

  @ViewById(R.id.server_list)
  protected ListView serverList;

  @Bean
  PlexServerListAdapter adapter;

  @RestService
  protected PlexClient plexClient;


  @AfterViews
  @AfterInject
  @Background
  void bindAdapter() {
    final PlexServer[] plexServers = plexClient.getServers();
    adapter.setPlexServers(plexServers);
    initializeServerList();
  }

  @UiThread
  protected void initializeServerList() {
    serverList.setAdapter(adapter);
  }

  @ItemClick(R.id.server_list)
  void serverListItemClicked(PlexServer plexServer) {
    Toast.makeText(this, plexServer.getName(), LENGTH_SHORT).show();
  }

  @EBean
  public static class PlexServerListAdapter extends BaseAdapter {

    private PlexServer[] plexServers;

    @RootContext
    Context context;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

      final PlexServerItemView itemView;
      if (convertView == null) {
        itemView = PlexServerItemView_.build(context);
      } else {
        itemView = (PlexServerItemView) convertView;
      }

      itemView.bind(getItem(position));

      return itemView;
    }

    @Override
    public int getCount() {
      return plexServers.length;
    }

    @Override
    public PlexServer getItem(int position) {
      return plexServers[position];
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    void setPlexServers(PlexServer[] plexServers) {
      this.plexServers = plexServers;
    }
  }


  @EViewGroup(R.layout.server_item)
  public static class PlexServerItemView extends LinearLayout {

    @ViewById(R.id.name)
    protected TextView name;

    public PlexServerItemView(Context context) {
      super(context);
    }

    public void bind(PlexServer plexServer) {
      name.setText(plexServer.getName());
    }
  }

}
