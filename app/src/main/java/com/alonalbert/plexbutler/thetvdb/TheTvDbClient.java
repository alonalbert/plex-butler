package com.alonalbert.plexbutler.thetvdb;

import com.alonalbert.plexbutler.thetvdb.model.GetInfoResponse;
import com.alonalbert.plexbutler.thetvdb.model.LoginResponse;
import com.alonalbert.plexbutler.thetvdb.model.SearchResults;

import org.androidannotations.rest.spring.annotations.Get;
import org.androidannotations.rest.spring.annotations.Path;
import org.androidannotations.rest.spring.annotations.Post;
import org.androidannotations.rest.spring.annotations.Rest;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

/**
 * A client for The TV DB
 */
@Rest(
    rootUrl = "https://api.thetvdb.com",
    interceptors = TheTvDbInterceptor.class,
    converters = GsonHttpMessageConverter.class
)
interface TheTvDbClient {
  @Post("/login")
  LoginResponse login();

  @Get("/search/series?name={name}")
  SearchResults search(@Path String name);

  @Get("/series/{id}")
  GetInfoResponse getInfo(@Path int id);
}
