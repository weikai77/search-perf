package com.linkedin.searchperf.common.query;

import static org.junit.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class DataCollectorTest {
  private DataCollector dataCollector;
  private BufferedInputStream input;

  @Before
  public void before() throws Exception {
    dataCollector = new DataCollector();
    input = new BufferedInputStream(getClass().getClassLoader().getResourceAsStream("cars.json"));
  }
  @Test
  public void test1CollectData() throws Exception {
    long time = System.currentTimeMillis();
    try {
    Map<String, Set<Object>> collectValues = dataCollector.collectValues(input);
    assertEquals(11, collectValues.size());
    assertEquals(11, collectValues.get("category").size());
    assertEquals(1500, collectValues.get("groupid").size());
    } finally {
      System.out.println(System.currentTimeMillis() - time);
    }
  }

}
