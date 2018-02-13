package com.alonalbert.plexbutler.plex;

import android.annotation.SuppressLint;

import com.alonalbert.plexbutler.plex.model.LoginResponse;
import com.alonalbert.plexbutler.plex.model.Media;
import com.alonalbert.plexbutler.plex.model.Section;
import com.alonalbert.plexbutler.plex.model.Server;
import com.alonalbert.plexbutler.settings.PlexButlerPreferences_;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.androidannotations.rest.spring.annotations.RestService;
import org.springframework.util.LinkedMultiValueMap;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A wrapper for the client so we can tweak the interface..
 */
@EBean
public class PlexClientImpl {
  private static final String FILTER_UNWATCHED = "unwatched";
  private static final String FILTER_ALL = "all";

  @RestService
  PlexClient plexClient;

  @Pref
  PlexButlerPreferences_ prefs;

  public LoginResponse.User login(LinkedMultiValueMap<String, String> data) {
    return plexClient.login(data).getUser();
  }

  public Server[] getServers() {
    return plexClient.getServers();
  }

  public Map<String, Server> getServersAsMap() {
    final Server[] servers = plexClient.getServers();
    HashMap<String, Server> map = new HashMap<>();
    for (Server server : servers) {
      map.put(server.getName(), server);
    }
    return map;
  }

  public List<Section> getSections(Server server) {
    return plexClient.getSections(server.getAddress(), server.getPort()).getSections();
  }

  public List<Media> getSection(Server server, String key, boolean unwatched) {
    return plexClient.getMedia(
        server.getAddress(),
        server.getPort(),
        "/library/sections/" + key + "/" + (unwatched ? FILTER_UNWATCHED : FILTER_ALL))
        .getItems();
  }

  public List<Media> getShow(Server server, String key) {
    final String address = server.getAddress();
    final int port = server.getPort();
    final List<Media> seasons = plexClient.getMedia(address, port, key).getItems();
    final ArrayList<Media> episodes = new ArrayList<>();
    for (Media season : seasons) {
      episodes.addAll(plexClient.getMedia(address, port, season.getKey()).getItems());
    }
    return episodes;
  }

  @SuppressLint("DefaultLocale")
  public String getPhotoUrl(Server server, String photoUri, int width, int height) {
    final String authToken = prefs.plexAuthToken().get();
    final String uri;
    try {
      uri = URLEncoder.encode(photoUri + "?X-Plex-Token=" + authToken, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      // Should not happen
      throw new RuntimeException(e);
    }

    return String.format("http://%s:%d/photo/:/transcode?width=%d&height=%d&minSize=1&url=%s&X-Plex-Token=%s",
        server.getAddress(),
        server.getPort(),
        width,
        height,
        uri,
        authToken);
  }
}