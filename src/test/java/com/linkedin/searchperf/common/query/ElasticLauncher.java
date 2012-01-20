package com.linkedin.searchperf.common.query;

import com.linkedin.searchperf.common.launcher.GenericPerformanceLauncher;


public class ElasticLauncher {
  public static void main(String[] args) throws Exception {

    GenericPerformanceLauncher.main(new String[]{"configs/test-elastic.properties"});

  }
}
