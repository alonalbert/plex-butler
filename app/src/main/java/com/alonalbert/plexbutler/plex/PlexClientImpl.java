package com.alonalbert.plexbutler.plex;

import com.alonalbert.plexbutler.plex.model.LoginResponse;
import com.alonalbert.plexbutler.plex.model.Media;
import com.alonalbert.plexbutler.plex.model.Section;
import com.alonalbert.plexbutler.plex.model.Server;

import org.androidannotations.annotations.EBean;
import org.androidannotations.rest.spring.annotations.RestService;
import org.springframework.util.LinkedMultiValueMap;

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

    public LoginResponse login(LinkedMultiValueMap<String, String> data) {
        return plexClient.login(data);
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

    public List<Section> getSections(String address, int port) {
        return plexClient.getSections(address, port).getSections();
    }

    public List<Media> getMedia(String address, int port, String key, boolean unwatched) {
        return plexClient.getMedia(address, port, key, unwatched ? FILTER_UNWATCHED : FILTER_ALL).getItems();
    }
}