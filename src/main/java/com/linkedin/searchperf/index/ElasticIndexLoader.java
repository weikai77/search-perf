package com.linkedin.searchperf.index;

import java.util.List;

import org.apache.commons.io.LineIterator;
import org.apache.commons.logging.Log;
import org.json.JSONObject;

import com.linkedin.searchperf.common.util.HttpClient;

public class ElasticIndexLoader extends SolrIndexLoader {

  public ElasticIndexLoader(LineIterator lineIterator, String url, HttpClient httpClient) {
    super(lineIterator, url, httpClient);   
  }
  @Override
  protected void sendRequest(List<JSONObject> request, Log QueryLog) {
    
    if (request.size() == 0) {
      //System.out.println("!!Stopping sentCount = " + sentCount.get() + ", readCount = " + readCount.get());
      if (sentCount.get() == readCount.get()) {
      stop();
      }
    }
    if (sentCount.get() % 500 == 0 && sentCount.get() > 0) {System.out.println("Sent " + sentCount.get() + " documents");}
    if (request.size() > 0){
      for (int i = 0; i < request.size(); i++) {
        httpClient.postJson(url, request.get(i).toString());
      }
    }
    sentCount.addAndGet(request.size());   
   
      if (sentCount.get() == readCount.get()) {
        stop();
      }
    
  }
}
