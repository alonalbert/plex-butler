package com.alonalbert.plexbutler.ui.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.alonalbert.plexbutler.GlideApp;
import com.alonalbert.plexbutler.GlideRequest;
import com.alonalbert.plexbutler.R;
import com.alonalbert.plexbutler.plex.PlexClientImpl;
import com.alonalbert.plexbutler.plex.model.Media;
import com.alonalbert.plexbutler.plex.model.Server;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;
import org.androidannotations.annotations.res.DimensionRes;

import static com.alonalbert.plexbutler.plex.model.Media.Type.EPISODE;
import static com.alonalbert.plexbutler.plex.model.Media.Type.MOVIE;
import static com.alonalbert.plexbutler.plex.model.Media.Type.SHOW;

/**
 * A MainItemView for a Shows
 */
@EViewGroup(R.layout.media_item)
public class MediaItemView extends MainItemView {

  @Bean
  protected PlexClientImpl plexClient;

  @ViewById(R.id.image)
  protected ImageView image;

  @ViewById(R.id.text1)
  protected TextView text1;

  @ViewById(R.id.text2)
  protected TextView text2;

  @ViewById(R.id.text3)
  protected TextView text3;

  @ViewById(R.id.toggle_watched)
  protected ImageView toggleWatched;

  @ViewById(R.id.unwatched_count)
  protected TextView unwatchedCount;

  @ColorRes(R.color.unwatched)
  protected int unwatchedColor;

  @ColorRes(R.color.watched)
  protected int watchedColor;

  @DimensionRes(R.dimen.list_item_image_width)
  protected float imageWidth;

  @DimensionRes(R.dimen.list_item_image_height)
  protected float imageHeight;

  private Media media;

  public MediaItemView(Context context) {
    super(context);
  }

  @Override
  public void bind(MainItem item, Server server) {
    this.media = ((MediaItem) item).get();

    final int placeholder = media.getType() == MOVIE ? R.drawable.library_type_movie : R.drawable.library_type_show;

    if (media.getThumb() != null) {
      final String photoUrl = plexClient.getPhotoUrl(server, media.getThumb(), (int) imageWidth, (int) imageHeight);
      final GlideRequest<Drawable> request = GlideApp
          .with(getContext())
          .load(photoUrl)
          .placeholder(placeholder);
      if (media.getType() == EPISODE) {
        request.centerCrop();
      } else {
        request.fitCenter();
      }
      request.into(image);
    } else {
      image.setImageResource(placeholder);
    }

    final Resources res = getResources();
    if (media.getType() == EPISODE) {
      text1.setText(res.getString(R.string.episode_title, media.getParentIndex(), media.getIndex()));
      text2.setText(media.getTitle());
      text3.setText(res.getString(R.string.aired_on, media.getOriginallyAvailableAt()));
    } else {
      text1.setText(media.getTitle());
      if (media.getRating() != null) {
        text2.setText(res.getString(R.string.year_and_rating, media.getYear(), media.getRating()));
      } else {
        text2.setText(String.valueOf(media.getYear()));
      }
      text3.setText(TextUtils.join(", ", media.getGenres()));
    }
    updatedWatchedToggle();
  }

  @SuppressLint("DefaultLocale")
  protected void updatedWatchedToggle() {
    if (media.getType() == SHOW) {
      final int leafCount = media.getLeafCount();
      final int numUnwatched = leafCount - media.getViewedLeafCount();
      final int color = numUnwatched > 0 ? unwatchedColor : watchedColor;
      toggleWatched.setColorFilter(color);

      if (numUnwatched != 0) {
        final String s = String.format("<font color=#%1$06x>%2$d</font><font color=#%3$06x>/%4$d</font>",
            unwatchedColor & 0xFFFFFF, numUnwatched,
            watchedColor & 0xFFFFFF, leafCount);
        unwatchedCount.setText(Html.fromHtml(s));
      } else {
        unwatchedCount.setText(String.valueOf(leafCount));
      }
      unwatchedCount.setVisibility(VISIBLE);
    } else {
      unwatchedCount.setVisibility(GONE);
      toggleWatched.setColorFilter(media.getViewCount() > 0 ? unwatchedColor : watchedColor);
    }
  }
  @Click(R.id.layout)
  protected void onItemClick() {
    if (media.getType() == SHOW) {
      ((MainActivity) getContext()).loadPlexObject(media);
    }
  }

  @Click(R.id.toggle_watched)
  protected void onImageClick() {
    if (media.getType() == SHOW) {
      final int leafCount = media.getLeafCount();
      final int numUnwatched = leafCount - media.getViewedLeafCount();
      if (numUnwatched == 0) {
        media.setViewedLeafCount(0);
      } else {
        media.setViewedLeafCount(leafCount);
      }
    } else {
      if (media.getViewCount() == 0) {
        media.setViewCount(1);
      } else {
        media.setViewCount(0);
      }
    }
    updatedWatchedToggle();
  }
}
