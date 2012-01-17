package com.linkedin.searchperf.common;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.linkedin.searchperf.common.util.HttpClient;

public class HttpIndexLoaderTest {
  public static class MockHttpClient extends HttpClient {
    public List<String> postedData = Collections.synchronizedList(new ArrayList<String>());
    @Override
    public String postJson(String url, String jsonData) {
      postedData.add(jsonData);
      return null;
    }
  }

  private HttpIndexLoader httpIndexLoader;
  private MockHttpClient mockHttpClient;
  @Before
  public void setUp() throws Exception {
     httpIndexLoader = new HttpIndexLoader();
     mockHttpClient = new MockHttpClient();
     httpIndexLoader.setHttpClient(mockHttpClient);
  }

  @Test
  public void testLoadData() {
    httpIndexLoader.loadData("anyUrl", getClass().getClassLoader().getResourceAsStream("cars.json"));
    assertEquals(150, mockHttpClient.postedData.size());
  }

}
