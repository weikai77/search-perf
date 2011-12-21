package com.linkedin.searchperf.common.concurrent;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.configuration.Configuration;

public class SenseiRunnerConfig {
  private int numThreads;
  private long testingTime;
  private boolean includeFacets;
  private int simpleSelections;
  private int rangeSelections;
  private int pathSelections;
  private String dataFilePath;
  private String schemaPath;

  private SenseiRunnerConfig() {
  }

  public SenseiRunnerConfig(String schemaPath, String dataFilePath, int numThreads, int testingTime,
      boolean includeFacets, int simpleSelections, int rangeSelections, int pathSelections) {
    this.schemaPath = schemaPath;
    this.dataFilePath = dataFilePath;
    this.numThreads = numThreads;
    this.testingTime = testingTime;
    this.includeFacets = includeFacets;
    this.simpleSelections = simpleSelections;
    this.rangeSelections = rangeSelections;
    this.pathSelections = pathSelections;
  }

  public int getNumThreads() {
    return numThreads;
  }

  public long getTestingTime() {
    return testingTime;
  }

  public boolean isIncludeFacets() {
    return includeFacets;
  }

  public int getSimpleSelections() {
    return simpleSelections;
  }

  public int getRangeSelections() {
    return rangeSelections;
  }

  public int getPathSelections() {
    return pathSelections;
  }

  public String getDataFilePath() {
    return dataFilePath;
  }

  public String getSchemaPath() {
    return schemaPath;
  }

  public static InputStream getResource(String path) {
    try {
      URL resource = SenseiRunnerConfig.class.getClassLoader().getResource(path);
      if (resource != null) {
        return new BufferedInputStream(resource.openStream());
      }
      return new BufferedInputStream(new FileInputStream(new File(path)));
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  public static SenseiRunnerConfig build(Configuration conf) {
    SenseiRunnerConfig ret = new SenseiRunnerConfig();
    ret.schemaPath = conf.getString("schemaPath", "sensei/schema.xml");
    ret.dataFilePath = conf.getString("dataFilePath", "dataFilePath");
    ret.numThreads = conf.getInt("numThreads", 1);
    ret.testingTime = conf.getLong("testingTime", 30000);
    ret.includeFacets = conf.getBoolean("includeFacets", false);
    ret.simpleSelections = conf.getInt("simpleSelections", 1);
    ret.rangeSelections = conf.getInt("rangeSelections", 1);
    ret.pathSelections = conf.getInt("pathSelections", 1);
    return ret;
  }
}
