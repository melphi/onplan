package com.onplan.connector;

import com.google.common.collect.Lists;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractServiceConnection implements ServiceConnection {
  private final List<ServiceConnectionListener> serviceConnectionListeners = Lists.newArrayList();

  @Override
  public void addServiceConnectionListener(ServiceConnectionListener serviceConnectionListener) {
    checkNotNull(serviceConnectionListener);
    synchronized (serviceConnectionListeners) {
      if (serviceConnectionListeners.contains(serviceConnectionListener)) {
        throw new IllegalArgumentException("ServiceConnectionListener already set.");
      }
      serviceConnectionListeners.add(serviceConnectionListener);
    }
  }

  @Override
  public void removeConnectionServiceListener(ServiceConnectionListener priceServiceListener) {
    checkNotNull(priceServiceListener);
    synchronized (serviceConnectionListeners) {
      if (serviceConnectionListeners.contains(priceServiceListener)) {
        serviceConnectionListeners.remove(priceServiceListener);
      }
    }
  }

  protected void dispatchServiceConnectionEstablishedEvent() {
    synchronized (serviceConnectionListeners) {
      for (ServiceConnectionListener serviceConnectionListener : serviceConnectionListeners) {
        serviceConnectionListener.onConnectionEstablished();
      }
    }
  }

  protected void dispatchServiceDisconnectedEvent() {
    synchronized (serviceConnectionListeners) {
      for (ServiceConnectionListener serviceConnectionListener : serviceConnectionListeners) {
        serviceConnectionListener.onDisconnected();
      }
    }
  }
}
