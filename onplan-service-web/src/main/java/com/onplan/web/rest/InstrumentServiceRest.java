package com.onplan.web.rest;

import com.onplan.domain.transitory.InstrumentInfo;
import com.onplan.service.InstrumentServiceRemote;
import com.onplan.service.ServiceConnectionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

@RestController
@RequestMapping(value = "/rest/instruments", produces = "application/json")
public class InstrumentServiceRest implements InstrumentServiceRemote {
  @Autowired
  private InstrumentServiceRemote instrumentService;

  @Override
  public ServiceConnectionInfo getServiceConnectionInfo() {
    return instrumentService.getServiceConnectionInfo();
  }

  @Override
  public InstrumentInfo getInstrumentInfo(String instrumentId) throws IOException {
    checkNotNullOrEmpty(instrumentId);
    return instrumentService.getInstrumentInfo(instrumentId);
  }

  @RequestMapping(value = "/", method = RequestMethod.GET)
  @Override
  public List<InstrumentInfo> findInstrumentsBySearchTerm(
      @RequestParam(value = "searchTerm") String searchTerm) throws Exception {
    checkNotNullOrEmpty(searchTerm);
    return instrumentService.findInstrumentsBySearchTerm(searchTerm);
  }
}
