package com.onplan.connector;

import com.onplan.service.PriceServiceRemote;

public interface PriceService extends PriceServiceRemote {
  public boolean isConnected();
  public boolean isInstrumentSubscribed(String instrumentId);
  public void setPriceListener(PriceListener listener);
  public void subscribeInstrument(String instrumentId) throws Exception;
  public void unSubscribeInstrument(String instrumentId) throws Exception;
}
