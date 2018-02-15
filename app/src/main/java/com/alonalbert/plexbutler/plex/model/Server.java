package com.alonalbert.plexbutler.plex.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A Plex Server Object
 */
public class Server implements Parcelable {

  private final String name;
  private final String address;
  private final int port;

  public Server(String name, String address, int port) {
    this.name = name;
    this.address = address;
    this.port = port;
  }

  protected Server(Parcel in) {
    name = in.readString();
    address = in.readString();
    port = in.readInt();
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

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(name);
    dest.writeString(address);
    dest.writeInt(port);
  }

  public static final Creator<Server> CREATOR = new Creator<Server>() {
    @Override
    public Server createFromParcel(Parcel in) {
      return new Server(in);
    }

    @Override
    public Server[] newArray(int size) {
      return new Server[size];
    }
  };
}
