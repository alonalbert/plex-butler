package com.alonalbert.plexbutler.plex;

import com.alonalbert.plexbutler.plex.model.LoginResponse;

import org.androidannotations.rest.spring.annotations.Body;
import org.androidannotations.rest.spring.annotations.Header;
import org.androidannotations.rest.spring.annotations.Headers;
import org.androidannotations.rest.spring.annotations.Post;
import org.androidannotations.rest.spring.annotations.Rest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;

/**
 * Plex API Client
 */
@Rest(rootUrl =  "https://plex.tv", converters = { GsonHttpMessageConverter.class, FormHttpMessageConverter.class } )
public interface PlexClient {
  @Post("/users/sign_in.json")
  @Headers({
    @Header(name = "X-Plex-Product", value = "Plex Butler"),
    @Header(name = "X-Plex-Version", value = "1.0"),
    @Header(name = "X-Plex-Client-Identifier", value = "111"),
    @Header(name = HttpHeaders.CONTENT_TYPE, value = MediaType.APPLICATION_FORM_URLENCODED_VALUE),
  })
  ResponseEntity<LoginResponse> login(@Body LinkedMultiValueMap<String, String> data);
}
