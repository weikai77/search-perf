package com.linkedin.searchperf.index;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.LineIterator;
import org.apache.commons.logging.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.linkedin.searchperf.common.util.HttpClient;
import com.linkedin.searchperf.runner.AbstractConcurrentRunner;
import com.linkedin.searchperf.runner.PerfRunnerConfig;

public class SolrIndexLoader extends AbstractConcurrentRunner<List<JSONObject>> {

  
  private LineIterator lineIterator;
  protected String url;
  protected  HttpClient httpClient;
  protected volatile boolean empty = false;
  public SolrIndexLoader(LineIterator lineIterator, String url, HttpClient httpClient) {
    this.lineIterator = lineIterator;
    this.url = url;
    this.httpClient = httpClient;
  }

  @Override
  protected void sendRequest(List<JSONObject> request, Log QueryLog) {
    
    if (request.size() == 0 && empty) {
      //System.out.println("!!Stopping sentCount = " + sentCount.get() + ", readCount = " + readCount.get());
      if (sentCount.get() == readCount.get()) {
      stop();
      }
    }
    if (sentCount.get() % 2000 == 0  && sentCount.get() > 0) {System.out.println("Sent " + sentCount.get() + " documents");}
    if (request.size() > 0){
     
      httpClient.postJson(url, new JSONArray(request).toString());;
    }
    sentCount.addAndGet(request.size()); 
    if (sentCount.get() == readCount.get() && empty) {
      stop();
    }
  }

  @Override
  protected synchronized List<JSONObject> createRequest() {
    List<JSONObject> ret = new LinkedList<JSONObject>();
    int i = 0;
    if (!lineIterator.hasNext()) {       
      empty = true;
      return Collections.EMPTY_LIST;
    }
    while (i < 1000){
      if (!lineIterator.hasNext()) {       
        empty = true;
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

  @Override
  public void init(PerfRunnerConfig perfRunnerConfig) {    
    
  }

}