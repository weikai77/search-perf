package com.linkedin.searchperf.solr;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.json.JSONObject;

import com.linkedin.searchperf.common.Searcher;

public class SolrSearcher implements Searcher {
  private final CommonsHttpSolrServer _solr;
  
  public SolrSearcher(String host,int port) throws Exception{
    String url = "http://"+host+":"+port+"/solr/";
    _solr = new CommonsHttpSolrServer(url);
  }
  

  private static SolrQuery buildSearchReq(JSONObject req){
    return null;
  }
  
  @Override
  public void doSearch(JSONObject req) throws Exception {
    _solr.query(buildSearchReq(req));
  }

  @Override
  public void shutdown() throws Exception {
    
  }

}
