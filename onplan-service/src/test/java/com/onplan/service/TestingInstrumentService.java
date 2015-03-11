package com.onplan.service;

import com.google.common.collect.ImmutableList;
import com.onplan.connector.InstrumentService;
import com.onplan.domain.InstrumentType;
import com.onplan.domain.transitory.InstrumentInfo;
import org.apache.log4j.Logger;

import java.util.List;

public class TestingInstrumentService implements InstrumentService {
  private static final Logger LOGGER = Logger.getLogger(TestingInstrumentService.class);

  private static final List<InstrumentInfo> availableInstruments = ImmutableList.of(
      new InstrumentInfo("CS.EURUSD.TODAY", "EURUSD", InstrumentType.CURRENCY, 4, "DAILY"),
      new InstrumentInfo("CS.AUDUSD.TODAY", "AUDUSD", InstrumentType.CURRENCY, 4, "DAILY"),
      new InstrumentInfo("IX.DAX.DAILY", "DAX", InstrumentType.INDEX, 0, "DAILY"),
      new InstrumentInfo("IX.FTSE.DAILY", "FTSE 100", InstrumentType.INDEX, 0, "DAILY"));

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
    LOGGER.warn(String.format("Instrument [%s] not found.", instrumentId));
    return null;
  }

  @Override
  public List<InstrumentInfo> findInstrumentsBySearchTerm(String searchTerm) throws Exception {
    throw new IllegalArgumentException("Not yet implemented.");
  }
}
