package com.alonalbert.plexbutler.plex;

import com.alonalbert.plexbutler.plex.model.LoginResponse;
import com.alonalbert.plexbutler.plex.model.MediaResponse;
import com.alonalbert.plexbutler.plex.model.SectionsResponse;
import com.alonalbert.plexbutler.plex.model.Server;

import org.androidannotations.annotations.EBean;
import org.androidannotations.rest.spring.annotations.RestService;
import org.springframework.util.LinkedMultiValueMap;

import java.util.HashMap;
import java.util.Map;

/**
 * A wrapper for the client so we can tweak the interface..
 */
@EBean
public class PlexClientImpl {
    @RestService
    protected PlexClient plexClient;

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

    public SectionsResponse getSections(String address, int port) {
        return plexClient.getSections(address, port);
    }

    public MediaResponse getAllShows(String address, int port, String key) {
        return plexClient.getAllShows(address, port, key);
    }

    public MediaResponse getShowsUnwatched(String address, int port, String key) {
        return plexClient.getShowsUnwatched(address, port, key);
    }
}