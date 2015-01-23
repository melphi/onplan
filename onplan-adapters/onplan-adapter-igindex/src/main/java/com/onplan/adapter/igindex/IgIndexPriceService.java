package com.onplan.adapter.igindex;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.lightstreamer.ls_client.*;
import com.onplan.adapter.AbstractPriceService;
import com.onplan.service.ServiceConnectionInfo;
import com.onplan.adapter.ServiceConnectionListener;
import com.onplan.domain.PriceTick;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class IgIndexPriceService extends AbstractPriceService {
  private static final Logger logger = Logger.getLogger(IgIndexPriceService.class);
  private static final String UPDATE_MODE = "DISTINCT";

  private final ServiceConnectionListener serviceConnectionListener =
      new IgIndexServiceConnectionListener();
  private final PriceTickTableListener priceTickTableListener = new PriceTickTableListener();
  private final Map<String, PriceTick> lastPriceTicks = Maps.newHashMap();

  private final IgIndexConnection igIndexConnection;

  private ImmutableSet<String> priceTickInstruments = ImmutableSet.of(
      "CHART:CS.D.EURUSD.TODAY.IP:TICK",
      "CHART:CS.D.AUDUSD.TODAY.IP:TICK",
      "CHART:IX.D.FTSE.DAILY.IP:TICK",
      "CHART:IX.D.DAX.DAILY.IP:TICK");

  private ImmutableSet<String> priceTickFields = ImmutableSet.of(
      "OFR",
      "BID",
      "UTM");

  public IgIndexPriceService(IgIndexConnection igIndexConnection) {
    logger.info("Setting IgIndexConnection in IgIndexPriceService.");
    this.igIndexConnection = checkNotNull(igIndexConnection);
    this.igIndexConnection.addServiceConnectionListener(serviceConnectionListener);
  }

  @Override
  public ServiceConnectionInfo getServiceConnectionInfo() {
    return igIndexConnection.getConnectionInfo();
  }

  @Override
  public boolean isConnected() {
    return igIndexConnection.isConnected();
  }

  @Override
  public Collection<String> getSubscribedInstruments() {
    ImmutableList.Builder subscribedInstrument = ImmutableList.builder();
    for (String instrument : priceTickInstruments) {
      subscribedInstrument.add(IgIndexMapper.getInstrumentIdByEpic(instrument));
    }
    return subscribedInstrument.build();
  }

  @Override
  public PriceTick getLastPriceTick(String instrumentId) {
    synchronized (lastPriceTicks) {
      return lastPriceTicks.get(instrumentId);
    }
  }

  private void subscribeInstruments()
      throws SubscrException, PushUserException, PushServerException, PushConnException {
    String[] ticksItems = priceTickInstruments.toArray(new String[priceTickInstruments.size()]);
    String[] ticksFields = priceTickFields.toArray(new String[priceTickFields.size()]);
    ExtendedTableInfo ticksTableInfo = new ExtendedTableInfo(ticksItems, UPDATE_MODE, ticksFields, true);
    igIndexConnection.getLightStreamerClient().subscribeItems(ticksTableInfo, priceTickTableListener);
    logger.info(String.format(
        "Subscribed instruments [%s].",
        Joiner.on(", ").join(priceTickInstruments)));
  }

  private PriceTick createPriceTick(UpdateInfo updateInfo) {
    String instrumentId = IgIndexMapper.getInstrumentIdByEpic(updateInfo.getItemName());
    double askPrice = Double.parseDouble(updateInfo.getNewValue(1));
    double bidPrice = Double.parseDouble(updateInfo.getNewValue(2));
    long timestamp = Long.parseLong(updateInfo.getNewValue(3));
    return new PriceTick(instrumentId, timestamp, askPrice, bidPrice);
  }

  private class IgIndexServiceConnectionListener implements ServiceConnectionListener {
    @Override
    public void onConnectionEstablished() {
      try {
        subscribeInstruments();
      } catch (SubscrException | PushUserException | PushServerException | PushConnException e) {
        logger.error(String.format("Error while subscribing instruments: [%s]", e.getMessage()));
      }
    }

    @Override
    public void onDisconnected() {
      // Intentionally empty.
    }
  }

  private class PriceTickTableListener implements HandyTableListener {
    @Override
    public void onUpdate(int itemPos, String itemName, UpdateInfo updateInfo) {
      PriceTick priceTick = createPriceTick(updateInfo);
      dispatchPriceTick(createPriceTick(updateInfo));
      synchronized (lastPriceTicks) {
        lastPriceTicks.put(priceTick.getInstrumentId(), priceTick);
      }
    }

    @Override
    public void onSnapshotEnd(int itemPos, String itemName) {
      // Intentionally empty.
    }

    @Override
    public void onRawUpdatesLost(int itemPos, String itemName, int lostUpdates) {
      logger.warn(
          String.format("Raw update lost: Item name [%s], item position [%d].", itemName, itemPos));
    }

    @Override
    public void onUnsubscr(int itemPos, String itemName) {
      logger.info(String.format("Instrument [%s] un-subscribed.", itemName));
    }

    @Override
    public void onUnsubscrAll() {
      logger.info("All instrument un-subscribed");
    }
  }
}
