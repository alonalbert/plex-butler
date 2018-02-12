package com.alonalbert.plexbutler.ui.main;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
  @ViewById(R.id.image)
  protected ImageView image;

  @ViewById(R.id.title)
  protected TextView title;

  @ViewById(R.id.year)
  protected TextView year;

  @ViewById(R.id.genres)
  protected TextView genres;

  private Media media;

  public ShowItemView(Context context) {
    super(context);
  }

  @Override
  public void bind(MainItem item) {
    final MediaItem mediaItem = (MediaItem) item;
    final Media media = mediaItem.get();
    image.setImageResource(R.drawable.library_type_show);
    title.setText(media.getTitle());
    if (media.getYear() > 0) {
      year.setText(String.valueOf(media.getYear()));
    }
    genres.setText(TextUtils.join(", ", media.getGenres()));
    this.media = media;
  }

  @Click(R.id.layout)
  protected void onItemClick() {
    if (media.getType() == Media.Type.SHOW) {
      ((MainActivity) getContext()).loadShow(media);
    }
  }

  @Click(R.id.image)
  protected void onImageClick() {
    Toast.makeText(getContext(), "Image", Toast.LENGTH_SHORT).show();
  }

}
