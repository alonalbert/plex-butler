package com.alonalbert.plexbutler.plex.model;

/**
 * A Plex Server Pojo
 */
public class PlexServer {

  private final String name;
  private final String address;
  private final int port;

  public PlexServer(String name, String address, int port) {

    this.name = name;
    this.address = address;
    this.port = port;
  }

  public String getName() {
    return name;
  }

  public String getAddress() {
    return address;
  }

  public int getPort() {
    return port;
  }

  @Override
  public String toString() {
    return "PlexServer{" +
      "name='" + name + '\'' +
      ", address='" + address + '\'' +
      ", port=" + port +
      '}';
  }
}
