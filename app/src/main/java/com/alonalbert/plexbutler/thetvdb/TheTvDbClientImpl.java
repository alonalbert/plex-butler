package com.alonalbert.plexbutler.thetvdb;

import com.alonalbert.plexbutler.thetvdb.model.GetInfoResponse.SeriesInfo;
import com.alonalbert.plexbutler.thetvdb.model.SearchResults.SeriesSearchData;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.rest.spring.annotations.RestService;

import java.util.List;

/**
 * A wrapper for the TV DB client
 */
@EBean
public class TheTvDbClientImpl {
  @RestService
  TheTvDbClient theTvDbClient;

  @Bean
  TheTvDbInterceptor interceptor;

  public List<SeriesSearchData> search(String name) {
    ensureToken();
    return theTvDbClient.search(name).getData();
  }

  public SeriesInfo getInfo(int id) {
    ensureToken();
    return theTvDbClient.getInfo(id).getData();
  }

  private void ensureToken() {
    if (interceptor.getToken() == null) {
      interceptor.setToken(theTvDbClient.login().getToken());
    }
  }

}
