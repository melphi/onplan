package com.onplan.connector.igindex.client;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.onplan.util.http.HttpClientRequest;
import com.onplan.util.http.HttpClientResponse;
import com.onplan.util.http.HttpMethod;

import java.io.IOException;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;
import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

public final class IgIndexClientRequest {
  private final HttpClientRequest httpClientRequest;
  private final Map<String, String> httpRequestHeader;
  private final Map<String, String> customHeader;
  private final IgIndexConnectionCredentials connectionCredentials;
  private final HttpMethod httpMethod;
  private final String url;
  private final Map<String, String> urlParameters;
  private final String apiVersion;
  private final String body;

  public static Builder newBuilder() {
    return new Builder();
  }

  public HttpClientResponse executeRequest() throws IOException {
    return httpClientRequest.executeRequest();
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("url", url)
        .add("urlParameters", urlParameters)
        .add("customHeader", customHeader)
        .add("httpMethod", httpMethod)
        .add("apiVersion", apiVersion)
        .add("body", body)
        .toString();
  }

  private IgIndexClientRequest(IgIndexConnectionCredentials connectionCredentials,
      HttpMethod httpMethod, String url, Map<String, String> urlParameters, String apiVersion,
      Map<String, String> customHeader, String body) {
    this.httpMethod = checkNotNull(httpMethod);
    this.url = checkNotNullOrEmpty(url);
    this.urlParameters = checkNotNull(urlParameters);
    this.apiVersion = checkNotNull(apiVersion);
    this.body = body;
    this.customHeader = customHeader;
    this.connectionCredentials = connectionCredentials;
    this.httpRequestHeader = createHttpRequestHeader();
    this.httpClientRequest = createHttpClientRequest();
  }

  private Map<String, String> createHttpRequestHeader() {
    if (null != customHeader) {
      return customHeader;
    } else {
      checkNotNull(connectionCredentials, "Set connection credentials first.");
      return ImmutableMap.<String, String>builder()
          .put(IgIndexConstant.HEADER_PARAMETER_API_KEY, connectionCredentials.getApiKey())
          .put(IgIndexConstant.HEADER_PARAMETER_CLIENT_SESSION_TOKEN, connectionCredentials.getClientSessionToken())
          .put(IgIndexConstant.HEADER_PARAMETER_ACCOUNT_SESSION_TOKEN,
              connectionCredentials.getAccountSessionToken())
          .put(IgIndexConstant.HEADER_PARAMETER_VERSION, apiVersion)
          .put(IgIndexConstant.HEADER_PARAMETER_CONTENT_TYPE, IgIndexConstant.HEADER_PARAMETER_VALUE_JSON_UTF8)
          .put(IgIndexConstant.HEADER_PARAMETER_ACCEPT, IgIndexConstant.HEADER_PARAMETER_VALUE_JSON_UTF8)
          .build();
    }
  }

  private HttpClientRequest createHttpClientRequest() {
    StringBuilder urlSuffix = new StringBuilder();
    for (Map.Entry<String, String> entry : urlParameters.entrySet()) {
      if (urlSuffix.length() == 0) {
        urlSuffix.append("?");
      } else {
        urlSuffix.append("&");
      }
      urlSuffix.append(
          String.format("%s=%s", escapeHtml4(entry.getKey()), escapeHtml4(entry.getValue())));
    }
    String completeUrl = url + urlSuffix.toString();
    return HttpClientRequest.newBuilder()
        .setHttpRequestMethod(httpMethod)
        .setHeaders(httpRequestHeader)
        .setUrl(completeUrl)
        .setBody(body)
        .build();
  }

  public static class Builder {
    private Map<String, String> urlParameters = ImmutableMap.of();
    private String apiVersion = IgIndexConstant.API_VERSION_1;
    private String body = "";
    private IgIndexConnectionCredentials connectionCredentials;
    private HttpMethod httpMethod;
    private String url;
    private Map<String, String> customHeader;

    public Builder setConnectionCredentials(IgIndexConnectionCredentials connectionCredentials) {
      this.connectionCredentials =
          checkNotNull(connectionCredentials, "Connection credentials are null.");
      return this;
    }

    public Builder setHttpMethod(HttpMethod httpMethod) {
      this.httpMethod = checkNotNull(httpMethod);
      return this;
    }

    public Builder setUrl(String url) {
      this.url = checkNotNullOrEmpty(url);
      return this;
    }

    public Builder setUrlParameters(Map<String, String> urlParameters) {
      this.urlParameters = ImmutableMap.copyOf(checkNotNull(urlParameters));
      return this;
    }

    public Builder setApiVersion(String apiVersion) {
      this.apiVersion = checkNotNullOrEmpty(apiVersion);
      return this;
    }

    public Builder setCustomHeader(Map<String, String> customHeader) {
      this.customHeader = ImmutableMap.copyOf(checkNotNull(customHeader));
      return this;
    }

    public Builder setBody(String body) {
      this.body = checkNotNull(body);
      return this;
    }

    public IgIndexClientRequest build() {
      return new IgIndexClientRequest(
          connectionCredentials, httpMethod, url, urlParameters, apiVersion, customHeader, body);
    }
  }
}
