package com.alonalbert.plexbutler.tmdb.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Response from a search request
 */
public class SearchResponse {
  @SerializedName("results")
  private List<SearchResults> results;

  public List<SearchResults> getResults() {
    return results;
  }

  public static class SearchResults {
    private String title;
    private int id;

    public String getTitle() {
      // Movies have titles, TV shows have names
      return title;
    }

    public int getId() {
      return id;
    }
  }
}
