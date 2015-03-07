package com.onplan.service;

import com.onplan.domain.transitory.InstrumentInfo;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public interface InstrumentServiceRemote extends Serializable {
  public ServiceConnectionInfo getServiceConnectionInfo();
  public InstrumentInfo getInstrumentInfo(String instrumentId) throws IOException;
  public List<InstrumentInfo> findInstrumentsBySearchTerm(String searchTerm) throws Exception;
}
