package com.linkedin.searchperf.common.concurrent;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.configuration.Configuration;

import com.linkedin.searchperf.common.util.Assert;

public class SenseiRunnerConfig {
  public void setNumThreads(int numThreads) {
    this.numThreads = numThreads;
  }

  private int numThreads;
  private long testingTime;
  private boolean includeFacets;
  private int simpleSelections;
  private int rangeSelections;
  private int pathSelections;
  private String dataFilePath;
  private String schemaPath;
  private String senseiHost;
  private int senseiPort;
  private SenseiRunnerConfig() {
  }

  public SenseiRunnerConfig(String schemaPath, String dataFilePath, int numThreads, int testingTime,
      boolean includeFacets, int simpleSelections, int rangeSelections, int pathSelections, String senseiHost, int senseiPort) {
    this.schemaPath = schemaPath;
    this.dataFilePath = dataFilePath;
    this.numThreads = numThreads;
    this.testingTime = testingTime;
    this.includeFacets = includeFacets;
    this.simpleSelections = simpleSelections;
    this.rangeSelections = rangeSelections;
    this.pathSelections = pathSelections;
    this.senseiHost = senseiHost;
    this.senseiPort = senseiPort;
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

  public String getSenseiHost() {
    return senseiHost;
  }

  public int getSenseiPort() {
    return senseiPort;
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
    ret.schemaPath = conf.getString("schemaPath");
    Assert.notNull(ret.schemaPath, "schemaPath entry should be present in the config");
    ret.dataFilePath = conf.getString("dataFilePath");
    Assert.notNull(ret.dataFilePath, "dataFilePath entry should be present in the config");

    ret.testingTime = conf.getLong("testingTime");
    ret.includeFacets = conf.getBoolean("includeFacets");
    ret.simpleSelections = conf.getInt("simpleSelections");
    ret.rangeSelections = conf.getInt("rangeSelections");
    ret.pathSelections = conf.getInt("pathSelections");
    ret.senseiHost = conf.getString("senseiHost");
    Assert.notNull(ret.senseiHost, "senseiHost entry should be present in the config");
    ret.senseiPort = conf.getInt("senseiPort");
    ret.numThreads = 10;
    return ret;
  }
}
