package com.linkedin.searchperf.sensei;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;

import com.linkedin.searchperf.common.concurrent.AbstractConcurrentRunner;
import com.linkedin.searchperf.common.concurrent.SenseiRunnerConfig;
import com.linkedin.searchperf.common.query.QueryProducer;
import com.sensei.search.client.json.JsonSerializer;
import com.sensei.search.client.json.SenseiServiceProxy;
import com.yammer.metrics.core.TimerContext;
import com.yammer.metrics.core.TimerMetric;

public class SenseiConcurrentRunner extends AbstractConcurrentRunner<List<String>> {
  private SenseiServiceProxy senseiServiceProxy;
  private QueryProducer queryProducer;
  private int simpleSelections;
  private int rangeSelections;
  private int pathSelections;
  private boolean includeFacets;

  public void init(SenseiRunnerConfig config) {
    QueryProducer queryProducer = new QueryProducer();
    queryProducer.init(SenseiRunnerConfig.getResource(config.getSchemaPath()),
        SenseiRunnerConfig.getResource(config.getDataFilePath()));
    senseiServiceProxy = new SenseiServiceProxy(config.getSenseiHost(), config.getSenseiPort());
    this.init(config, senseiServiceProxy, queryProducer);
  }

  public void init(SenseiRunnerConfig config, SenseiServiceProxy senseiServiceProxy, QueryProducer queryProducer) {
    super.init(config.getNumThreads(), config.getTestingTime());
    this.senseiServiceProxy = senseiServiceProxy;
    this.queryProducer = queryProducer;
    simpleSelections = config.getSimpleSelections();
    rangeSelections = config.getRangeSelections();
    pathSelections = config.getPathSelections();
    includeFacets = config.isIncludeFacets();
  }

  @Override
  public List<String> createRequest() {
    List<String> ret = new ArrayList<String>(100);
    for (int i = 0; i < 100; i++) {

      ret.add(JsonSerializer.serialize(queryProducer.createQuery(includeFacets, simpleSelections, rangeSelections, pathSelections)).toString());
    }
    return ret;
  }

  @Override
  protected void sendRequest(List<String> requests, Log QueryLog, TimerMetric responseTimeMetric, AtomicBoolean stop) {
    for (String request : requests) {
      if (stop.get()) {
        //LOG.error("!!!Interrupted" + System.currentTimeMillis());
        break;
      }
      long now = System.currentTimeMillis();
      TimerContext time = responseTimeMetric.time();
      String senseiResult = senseiServiceProxy.sendPostRaw(senseiServiceProxy.getSearchUrl(), request);
      time.stop();
      if (QueryLog.isErrorEnabled()) {
        int numHitsIndex = senseiResult.indexOf("\"numhits\"");
        numHitsIndex = senseiResult.indexOf(":", numHitsIndex);
        String numHits = senseiResult.substring(numHitsIndex + 1, senseiResult.indexOf(",", numHitsIndex));
        QueryLog.warn("time=[" + (System.currentTimeMillis() - now) + "], 1hits=[" + numHits + "], query=" + request + "]");
      }
    }
  }
}
