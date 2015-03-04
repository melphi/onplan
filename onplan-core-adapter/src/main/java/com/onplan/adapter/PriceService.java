package com.onplan.adapter;

import com.onplan.service.PriceServiceRemote;

public interface PriceService extends PriceServiceRemote {
  public void setPriceListener(PriceListener listener);
  public void subscribeInstrument(String instrumentId) throws Exception;
  public void unSubscribeInstrument(String instrumentId) throws Exception;
}
