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

import static com.alonalbert.plexbutler.plex.model.Media.Type.SHOW;

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

  @ViewById(R.id.toggle_watched)
  protected ImageView toggleWatched;

  @ViewById(R.id.unwatched_count)
  protected TextView unwatchedCount;

  private Media media;
  private int numUnwatched;

  public ShowItemView(Context context) {
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

    if (media.getType() == SHOW) {
      numUnwatched = media.getLeafCount() - media.getViewedLeafCount();
    } else {
      numUnwatched = 1 - media.getViewCount();
    }
    updatedWatchedToggle();
  }

  private void updatedWatchedToggle() {
    toggleWatched.setColorFilter(numUnwatched > 0 ? getResources().getColor(R.color.unwatched) : 0xffffffff);
    unwatchedCount.setText(getResources().getString(R.string.unwatched_count, numUnwatched));
    unwatchedCount.setVisibility(numUnwatched > 0 && media.getType() == SHOW ? VISIBLE : INVISIBLE);
  }
  @Click(R.id.layout)
  protected void onItemClick() {
    if (media.getType() == SHOW) {
      ((MainActivity) getContext()).loadShow(media);
    }
  }

  @Click(R.id.toggle_watched)
  protected void onImageClick() {
    if (numUnwatched == 0) {
      if (media.getType() == SHOW) {
        numUnwatched = media.getLeafCount();
      } else {
        numUnwatched = 1;
      }
    } else {
      numUnwatched = 0;
    }
    updatedWatchedToggle();
  }
}
