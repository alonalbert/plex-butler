package com.alonalbert.plexbutler.tmdb;

import com.alonalbert.plexbutler.tmdb.model.Details;
import com.alonalbert.plexbutler.tmdb.model.SearchResponse;

import org.androidannotations.annotations.EBean;
import org.androidannotations.rest.spring.annotations.RestService;

import java.util.List;

/**
 * A wrapper for the Movie DB client
 */
@EBean
public class TheMovieDbClientImpl {
  @RestService
  TheMovieDbClient theMovieDbClient;

  public List<SearchResponse.SearchResults> search(String query) {
    return theMovieDbClient.search(query).getResults();
  }

  public Details getDetails(int id) {
    return theMovieDbClient.getDetails(id);
  }
}
