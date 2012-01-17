package com.linkedin.searchperf.common.launcher;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;

import com.linkedin.searchperf.common.concurrent.SenseiRunnerConfig;
import com.linkedin.searchperf.common.util.Assert;
import com.linkedin.searchperf.sensei.SenseiConcurrentRunner;
import com.linkedin.searchperf.sensei.query.SenseiQueryProducer;
import com.sensei.search.client.json.SenseiServiceProxy;
import com.yammer.metrics.reporting.ConsoleReporter;

public class SenseiPerformanceLauncher {
  private static final String RESULT_FILE = "performanceResult.txt";
  public static void main(String[] args) throws Exception {
    PropertiesConfiguration config = extractPropertyConfig(args);
    List<Integer> threads = extractNumberOfThreads(config);
    SenseiRunnerConfig senseiRunnerConfig = SenseiRunnerConfig.build(config);
    SenseiQueryProducer queryProducer = new SenseiQueryProducer();
    queryProducer.init(SenseiRunnerConfig.getResource(senseiRunnerConfig.getSchemaPath()),
        SenseiRunnerConfig.getResource(senseiRunnerConfig.getDataFilePath()));
    SenseiServiceProxy senseiServiceProxy = new SenseiServiceProxy(senseiRunnerConfig.getSenseiHost(), senseiRunnerConfig.getSenseiPort());
    List<PerformanceResult> ret = new ArrayList<PerformanceResult>();
    FileUtils.deleteQuietly(new File(RESULT_FILE));
    File resultFile = new File(RESULT_FILE);
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


  private static List<Integer> extractNumberOfThreads(PropertiesConfiguration config) {
    List<Integer> ret = new LinkedList<Integer>();
    String threadsStr = config.getString("numThreads");
    Assert.notNull(threadsStr, "numThreads config entry should not be null");
    for (String number : threadsStr.split(",")) {
      ret.add(Integer.parseInt(number.trim()));
    }
    return ret;
  }


  private static PerformanceResult run(SenseiRunnerConfig senseiRunnerConfig, SenseiQueryProducer queryProducer,
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
    PerformanceResult performanceResult = process(output, senseiRunnerConfig);

    return performanceResult;
  }


  private static PerformanceResult process(String output, SenseiRunnerConfig senseiRunnerConfig) {
    Map<String, String> map = new HashMap<String, String>();
    for (String str : output.split("\\n")) {
      if (str == null || str.contains("==") || !str.contains("=")) {
        continue;
      }
      //System.out.println(str);
      map.put(str.split("=")[0].trim(), str.split("=")[1].trim());
    }
    double qps = ((double)Integer.valueOf(map.get("count"))) * 1000 / senseiRunnerConfig.getTestingTime();
    String min = map.get("min");
    String mean = map.get("mean").trim();
    String median = map.get("median").trim();
    String s75 = map.get("75% <").trim();
    PerformanceResult ret = new PerformanceResult(senseiRunnerConfig.getNumThreads(), min, mean, median, s75,String.valueOf(qps));
    return ret;

  }
  private static PropertiesConfiguration extractPropertyConfig(String[] args) throws ConfigurationException {
    PropertiesConfiguration config = new PropertiesConfiguration();
    config.setDelimiterParsingDisabled(true);
    File configFile = null;
    if (args.length<1){
      System.out.println(help());
      System.exit(1);
    } else {
       configFile = new File(args[0]);
      config.load(configFile);
    }
    org.apache.log4j.PropertyConfigurator.configure(configFile.getParent() + "/log4j.properties");
    return config;
  }
  private static String help(){
    StringBuffer buffer = new StringBuffer();
    buffer.append("Usage: <conf.file> \n");
    buffer.append("====================================\n");
    buffer.append("conf.file - file that contains config data to run the perf test e.g. perf-sensei.properties\n");
    buffer.append("Please specify the conf file path!\n");
    buffer.append("====================================\n");
    return buffer.toString();
  }
  public static class PerformanceResult {
    int numberOfThreads;
    String min;
    String mean;
   
    String qps;
    String median;
    String valueFor75;
    public PerformanceResult(int numberOfThreads, String min, String mean,String median, String valueFor75, String qps) {
      super();
      this.numberOfThreads = numberOfThreads;
      this.min = min;
      this.mean = mean;
      this.median = median;
      this.valueFor75 = valueFor75;
      this.qps = qps;
    }
    @Override
    public String toString() {
      return "   " + numberOfThreads + "         "+  min + "  "+  median + "    "+  mean + "     "+  valueFor75 + "     " + qps + "";
    }
    public static String getMetadata() {
      return "#Threads     min     median     mean         75%         qps";
    }
  }
}
