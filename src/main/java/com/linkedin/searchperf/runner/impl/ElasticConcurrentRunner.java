package com.linkedin.searchperf.runner.impl;

import org.apache.commons.logging.Log;

import com.linkedin.searchperf.common.util.HttpClient;
import com.linkedin.searchperf.query.sensei.SenseiQueryProducer;
import com.linkedin.searchperf.query.solr.ElasticQueryProducer;
import com.linkedin.searchperf.runner.AbstractConcurrentRunner;
import com.linkedin.searchperf.runner.PerfRunnerConfig;

public class ElasticConcurrentRunner extends AbstractConcurrentRunner<String> {

  private ElasticQueryProducer elasticQueryProducer;
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
   
    this.elasticQueryProducer = new ElasticQueryProducer(SenseiQueryProducer.build(config));
  }
  
  @Override
  public String createRequest() {
    return elasticQueryProducer.createQuery(includeFacets, simpleSelections, rangeSelections, pathSelections);
  }
  @Override
  protected void sendRequest(String request, Log QueryLog) {
    long now = System.currentTimeMillis();
    String senseiResult = httpClient.sendPostRaw(url, request);
    if (QueryLog.isErrorEnabled()) {
      int numHitsIndex = senseiResult.indexOf("\"hits\"");
      numHitsIndex = senseiResult.indexOf("total", numHitsIndex);
      numHitsIndex = senseiResult.indexOf(":", numHitsIndex);
      String numHits = senseiResult.substring(numHitsIndex + 1, senseiResult.indexOf(",", numHitsIndex));
      QueryLog.warn("time=[" + (System.currentTimeMillis() - now) + "], 1hits=[" + numHits + "], query=" + request+ "]");
    }

  }
  

  
}
