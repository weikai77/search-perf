package com.linkedin.searchperf.common.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sensei.search.client.json.SenseiServiceProxy;

public class HttpClient {
  protected static Log LOG = LogFactory.getLog(HttpClient.class);
  private static SenseiServiceProxy serviceProxy = new SenseiServiceProxy("", 0);
  public String postJson(String url, String jsonData) {
    return serviceProxy.sendPostRaw(url, jsonData);
  }
}
