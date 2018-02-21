package com.alonalbert.plexbutler.thetvdb;

import org.androidannotations.annotations.EBean;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.net.URI;

/**
 * The TV DB Request Interceptor
 */
@EBean(scope = EBean.Scope.Singleton)
class TheTvDbInterceptor implements ClientHttpRequestInterceptor {
  private static final String APIKEY = "E887D34BD4A85535";
  private String token;

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    final URI uri = request.getURI();
    if (uri.getPath().equals("/login")) {
      body = ("{\"apikey\": \"" + APIKEY + "\"}").getBytes();
    } else {
      request.getHeaders().add("Authorization", "Bearer " + token);
    }
    return execution.execute(request, body);
  }

  public void setToken(String token) {
    this.token = token;
  }

  String getToken() {
    return token;
  }
}
