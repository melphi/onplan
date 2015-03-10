package com.onplan.service;

import com.google.common.collect.ImmutableList;
import com.onplan.connector.InstrumentService;
import com.onplan.domain.transitory.InstrumentInfo;
import com.onplan.domain.InstrumentType;

import java.util.List;

public class TestingInstrumentService implements InstrumentService {
  private static final List<InstrumentInfo> availableInstruments = ImmutableList.of(
      new InstrumentInfo("EURUSD", "EURUSD", InstrumentType.CURRENCY, 4, "DAILY"),
      new InstrumentInfo("AUDUSD", "AUDUSD", InstrumentType.CURRENCY, 4, "DAILY"),
      new InstrumentInfo("DAX", "DAX", InstrumentType.INDEX, 0, "DAILY"),
      new InstrumentInfo("FTSE100", "FTSE 100", InstrumentType.INDEX, 0, "DAILY"));

  @Override
  public ServiceConnectionInfo getServiceConnectionInfo() {
    throw new IllegalArgumentException("Not yet implemented.");
  }

  @Override
  public InstrumentInfo getInstrumentInfo(String instrumentId) {
    for (InstrumentInfo instrumentInfo : availableInstruments) {
      if (instrumentId.equals(instrumentInfo.getInstrumentId())) {
        return instrumentInfo;
      }
    }
    return null;
  }

  @Override
  public List<InstrumentInfo> findInstrumentsBySearchTerm(String searchTerm) throws Exception {
    throw new IllegalArgumentException("Not yet implemented.");
  }
}
