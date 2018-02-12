package com.alonalbert.plexbutler.plex;

import com.alonalbert.plexbutler.plex.model.LoginResponse;
import com.alonalbert.plexbutler.plex.model.Media;
import com.alonalbert.plexbutler.plex.model.Section;
import com.alonalbert.plexbutler.plex.model.Server;

import org.androidannotations.annotations.EBean;
import org.androidannotations.rest.spring.annotations.RestService;
import org.springframework.util.LinkedMultiValueMap;

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
}