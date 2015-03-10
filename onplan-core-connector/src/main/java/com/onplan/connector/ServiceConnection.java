package com.onplan.connector;

import com.onplan.service.ServiceConnectionInfo;

public interface ServiceConnection {
  public void connect();
  public void disconnect();
  public boolean isConnected();
  public ServiceConnectionInfo getConnectionInfo();
  public void addServiceConnectionListener(ServiceConnectionListener serviceConnectionListener);
  public void removeConnectionServiceListener(ServiceConnectionListener serviceConnectionListener);
}
