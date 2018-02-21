package com.alonalbert.plexbutler.plex;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.alonalbert.plexbutler.R;
import com.alonalbert.plexbutler.settings.PlexButlerPreferences_;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.res.StringRes;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * Plex Request Interceptor
 */
@EBean(scope = EBean.Scope.Singleton)
public class PlexRequestInterceptor implements ClientHttpRequestInterceptor {

  @Pref
  PlexButlerPreferences_ prefs;

  @RootContext
  protected Context context;

  @StringRes(R.string.app_name)
  String appName;


  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    final HttpHeaders headers = request.getHeaders();
    final String path = request.getURI().getPath();
    if (path.equals("/users/sign_in.json")) {
      headers.add("X-Plex-Product", appName);
      headers.add("X-Plex-Version", getVersion());
      // TODO: 2/10/18 Device id
      headers.add("X-Plex-Client-Identifier", "111");
    } else {
      headers.add("X-Plex-Token", prefs.plexAuthToken().get());
      if (!path.equals("/pms/servers.xml")) {
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
      }
    }
    return execution.execute(request, body);
  }

  private String getVersion() {
    try {
      final PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
      return info.versionName;
    } catch (PackageManager.NameNotFoundException e) {
      return "unknown";
    }
  }
}
