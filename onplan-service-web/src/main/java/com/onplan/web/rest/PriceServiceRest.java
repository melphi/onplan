package com.onplan.web.rest;

import com.onplan.domain.transitory.PriceTick;
import com.onplan.service.PriceServiceRemote;
import com.onplan.service.ServiceConnectionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

@RestController
@RequestMapping(value = "/rest/prices", produces = "application/json")
public class PriceServiceRest implements PriceServiceRemote {
  @Autowired
  private PriceServiceRemote priceService;

  @Override
  @RequestMapping(value = "/serviceconnectioninfo", method = RequestMethod.GET)
  public ServiceConnectionInfo getServiceConnectionInfo() {
    return priceService.getServiceConnectionInfo();
  }

  @Override
  public PriceTick getLastPriceTick(String instrumentId) {
    checkNotNullOrEmpty(instrumentId);
    return priceService.getLastPriceTick(instrumentId);
  }
}
