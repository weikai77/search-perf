package com.linkedin.searchperf.es;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.action.search.SearchRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.json.JSONObject;

import com.linkedin.searchperf.common.Searcher;

public class ElasticSearchSearcher implements Searcher {

  private final String _host;
  private final int _port;
  private final Client _client;
  public ElasticSearchSearcher(String host,int port){
    _host = host;
    _port = port;
    _client = new TransportClient().addTransportAddress(new InetSocketTransportAddress(host, port));
  }
  
  private static SearchRequestBuilder buildSearchReq(JSONObject req){
    return null;
  }
  
  @Override
  public void doSearch(JSONObject req) throws Exception {
    SearchRequestBuilder reqBuilder = buildSearchReq(req); 
    reqBuilder.execute().actionGet();
  }

  @Override
  public void shutdown() throws Exception {
    _client.close();
  }

}
