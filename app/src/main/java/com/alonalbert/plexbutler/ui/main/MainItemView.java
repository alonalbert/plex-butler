package com.alonalbert.plexbutler.ui.main;

import android.content.Context;
import android.support.constraint.ConstraintLayout;

/**
 * A base class for main screen item views
 */
public abstract class MainItemView extends ConstraintLayout {
  public MainItemView(Context context) {
    super(context);
  }

  public abstract void bind(MainItem item);

}
