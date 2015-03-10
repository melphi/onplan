package com.onplan.connector.igindex.client;

import com.google.common.base.MoreObjects;

import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

public final class IgIndexConnectionCredentials {
  private String apiKey;
  private String clientSessionToken;
  private String accountSessionToken;
  private String lightStreamerEndpoint;

  public String getClientSessionToken() {
    return clientSessionToken;
  }

  public String getAccountSessionToken() {
    return accountSessionToken;
  }

  public String getLightStreamerEndpoint() {
    return lightStreamerEndpoint;
  }

  public String getApiKey() {
    return apiKey;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("apiKey", apiKey)
        .add("clientSessionToken", clientSessionToken)
        .add("accountSessionToken", accountSessionToken)
        .add("lightStreamerEndpoint", lightStreamerEndpoint)
        .toString();
  }

  private IgIndexConnectionCredentials(String apiKey, String clientSessionToken,
      String accountSessionToken, String lightStreamerEndpoint) {
    this.apiKey = checkNotNullOrEmpty(apiKey);
    this.clientSessionToken = checkNotNullOrEmpty(clientSessionToken);
    this.accountSessionToken = checkNotNullOrEmpty(accountSessionToken);
    this.lightStreamerEndpoint = checkNotNullOrEmpty(lightStreamerEndpoint);
  }

  public static class Builder {
    private String apiKey;
    private String clientSessionToken;
    private String accountSessionToken;
    private String lightStreamerEndpoint;

    public Builder setClientSessionToken(String clientSessionToken) {
      this.clientSessionToken = checkNotNullOrEmpty(clientSessionToken);
      return this;
    }

    public Builder setAccountSessionToken(String accountSessionToken) {
      this.accountSessionToken = checkNotNullOrEmpty(accountSessionToken);
      return this;
    }

    public Builder setLightStreamerEndpoint(String lightStreamerEndpoint) {
      this.lightStreamerEndpoint = checkNotNullOrEmpty(lightStreamerEndpoint);
      return this;
    }

    public Builder setApiKey(String apiKey) {
      this.apiKey = apiKey;
      return this;
    }

    public IgIndexConnectionCredentials build() {
      return new IgIndexConnectionCredentials(
          apiKey, clientSessionToken, accountSessionToken, lightStreamerEndpoint);
    }
  }
}
