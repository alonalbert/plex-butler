package com.alonalbert.plexbutler.ui.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * AndroidAnnotations needs this to support RecyclerView.
 * https://github.com/androidannotations/androidannotations/wiki/Adapters-and-lists#recyclerview-and-viewholder
 */
public abstract class AbstractRecyclerViewAdapter<T, V extends View> extends RecyclerView.Adapter<ViewWrapper<V>> {

  protected List<T> items = new ArrayList<>();

  @Override
  public int getItemCount() {
    return items.size();
  }

  @Override
  public final ViewWrapper<V> onCreateViewHolder(ViewGroup parent, int viewType) {
    return new ViewWrapper<>(onCreateItemView(parent, viewType));
  }

  protected abstract V onCreateItemView(ViewGroup parent, @SuppressWarnings("unused") int viewType);

  // additional methods to manipulate the items
}
