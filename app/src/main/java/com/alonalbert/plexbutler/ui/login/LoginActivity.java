package com.alonalbert.plexbutler.ui.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alonalbert.plexbutler.R;
import com.alonalbert.plexbutler.plex.PlexClient;
import com.alonalbert.plexbutler.plex.model.LoginResponse;
import com.alonalbert.plexbutler.settings.PlexButlerPreferences_;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.androidannotations.rest.spring.annotations.RestService;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;

/**
 * A login screen that offers login via email/password.
 */
@SuppressLint("Registered")
@EActivity(R.layout.activity_login)
public class LoginActivity extends AppCompatActivity {

  @Pref
  PlexButlerPreferences_ prefs;

  @RestService
  protected PlexClient plexClient;

  @ViewById(R.id.email_edit)
  protected EditText emailEdit;
  @ViewById(R.id.password_edit)
  protected EditText passwordEdit;
  @ViewById(R.id.login_progress)
  protected View loginProgress;
  @ViewById(R.id.login_form)
  protected View loginForm;
  @ViewById(R.id.sign_in_button)
  protected Button signInButton;

  @AfterViews
  protected void initialize() {
    passwordEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
        if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
          attemptLogin();
          return true;
        }
        return false;
      }
    });
  }

  @Click({R.id.sign_in_button})
  protected void signInButtonClicked() {
    attemptLogin();
  }

  /**
   * Attempts to sign in or register the account specified by the login form.
   * If there are form errors (invalid email, missing fields, etc.), the
   * errors are presented and no actual login attempt is made.
   */
  public void attemptLogin() {
    // Reset errors.
    emailEdit.setError(null);
    passwordEdit.setError(null);

    // Store values at the time of the login attempt.
    final String email = emailEdit.getText().toString();
    final String password = passwordEdit.getText().toString();

    boolean cancel = false;
    View focusView = null;

    // Check for a valid email address.
    if (TextUtils.isEmpty(email)) {
      emailEdit.setError(getString(R.string.error_field_required));
      focusView = emailEdit;
      cancel = true;
    } else if (!email.contains("@")) {
      emailEdit.setError(getString(R.string.error_invalid_email));
      focusView = emailEdit;
      cancel = true;
    }

    if (cancel) {
      // There was an error; don't attempt login and focus the first
      // form field with an error.
      focusView.requestFocus();
    } else {
      // Show a progress spinner, and kick off a background task to
      // perform the user login attempt.
      showProgress(true);
      doLogin(email, password);
    }
  }

  @Background
  protected void doLogin(String email, String password) {
    // TODO: attempt authentication against a network service.

    final LinkedMultiValueMap<String, String> data = new LinkedMultiValueMap<>();
    data.set("user[login]", email);
    data.set("user[password]", password);
    try {
      final ResponseEntity<LoginResponse> response = plexClient.login(data);
      prefs.edit()
        .plexAuthToken().put(response.getBody().getUser().getAuthToken())
        .apply();
      loginFinished(true, null);
    } catch (HttpClientErrorException e) {
      Log.e("PlexButler", "Error", e);
      final JsonObject json = new JsonParser().parse(e.getResponseBodyAsString()).getAsJsonObject();
      loginFinished(false, json.get("error").getAsString());
    }
  }

  @UiThread
  protected void loginFinished(boolean success, String message) {
    showProgress(false);

    if (success) {
      finish();
    } else {
      passwordEdit.setError(message);
      passwordEdit.requestFocus();
    }
  }
  /**
   * Shows the progress UI and hides the login form.
   */
  @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
  private void showProgress(final boolean show) {

    final int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

    loginForm.setVisibility(show ? View.GONE : View.VISIBLE);
    loginForm.animate().setDuration(shortAnimTime).alpha(
        show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        loginForm.setVisibility(show ? View.GONE : View.VISIBLE);
      }
    });

    loginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
    loginProgress.animate().setDuration(shortAnimTime).alpha(
        show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        loginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
      }
    });
  }
}

