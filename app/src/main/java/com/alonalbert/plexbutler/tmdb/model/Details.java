package com.alonalbert.plexbutler.tmdb.model;

import com.google.gson.annotations.SerializedName;

/**
 * Response from a get series info request
 */
public class Details {
  @SerializedName("imdb_id")
  private String imdbId;

  public String getImdbId() {
      return imdbId;
    }
}
