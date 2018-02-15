package com.alonalbert.plexbutler.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alonalbert.plexbutler.GlideApp;
import com.alonalbert.plexbutler.GlideRequest;
import com.alonalbert.plexbutler.R;
import com.alonalbert.plexbutler.plex.PlexClientImpl;
import com.alonalbert.plexbutler.plex.model.Media;
import com.alonalbert.plexbutler.plex.model.PlexObject;
import com.alonalbert.plexbutler.settings.PlexButlerPreferences_;
import com.alonalbert.plexbutler.ui.MainFragment_.HeaderItemView_;
import com.alonalbert.plexbutler.ui.MainFragment_.RowItemView_;
import com.alonalbert.plexbutler.ui.recyclerview.AbstractRecyclerViewAdapter;
import com.alonalbert.plexbutler.ui.recyclerview.ViewWrapper;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.List;

import static com.alonalbert.plexbutler.plex.model.Media.Type.EPISODE;
import static com.alonalbert.plexbutler.plex.model.Media.Type.MOVIE;
import static com.alonalbert.plexbutler.plex.model.Media.Type.SHOW;
import static com.alonalbert.plexbutler.ui.FixMatchActivity.EXTRA_AGENT;
import static com.alonalbert.plexbutler.ui.FixMatchActivity.EXTRA_KEY;
import static com.alonalbert.plexbutler.ui.FixMatchActivity.EXTRA_PLACEHOLDER;
import static com.alonalbert.plexbutler.ui.FixMatchActivity.EXTRA_SERVER;
import static com.alonalbert.plexbutler.ui.FixMatchActivity.EXTRA_TITLE;
import static com.alonalbert.plexbutler.ui.FixMatchActivity.EXTRA_YEAR;

/**
 * Main Fragment
 */
@EFragment(R.layout.main_fragment)
public class MainFragment extends Fragment {
  @Bean
  protected Adapter adapter;

  @Bean
  protected PlexClientImpl plexClient;

  @ViewById(R.id.swipe_refresh)
  protected SwipeRefreshLayout swipeRefresh;

  @ViewById(R.id.recycler_view)
  protected RecyclerView recyclerView;

  private MainActivity mainActivity;
  private LinearLayoutManager layoutManager;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    mainActivity = ((MainActivity) context);
  }

  @AfterInject
  protected void afterInject() {
    if (mainActivity.items != null) {
      adapter.setItems(mainActivity.currentPlexObject, mainActivity.items);
    }
  }

  @AfterViews
  void afterViews() {
    swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        mainActivity.refreshItems();
      }
    });

    recyclerView.setAdapter(adapter);
    layoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
  }

  @UiThread
  void handleLoadItemsResults(PlexObject parent, List<Media> items, int scrollTo) {
    adapter.setItems(parent, items);
    swipeRefresh.setRefreshing(false);
    if (scrollTo > 0) {
      layoutManager.scrollToPositionWithOffset(scrollTo, 0);
    } else {
      recyclerView.scrollToPosition(0);
    }
  }

  public void setItems(Media parent, List<Media> items) {
    adapter.setItems(parent, items);
  }

  int getCurrentPosition() {
    return layoutManager.findFirstCompletelyVisibleItemPosition();
  }

  @EBean
  public static class Adapter extends AbstractRecyclerViewAdapter<Media, ItemView> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ROW = 1;

    @Pref
    protected PlexButlerPreferences_ prefs;

    private final List<Media> unwatchedItems = new ArrayList<>();
    private boolean unwatchedOnly;
    private boolean hasHeader;

    @AfterInject
    void afterInject() {
      unwatchedOnly = prefs.filterUnwatched().get();
    }

    @Override
    protected ItemView onCreateItemView(ViewGroup parent, int viewType) {
      if (viewType == TYPE_HEADER) {
        return HeaderItemView_.build(parent.getContext());
      } else {
        return RowItemView_.build(parent.getContext());
      }
    }

    @Override
    public void onBindViewHolder(ViewWrapper<ItemView> holder, int position) {
      ItemView view = holder.getView();
      final Media item = getItem(position);

      view.bind(item);
    }

    void notifyDataSetChanged(boolean unwatchedOnly) {
      this.unwatchedOnly = unwatchedOnly;
      notifyDataSetChanged();
    }

    private void setItems(PlexObject parent, List<Media> items) {
      if (parent instanceof Media) {
        items.add(0, (Media) parent);
        hasHeader = true;
      } else {
        hasHeader = false;
      }
      this.items = items;
      unwatchedItems.clear();
      for (Media item : items) {
        if (!item.isWatched() || item == parent) {
          unwatchedItems.add(item);
        }
      }
      notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
      if (unwatchedOnly) {
        return unwatchedItems.size();
      } else {
        return items.size();
      }
    }

    private Media getItem(int position) {
      if (unwatchedOnly) {
        return unwatchedItems.get(position);
      } else {
        return items.get(position);
      }
    }

    @Override
    public int getItemViewType(int position) {
      if (hasHeader) {
        return position == 0 ? TYPE_HEADER : TYPE_ROW;
      } else {
        return TYPE_ROW;
      }
    }
  }

  static abstract class ItemView extends RelativeLayout {
    public ItemView(Context context) {
      super(context);
    }

    public abstract void bind(Media media);
  }

  /**
   * A MainItemView for a Shows
   */
  @EViewGroup(R.layout.header_item)
  public static class HeaderItemView extends ItemView {

    @ViewById(R.id.image)
    protected ImageView image;


    private Media media;
    private final MainActivity mainActivity;

    public HeaderItemView(Context context) {
      super(context);
      mainActivity = ((MainActivity) getContext());
    }

    @Override
    public void bind(Media media) {
      this.media = media;

      final int placeholder = media.getType() == MOVIE ? R.drawable.library_type_movie : R.drawable.library_type_show;

      if (media.getArt() != null) {
        final String photoUrl = mainActivity.getPhotoUrl(media.getArt());
        GlideApp
            .with(getContext())
            .load(photoUrl)
            .placeholder(placeholder)
            .fitCenter()
            .into(image);
      } else {
        image.setImageResource(placeholder);
      }
    }

    @Click(R.id.unmatch)
    protected void unmatch() {
      mainActivity.plexClient.unmatch(mainActivity.server, media.getRatingKey());
      Snackbar.make(this, getResources().getString(R.string.unmatched_snack, media.getTitle()), Snackbar.LENGTH_SHORT).show();
    }

    @Click(R.id.fix_match)
    protected void fixMatch() {
      mainActivity.startActivity(new Intent(mainActivity, FixMatchActivity_.class)
          .putExtra(EXTRA_SERVER, mainActivity.server)
          .putExtra(EXTRA_KEY, media.getRatingKey())
          .putExtra(EXTRA_AGENT, mainActivity.currentSection.getAgent())
          .putExtra(EXTRA_TITLE, media.getTitle())
          .putExtra(EXTRA_YEAR, media.getYear())
          .putExtra(EXTRA_PLACEHOLDER, media.getType() == SHOW ? R.drawable.library_type_video : R.drawable.library_type_movie)
      );
    }
  }

  /**
   * A MainItemView for a Shows
   */
  @EViewGroup(R.layout.media_item)
  public static class RowItemView extends ItemView {

    @ViewById(R.id.image)
    protected ImageView image;

    @ViewById(R.id.text1)
    protected TextView text1;

    @ViewById(R.id.text2)
    protected TextView text2;

    @ViewById(R.id.text3)
    protected TextView text3;

    @ViewById(R.id.toggle_watched)
    protected TextView toggleWatched;

    private Media media;
    private final MainActivity mainActivity;

    public RowItemView(Context context) {
      super(context);
      mainActivity = ((MainActivity) getContext());
    }

    @Override
    public void bind(Media media) {
      this.media = media;

      final int placeholder = media.getType() == MOVIE ? R.drawable.library_type_movie : R.drawable.library_type_show;

      if (media.getThumb() != null) {
        final String photoUrl = mainActivity.getPhotoUrl(media.getThumb());
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
        if (media.getOriginallyAvailableAt() != null) {
          text3.setText(res.getString(R.string.aired_on, media.getOriginallyAvailableAt()));
        }
      } else {
        text1.setText(media.getTitle());
        if (media.getRating() != null) {
          if (media.getYear() > 0) {
            text2.setText(res.getString(R.string.year_and_rating, media.getYear(), media.getRating()));
          } else {
            text2.setText(media.getRating());
          }
        } else {
          text2.setText(media.getYear() > 0 ? String.valueOf(media.getYear()) : "");
        }
        text3.setText(TextUtils.join(", ", media.getGenres()));
      }
      updatedWatchedToggle();
    }

    @SuppressLint("DefaultLocale")
    void updatedWatchedToggle() {
      if (media.getType() == SHOW) {
        final int leafCount = media.getLeafCount();
        final int numUnwatched = leafCount - media.getViewedLeafCount();
        setToggleUnwatchedColor(numUnwatched > 0);

        if (numUnwatched == 0) {
          toggleWatched.setText(String.valueOf(leafCount));
          toggleWatched.setTextColor(mainActivity.watchedColor);
        } else {
          if (numUnwatched == leafCount) {
            toggleWatched.setText(String.valueOf(leafCount));
            toggleWatched.setTextColor(mainActivity.unwatchedColor);
          } else {
            final String source = numUnwatched + "/" + leafCount;
            Spannable spannable = new SpannableString(source);
            final int p = source.indexOf('/');
            spannable.setSpan(new ForegroundColorSpan(mainActivity.unwatchedColor), 0, p, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new ForegroundColorSpan(mainActivity.watchedColor), p + 1, source.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            toggleWatched.setText(spannable);
          }
        }
      } else {
        toggleWatched.setText("");
        setToggleUnwatchedColor(media.getViewCount() == 0);
      }
    }

    private void setToggleUnwatchedColor(boolean isUnwatched) {
      final int color = isUnwatched ? mainActivity.unwatchedColor : mainActivity.watchedColor;
      toggleWatched.getCompoundDrawablesRelative()[2].setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    @Click(R.id.layout)
    protected void onItemClick() {
      switch (media.getType()) {
        case SHOW:
          mainActivity.loadShow(media);
          break;
        case MOVIE:
          mainActivity.loadMovie(media);
          break;
        case SEASON:
        case EPISODE:
          break;
      }
    }

    @Click(R.id.image)
    protected void onImageClick() {
      if (media.getType() != EPISODE) {
        mainActivity.startActivity(new Intent(mainActivity, FixMatchActivity_.class)
            .putExtra(EXTRA_SERVER, mainActivity.server)
            .putExtra(EXTRA_KEY, media.getRatingKey())
            .putExtra(EXTRA_AGENT, mainActivity.currentSection.getAgent())
            .putExtra(EXTRA_TITLE, media.getTitle())
            .putExtra(EXTRA_YEAR, media.getYear())
            .putExtra(EXTRA_PLACEHOLDER, media.getType() == SHOW ? R.drawable.library_type_video : R.drawable.library_type_movie)
        );
      }
    }

    @Click(R.id.toggle_watched)
    protected void onToggleWatchedClicked() {
      final boolean watched;
      if (media.getType() == SHOW) {
        final int leafCount = media.getLeafCount();
        final int numUnwatched = leafCount - media.getViewedLeafCount();
        if (numUnwatched == 0) {
          media.setViewedLeafCount(0);
          watched = false;
        } else {
          media.setViewedLeafCount(leafCount);
          watched = true;
        }

      } else {
        if (media.getViewCount() == 0) {
          media.setViewCount(1);
          watched = true;
        } else {
          media.setViewCount(0);
          watched = false;
        }
      }
      mainActivity.setMediaWatched(media, watched);
      updatedWatchedToggle();
    }
  }

}
