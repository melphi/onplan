package com.onplan.adapter;

import com.onplan.service.PriceServiceRemote;

public interface PriceService extends PriceServiceRemote {
  public void setPriceListener(PriceListener listener);
}
