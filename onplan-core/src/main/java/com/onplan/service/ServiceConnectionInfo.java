package com.onplan.service;

import java.io.Serializable;

import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

public class ServiceConnectionInfo implements Serializable {
  private final String providerName;
  private final boolean isConnected;
  private final Long connectionUpdateTimestamp;

  public ServiceConnectionInfo(String providerName, boolean isConnected, Long connectionDate) {
    this.providerName = checkNotNullOrEmpty(providerName);
    this.isConnected = isConnected;
    this.connectionUpdateTimestamp = connectionDate;
  }

  /**
   * Returns the provider displayName.
   */
  public String getProviderName() {
    return providerName;
  }

  /**
   * Returns true if the service is connected.
   */
  public boolean getIsConnected() {
    return isConnected;
  }

  /**
   * Returns the timestamp of the last connection / disconnection. Returns null if any connection
   * attempt has ever been established.
   */
  public Long getConnectionUpdateTimestamp() {
    return connectionUpdateTimestamp;
  }
}
