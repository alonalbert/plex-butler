package com.alonalbert.plexbutler;

import android.support.v7.widget.RecyclerView;

/**
 * AndroidAnnotations needs this.
 * https://github.com/androidannotations/androidannotations/wiki/Adapters-and-lists#recyclerview-and-viewholder
 */
public class ViewWrapper<V extends MainItemView> extends RecyclerView.ViewHolder {

  private V view;

  public ViewWrapper(V itemView) {
    super(itemView);
    view = itemView;
  }

  public V getView() {
    return view;
  }
}
