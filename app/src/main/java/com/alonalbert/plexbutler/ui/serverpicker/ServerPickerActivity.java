package com.alonalbert.plexbutler.ui.serverpicker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alonalbert.plexbutler.R;
import com.alonalbert.plexbutler.plex.PlexClientImpl;
import com.alonalbert.plexbutler.plex.model.Server;
import com.alonalbert.plexbutler.ui.serverpicker.ServerPickerActivity_.PlexServerItemView_;

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

@SuppressLint("Registered")
@EActivity(R.layout.activity_server_picker)
public class ServerPickerActivity extends AppCompatActivity {

  public static final String EXTRA_NAME = "EXTRA_NAME";
  public static final String EXTRA_ADDRESS = "ADDRESS";
  public static final String EXTRA_PORT = "PORT";
  @ViewById(R.id.server_list)
  protected ListView serverList;

  @Bean
  protected PlexServerListAdapter adapter;

  @Bean
  protected PlexClientImpl plexClient;


  @AfterViews
  @AfterInject
  @Background
  void bindAdapter() {
    final Server[] servers = plexClient.getServers();
    adapter.setServers(servers);
    initializeServerList();
  }

  @UiThread
  protected void initializeServerList() {
    serverList.setAdapter(adapter);
  }

  @ItemClick(R.id.server_list)
  void serverListItemClicked(Server server) {
    final Intent data = new Intent()
        .putExtra(EXTRA_NAME, server.getName())
        .putExtra(EXTRA_ADDRESS, server.getAddress())
        .putExtra(EXTRA_PORT, server.getPort());
    setResult(RESULT_OK, data);
    finish();
  }

  @EBean
  public static class PlexServerListAdapter extends BaseAdapter {

    private Server[] servers;

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
      return servers.length;
    }

    @Override
    public Server getItem(int position) {
      return servers[position];
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    void setServers(Server[] servers) {
      this.servers = servers;
    }
  }

  @EViewGroup(R.layout.server_item)
  public static class PlexServerItemView extends LinearLayout {

    @ViewById(R.id.name)
    protected TextView name;

    public PlexServerItemView(Context context) {
      super(context);
    }

    public void bind(Server server) {
      name.setText(server.getName());
    }
  }

}
