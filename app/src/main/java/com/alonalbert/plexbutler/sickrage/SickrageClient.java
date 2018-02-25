package com.alonalbert.plexbutler.sickrage;

import com.alonalbert.plexbutler.sickrage.model.AddShowResponse;

import org.androidannotations.rest.spring.annotations.Get;
import org.androidannotations.rest.spring.annotations.Path;
import org.androidannotations.rest.spring.annotations.Rest;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

/**
 * Sickrage REST client
 */
@Rest(converters = GsonHttpMessageConverter.class)
public interface SickrageClient {
  @Get("{schema}://{server}:{port}api/{apikey}/?cmd=show.addnew&tvdbid={id}&status=wanted")
  AddShowResponse addShow(@Path String schema, @Path String server, @Path int port, @Path String apikey, @Path int id);
}
