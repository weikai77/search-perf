package com.linkedin.searchperf.common;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.io.LineIterator;
import org.apache.commons.logging.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.linkedin.searchperf.common.concurrent.AbstractConcurrentRunner;
import com.linkedin.searchperf.common.util.HttpClient;

public class HttpConcurrentLoader extends AbstractConcurrentRunner<List<JSONObject>> {

  private AtomicLong readCount = new AtomicLong(0);
  private AtomicLong sentCount = new AtomicLong(0);
  private LineIterator lineIterator;
  private String url;
  private  HttpClient httpClient;

  public HttpConcurrentLoader(LineIterator lineIterator, String url, HttpClient httpClient) {
    this.lineIterator = lineIterator;
    this.url = url;
    this.httpClient = httpClient;
  }

  @Override
  protected void sendRequest(List<JSONObject> request, Log QueryLog) {
    if (request.size() > 0){
      System.out.println(httpClient.postJson(url, new JSONArray(request).toString()));
    }
    sentCount.addAndGet(request.size());
    if (sentCount.get() == readCount.get()) {
      synchronized (this) {
        if (!lineIterator.hasNext()) {
          stop();
        }
      }
    }
  }

  @Override
  protected synchronized List<JSONObject> createRequest() {
    List<JSONObject> ret = new LinkedList<JSONObject>();
    int i = 0;
    while (i < 100){
      if (!lineIterator.hasNext()) {
        break;
      }
      String line = lineIterator.nextLine();
      if (line.length() > 0 && line.contains("{")) {
        try {
          ret.add(new JSONObject(line));
          readCount.incrementAndGet();
        } catch (JSONException ex) {
          throw new RuntimeException(ex);
        }
        i ++;
      }
    }
    return ret;
  }
  @Override
  protected List<List<JSONObject>> createRequestBatch(int numOfRequests) {
    return Arrays.asList(createRequest());
  }
  @Override
  protected void warmUp() {
  }

}