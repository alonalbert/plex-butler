package com.alonalbert.plexbutler.ui.main;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.alonalbert.plexbutler.R;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * A MainItemView for a Shows
 */
@EViewGroup(R.layout.show_item)
public class ShowItemView extends MainItemView {
  @ViewById(R.id.title)
  protected TextView title;

  @ViewById(R.id.image)
  protected ImageView image;

  public ShowItemView(Context context) {
    super(context);
  }

  @Override
  public void bind(MainItem item) {
    final MediaItem mediaItem = (MediaItem) item;
    title.setText(mediaItem.get().getTitle());
    image.setImageResource(R.drawable.library_type_show);
  }
}