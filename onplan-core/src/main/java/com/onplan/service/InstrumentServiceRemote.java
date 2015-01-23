package com.onplan.service;

import com.onplan.domain.InstrumentInfo;

import java.io.Serializable;
import java.util.List;

public interface InstrumentServiceRemote extends Serializable {
  public ServiceConnectionInfo getServiceConnectionInfo();
  public InstrumentInfo getInstrumentInfo(String instrumentId);
  public List<InstrumentInfo> findInstrumentsBySearchTerm(String name) throws Exception;
}
