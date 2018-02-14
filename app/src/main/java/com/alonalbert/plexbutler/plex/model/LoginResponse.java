package com.alonalbert.plexbutler.plex.model;

/**
 * Response object to login request
 */
@SuppressWarnings("unused")
public class LoginResponse {
  private User user;

  public User getUser() {
    return user;
  }

  public static class User {
    private String authToken;

    public String getAuthToken() {
      return authToken;
    }
  }
}
