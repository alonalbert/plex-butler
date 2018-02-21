package com.alonalbert.plexbutler.thetvdb.model;

import java.util.List;

/**
 * Response from a search series info request
 */
public class SearchResults {
  private List<SeriesSearchData> data;

  public List<SeriesSearchData> getData() {
    return data;
  }

  public static class SeriesSearchData {
    private String seriesName;
    private int id;

    public String getSeriesName() {
      return seriesName;
    }

    public int getId() {
      return id;
    }
  }
}
