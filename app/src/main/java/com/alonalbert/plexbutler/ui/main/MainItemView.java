package com.alonalbert.plexbutler.ui.main;

import android.content.Context;
import android.widget.RelativeLayout;

import com.alonalbert.plexbutler.plex.model.Server;

/**
 * A base class for main screen item views
 */
public abstract class MainItemView extends RelativeLayout {
  public MainItemView(Context context) {
    super(context);
  }

  public abstract void bind(MainItem item, Server server);

}
