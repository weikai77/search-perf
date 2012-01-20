package com.linkedin.searchperf.common.query;

import com.linkedin.searchperf.common.launcher.SenseiPerformanceLauncher;


public class SenseiLauncher {
  public static void main(String[] args) throws Exception {

    SenseiPerformanceLauncher.main(new String[]{"configs/test-sensei.properties"});

  }
}
