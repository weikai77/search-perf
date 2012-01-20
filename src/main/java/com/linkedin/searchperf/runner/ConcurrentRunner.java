package com.linkedin.searchperf.runner;

public interface ConcurrentRunner {
  public void run();
  public void init(PerfRunnerConfig perfRunnerConfig);
  public void shutdown();
}
