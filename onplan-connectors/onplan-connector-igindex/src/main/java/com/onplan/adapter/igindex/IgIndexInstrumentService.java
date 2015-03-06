package com.onplan.adapter.igindex;

import com.onplan.adapter.InstrumentService;
import com.onplan.service.ServiceConnectionInfo;
import com.onplan.adapter.igindex.client.IgIndexClient;
import com.onplan.domain.InstrumentInfo;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.adapter.igindex.IgIndexMapper.getEpicByInstrumentId;

public class IgIndexInstrumentService implements InstrumentService {
  private static final Logger LOGGER = Logger.getLogger(IgIndexInstrumentService.class);

  private final IgIndexConnection igIndexConnection;
  private final IgIndexClient igIndexClient;

  public IgIndexInstrumentService(IgIndexConnection igIndexConnection) {
    LOGGER.info("Setting IgIndexConnection in IgIndexInstrumentService.");
    this.igIndexConnection = checkNotNull(igIndexConnection);
    this.igIndexClient = igIndexConnection.getIgIndexClient();
  }

  @Override
  public ServiceConnectionInfo getServiceConnectionInfo() {
    return igIndexConnection.getConnectionInfo();
  }

  @Override
  public InstrumentInfo getInstrumentInfo(String instrumentId) throws IOException {
    return igIndexClient.getInstrumentInfo(getEpicByInstrumentId(instrumentId));
  }

  @Override
  public List<InstrumentInfo> findInstrumentsBySearchTerm(String searchTerm) throws Exception {
    try {
      return igIndexClient.findInstrumentsBySearchTerm(searchTerm);
    } catch (IOException e) {
      LOGGER.error(e.getMessage());
      throw new Exception(e);
    }
  }
}
