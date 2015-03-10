package com.onplan.connector;

public interface ServiceConnectionListener {
  public void onConnectionEstablished();
  public void onDisconnected();
}
