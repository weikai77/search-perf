package com.linkedin.searchperf.common.concurrent;

import java.io.File;
import java.io.PrintStream;

import org.apache.commons.configuration.PropertiesConfiguration;

import com.sensei.search.client.json.SenseiServiceProxy;
import com.yammer.metrics.reporting.ConsoleReporter;

public class PerformanceTestLauncher {

  public static void main(String[] args) throws Exception {
    String confDir = null;
    PropertiesConfiguration config = null;
    if (args.length<1){
      System.out.println(help());
      config = new PropertiesConfiguration();
      config.load(PerformanceTestLauncher.class.getClassLoader().getResourceAsStream("perf-sensei.properties"));
    } else {
      config.load(new File(args[0], "perf-sensei.properties"));
    }
    File resultFile = new File("test-result");
    if (resultFile.exists()) {
      resultFile.delete();
    }
    //org.apache.log4j.PropertyConfigurator.configure("\log4j.properties");
    SenseiRunnerConfig senseiRunnerConfig = SenseiRunnerConfig.build(config);
    SenseiServiceProxy senseiServiceProxy = new SenseiServiceProxy(config.getString("sensei.host", "localhost"), config.getInt("sensei.port", 8080));
    SenseiConcurrentRunner senseiConcurrentRunner = new SenseiConcurrentRunner();
    senseiConcurrentRunner.run(senseiRunnerConfig, senseiServiceProxy);

    PrintStream printStream = new PrintStream(new File("perf-result"));
    new ConsoleReporter(printStream).run();
    printStream.close();
    //senseiConcurrentRunner.responseTimeMetric.dump(new File("perf-result"));
  }
  private static String help(){
    StringBuffer buffer = new StringBuffer();
    buffer.append("Usage: <conf.dir> \n");
    buffer.append("====================================\n");
    buffer.append("conf.dir - Dir that contains perf-sensei.properties\n");
    buffer.append("Luanching configuration from the classpath\n");
    buffer.append("====================================\n");
    return buffer.toString();
  }
}
