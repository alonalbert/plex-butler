package com.alonalbert.plexbutler.ui.main;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.alonalbert.plexbutler.R;
import com.alonalbert.plexbutler.plex.model.Media;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;

import static com.alonalbert.plexbutler.plex.model.Media.Type.SHOW;

/**
 * A MainItemView for a Movies
 */
@EViewGroup(R.layout.movie_item)
public class MovieItemView extends MainItemView {
  @ViewById(R.id.image)
  protected ImageView image;

  @ViewById(R.id.title)
  protected TextView title;

  @ViewById(R.id.year)
  protected TextView year;

  @ViewById(R.id.genres)
  protected TextView genres;

  @ViewById(R.id.toggle_watched)
  protected ImageView toggleWatched;

  @ColorRes(R.color.unwatched)
  protected int unwatchedColor;

  @ColorRes(R.color.watched)
  protected int watchedColor;

  private Media media;

  public MovieItemView(Context context) {
    super(context);
  }

  @Override
  public void bind(MainItem item) {
    final MediaItem mediaItem = (MediaItem) item;
    this.media = mediaItem.get();
    image.setImageResource(R.drawable.library_type_show);

    title.setText(mediaItem.get().getTitle());
    if (mediaItem.get().getYear() > 0) {
      year.setText(String.valueOf(mediaItem.get().getYear()));
    }
    genres.setText(TextUtils.join(", ", mediaItem.get().getGenres()));

    updatedWatchedToggle();
  }

  private void updatedWatchedToggle() {
    toggleWatched.setColorFilter(media.getViewCount() > 0 ? unwatchedColor : watchedColor);
  }
  @Click(R.id.layout)
  protected void onItemClick() {
    if (media.getType() == SHOW) {
      ((MainActivity) getContext()).loadShow(media);
    }
  }

  @Click(R.id.toggle_watched)
  protected void onImageClick() {
    if (media.getViewCount() == 0) {
      media.setViewCount(1);
    } else {
      media.setViewCount(0);
    }
    updatedWatchedToggle();
  }
}
