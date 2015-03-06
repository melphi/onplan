package com.onplan.util.http;

import com.google.common.base.MoreObjects;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public final class HttpClientResponse {
  private final String body;
  private final Map<String, String> headers;
  private final int statusCode;

  public String getBody() {
    return body;
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public HttpClientResponse(String body, Map<String, String> header, int statusCode) {
    this.body = checkNotNull(body);
    this.headers = checkNotNull(header);
    this.statusCode = statusCode;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("body", body)
        .add("headers", headers)
        .add("statusCode", statusCode)
        .toString();
  }
}
