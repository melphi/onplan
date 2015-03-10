package com.onpla.adapter.util;

import com.onplan.connector.util.CompleteEnumMapper;
import org.junit.Test;

public class CompleteEnumMapperTest {
  @Test(expected = IllegalArgumentException.class)
  public void testUncompletedMap() {
    CompleteEnumMapper.<String, SampleEnum>newBuilder(SampleEnum.class)
        .put(SampleEnum.VALUE_A, "A")
        .setUnmapped(SampleEnum.VALUE_B)
        .build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWrongMapping() {
    CompleteEnumMapper.<String, SampleEnum>newBuilder(SampleEnum.class)
        .put(SampleEnum.VALUE_A, "A")
        .put(SampleEnum.VALUE_B, "C")
        .put(SampleEnum.VALUE_C, "C")
        .setUnmapped(SampleEnum.VALUE_C)
        .build();
  }

  @Test
  public void testCompletedMap() {
    CompleteEnumMapper.<String, SampleEnum>newBuilder(SampleEnum.class)
        .put(SampleEnum.VALUE_A, "A")
        .put(SampleEnum.VALUE_B, "B", "C")
        .setUnmapped(SampleEnum.VALUE_C)
        .build();
  }

  @Test(expected = NullPointerException.class)
  public void testElementNotFound() {
    CompleteEnumMapper map = CompleteEnumMapper.<String, SampleEnum>newBuilder(SampleEnum.class)
        .put(SampleEnum.VALUE_A, "A")
        .put(SampleEnum.VALUE_B, "B", "C")
        .setUnmapped(SampleEnum.VALUE_C)
        .build();
    map.map("D");
  }

  private enum SampleEnum {
    VALUE_A,
    VALUE_B,
    VALUE_C
  }
}
