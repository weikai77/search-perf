package com.linkedin.searchperf.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;

import com.linkedin.searchperf.common.util.HttpClient;

public class HttpIndexLoader implements IndexLoader {
  private HttpClient httpClient = new HttpClient();

  @Override
  public void loadData(String url, InputStream data) {
    HttpConcurrentLoader concurrentLoader = null;
    try {
      LineIterator lineIterator = IOUtils.lineIterator(data, UTF8.name());
      concurrentLoader = new HttpConcurrentLoader(lineIterator, url, httpClient);
      concurrentLoader.init(5, Long.MAX_VALUE / 2);
      concurrentLoader.run();
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    } finally {
      if (concurrentLoader != null) {
        concurrentLoader.getExecutors().shutdown();
        try {
          concurrentLoader.getExecutors().awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
         throw new RuntimeException(e);
        }
      }
    }
  }

  public void setHttpClient(HttpClient httpClient) {
    this.httpClient = httpClient;
  }
 public static void main(String[] args) throws Exception {
   org.apache.log4j.PropertyConfigurator.configure("configs/log4j.properties");
   new HttpIndexLoader().loadData("http://localhost:8983/solr/update/json?commit=true", new FileInputStream(new File("data/cars.json")));
  }
}
