package com.onplan.adapter.igindex;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.lightstreamer.ls_client.ExtendedTableInfo;
import com.lightstreamer.ls_client.HandyTableListener;
import com.lightstreamer.ls_client.SubscribedTableKey;
import com.lightstreamer.ls_client.UpdateInfo;
import com.onplan.adapter.AbstractPriceService;
import com.onplan.adapter.ServiceConnectionListener;
import com.onplan.domain.PriceTick;
import com.onplan.service.ServiceConnectionInfo;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.adapter.igindex.IgIndexMapper.getInstrumentIdByEpic;
import static com.onplan.adapter.igindex.IgIndexMapper.getTickSubscriptionEpicByInstrumentId;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

public class IgIndexPriceService extends AbstractPriceService {
  private static final Logger LOGGER = Logger.getLogger(IgIndexPriceService.class);
  private static final String UPDATE_MODE = "DISTINCT";
  private static final String[] PRICE_TICK_FIELDS = {"OFR", "BID", "UTM"};
  private static final Map<String, SubscribedTableKey> SUBSCRIBED_INSTRUMENTS =
      Maps.newConcurrentMap();

  private final ServiceConnectionListener serviceConnectionListener =
      new IgIndexServiceConnectionListener();
  private final Map<String, PriceTick> lastPriceTicks = Maps.newHashMap();
  private final IgIndexConnection igIndexConnection;

  public IgIndexPriceService(IgIndexConnection igIndexConnection) {
    LOGGER.info("Setting IgIndexConnection in IgIndexPriceService.");
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
    return ImmutableSet.copyOf(SUBSCRIBED_INSTRUMENTS.keySet());
  }

  @Override
  public PriceTick getLastPriceTick(String instrumentId) {
    synchronized (lastPriceTicks) {
      return lastPriceTicks.get(instrumentId);
    }
  }

  @Override
  public void subscribeInstrument(String instrumentId) throws Exception {
    checkNotNullOrEmpty(instrumentId);
    checkArgument(igIndexConnection.isConnected(), "Service not connected.");
    if (SUBSCRIBED_INSTRUMENTS.containsKey(instrumentId)) {
      LOGGER.warn(String.format("Instrument [%s] already subscribed.", instrumentId));
      return;
    }
    String instrumentEpic = getTickSubscriptionEpicByInstrumentId(instrumentId);
    LOGGER.info(String.format(
        "Subscribing instrument id [%s] as IgIndex epic [%s].", instrumentId, instrumentEpic));
    ExtendedTableInfo extendedTableInfo =
        new ExtendedTableInfo(new String[] {instrumentEpic}, UPDATE_MODE, PRICE_TICK_FIELDS, true);
    SubscribedTableKey subscribedTableKey = igIndexConnection.getLightStreamerClient()
        .subscribeTable(extendedTableInfo, new PriceTickTableListener(), false);
    SUBSCRIBED_INSTRUMENTS.put(instrumentId, subscribedTableKey);
    LOGGER.info(String.format("Instrument [%s] subscribed.", instrumentId));
  }

  @Override
  public void unsubscribeInstrument(String instrumentId) throws Exception {
    checkNotNullOrEmpty(instrumentId);
    checkArgument(igIndexConnection.isConnected(), "Service not connected.");
    if (!SUBSCRIBED_INSTRUMENTS.containsKey(instrumentId)) {
      LOGGER.warn(String.format("Instrument [%s] is not subscribed.", instrumentId));
      return;
    }
    SubscribedTableKey subscribedTableKey = checkNotNull(SUBSCRIBED_INSTRUMENTS.get(instrumentId));
    igIndexConnection.getLightStreamerClient().unsubscribeTable(subscribedTableKey);
    LOGGER.info(String.format("Instrument [%s] un-subscribed.", instrumentId));
  }

  private PriceTick createPriceTick(UpdateInfo updateInfo) {
    String instrumentId = getInstrumentIdByEpic(updateInfo.getItemName());
    double askPrice = Double.parseDouble(updateInfo.getNewValue(1));
    double bidPrice = Double.parseDouble(updateInfo.getNewValue(2));
    long timestamp = Long.parseLong(updateInfo.getNewValue(3));
    return new PriceTick(instrumentId, timestamp, askPrice, bidPrice);
  }

  private class IgIndexServiceConnectionListener implements ServiceConnectionListener {
    @Override
    public void onConnectionEstablished() {
      // Intentionally empty.
    }

    @Override
    public void onDisconnected() {
      LOGGER.info("Service disconnection, clearing subscribed instruments.");
      SUBSCRIBED_INSTRUMENTS.clear();
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
      LOGGER.warn(
          String.format("Raw update lost: Item name [%s], item position [%d].", itemName, itemPos));
    }

    @Override
    public void onUnsubscr(int itemPos, String itemName) {
      LOGGER.info(String.format("Instrument [%s] un-subscribed.", itemName));
    }

    @Override
    public void onUnsubscrAll() {
      LOGGER.info("All instrument un-subscribed");
    }
  }
}
