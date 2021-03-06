package com.alonalbert.plexbutler.plex;

import com.alonalbert.plexbutler.plex.model.LoginResponse;
import com.alonalbert.plexbutler.plex.model.MediaResponse;
import com.alonalbert.plexbutler.plex.model.SearchResponse;
import com.alonalbert.plexbutler.plex.model.SectionsResponse;
import com.alonalbert.plexbutler.plex.model.Server;

import org.androidannotations.rest.spring.annotations.Body;
import org.androidannotations.rest.spring.annotations.Get;
import org.androidannotations.rest.spring.annotations.Header;
import org.androidannotations.rest.spring.annotations.Headers;
import org.androidannotations.rest.spring.annotations.Path;
import org.androidannotations.rest.spring.annotations.Post;
import org.androidannotations.rest.spring.annotations.Put;
import org.androidannotations.rest.spring.annotations.Rest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;

/**
 * Plex API Client
 */
@Rest(
  rootUrl =  "https://plex.tv",
  converters = { GsonHttpMessageConverter.class, FormHttpMessageConverter.class, PlexServersXmlConverter.class },
  interceptors = {PlexRequestInterceptor.class })
interface PlexClient {
  @Post("/users/sign_in.json")
  @Headers({
    @Header(name = HttpHeaders.CONTENT_TYPE, value = MediaType.APPLICATION_FORM_URLENCODED_VALUE),
  })
  LoginResponse login(@Body LinkedMultiValueMap<String, String> data);

  @Get("/pms/servers.xml")
  Server[] getServers();

  @Get("http://{address}:{port}/library/sections")
  SectionsResponse getSections(@Path String address, @Path int port);

  @Get("http://{address}:{port}{key}")
  MediaResponse getMedia(@Path String address, @Path int port, @Path String key);

  @Get("http://{address}:{port}/:/{action}?identifier=com.plexapp.plugins.library&key={key}")
  void doAction(@Path String address, @Path int port, @Path String action, @Path String key);

  @Get("http://{address}:{port}/library/sections/{key}/refresh")
  void scanLibrary(@Path String address, @Path int port, @Path String key);

  @Get("http://{address}:{port}/library/metadata/{key}/matches?manual=1&title={title}&year={year}&agent={agent}&language=en")
  SearchResponse getMatches(@Path String address, @Path int port, @Path String key, @Path String agent, @Path String title, @Path String year);

  @Get("http://{address}:{port}/library/metadata/{key}/match?guid={guid}&name={name}")
  void setMatch(@Path String address, @Path int port, @Path String key, @Path String guid, @Path String name);

  @Put("http://{address}:{port}/library/metadata/{key}/unmatch")
  void unmatch(@Path String address, @Path int port, @Path String key);
}
