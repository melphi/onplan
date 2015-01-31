package com.onplan.util.http;

import com.google.common.base.Charsets;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

public class HttpClientRequest {
  private final String url;
  private final String body;
  private final Map<String, String> headers;
  private final HttpMethod requestMethod;
  private final CloseableHttpClient httpClient = HttpClients.createDefault();
  private final HttpRequestBase httpRequest;

  public static Builder newBuilder() {
    return new Builder();
  }

  public HttpClientResponse executeRequest() throws IOException {
    HttpResponse httpResponse = httpClient.execute(httpRequest);
    return createHttpClientResponse(httpResponse);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("url", url)
        .add("body", body)
        .add("headers", headers)
        .add("requestMethod", requestMethod)
        .add("body", body)
        .toString();
  }

  private HttpClientResponse createHttpClientResponse(HttpResponse httpResponse)
      throws IOException {
    String body = getResponseBody(httpResponse);
    Map<String, String> headers = Maps.newHashMap();
    for(Header header: httpResponse.getAllHeaders()) {
      headers.put(header.getName(), header.getValue());
    }
    int statusCode = httpResponse.getStatusLine().getStatusCode();
    return new HttpClientResponse(body, headers, statusCode);
  }

  protected String getResponseBody(HttpResponse httpResponse) throws IOException {
    HttpEntity entity = httpResponse.getEntity();
    return entity == null ? null : EntityUtils.toString(entity);
  }

  private HttpRequestBase createHttpRequest() {
    switch (requestMethod) {
      case GET:
        return createHttpGetRequest();
      case POST:
        return createHttpPostRequest();
      default:
        throw new IllegalArgumentException(
            String.format("Unsupported request type [%s].", requestMethod));
    }
  }

  private void setHttpRequestHeaders(HttpRequestBase httpRequestBase) {
    if (!headers.isEmpty()) {
      for (Map.Entry<String, String> header: headers.entrySet()) {
        httpRequestBase.setHeader(header.getKey(), header.getValue());
      }
    }
  }

  private HttpGet createHttpGetRequest() {
    HttpGet httpGet = new HttpGet(url);
    setHttpRequestHeaders(httpGet);
    return httpGet;
  }

  private HttpPost createHttpPostRequest() {
    HttpPost httpPost = new HttpPost(url);
    httpPost.setEntity(new StringEntity(body, Charsets.UTF_8.name()));
    setHttpRequestHeaders(httpPost);
    return httpPost;
  }

  private HttpClientRequest(
      String url, String body, Map<String, String> headers, HttpMethod requestMethod) {
    this.requestMethod = checkNotNull(requestMethod);
    this.url = checkNotNullOrEmpty(url);
    this.headers = checkNotNull(headers);
    if (HttpMethod.POST.equals(requestMethod)) {
      this.body = checkNotNull(body, "Body must be set for POST request method.");
    } else {
      this.body = body;
    }
    this.httpRequest = createHttpRequest();
  }

  public static class Builder {
    private String url;
    private String body;
    private Map<String, String> headers = Maps.newHashMap();
    private HttpMethod httpRequestMethod = HttpMethod.GET;

    public Builder setUrl(String url) {
      this.url = checkNotNullOrEmpty(url);
      return this;
    }

    public Builder setBody(String body) {
      this.body = checkNotNull(body);
      return this;
    }

    public Builder setHeaders(Map<String, String> headers) {
      this.headers = ImmutableMap.copyOf(checkNotNull(headers));
      return this;
    }

    public Builder setHttpRequestMethod(HttpMethod httpRequestMethod) {
      this.httpRequestMethod = checkNotNull(httpRequestMethod);
      return this;
    }

    public HttpClientRequest build() {
      return new HttpClientRequest(url, body, headers, httpRequestMethod);
    }
  }
}
