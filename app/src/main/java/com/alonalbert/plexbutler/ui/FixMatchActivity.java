package com.alonalbert.plexbutler.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alonalbert.plexbutler.GlideApp;
import com.alonalbert.plexbutler.R;
import com.alonalbert.plexbutler.plex.PlexClientImpl;
import com.alonalbert.plexbutler.plex.model.Match;
import com.alonalbert.plexbutler.plex.model.Server;
import com.alonalbert.plexbutler.ui.FixMatchActivity_.ItemView_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.EditorAction;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.List;

@SuppressLint("Registered")
@EActivity(R.layout.fix_match_activity)
public class FixMatchActivity extends AppCompatActivity {

  public static final String EXTRA_SERVER = "SERVER";
  public static final String EXTRA_KEY = "KEY";
  public static final String EXTRA_AGENT = "AGENT";
  public static final String EXTRA_TITLE = "TITLE";
  public static final String EXTRA_YEAR = "YEAR";
  public static final String EXTRA_PLACEHOLDER = "PLACEHOLDER";
  @Bean
  protected Adapter adapter;

  @Bean
  protected PlexClientImpl plexClient;

  @SystemService
  protected InputMethodManager imm;

  @Extra(EXTRA_SERVER)
  protected Server server;

  @Extra(EXTRA_KEY)
  protected String key;

  @Extra(EXTRA_AGENT)
  protected String agent;

  @Extra(EXTRA_TITLE)
  protected String title;

  @Extra(EXTRA_YEAR)
  protected int year;

  @Extra(EXTRA_PLACEHOLDER)
  protected int placeholder;

  @ViewById(R.id.title)
  protected EditText titleEdit;

  @ViewById(R.id.year)
  protected EditText yearEdit;

  @ViewById(R.id.matches)
  protected ListView matches;

  @ViewById(R.id.progress)
  protected ProgressBar progress;

  @AfterViews
  void afterViews() {
    titleEdit.setText(title);
    yearEdit.setText(year > 0 ? String.valueOf(year) : "");
    matches.setAdapter(adapter);

    titleEdit.setSelection(titleEdit.getText().length());
    yearEdit.setSelection(yearEdit.getText().length());
  }

  @Click(R.id.search)
  void onSearch() {
    //noinspection ConstantConditions
    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    matches.setVisibility(View.GONE);
    progress.setVisibility(View.VISIBLE);
    doSearch();
  }

  @Background
  protected void doSearch() {
    handleSearchResults(plexClient.getMatches(server, key, agent, titleEdit.getText().toString(), yearEdit.getText().toString()));
  }

  @UiThread
  protected void handleSearchResults(List<Match> items) {
    matches.setVisibility(View.VISIBLE);
    progress.setVisibility(View.GONE);
    adapter.setItems(items);
    adapter.notifyDataSetChanged();
  }

  @Click(R.id.cancel)
  @Background
  void onCancel() {
    finish();
  }

  @EditorAction({R.id.title, R.id.year})
  void onEditorActions(int actionId) {
    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
      onSearch();
    }
  }

  @UiThread
  protected void beforeSearch() {
  }

  @ItemClick(R.id.matches)
  @Background
  protected void matchSelected(Match match) {
      plexClient.setMatch(server, key, match);
      finish();
  }

  @EBean
  public static class Adapter extends BaseAdapter {

    private List<Match> items;

    @RootContext
    Context context;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

      final ItemView itemView;
      if (convertView == null) {
        itemView = ItemView_.build(context);
      } else {
        itemView = (ItemView) convertView;
      }

      itemView.bind(getItem(position));

      return itemView;
    }

    @Override
    public int getCount() {
      return items != null ? items.size() : 0;
    }

    @Override
    public Match getItem(int position) {
      return items.get(position);
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @UiThread
    void setItems(List<Match> items) {
      this.items = items;
      notifyDataSetChanged();
    }
  }

  @EViewGroup(R.layout.match_item)
  public static class ItemView extends RelativeLayout {

    @ViewById(R.id.thumb)
    protected ImageView thumb;
    @ViewById(R.id.name)
    protected TextView name;

    @ViewById(R.id.year)
    protected TextView year;

    @ViewById(R.id.score)
    protected TextView score;

    private final FixMatchActivity activity;

    public ItemView(Context context) {
      super(context);
      activity = (FixMatchActivity) context;
    }

    public void bind(Match match) {
      GlideApp
          .with(getContext())
          .load(match.getThumb())
          .placeholder(activity.placeholder)
          .fitCenter()
          .into(thumb);
      name.setText(match.getName());
      year.setText(String.valueOf(match.getYear()));
      score.setText(getResources().getString(R.string.score, match.getScore()));
    }
  }
}
