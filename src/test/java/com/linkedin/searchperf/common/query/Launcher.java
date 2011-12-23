package com.linkedin.searchperf.common.query;

import com.linkedin.searchperf.common.launcher.PerformanceLauncher;

public class Launcher {
  public static void main(String[] args) throws Exception {
    PerformanceLauncher.main(new String[]{"configs/campaigns/campaign-perf.properties"});
  }
}
