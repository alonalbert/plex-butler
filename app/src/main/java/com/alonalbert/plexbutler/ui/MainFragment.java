package com.alonalbert.plexbutler.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.alonalbert.plexbutler.ui.MainFragment_.ItemView_;
import com.alonalbert.plexbutler.ui.recyclerview.AbstractRecyclerViewAdapter;
import com.alonalbert.plexbutler.ui.recyclerview.ViewWrapper;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
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
    if (mainActivity.items != null) {
      adapter.setItems(mainActivity.items);
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

  @Background
  protected void loadItems(PlexObject plexObject, int scrollTo) {
    preLoadItems();
    final List<Media> items = plexObject.load(plexClient, mainActivity.server, false);
    postLoadItems(items, scrollTo);
  }

  int getCurrentPosition() {
    return layoutManager.findFirstCompletelyVisibleItemPosition();
  }

  @UiThread
  void preLoadItems() {
    mainActivity.setTitle(mainActivity.currentPlexObject.getTitle());
    swipeRefresh.setRefreshing(true);
  }

  @UiThread
  void postLoadItems(List<Media> items, int scrollTo) {
    adapter.setItems(items);
    swipeRefresh.setRefreshing(false);
    if (scrollTo > 0) {
      layoutManager.scrollToPositionWithOffset(mainActivity.currentPosition, 0);
    } else {
      recyclerView.scrollToPosition(0);
    }
  }

  @EBean
  public static class Adapter extends AbstractRecyclerViewAdapter<Media, ItemView> {
    @Pref
    protected PlexButlerPreferences_ prefs;

    private final List<Media> unwatchedItems = new ArrayList<>();
    private boolean unwatchedOnly;

    @AfterInject
    void afterInject() {
      unwatchedOnly = prefs.filterUnwatched().get();
    }

    @Override
    protected ItemView onCreateItemView(ViewGroup parent, int viewType) {
      return ItemView_.build(parent.getContext());
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

    private void setItems(List<Media> items) {
      this.items = items;
      unwatchedItems.clear();
      for (Media item : items) {
        if (!item.isWatched()) {
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
  }

  /**
   * A MainItemView for a Shows
   */
  @EViewGroup(R.layout.media_item)
  public static class ItemView extends RelativeLayout {

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

    public ItemView(Context context) {
      super(context);
      mainActivity = ((MainActivity) getContext());
    }

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
          text2.setText(String.valueOf(media.getYear()));
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
      if (media.getType() == SHOW) {
        mainActivity.loadShow(media);
      }
    }

    @Click(R.id.toggle_watched)
    protected void onImageClick() {
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
