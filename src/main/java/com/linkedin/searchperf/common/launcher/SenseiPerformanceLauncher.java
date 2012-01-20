package com.linkedin.searchperf.common.launcher;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;

import com.linkedin.searchperf.common.launcher.GenericPerformanceLauncher.PerformanceResult;
import com.linkedin.searchperf.query.sensei.SenseiQueryProducer;
import com.linkedin.searchperf.runner.PerfRunnerConfig;
import com.linkedin.searchperf.runner.impl.SenseiConcurrentRunner;
import com.senseidb.search.client.json.SenseiServiceProxy;
import com.yammer.metrics.reporting.ConsoleReporter;

public class SenseiPerformanceLauncher {
  
  public static void main(String[] args) throws Exception {
    PropertiesConfiguration config = GenericPerformanceLauncher.extractPropertyConfig(args);
    List<Integer> threads = GenericPerformanceLauncher.extractNumberOfThreads(config);
    PerfRunnerConfig senseiRunnerConfig = PerfRunnerConfig.build(config);
    SenseiQueryProducer queryProducer = new SenseiQueryProducer();
    queryProducer.init(PerfRunnerConfig.getResource(senseiRunnerConfig.getSchemaPath()),
        PerfRunnerConfig.getResource(senseiRunnerConfig.getDataFilePath()));
    SenseiServiceProxy senseiServiceProxy = new SenseiServiceProxy(senseiRunnerConfig.getUrl());
    List<PerformanceResult> ret = new ArrayList<PerformanceResult>();
    FileUtils.deleteQuietly(new File(GenericPerformanceLauncher.RESULT_FILE));
    File resultFile = new File(GenericPerformanceLauncher.RESULT_FILE);
    FileWriter fileWriter = new FileWriter(resultFile, true);
    fileWriter.append(PerformanceResult.getMetadata() + "\n");
    System.gc();
    System.out.println(PerformanceResult.getMetadata());
    for (int i : threads) {
      senseiRunnerConfig.setNumThreads(i);
      SenseiConcurrentRunner senseiConcurrentRunner = new SenseiConcurrentRunner();
      PerformanceResult result = run(senseiRunnerConfig, queryProducer, senseiServiceProxy, senseiConcurrentRunner);
      fileWriter.append(result.toString() + "\n");
      System.out.println(result);
    }
    fileWriter.close();
    System.exit(0);
  }

  private static PerformanceResult run(PerfRunnerConfig senseiRunnerConfig, SenseiQueryProducer queryProducer,
      SenseiServiceProxy senseiServiceProxy, SenseiConcurrentRunner senseiConcurrentRunner)
      throws UnsupportedEncodingException {
    senseiConcurrentRunner.init(senseiRunnerConfig, senseiServiceProxy, queryProducer);
    senseiConcurrentRunner.run();
    senseiConcurrentRunner.getExecutors().shutdown();
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    PrintStream printStream = new PrintStream(byteArrayOutputStream);
    ConsoleReporter consoleReporter = new ConsoleReporter(printStream);
    consoleReporter.run();
    consoleReporter.shutdown();
    String output = byteArrayOutputStream.toString("UTF-8");
    //System.out.println(((List)senseiConcurrentRunner.createRequest()).get(0).toString());
    PerformanceResult performanceResult = GenericPerformanceLauncher.process(output, senseiRunnerConfig);
    return performanceResult;
  } 
}
