package com.onplan.adapter.igindex;

import com.onplan.adapter.HistoricalPriceService;
import com.onplan.service.ServiceConnectionInfo;
import com.onplan.domain.PriceBar;
import org.apache.log4j.Logger;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;

public class IgIndexHistoricalPriceService implements HistoricalPriceService {
  private static final Logger LOGGER = Logger.getLogger(IgIndexHistoricalPriceService.class);

  private final IgIndexConnection igIndexConnection;

  public IgIndexHistoricalPriceService(IgIndexConnection igIndexConnection) {
    LOGGER.info("Setting IgIndexConnection in IgIndexHistoricalPriceService.");
    this.igIndexConnection = checkNotNull(igIndexConnection);
  }

  @Override
  public ServiceConnectionInfo getServiceConnectionInfo() {
    return igIndexConnection.getConnectionInfo();
  }

  @Override
  public Collection<PriceBar> getHistoricalPriceBars(
      String instrumentId, long barPeriod, long startTimestamp, long endTimestamp) {
    throw new IllegalArgumentException("Not yet implemented.");
  }
}
