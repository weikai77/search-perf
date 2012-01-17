package com.linkedin.searchperf.sensei;

import org.apache.commons.logging.Log;

import com.linkedin.searchperf.common.concurrent.AbstractConcurrentRunner;
import com.linkedin.searchperf.common.concurrent.SenseiRunnerConfig;
import com.linkedin.searchperf.sensei.query.SenseiQueryProducer;
import com.senseidb.search.client.json.JsonSerializer;
import com.senseidb.search.client.json.SenseiServiceProxy;

public class SenseiConcurrentRunner extends AbstractConcurrentRunner<String> {
  private SenseiServiceProxy senseiServiceProxy;
  private SenseiQueryProducer queryProducer;
  private int simpleSelections;
  private int rangeSelections;
  private int pathSelections;
  private boolean includeFacets;

  public void init(SenseiRunnerConfig config) {
    SenseiQueryProducer queryProducer = new SenseiQueryProducer();
    queryProducer.init(SenseiRunnerConfig.getResource(config.getSchemaPath()),
        SenseiRunnerConfig.getResource(config.getDataFilePath()));
    senseiServiceProxy = new SenseiServiceProxy(config.getSenseiHost(), config.getSenseiPort());
    this.init(config, senseiServiceProxy, queryProducer);
  }

  public void init(SenseiRunnerConfig config, SenseiServiceProxy senseiServiceProxy, SenseiQueryProducer queryProducer) {
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
    return JsonSerializer.serialize(
        queryProducer.createQuery(includeFacets, simpleSelections, rangeSelections, pathSelections)).toString();

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
