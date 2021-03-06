package com.alonalbert.plexbutler.plex;

import android.annotation.SuppressLint;

import com.alonalbert.plexbutler.plex.model.LoginResponse;
import com.alonalbert.plexbutler.plex.model.Match;
import com.alonalbert.plexbutler.plex.model.Media;
import com.alonalbert.plexbutler.plex.model.Section;
import com.alonalbert.plexbutler.plex.model.Server;
import com.alonalbert.plexbutler.settings.PlexButlerPreferences_;
import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.sharedpreferences.Pref;
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
  private static final String MARK_WATCHED = "scrobble";
  private static final String MARK_UNWATCHED = "unscrobble";

  private static final Escaper ESCAPER = UrlEscapers.urlFormParameterEscaper();

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

  public List<Media> getShow(Server server, String key, boolean unwatched) {
    final String address = server.getAddress();
    final int port = server.getPort();
    final ArrayList<Media> episodes = new ArrayList<>();
    final List<Media> seasons = plexClient.getMedia(address, port, key).getItems();
    if (seasons != null) {
      for (Media season : seasons) {
        final List<Media> items = plexClient.getMedia(address, port, season.getKey()).getItems();
        if (unwatched) {
          for (Media item : items) {
            if (item.getViewCount() == 0) {
              episodes.add(item);
            }
          }
        } else {
          episodes.addAll(items);
        }
      }
    }
    return episodes;
  }

  @SuppressLint("DefaultLocale")
  public String getPhotoUrl(Server server, String photoUri, int width, int height) {
    final String authToken = prefs.plexAuthToken().get();
    final String uri = ESCAPER.escape(photoUri + "?X-Plex-Token=" + authToken);

    return "http://" + server.getAddress() + ":" + server.getPort()
        + "/photo/:/transcode?width=" + width + "&height=" + height + "&minSize=1&url=" + uri
        + "&X-Plex-Token=" + authToken;
  }

  public void setWatched(Server server, Media media, boolean watched) {
    plexClient.doAction(
        server.getAddress(),
        server.getPort(),
        watched ? MARK_WATCHED : MARK_UNWATCHED,
        media.getRatingKey());

  }

  public void scanLibrary(Server server, Section section) {
    plexClient.scanLibrary(server.getAddress(), server.getPort(), section.getKey());
  }

  public List<Match> getMatches(Server server, String key, String agent, String title, String year) {
    return plexClient.getMatches(server.getAddress(), server.getPort(), key, agent, title, year)
        .getItems();
  }

  public void setMatch(Server server, String key, Match match) {
    plexClient.setMatch(
        server.getAddress(),
        server.getPort(),
        key,
        match.getGuid(),
        match.getName());
  }

  @Background
  public void unmatch(Server server, String key) {
    plexClient.unmatch(server.getAddress(), server.getPort(), key);
  }
}