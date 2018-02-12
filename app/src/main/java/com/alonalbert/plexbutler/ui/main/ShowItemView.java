package com.alonalbert.plexbutler.ui.main;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.alonalbert.plexbutler.R;
import com.alonalbert.plexbutler.plex.model.Media;

import org.androidannotations.annotations.Click;
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

  private Media media;

  public ShowItemView(Context context) {
    super(context);
  }

  @Override
  public void bind(MainItem item) {
    final MediaItem mediaItem = (MediaItem) item;
    final Media media = mediaItem.get();
    title.setText(media.getTitle());
    image.setImageResource(R.drawable.library_type_show);
    this.media = media;
  }

  @Click(R.id.title)
  protected void onItemClick() {
    if (media.getType() == Media.Type.SHOW) {
      ((MainActivity) getContext()).loadShow(media);
    }
  }
}
