package com.linkedin.searchperf.sensei;

import org.json.JSONObject;

import com.linkedin.searchperf.common.Searcher;
import com.sensei.search.client.json.SenseiServiceProxy;
import com.sensei.search.client.json.req.SenseiClientRequest;

public class SenseiSearcher implements Searcher {

  private String _url;
  private SenseiServiceProxy _proxy;
  public SenseiSearcher(String host,int port){
    _url = "http://"+host+":"+port+"/sensei";
    _proxy = new SenseiServiceProxy(host, port);
  }

  private static SenseiClientRequest buildReq(JSONObject req){
    return null;
  }

  @Override
  public void doSearch(JSONObject req) throws Exception {
    _proxy.sendSearchRequest(buildReq(req));
  }

  @Override
  public void shutdown() throws Exception {

  }

}
