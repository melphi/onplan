package com.onplan.domain.persistent;

import java.io.Serializable;

public interface Price extends Serializable {
  public String getInstrumentId();
  public long getTimestamp();
  public double getClosePriceAsk();
  public double getClosePriceBid();
}
