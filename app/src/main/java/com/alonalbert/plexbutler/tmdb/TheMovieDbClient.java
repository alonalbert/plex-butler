package com.alonalbert.plexbutler.tmdb;

import com.alonalbert.plexbutler.tmdb.model.Details;
import com.alonalbert.plexbutler.tmdb.model.SearchResponse;

import org.androidannotations.rest.spring.annotations.Get;
import org.androidannotations.rest.spring.annotations.Path;
import org.androidannotations.rest.spring.annotations.Rest;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

/**
 * A client for The TV DB
 */
@Rest(
    rootUrl = "https://api.themoviedb.org/3",
    converters = GsonHttpMessageConverter.class
)
interface TheMovieDbClient {
  @Get("/search/movie?api_key=4fe439906bafeafd5c2693329c839cc1&query={query}&page=1&include_adult=false")
  SearchResponse search(@Path String query);

  @Get("/movie/{id}?api_key=4fe439906bafeafd5c2693329c839cc1")
  Details getDetails(@Path int id);
}
