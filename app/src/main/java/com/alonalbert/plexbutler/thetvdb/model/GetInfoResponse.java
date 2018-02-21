package com.alonalbert.plexbutler.thetvdb.model;

/**
 * Response from a get series info request
 */
public class GetInfoResponse {
  private SeriesInfo data;

  public SeriesInfo getData() {
    return data;
  }

  public static class SeriesInfo {
    private String imdbId;

    public String getImdbId() {
      return imdbId;
    }
  }
}
