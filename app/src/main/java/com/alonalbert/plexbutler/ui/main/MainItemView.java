package com.alonalbert.plexbutler.ui.main;

import android.content.Context;
import android.widget.LinearLayout;

/**
 * A base class for main screen item views
 */
public abstract class MainItemView extends LinearLayout {
  public MainItemView(Context context) {
    super(context);
  }

  public abstract void bind(MainItem item);

}
