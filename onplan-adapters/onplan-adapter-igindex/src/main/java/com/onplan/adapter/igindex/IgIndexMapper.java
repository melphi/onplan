package com.onplan.adapter.igindex;

import com.google.common.base.Splitter;
import com.onplan.adapter.util.CompleteEnumMapper;
import com.onplan.domain.InstrumentType;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

public final class IgIndexMapper {
  private static final String CHART_PREFIX = "CHART:";
  private static final String TICK_SUFFIX = ":TICK";

  // TODO(robertom): Fix the unchecked type issue.
  private static final CompleteEnumMapper<String, InstrumentType> INSTRUMENT_TYPE_MAPPER =
      CompleteEnumMapper.<String, InstrumentType>newBuilder(InstrumentType.class)
          .put(InstrumentType.BINARY, "BINARY")
          .put(InstrumentType.UNKNOWN, "BUNGEE_CAPPED", "BUNGEE_COMMODITIES", "BUNGEE_CURRENCIES",
              "BUNGEE_INDICES", "TEST_MARKET", "UNKNOWN")
          .put(InstrumentType.CURRENCY, "CURRENCIES")
          .put(InstrumentType.COMMODITY, "COMMODITIES")
          .put(InstrumentType.INDEX, "INDICES")
          .put(InstrumentType.OPTION_COMMODITY, "OPT_COMMODITIES")
          .put(InstrumentType.OPTION_CURRENCY, "OPT_CURRENCIES")
          .put(InstrumentType.OPTION_INDEX, "OPT_INDICES")
          .put(InstrumentType.OPTION_RATE, "OPT_RATES")
          .put(InstrumentType.OPTION_SHARE, "OPT_SHARES")
          .put(InstrumentType.RATE, "RATES")
          .put(InstrumentType.SECTOR, "SECTORS")
          .put(InstrumentType.SHARE, "SHARES")
          .put(InstrumentType.SPRINT_MARKET, "SPRINT_MARKET")
          .build();

  /**
   * Returns the instrumentId code which match the IgIndex epic.
   * This function is optimized for speed.
   *
   * @param epicCode IgIndex epic code, for example "CF.D.EURAUD.MAR.IP".
   * @return the instrumentId, for example "CF.EURAUD.MAR".
   */
  // TODO(robertom): Map the first two characters of the epic to a custom instrument type.
  public static String getInstrumentIdByEpic(final String epicCode) {
    final StringBuilder result = new StringBuilder();
    int dotsCount = 0;
    int startIndex = 0;
    for (int i = 0; i < epicCode.length(); i++) {
      if (epicCode.charAt(i) == '.') {
        dotsCount++;
        switch (dotsCount) {
          case 1:
            // First dot, the last two letters are the instrument type.
            result.append(epicCode.substring(i - 2, i)).append('.');
            break;
          case 2:
            // Second dot, just update the placeholder.
            startIndex = i;
            break;
          case 3:
            // Third dot, the letters from the second to the third dots are the instrument code.
            result.append(epicCode.substring(startIndex + 1, i)).append('.');
            startIndex = i;
            break;
          case 4:
            // Fourth dot, the letters from the third to the fourth dots are expiration.
            result.append(epicCode.substring(startIndex + 1, i));
            return result.toString();
          default:
            throw new IllegalArgumentException(String.format(
                "Unexpected number of dots in epic [%s].", epicCode));
        }
      }
    }
    throw new IllegalArgumentException(String.format(
        "Out of the switch not expected in epic [%s].", epicCode));
  }

  /**
   * Returns the IgIndex epic used to subscribe the price ticks for a given instrument id.
   * This function is not optimized for speed.
   *
   * @param instrumentId the instrumentId code, for example "CF.EURAUD.MAR".
   * @return the IgIndex epic, for example "CHART:CF.D.EURAUD.MAR.IP:TICK".
   */

  public static String getTickSubscriptionEpicByInstrumentId(final String instrumentId) {
    return new StringBuilder()
        .append(CHART_PREFIX)
        .append(getEpicByInstrumentId(instrumentId))
        .append(TICK_SUFFIX)
        .toString();
  }

  /**
   * Returns the IgIndex epic which match the instrumentId.
   * This function is not optimized for speed.
   *
   * @param instrumentId the instrumentId code, for example "CF.EURAUD.MAR".
   * @return the IgIndex epic, for example "CF.D.EURAUD.MAR.IP".
   */
  public static String getEpicByInstrumentId(final String instrumentId) {
    checkNotNullOrEmpty(instrumentId);
    Iterator<String> chunks = Splitter.on('.').split(instrumentId).iterator();
    try {
      return new StringBuilder()
          .append(chunks.next())
          .append(".D.")
          .append(chunks.next())
          .append(".")
          .append(chunks.next())
          .append(".IP")
          .toString();
    } catch (NoSuchElementException e) {
      throw new NoSuchElementException(String.format(
          "Instrument id [%s] can not be converted to an igIndex epic.", instrumentId));
    }
  }

  public static InstrumentType getInstrumentTypeByText(final String instrumentType) {
    return INSTRUMENT_TYPE_MAPPER.map(checkNotNullOrEmpty(instrumentType));
  }
}
