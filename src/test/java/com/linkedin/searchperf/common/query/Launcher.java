package com.linkedin.searchperf.common.query;

import com.linkedin.searchperf.common.launcher.SenseiPerformanceLauncher;

public class Launcher {
  public static void main(String[] args) throws Exception {
    SenseiPerformanceLauncher.main(new String[]{"configs/campaigns/campaign-perf.properties"});
  }
}
