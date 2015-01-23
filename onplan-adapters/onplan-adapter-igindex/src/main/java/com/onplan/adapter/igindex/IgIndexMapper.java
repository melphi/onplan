package com.onplan.adapter.igindex;

import com.google.common.base.Splitter;
import com.onplan.adapter.util.CompleteEnumMapper;
import com.onplan.domain.InstrumentType;

import java.util.Iterator;

import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

public class IgIndexMapper {
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
   * Converts the IgIndex epic code to the instrumentId code. This function is optimized for speed.
   *
   * @param epicCode IgIndex epic code, for example "CF.D.EURAUD.MAR.IP".
   * @return the instrumentId, for example "CF.EURAUD.MAR".
   */
  // TODO(robertom): Map the first two characters of the epic to a custom instrument type.
  public static String getInstrumentIdByEpic(final String epicCode) {
    final StringBuilder result = new StringBuilder();
    // First two characters is the instrument type.
    result.append(epicCode.substring(0, 2)).append('.');

    // Sixth to the next dot is the instrument code.
    int endPosition = epicCode.indexOf(".", 6);
    result.append(epicCode.substring(6, endPosition)).append('.');

    // Third dot to next dot is the expiration.
    endPosition = epicCode.indexOf(".", endPosition + 1);
    result.append(epicCode.substring(result.length() + 3, endPosition).toUpperCase());
    return result.toString();
  }

  /**
   * Converts the instrumentId code to the IgIndex epic. This function is not optimized for speed
   * but for readability.
   *
   * @param instrumentId the instrumentId code, for example "CF.EURAUD.MAR".
   * @return the IgIndex epic, for example "CF.D.EURAUD.MAR.IP".
   */
  public static String getEpicByInstrumentId(final String instrumentId) {
    checkNotNullOrEmpty(instrumentId);
    Iterator<String> chunks = Splitter.on('.').split(instrumentId).iterator();
    return new StringBuilder()
        .append(chunks.next())
        .append(".D.")
        .append(chunks.next())
        .append(".")
        .append(chunks.next())
        .append(".IP")
        .toString();
  }

  public static InstrumentType getInstrumentTypeByText(final String instrumentType) {
    return INSTRUMENT_TYPE_MAPPER.map(checkNotNullOrEmpty(instrumentType));
  }
}
