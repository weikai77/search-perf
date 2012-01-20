package com.linkedin.searchperf.common.query;

import com.linkedin.searchperf.common.launcher.GenericPerformanceLauncher;


public class SolrLauncher {
  public static void main(String[] args) throws Exception {

    GenericPerformanceLauncher.main(new String[]{"configs/test-solr.properties"});

  }
}
