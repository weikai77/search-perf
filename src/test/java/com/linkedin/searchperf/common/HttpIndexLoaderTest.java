package com.linkedin.searchperf.common;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import com.linkedin.searchperf.common.util.HttpClient;
import com.linkedin.searchperf.index.IndexLoaderMain;
import com.linkedin.searchperf.index.SolrIndexLoader;

public class HttpIndexLoaderTest {
  public static class MockHttpClient extends HttpClient {
    public List<String> postedData = Collections.synchronizedList(new ArrayList<String>());
    @Override
    public String postJson(String url, String jsonData) {
      postedData.add(jsonData);
      return null;
    }
  }

  private IndexLoaderMain httpIndexLoader;
  private MockHttpClient mockHttpClient;
  @Before
  public void setUp() throws Exception {
     try {
     httpIndexLoader = new IndexLoaderMain();
     mockHttpClient = new MockHttpClient();
     httpIndexLoader.setHttpClient(mockHttpClient);} catch (Exception ex) {ex.printStackTrace();}
  }

  @Test
  public void testLoadData() throws Exception {
    try {
    httpIndexLoader.loadData(new SolrIndexLoader(IOUtils.lineIterator(getClass().getClassLoader().getResourceAsStream("cars.json"), "UTF-8"),"anyUrl", mockHttpClient), 5);
    assertEquals(150, mockHttpClient.postedData.size());} catch (Exception ex) {ex.printStackTrace();}
  }

}
