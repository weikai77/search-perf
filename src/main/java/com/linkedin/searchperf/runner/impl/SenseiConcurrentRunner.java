package com.linkedin.searchperf.runner.impl;

import java.util.ArrayList;

import org.apache.commons.logging.Log;

import com.linkedin.searchperf.query.sensei.SenseiQueryProducer;
import com.linkedin.searchperf.runner.AbstractConcurrentRunner;
import com.linkedin.searchperf.runner.PerfRunnerConfig;
import com.senseidb.search.client.json.JsonSerializer;
import com.senseidb.search.client.json.SenseiServiceProxy;
import com.senseidb.search.client.json.req.Selection;
import com.senseidb.search.client.json.req.SenseiClientRequest;
import com.senseidb.search.client.json.req.filter.Filter;
import com.senseidb.search.client.json.req.filter.Filters;

public class SenseiConcurrentRunner extends AbstractConcurrentRunner<String> {
  private SenseiServiceProxy senseiServiceProxy;
  protected SenseiQueryProducer queryProducer;
  protected int simpleSelections;
  protected int rangeSelections;
  protected int pathSelections;
  protected boolean includeFacets;

  public void init(PerfRunnerConfig config) {
    SenseiQueryProducer queryProducer = SenseiQueryProducer.build(config);
    senseiServiceProxy = new SenseiServiceProxy(config.getUrl());
    this.init(config, senseiServiceProxy, queryProducer);
  }

  public void init(PerfRunnerConfig config, SenseiServiceProxy senseiServiceProxy, SenseiQueryProducer queryProducer) {
    super.init(config.getNumThreads(), config.getTestingTime());
    this.senseiServiceProxy = senseiServiceProxy;
    this.queryProducer = queryProducer;
    simpleSelections = config.getSimpleSelections();
    rangeSelections = config.getRangeSelections();
    pathSelections = config.getPathSelections();
    includeFacets = config.isIncludeFacets();
  }

  @Override
  public String createRequest() {
    SenseiClientRequest query = queryProducer.createQuery(includeFacets, simpleSelections, rangeSelections, pathSelections);
    
   
    query.setFilter(Filters.or(query.getSelections().toArray(new Filter[query.getSelections().size()])));
    query.setSelections(new ArrayList<Selection>());
    
    return JsonSerializer.serialize(
        query).toString();

  }

  @Override
  protected void sendRequest(String request, Log QueryLog) {
    long now = System.currentTimeMillis();
    String senseiResult = senseiServiceProxy.sendPostRaw(senseiServiceProxy.getSearchUrl(), request);
    if (QueryLog.isErrorEnabled()) {
      int numHitsIndex = senseiResult.indexOf("\"numhits\"");
      numHitsIndex = senseiResult.indexOf(":", numHitsIndex);
      String numHits = senseiResult.substring(numHitsIndex + 1, senseiResult.indexOf(",", numHitsIndex));
      QueryLog.warn("time=[" + (System.currentTimeMillis() - now) + "], 1hits=[" + numHits + "], query=" + request+ "]");
    }

  }
}
