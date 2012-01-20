package com.linkedin.searchperf.runner;

import com.linkedin.searchperf.runner.impl.ElasticConcurrentRunner;
import com.linkedin.searchperf.runner.impl.SenseiConcurrentRunner;
import com.linkedin.searchperf.runner.impl.SolrConcurrentRunner;

public class Runners {
  public static ConcurrentRunner createByType(String type) {
    if ("sensei".equalsIgnoreCase(type)) {
      return new SenseiConcurrentRunner();
    }
    if ("solr".equalsIgnoreCase(type)) {
      return new SolrConcurrentRunner();
    }
    if (type.toLowerCase().contains("elastic")) {
      return new ElasticConcurrentRunner();
    }
    throw new UnsupportedOperationException();
  } 
}
