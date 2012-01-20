package com.linkedin.searchperf.runner.impl;

import org.apache.commons.logging.Log;

import com.linkedin.searchperf.common.util.HttpClient;
import com.linkedin.searchperf.query.sensei.SenseiQueryProducer;
import com.linkedin.searchperf.query.solr.SolrQueryProducer;
import com.linkedin.searchperf.query.solr.SolrQueryProducer.SolrQuery;
import com.linkedin.searchperf.runner.AbstractConcurrentRunner;
import com.linkedin.searchperf.runner.PerfRunnerConfig;

public class SolrConcurrentRunner extends AbstractConcurrentRunner<SolrQuery> {

  private SolrQueryProducer solrQueryProducer;
  private int simpleSelections;
  private int rangeSelections;
  private int pathSelections;
  private boolean includeFacets;
  private String url;
  private HttpClient httpClient = new HttpClient();
 
  public void init(PerfRunnerConfig config) {
    super.init(config.getNumThreads(), config.getTestingTime());    
    url = config.getUrl();
    simpleSelections = config.getSimpleSelections();
    rangeSelections = config.getRangeSelections();
    pathSelections = config.getPathSelections();
    includeFacets = config.isIncludeFacets();
   
    this.solrQueryProducer = new SolrQueryProducer(SenseiQueryProducer.build(config));
  }
  
  @Override
  public SolrQuery createRequest() {
    return solrQueryProducer.createSolrQuery(includeFacets, simpleSelections, rangeSelections, pathSelections);

  }

  @Override
  protected void sendRequest(SolrQuery request, Log QueryLog) {
    long now = System.currentTimeMillis();
    String query = request.query;
    //Unwrap from high level parentheses
    query = query.substring(1, query.length() - 1);
    StringBuilder queryStr = new StringBuilder("?q=").append(query).append("&facet=true");
    for (String facet : request.facets) {
      queryStr.append("&facet.field=").append(facet);
    }
    String modifiedUrl = url + queryStr.toString().replaceAll(" ", "%20");
    String senseiResult = httpClient.sendGet(modifiedUrl);
    //System.out.println(senseiResult);
    if (QueryLog.isErrorEnabled()) {
      int numHitsIndex = senseiResult.indexOf("numFound=");
      numHitsIndex = senseiResult.indexOf("=", numHitsIndex);
      String numHits = senseiResult.substring(numHitsIndex + 2, senseiResult.indexOf("\"", numHitsIndex + 2));
      QueryLog.warn("time=[" + (System.currentTimeMillis() - now) + "], 1hits=[" + numHits + "], query=" + modifiedUrl + "response=" + senseiResult + "]");
    }

  }

}
